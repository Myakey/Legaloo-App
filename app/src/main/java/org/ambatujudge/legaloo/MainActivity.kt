package org.ambatujudge.legaloo

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
import org.ambatujudge.legaloo.navigation.AppNavGraph
import org.ambatujudge.legaloo.ui.components.LegalooBottomBar
import org.ambatujudge.legaloo.ui.theme.LegalooTheme
import org.ambatujudge.legaloo.viewmodel.CourseViewModel
import org.ambatujudge.legaloo.viewmodel.HomeViewModel

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
