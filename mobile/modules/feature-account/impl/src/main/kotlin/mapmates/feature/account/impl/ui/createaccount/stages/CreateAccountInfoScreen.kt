package mapmates.feature.account.impl.ui.createaccount.stages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mapmates.feature.account.impl.ui.createaccount.CreateAccountState
import mapmates.feature.account.impl.R as AccountR
@Composable
internal fun CreateAccountInfoScreen(
    state: CreateAccountState,
    onUpdateUsername: (String) -> Unit,
    onUpdateBio: (String) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(
            text = stringResource(id = AccountR.string.account_info_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )

        OutlinedTextField(
            value = state.username,
            onValueChange = onUpdateUsername,
            singleLine = true,
            label = {
                Text(
                    text = stringResource(AccountR.string.account_info_username_hint),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            trailingIcon = {
                Text(
                    text = "${state.username.length}/30",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            },
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = stringResource(AccountR.string.account_info_username_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        OutlinedTextField(
            value = state.bio,
            onValueChange = onUpdateBio,
            minLines = 3,
            maxLines = 3,
            label = {
                Text(
                    text = stringResource(AccountR.string.account_info_bio_hint),
                    style = MaterialTheme.typography.labelSmall
                )
            },
        )

        Text(
            text = stringResource(AccountR.string.account_info_bio_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}