package org.ambatujudge.legaloo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.ambatujudge.legaloo.ui.screens.CourseContentScreen
import org.ambatujudge.legaloo.ui.screens.CourseListScreen
import org.ambatujudge.legaloo.ui.screens.HomeScreen
import org.ambatujudge.legaloo.ui.screens.LoginScreen
import org.ambatujudge.legaloo.ui.screens.PlaceholderScreen
import org.ambatujudge.legaloo.ui.screens.RegisterScreen
import org.ambatujudge.legaloo.ui.screens.WelcomeScreen
import org.ambatujudge.legaloo.viewmodel.CourseViewModel
import org.ambatujudge.legaloo.viewmodel.HomeViewModel
import org.ambatujudge.legaloo.ui.screens.CourseMaterial1Screen
import org.ambatujudge.legaloo.ui.screens.ReminderScreen

@Composable
fun AppNavGraph(navController: NavHostController, courseViewModel: CourseViewModel, homeViewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val enterApp = { name: String ->
        homeViewModel.setUserName(name)
        navController.navigate(Routes.HOME) { popUpTo(Routes.WELCOME) { inclusive = true } }
    }
    NavHost(navController = navController, startDestination = Routes.WELCOME, modifier = modifier) {
        composable(Routes.WELCOME) { WelcomeScreen(onStartClick = { navController.navigate(Routes.REGISTER) }, onLoginClick = { navController.navigate(Routes.LOGIN) }) }
        composable(Routes.REGISTER) { RegisterScreen(viewModel = viewModel(), onRegistered = enterApp, onLoginClick = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.WELCOME) } }) }
        composable(Routes.LOGIN) { LoginScreen(viewModel = viewModel(), onLoggedIn = enterApp, onRegisterClick = { navController.navigate(Routes.REGISTER) { popUpTo(Routes.WELCOME) } }) }
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onContinueClick = { id -> navController.navigate(Routes.courseContent(id)) },
                onReminderClick = { navController.navigate(Routes.REMINDER) },
            )
        }
        composable(Routes.CHAT) { PlaceholderScreen("Tanya Legaloo") }
        composable(Routes.PROFILE) { PlaceholderScreen("Profil") }
        composable(Routes.COURSES) { CourseListScreen(viewModel = courseViewModel, onCourseClick = { id -> navController.navigate(Routes.courseContent(id)) }) }
        composable(route = Routes.COURSE_CONTENT, arguments = listOf(navArgument(Routes.COURSE_ID_ARG) { type = NavType.IntType })) { back ->
            val id = back.arguments?.getInt(Routes.COURSE_ID_ARG) ?: -1
            CourseContentScreen(
                courseId = id,
                viewModel = courseViewModel,
                onStartLesson = { navController.navigate(Routes.COURSE_MATERIAL_1) },
            )
        }
        composable(Routes.COURSE_MATERIAL_1) {
            CourseMaterial1Screen(
                onClose = { navController.popBackStack() },
                onFinish = { navController.popBackStack() },
            )
        }
        composable(Routes.REMINDER) {
            ReminderScreen(onBack = { navController.popBackStack() })
        }

    }
}
