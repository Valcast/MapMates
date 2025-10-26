package mapmates.feature.account.impl.data

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import mapmates.feature.account.impl.model.AccountCreationResult
import mapmates.feature.account.impl.ui.createaccount.Gender
import javax.inject.Inject

internal class AccountRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
) {

    suspend fun createAccount(
        userId: String,
        firstName: String,
        middleName: String,
        lastName: String,
        bio: String,
        picturesList: LinkedHashSet<Uri>,
        dateOfBirth: LocalDate,
        gender: Gender,
    ) = withContext(Dispatchers.IO) {
        val uploadResults = uploadProfilePictures(
            userId = userId,
            picturesList = picturesList,
        )
        val uploadedUrls = uploadResults.mapNotNull { it.successUrl }
        val userMap = hashMapOf(
            "firstName" to firstName,
            "middleName" to middleName,
            "lastName" to lastName,
            "bio" to bio,
            "dateOfBirth" to dateOfBirth.toString(),
            "gennder" to gender.name,
            "profilePictures" to uploadedUrls,
            "pictures" to picturesList.map { it.toString() }
        )

        try {
            db.collection("users")
                .document(userId)
                .set(userMap)
                .await()
            Log.d("AccountRepository", "Account created successfully for userId: $userId")
            AccountCreationResult.Success
        } catch (e: Throwable) {
            Log.e("AccountRepository", "Error creating account for userId: $userId", e)
            AccountCreationResult.Failure(1)
        }
    }

    private suspend fun uploadProfilePictures(
        userId: String,
        picturesList: LinkedHashSet<Uri>,
    ): List<UploadResult> = supervisorScope {
        val storageRef = storage.reference.child("users/$userId/profile_pictures/")

        picturesList.map { pictureUri ->
            async {
                val name = pictureUri.lastPathSegment
                    ?.substringAfterLast('/')
                    ?.takeIf { it.isNotBlank() }
                    ?: "profile_picture_${System.currentTimeMillis()}"

                val pictureRef = storageRef.child(name)

                try {
                    pictureRef.putFile(pictureUri).await()
                    val url = pictureRef.downloadUrl.await().toString()
                    UploadResult(successUrl = url)
                } catch (t: Throwable) {
                    UploadResult(error = t)
                }
            }
        }.awaitAll()
    }

    private data class UploadResult(
        val successUrl: String? = null,
        val error: Throwable? = null,
    )
}