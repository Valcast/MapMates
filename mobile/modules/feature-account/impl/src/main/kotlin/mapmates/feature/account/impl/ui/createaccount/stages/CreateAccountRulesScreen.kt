package mapmates.feature.account.impl.ui.createaccount.stages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mapmates.feature.account.impl.ui.createaccount.CreateAccountState
import mapmates.feature.account.impl.R as AccountR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CreateAccountRulesScreen(
    state: CreateAccountState, onUpdatePrivacyPolicy: () -> Unit, onUpdateTermsOfService: () -> Unit
) {
    Box(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = stringResource(AccountR.string.account_rules_title),
            style = MaterialTheme.typography.headlineMediumEmphasized,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = stringResource(AccountR.string.account_rules_privacy_policy),
                style = MaterialTheme.typography.headlineMediumEmphasized,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Button(
                onClick = onUpdatePrivacyPolicy, shapes = ButtonShapes(
                    shape = if (state.isPrivacyPolicyAccepted) {
                        MaterialTheme.shapes.medium
                    } else MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.medium,
                ), colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isPrivacyPolicyAccepted) {
                        MaterialTheme.colorScheme.primary
                    } else Color.Transparent,
                    contentColor = if (state.isPrivacyPolicyAccepted) {
                        MaterialTheme.colorScheme.onPrimary
                    } else MaterialTheme.colorScheme.onBackground,
                ), border = if (state.isPrivacyPolicyAccepted) {
                    null
                } else {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = stringResource(AccountR.string.account_rules_privacy_policy_accept),
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                )
            }

            Text(
                text = stringResource(AccountR.string.account_rules_terms_of_service),
                style = MaterialTheme.typography.headlineMediumEmphasized,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 32.dp)
            )

            Button(
                onClick = onUpdateTermsOfService, shapes = ButtonShapes(
                    shape = if (state.isTermsOfServiceAccepted) {
                        MaterialTheme.shapes.medium
                    } else MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.medium,
                ), colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isTermsOfServiceAccepted) {
                        MaterialTheme.colorScheme.primary
                    } else Color.Transparent,
                    contentColor = if (state.isTermsOfServiceAccepted) {
                        MaterialTheme.colorScheme.onPrimary
                    } else MaterialTheme.colorScheme.onBackground,
                ), border = if (state.isTermsOfServiceAccepted) {
                    null
                } else {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = stringResource(AccountR.string.account_rules_accept),
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                )
            }
        }
    }
}