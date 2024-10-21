package com.example.socialmeetingapp.presentation.event.createevent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDateScreen() {

    val viewModel = hiltViewModel<CreateEventViewModel>()
    val eventData by viewModel.eventData.collectAsStateWithLifecycle()

    var isStartDatePickerVisible by rememberSaveable { mutableStateOf(false) }
    var isEndDatePickerVisible by rememberSaveable { mutableStateOf(false) }
    var isStartTimePickerVisible by rememberSaveable { mutableStateOf(false) }
    var isEndTimePickerVisible by rememberSaveable { mutableStateOf(false) }

    Column {
        Text(
            text = "When will this event take place?",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
        )


        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text(
                text = "From",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            OutlinedButton(
                onClick = { isStartDatePickerVisible = true },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = String.format(
                        Locale.ROOT,
                        "%02d.%02d.%d",
                        eventData.startTime.dayOfMonth,
                        eventData.startTime.monthNumber,
                        eventData.startTime.year
                    )
                )
            }

            OutlinedButton(
                onClick = { isStartTimePickerVisible = true },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = String.format(
                        Locale.ROOT,
                        "%02d:%02d",
                        eventData.startTime.hour,
                        eventData.startTime.minute
                    )
                )
            }
        }

        Column {
            Text(
                text = "Till",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            OutlinedButton(
                onClick = { isEndDatePickerVisible = true },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = String.format(
                        Locale.ROOT,
                        "%02d.%02d.%d",
                        eventData.endTime.dayOfMonth,
                        eventData.endTime.monthNumber,
                        eventData.endTime.year
                    )
                )
            }

            OutlinedButton(
                onClick = { isEndTimePickerVisible = true },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = String.format(
                        Locale.ROOT,
                        "%02d:%02d",
                        eventData.endTime.hour,
                        eventData.endTime.minute
                    )
                )
            }
        }

        Text(
            text = when {
                eventData.startTime.date == eventData.endTime.date && eventData.startTime.time > eventData.endTime.time -> "End time must be after start time"
                eventData.startTime.date == eventData.endTime.date && eventData.startTime.time == eventData.endTime.time -> "Event duration must be at least 1 minute"
                else -> ""
            },
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

    }

    if (isStartDatePickerVisible) {
        DatePickerModalInput(
            initialDate = eventData.startTime,
            onDateSelected = {
                if (it != null) {
                    viewModel.setStartTime(it)
                    viewModel.setEndTime(it)
                }
            },
            onDismiss = { isStartDatePickerVisible = false })
    }

    if (isEndDatePickerVisible) {
        DatePickerModalInput(
            initialDate = eventData.endTime,
            onDateSelected = { if (it != null) viewModel.setEndTime(it) },
            onDismiss = { isEndDatePickerVisible = false },
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val startTimeMillis =
                        eventData.startTime.toInstant(TimeZone.currentSystemDefault())
                            .toEpochMilliseconds()
                    val endTimeMillis = eventData.startTime
                        .toInstant(TimeZone.currentSystemDefault())
                        .plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                        .toEpochMilliseconds()

                    return utcTimeMillis in startTimeMillis..endTimeMillis
                }
            }
        )
    }



    if (isStartTimePickerVisible) {
        Dial(
            onTimeSelected = {
                viewModel.setStartTime(eventData.startTime.date.atTime(it))
            },
            onDismiss = {
                isStartTimePickerVisible = false
            }
        )
    }

    if (isEndTimePickerVisible) {
        Dial(
            onTimeSelected = {
                viewModel.setEndTime(eventData.endTime.date.atTime(it))
            },
            onDismiss = {
                isEndTimePickerVisible = false
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    initialDate: LocalDateTime,
    onDateSelected: (LocalDateTime?) -> Unit,
    onDismiss: () -> Unit,
    selectableDates: SelectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return Clock.System.now().minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                .toEpochMilliseconds() < utcTimeMillis
        }
    }
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds(),
        selectableDates = selectableDates

    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis?.let {
                    Instant.fromEpochMilliseconds(it)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                })
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dial(
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time.hour,
        initialMinute = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).time.minute,
        is24Hour = true,
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TimePicker(state = timePickerState)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Button(onClick = {
                    onTimeSelected(
                        LocalTime(
                            hour = timePickerState.hour,
                            minute = timePickerState.minute
                        )
                    )
                    onDismiss()
                }) {
                    Text("OK")
                }
            }

        }
    }
}
