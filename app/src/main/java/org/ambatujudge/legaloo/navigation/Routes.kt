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
    const val COURSE_CONTENT = "course_content/{courseId}"
    val AUTH_ROUTES = setOf(WELCOME, REGISTER, LOGIN)
    fun courseContent(courseId: Int) = "course_content/$courseId"
    const val COURSE_MATERIAL_1 = "course_material_1"

    const val REMINDER = "reminder"
}
