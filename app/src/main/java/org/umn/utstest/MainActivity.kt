package org.umn.utstest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import org.umn.utstest.navigation.AppNavGraph
import org.umn.utstest.ui.components.LegalooBottomBar
import org.umn.utstest.ui.theme.LegalooTheme
import org.umn.utstest.viewmodel.CourseViewModel
import org.umn.utstest.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { LegalooTheme { LegalooApp() } }
    }
}

@Composable
fun LegalooApp() {
    val navController = rememberNavController()
    val courseViewModel: CourseViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    Scaffold(containerColor = MaterialTheme.colorScheme.background, bottomBar = {
        LegalooBottomBar(currentRoute = currentRoute, onItemClick = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        })
    }) { innerPadding ->
        AppNavGraph(navController = navController, courseViewModel = courseViewModel, homeViewModel = homeViewModel, modifier = Modifier.padding(innerPadding))
    }
}
