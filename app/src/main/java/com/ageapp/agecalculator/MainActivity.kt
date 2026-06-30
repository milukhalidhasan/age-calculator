package com.ageapp.agecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ageapp.agecalculator.ui.theme.AgeCalculatorTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgeCalculatorTheme {
                AgeCalculatorApp()
            }
        }
    }
}

private val DISPLAY_FORMAT: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.getDefault())

private val PARSE_FORMATS = listOf(
    DateTimeFormatter.ofPattern("d/M/uuuu"),
    DateTimeFormatter.ofPattern("d-M-uuuu"),
    DateTimeFormatter.ofPattern("d MMM uuuu", Locale.getDefault()),
    DateTimeFormatter.ofPattern("uuuu-M-d")
)

private fun parseDate(text: String): LocalDate? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    for (fmt in PARSE_FORMATS) {
        try {
            return LocalDate.parse(trimmed, fmt)
        } catch (_: Exception) {
            // try next
        }
    }
    return null
}

@Composable
fun AgeCalculatorApp() {
    val today = remember { LocalDate.now() }

    var birthText by remember { mutableStateOf("") }
    var toText by remember { mutableStateOf(today.format(DISPLAY_FORMAT)) }
    var result by remember { mutableStateOf<AgeResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { AppTopBar() },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InputCard(
                birthText = birthText,
                onBirthChange = { birthText = it; error = null },
                toText = toText,
                onToChange = { toText = it; error = null },
                onCalculate = {
                    val birth = parseDate(birthText)
                    val to = parseDate(toText)
                    when {
                        birth == null -> {
                            error = "Enter a valid date of birth (e.g. 05/03/1990)."
                            result = null
                        }
                        to == null -> {
                            error = "Enter a valid target date."
                            result = null
                        }
                        birth.isAfter(to) -> {
                            error = "Date of birth must be on or before the target date."
                            result = null
                        }
                        else -> {
                            error = null
                            result = AgeCalculator.calculate(birth, to)
                        }
                    }
                }
            )

            error?.let { ErrorBanner(it) }

            result?.let { ResultSection(it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Age Calculator",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
private fun InputCard(
    birthText: String,
    onBirthChange: (String) -> Unit,
    toText: String,
    onToChange: (String) -> Unit,
    onCalculate: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DateInputField(
                label = "Date of Birth",
                value = birthText,
                onValueChange = onBirthChange
            )
            DateInputField(
                label = "Age at the Date",
                value = toText,
                onValueChange = onToChange
            )
            Button(
                onClick = onCalculate,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "Calculate Age",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text("DD/MM/YYYY") },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = "Pick a date for $label",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    if (showPicker) {
        val initialMillis = (parseDate(value) ?: LocalDate.now())
            .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val picked = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC).toLocalDate()
                        onValueChange(picked.format(DISPLAY_FORMAT))
                    }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun ResultSection(result: AgeResult) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PrimaryAgeCard(result)
        NextBirthdayCard(result)
        DetailsGrid(result)
    }
}

@Composable
private fun PrimaryAgeCard(result: AgeResult) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "YOUR AGE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AgeUnit(result.years, "years")
                AgeUnit(result.months, "months")
                AgeUnit(result.days, "days")
            }
        }
    }
}

@Composable
private fun AgeUnit(value: Int, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun NextBirthdayCard(result: AgeResult) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Cake,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = if (result.isBirthdayToday) "Happy Birthday! 🎉" else "Next Birthday",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.height(4.dp))
                val sub = if (result.isBirthdayToday) {
                    "Today you turn ${result.turningAge}!"
                } else {
                    "${result.daysUntilNextBirthday} days to go — turning " +
                        "${result.turningAge} on ${result.nextBirthday.format(DISPLAY_FORMAT)} " +
                        "(${result.nextBirthdayDayOfWeek})"
                }
                Text(
                    text = sub,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun DetailsGrid(result: AgeResult) {
    val items = listOf(
        "Born on" to result.bornDayOfWeek,
        "Zodiac sign" to result.zodiac,
        "Total months" to formatNumber(result.totalMonths),
        "Total weeks" to formatNumber(result.totalWeeks),
        "Total days" to formatNumber(result.totalDays),
        "Total hours" to formatNumber(result.totalHours),
        "Total minutes" to formatNumber(result.totalMinutes),
        "Total seconds" to formatNumber(result.totalSeconds)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { (label, value) ->
                    DetailCard(
                        label = label,
                        value = value,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DetailCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatNumber(n: Long): String {
    return "%,d".format(n)
}
