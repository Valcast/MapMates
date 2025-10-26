package com.valcast.mapmates.presentation.introduction

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.valcast.mapmates.presentation.components.DashedProgressIndicator

@Composable
fun IntroductionScreen(onFinish: () -> Unit) {

    data class Page(val title: String, val description: String)

    val pages = listOf(
        Page(
            title = "Cześć!",
            description = "Witaj w naszej aplikacji, która łączy ludzi z pasjami i wydarzeniami!"
        ),
        Page(
            title = "Odkryj wydarzenia",
            description = "Znajdź ciekawe wydarzenia w swojej okolicy i bądź na bieżąco z tym, co się dzieje!"
        ),
        Page(
            title = "Twórz i zapraszaj",
            description = "Organizuj własne wydarzenia i zapraszaj znajomych, aby wspólnie spędzać czas!"
        ),
        Page(
            title = "Dołącz do społeczności",
            description = "Bądź częścią naszej społeczności, dziel się swoimi zainteresowaniami i poznawaj nowych ludzi!"
        )
    )


    var currentPage by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.weight(1f))

        AnimatedContent(targetState = currentPage, label = "Page") {
            IntroductionContent(title = pages[it].title, description = pages[it].description)
        }


        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
        ) {
            if (currentPage > 0) {
                FilledTonalButton(onClick = {
                    if (currentPage > 0) {
                        currentPage--
                    }
                }, modifier = Modifier.width(90.dp)) {
                    Text(text = "Back", color = MaterialTheme.colorScheme.onBackground)
                }
            } else {
                Spacer(modifier = Modifier.width(90.dp))
            }

            DashedProgressIndicator(
                progress = currentPage + 1, totalNumberOfBars = pages.size - 1,
                modifier = Modifier.width(80.dp)
            )

            Button(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        currentPage++
                    } else {
                        onFinish()
                    }

                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.width(90.dp)
            ) {
                Text(text = if (currentPage == pages.size - 1) "Finish" else "Next")
            }
        }

        TextButton(onClick = onFinish) {
            Text(
                text = "You already have an account? Log in",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(.5F)
            )
        }
    }

}

@Composable
fun IntroductionContent(title: String, description: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 100.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(.5F)
        )
    }
}
