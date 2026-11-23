package org.ambatujudge.legaloo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.ambatujudge.legaloo.data.SessionStore
import org.ambatujudge.legaloo.navigation.AppNavGraph
import org.ambatujudge.legaloo.navigation.Routes
import org.ambatujudge.legaloo.ui.components.LegalooBottomBar
import org.ambatujudge.legaloo.ui.theme.LegalooTheme
import org.ambatujudge.legaloo.viewmodel.CourseViewModel
import org.ambatujudge.legaloo.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionStore.init(this)
        enableEdgeToEdge()
        setContent { LegalooTheme { LegalooApp() } }
    }
}

@Composable
fun LegalooApp() {
    val navController = rememberNavController()
    val courseViewModel: CourseViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val startDestination = remember { if (SessionStore.isLoggedIn) Routes.HOME else Routes.WELCOME }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Any 401 clears the token; send the user back to the login screen.
    LaunchedEffect(navController) {
        SessionStore.expired.collect {
            navController.navigate(Routes.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background, bottomBar = {
        if (currentRoute != null && currentRoute !in Routes.AUTH_ROUTES) {
            LegalooBottomBar(currentRoute = currentRoute, onItemClick = { route ->
                navController.navigate(route) {
                    popUpTo(Routes.HOME)
                    launchSingleTop = true
                }
            })
        }
    }) { innerPadding ->
        AppNavGraph(
            navController = navController,
            courseViewModel = courseViewModel,
            homeViewModel = homeViewModel,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
        )
    }
}
