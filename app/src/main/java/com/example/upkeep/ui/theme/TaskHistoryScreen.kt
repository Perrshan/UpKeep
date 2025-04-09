package com.example.upkeep.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upkeep.data.TaskHistory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun convertUtcToLocal(utcTimestamp: String): String {
    val instant = Instant.parse(utcTimestamp.replace(" ", "T") + "Z")
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
    return localDateTime.format(formatter)
}

@Composable
fun TaskHistoryScreen(taskHistories: List<TaskHistory>) {
    var tasksCompleted = taskHistories.size

    if (taskHistories.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Tasks Completed. \n Go complete some tasks!",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                style = TextStyle(
                    lineHeight = 40.sp
                )
            )
        }
    } else {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Tasks Completed: $tasksCompleted",
                    fontSize = 20.sp
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(taskHistories) { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Card {
                            Text(
                                text = "${task.taskName} was completed on ${convertUtcToLocal(task.timestamp)}",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center // Add this line
                            )
                        }
                    }
                }
            }
        }
    }
}
