package org.umn.utstest.navigation

object Routes {
    const val HOME = "home"
    const val COURSES = "courses"
    const val CHAT = "chat"
    const val PROFILE = "profile"
    const val COURSE_ID_ARG = "courseId"
    const val COURSE_CONTENT = "course_content/{courseId}"
    fun courseContent(courseId: Int) = "course_content/$courseId"
}
