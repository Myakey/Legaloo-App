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
import org.ambatujudge.legaloo.ui.screens.LessonScreen
import org.ambatujudge.legaloo.ui.screens.LoginScreen
import org.ambatujudge.legaloo.ui.screens.PlaceholderScreen
import org.ambatujudge.legaloo.ui.screens.ProfileScreen
import org.ambatujudge.legaloo.ui.screens.RegisterScreen
import org.ambatujudge.legaloo.ui.screens.ReminderScreen
import org.ambatujudge.legaloo.ui.screens.WelcomeScreen
import org.ambatujudge.legaloo.viewmodel.CourseViewModel
import org.ambatujudge.legaloo.viewmodel.HomeViewModel

@Composable
fun AppNavGraph(navController: NavHostController, courseViewModel: CourseViewModel, homeViewModel: HomeViewModel, startDestination: String, modifier: Modifier = Modifier) {
    val enterApp = { _: String ->
        navController.navigate(Routes.HOME) { popUpTo(navController.graph.id) { inclusive = true } }
    }
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(Routes.WELCOME) { WelcomeScreen(onStartClick = { navController.navigate(Routes.REGISTER) }, onLoginClick = { navController.navigate(Routes.LOGIN) }) }
        composable(Routes.REGISTER) { RegisterScreen(viewModel = viewModel(), onRegistered = enterApp, onLoginClick = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.REGISTER) { inclusive = true } } }) }
        composable(Routes.LOGIN) { LoginScreen(viewModel = viewModel(), onLoggedIn = enterApp, onRegisterClick = { navController.navigate(Routes.REGISTER) { popUpTo(Routes.LOGIN) { inclusive = true } } }) }
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onContinueClick = { id -> navController.navigate(Routes.courseContent(id)) },
                onReminderClick = { navController.navigate(Routes.REMINDER) },
            )
        }
        composable(Routes.CHAT) { PlaceholderScreen("Tanya Legaloo") }
        composable(Routes.PROFILE) {
            ProfileScreen(onLogout = {
                navController.navigate(Routes.WELCOME) { popUpTo(navController.graph.id) { inclusive = true } }
            })
        }
        composable(Routes.COURSES) { CourseListScreen(viewModel = courseViewModel, onCourseClick = { id -> navController.navigate(Routes.courseContent(id)) }) }
        composable(route = Routes.COURSE_CONTENT, arguments = listOf(navArgument(Routes.COURSE_ID_ARG) { type = NavType.IntType })) { back ->
            val id = back.arguments?.getInt(Routes.COURSE_ID_ARG) ?: -1
            CourseContentScreen(
                courseId = id,
                viewModel = courseViewModel,
                onStartLesson = { levelId -> navController.navigate(Routes.lesson(id, levelId)) },
            )
        }
        composable(
            route = Routes.LESSON,
            arguments = listOf(
                navArgument(Routes.COURSE_ID_ARG) { type = NavType.IntType },
                navArgument(Routes.LEVEL_ID_ARG) { type = NavType.IntType },
            ),
        ) {
            LessonScreen(
                viewModel = viewModel(),
                onClose = { navController.popBackStack() },
                onFinish = { navController.popBackStack() },
            )
        }
        composable(Routes.REMINDER) {
            ReminderScreen(onBack = { navController.popBackStack() })
        }
    }
}
