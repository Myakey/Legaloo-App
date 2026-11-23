package org.ambatujudge.legaloo.navigation

object Routes {
    const val WELCOME = "welcome"
    const val REGISTER = "register"
    const val LOGIN = "login"
    const val HOME = "home"
    const val COURSES = "courses"
    const val CHAT = "chat"
    const val PROFILE = "profile"
    const val COURSE_ID_ARG = "courseId"
    const val LEVEL_ID_ARG = "levelId"
    const val COURSE_CONTENT = "course_content/{courseId}"
    const val LESSON = "lesson/{courseId}/{levelId}"
    val AUTH_ROUTES = setOf(WELCOME, REGISTER, LOGIN)
    fun courseContent(courseId: Int) = "course_content/$courseId"
    fun lesson(courseId: Int, levelId: Int) = "lesson/$courseId/$levelId"

    const val REMINDER = "reminder"
}
