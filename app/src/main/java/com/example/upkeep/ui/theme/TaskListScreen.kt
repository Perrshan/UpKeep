package com.example.upkeep.ui.theme

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.upkeep.R
import com.example.upkeep.data.TasksDatasource
import com.example.upkeep.task.Task
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.upkeep.data.TaskHistoryDatabase
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskListScreen(viewModel: TaskListScreenViewModel) {

    val dbHelper = TaskHistoryDatabase(context = LocalContext.current)

    val viewModel: TaskListScreenViewModel = viewModel()


    val selectedOption = viewModel.selectedOption.value

    var filteredTasks = TasksDatasource().loadTasks()

    if (selectedOption == stringResource(R.string.daily)) {

        filteredTasks = filteredTasks.filter { task ->
            task.frequency == R.string.daily
        }
    }

    if (selectedOption == stringResource(R.string.weekly)) {

        filteredTasks = filteredTasks.filter { task ->
            task.frequency == R.string.weekly
        }
    }

    if (selectedOption == stringResource(R.string.monthly)) {

        filteredTasks = filteredTasks.filter { task ->
            task.frequency == R.string.monthly
        }
    }

    val TAG = "FilterDebug"

    filteredTasks = filteredTasks.filter { task ->
        val taskName = LocalContext.current.getString(task.name)
        val firstRow = dbHelper.getFirstRow(taskName).firstOrNull()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val taskDate = firstRow?.timestamp?.let {
            LocalDateTime.parse(it, formatter).toLocalDate()
        }

        val todaysDate = LocalDate.now()
        val nextCompletionDate = firstRow?.taskFrequency?.let { taskDate?.plusDays(it.toLong()) }


        Log.d(TAG, "Task: $taskName | Timestamp: ${firstRow?.timestamp} | DateOnly: $taskDate | Today: $todaysDate | nextCompletionDate $nextCompletionDate")
        Log.d(TAG, "Raw timestamp: ${firstRow?.timestamp} | Parsed: $taskDate | Now: ${LocalDate.now()}")


        nextCompletionDate?.let {
            !todaysDate.isBefore(it)
        } ?: true
    }

    if (filteredTasks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "You have completed all tasks, great work!\nCome back tomorrow!",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                style = TextStyle(
                    lineHeight = 40.sp
                )
            )
        }
    } else {

        Column {

            FilterMenu(viewModel = viewModel)



            TaskList(filteredTasks, modifier = Modifier, dbHelper)
        }
    }
}

@Composable
fun TaskList(
    taskList: List<Task>,
    modifier: Modifier = Modifier,
    dbHelper: TaskHistoryDatabase
) {
    LazyColumn(modifier = modifier) {
        items(taskList, key = { task -> task.name }) { task ->
            TaskCard(
                task = task,
                modifier = Modifier.padding(8.dp),
                dbHelper
            )
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    modifier: Modifier = Modifier,
    dbHelper: TaskHistoryDatabase
) {
    Card(modifier = modifier) {
        Column {
            Image(
                painter = painterResource(task.imageResourceId),
                contentDescription = LocalContext.current.getString(task.name),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp),
                contentScale = ContentScale.Crop
            )
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = LocalContext.current.getString(task.name),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = LocalContext.current.getString(task.description),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = "Calendar Icon",
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = LocalContext.current.getString(task.frequency),
                    modifier = Modifier.padding(8.dp).weight(2f),
                    style = MaterialTheme.typography.bodySmall
                )
                var frequencyInt: Int = 0

                when (LocalContext.current.getString(task.frequency)) {
                    "Daily" -> frequencyInt = 1
                    "Weekly" -> frequencyInt = 7
                    "Monthly" -> frequencyInt = 31
                }

                TaskCompletionButton(
                    modifier = Modifier,
                    dbHelper = dbHelper,
                    name = LocalContext.current.getString(task.name),
                    frequency = frequencyInt
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterMenu(
    modifier: Modifier = Modifier,
    viewModel: TaskListScreenViewModel
) {
    val expanded = remember { mutableStateOf(false) }
    val options = listOf(
        stringResource(R.string.all),
        stringResource(R.string.daily),
        stringResource(R.string.weekly),
        stringResource(R.string.monthly)
    )


    val selectedOption = viewModel.selectedOption.value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            TextField(
                value = selectedOption,
                onValueChange = { },
                readOnly = true,
                label = { Text("Filter", fontSize = 12.sp) },
                textStyle = TextStyle(fontSize = 12.sp),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                },
                modifier = Modifier
                    .menuAnchor()
                    .width(200.dp)
                    .height(48.dp)
                    .clickable { expanded.value = !expanded.value }
            )

            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 12.sp) },
                        onClick = {
                            viewModel.updateSelectedOption(option)
                            expanded.value = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCompletionButton(
    modifier: Modifier = Modifier,
    dbHelper: TaskHistoryDatabase,
    name: String,
    frequency: Int
) {
    val buttonText = remember { mutableStateOf("Task Completed") }
    val isCompleted = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            val formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val currentTime = LocalDateTime.now().format(formatterDateTime)
            if (!isCompleted.value) {
                coroutineScope.launch {
                    dbHelper.insertTaskHistory(name, frequency, currentTime)
                    buttonText.value = "Good job! Go get a treat!"
                    isCompleted.value = true
                    delay(1000)
                }
            }
        },
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = buttonText.value,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 200)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 200))
            },
            label = "buttonTextAnimation"
        ) { targetText ->
            Text(text = targetText)
        }
    }
}