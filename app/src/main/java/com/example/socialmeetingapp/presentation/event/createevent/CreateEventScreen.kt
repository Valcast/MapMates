package com.example.socialmeetingapp.presentation.event.createevent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    latitude: Double,
    longtitude: Double,
    navigateToMap: () -> Unit,
    innerPadding: PaddingValues
) {

    val viewModel = hiltViewModel<EventViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf(LatLng(latitude, longtitude)) }
    var isPrivate by rememberSaveable { mutableStateOf(false) }
    var isOnline by rememberSaveable { mutableStateOf(false) }
    var maxParticipants by rememberSaveable { mutableIntStateOf(3) }


    var date by rememberSaveable { mutableStateOf(Date(System.currentTimeMillis())) }
    var isDatePickerVisible by rememberSaveable { mutableStateOf(false) }

    var time by rememberSaveable { mutableStateOf(Date(System.currentTimeMillis())) }
    var isTimePickerVisible by rememberSaveable { mutableStateOf(false) }

    var duration by rememberSaveable { mutableIntStateOf(1) }

    LaunchedEffect(state) {
        if (state is EventState.Success) {
            navigateToMap()
        }
    }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 40.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Event",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                if (it.length <= 30) {
                    title = it
                }
            },
            label = { Text(text = "Title") },
            singleLine = true,
            placeholder = { Text(text = "Enter event title") },
            trailingIcon = {
                Text(
                    text = "${title.length}/30",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                )
            }
        )
        OutlinedTextField(
            value = description,
            onValueChange = {
                if (it.length <= 100) {
                    description = it
                }
            },
            label = { Text(text = "Description") },
            placeholder = { Text(text = "Enter event description") },
            trailingIcon = {
                Text(
                    text = "${description.length}/100",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                )
            }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        OutlinedTextField(
            value = date.toDateFormattedString(),
            onValueChange = {},
            label = { Text(text = "Date") },
            singleLine = true,
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { isDatePickerVisible = true }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                }
            }
        )

        if (isDatePickerVisible) {
            DatePickerModalInput(
                onDateSelected = {

                },
                onDismiss = { isDatePickerVisible = false }
            )
        }


        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = time.to24HourTimeString(),
                onValueChange = {},
                label = { Text(text = "Time") },
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { isTimePickerVisible = true }) {
                        Icon(imageVector = Icons.Default.ThumbUp, contentDescription = null)
                    }
                },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = if (duration == 0) "" else duration.toString(),
                onValueChange = {
                    duration = if (it.isEmpty()) 0 else it.toInt()
                },
                label = { Text(text = "Duration") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }



        if (isTimePickerVisible) {
            Dial(
                onTimeSelected = { time = it },
                onDismiss = {
                    isTimePickerVisible = false
                }
            )
        }



        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Private")
            Checkbox(
                checked = isPrivate,
                onCheckedChange = { isPrivate = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(text = "Online")
            Checkbox(
                checked = isOnline,
                onCheckedChange = { isOnline = it })

        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Button(onClick = { /*TODO*/ }, shape = RoundedCornerShape(10.dp)) {
                Text(text = "Invite people")
            }

            OutlinedTextField(
                value = if (maxParticipants == 0) "" else maxParticipants.toString(),
                onValueChange = {
                    maxParticipants = if (it.isEmpty()) 0 else it.toInt()
                },
                label = { Text(text = "Max Participants") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Text(
            text = "Invited people",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(.5F)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = Icons.Default.AccountBox, contentDescription = null)
            Icon(imageVector = Icons.Default.AccountBox, contentDescription = null)
            Icon(imageVector = Icons.Default.AccountBox, contentDescription = null)
            Icon(imageVector = Icons.Default.AccountBox, contentDescription = null)
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = { navigateToMap() },
                shape = RoundedCornerShape(10.dp), modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(text = "Cancel")
            }
            Button(
                onClick = {
                    viewModel.createEvent(
                        title = title,
                        description = description,
                        location = location,
                        isPrivate = isPrivate,
                        isOnline = isOnline,
                        maxParticipants = maxParticipants,
                        date = date,
                        time = time,
                        duration = duration
                    )
                },
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(text = "Create Event")

            }
        }


    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (Date?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis?.let { Date(it) })
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
    onTimeSelected: (Date) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
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
                        Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                        }.time
                    )
                    onDismiss()
                }) {
                    Text("OK")
                }
            }

        }
    }
}

fun Date.toDateFormattedString(): String {
    val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
    return formatter.format(this)
}

fun Date.to24HourTimeString(): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    return formatter.format(this)
}