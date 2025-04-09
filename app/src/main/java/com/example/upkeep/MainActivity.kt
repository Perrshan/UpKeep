package com.example.upkeep

import DailyNotificationWorker
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.upkeep.ui.theme.UpKeepTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.upkeep.ui.theme.LoginScreen
import com.example.upkeep.ui.theme.SignUpScreen
import com.example.upkeep.ui.theme.TaskListScreen
import com.example.upkeep.ui.theme.LoginScreenViewModel
import com.example.upkeep.ui.theme.TaskListScreenViewModel
import com.example.upkeep.viewmodel.SignUpViewModel
import com.example.upkeep.data.TaskHistoryDatabase
import com.example.upkeep.ui.theme.ProfileScreen
import com.example.upkeep.ui.theme.TaskHistoryScreen
import com.example.upkeep.ui.theme.TimerScreen
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    fun scheduleDailyNotification(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<DailyNotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "dailyNotification",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun calculateInitialDelay(hour: Int = 9, minute: Int = 0): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    private val viewModel: LoginScreenViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        scheduleDailyNotification(this)

        setContent {
            UpKeepTheme {
                UpKeepApp(viewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpKeepApp(viewModel: LoginScreenViewModel) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route


    val dbHelper = TaskHistoryDatabase(context = LocalContext.current)

    Scaffold(
        topBar = { UpKeepTopBar() },
        bottomBar = {

            if (currentRoute in listOf("taskList", "timer", "completedTasks", "profile")) {
                UpKeepBottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    navController = navController,
                    viewModel = viewModel,
                    onLoginSuccess = { navController.navigate("taskList") }
                )
            }

            composable("signUp") {
                val signUpViewModel: SignUpViewModel = viewModel()
                SignUpScreen(
                    navController = navController,
                    viewModel = signUpViewModel
                )
            }

            composable("taskList") {
                val taskListViewModel: TaskListScreenViewModel = viewModel()
                TaskListScreen(viewModel = taskListViewModel)
            }

            composable("completedTasks") {
                val taskHistories = dbHelper.getAllTaskHistories()
                TaskHistoryScreen(taskHistories = taskHistories)
            }

            composable("profile") {
                ProfileScreen(
                    viewModel = viewModel,
                    navController
                )
            }

            composable("timer") {
                TimerScreen(navController = navController)
            }


        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object TaskList : Screen("taskList", "Tasks", Icons.Filled.Menu)
    object Timer : Screen("timer", "Timer", Icons.Filled.Timer)
    object CompletedTasks : Screen("completedTasks", "Completed Tasks", Icons.Filled.DoneAll)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpKeepTopBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.up_keep_logo),
                    contentDescription = stringResource(R.string.up_keep_logo),
                    modifier = modifier.size(100.dp)
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayMedium
                )
            }
        },
        modifier = modifier.padding(16.dp)
    )
}

@Composable
fun UpKeepBottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.TaskList,
        Screen.Timer,
        Screen.CompletedTasks,
        Screen.Profile
    )

    NavigationBar (containerColor = MaterialTheme.colorScheme.primary) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title, textAlign = TextAlign.Center) },
                selected = currentRoute == screen.route,
                onClick = { navController.navigate(screen.route) }
            )
        }
    }
}