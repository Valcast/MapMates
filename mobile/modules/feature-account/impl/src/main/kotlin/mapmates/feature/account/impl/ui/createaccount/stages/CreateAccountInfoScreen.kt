package mapmates.feature.account.impl.ui.createaccount.stages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mapmates.feature.account.impl.ui.createaccount.CreateAccountState
import mapmates.feature.account.impl.R as AccountR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CreateAccountInfoScreen(
    state: CreateAccountState,
    onUpdateFirstName: (String) -> Unit,
    onUpdateMiddleName: (String) -> Unit,
    onUpdateLastName: (String) -> Unit,
    onUpdateBio: (String) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = stringResource(id = AccountR.string.account_info_title),
            style = MaterialTheme.typography.headlineMediumEmphasized,
            fontWeight = FontWeight.Bold,
        )

        OutlinedTextField(
            value = state.firstName,
            onValueChange = onUpdateFirstName,
            singleLine = true,
            label = {
                Text(
                    text = stringResource(AccountR.string.account_info_firstname_hint),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = state.middleName,
            onValueChange = onUpdateMiddleName,
            singleLine = true,
            label = {
                Text(
                    text = stringResource(AccountR.string.account_info_middlename_hint),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = state.lastName,
            onValueChange = onUpdateLastName,
            singleLine = true,
            label = {
                Text(
                    text = stringResource(AccountR.string.account_info_lastname_hint),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Text(
            text = stringResource(AccountR.string.account_info_name_tip),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )
        Text(
            text = stringResource(AccountR.string.account_info_bio_description),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
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
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}
