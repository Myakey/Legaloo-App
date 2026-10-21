package org.umn.utstest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.umn.utstest.ui.screens.CourseContentScreen
import org.umn.utstest.ui.screens.CourseListScreen
import org.umn.utstest.ui.screens.HomeScreen
import org.umn.utstest.ui.screens.PlaceholderScreen
import org.umn.utstest.viewmodel.CourseViewModel
import org.umn.utstest.viewmodel.HomeViewModel

@Composable
fun AppNavGraph(navController: NavHostController, courseViewModel: CourseViewModel, homeViewModel: HomeViewModel, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Routes.HOME, modifier = modifier) {
        composable(Routes.HOME) { HomeScreen(viewModel = homeViewModel, onContinueClick = { id -> navController.navigate(Routes.courseContent(id)) }) }
        composable(Routes.CHAT) { PlaceholderScreen("Tanya Legaloo") }
        composable(Routes.PROFILE) { PlaceholderScreen("Profil") }
        composable(Routes.COURSES) { CourseListScreen(viewModel = courseViewModel, onCourseClick = { id -> navController.navigate(Routes.courseContent(id)) }) }
        composable(route = Routes.COURSE_CONTENT, arguments = listOf(navArgument(Routes.COURSE_ID_ARG) { type = NavType.IntType })) { back ->
            val id = back.arguments?.getInt(Routes.COURSE_ID_ARG) ?: -1
            CourseContentScreen(courseId = id, viewModel = courseViewModel)
        }
    }
}
