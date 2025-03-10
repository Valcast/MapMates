package com.example.socialmeetingapp.data.repository

import com.example.socialmeetingapp.data.utils.toNotification
import com.example.socialmeetingapp.domain.model.Notification
import com.example.socialmeetingapp.domain.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class NotificationRepositoryImpl(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : NotificationRepository {
    override val notifications: Flow<List<Notification>> = callbackFlow {
        val listenerRegistration =
            db.collection("users").document(auth.currentUser!!.uid).collection("notifications")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val notifications = snapshot.documents.map { doc ->
                            doc.toNotification()
                        }

                        trySend(notifications)
                    } else {
                        trySend(emptyList())
                    }
                }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun markAsRead(id: String) {
        db.collection("users").document(auth.currentUser!!.uid).collection("notifications")
            .document(id)
            .update("read", true)
    }

    override suspend fun markAllAsRead() {
        db.collection("users").document(auth.currentUser!!.uid).collection("notifications")
            .whereEqualTo("read", false)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.forEach { doc ->
                    doc.reference.update("read", true)
                }
            }
    }

    override suspend fun deleteNotification(id: String) {
        TODO("Not yet implemented")
    }

}