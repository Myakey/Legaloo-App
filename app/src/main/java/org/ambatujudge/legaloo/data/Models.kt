package org.ambatujudge.legaloo.data

enum class LevelState { COMPLETED, ACTIVE, LOCKED }
enum class LevelType { LESSON, PRACTICE }

data class LevelNode(val id: Int, val type: LevelType, val state: LevelState)

data class Course(val id: Int, val number: Int, val title: String, val progress: Float, val unitLabel: String, val isCurrent: Boolean, val levels: List<LevelNode>)

data class StudyDay(val label: String, val completed: Boolean)
data class UserProgress(val userName: String, val streakCount: Int, val minutesStudied: Int, val dailyGoalMinutes: Int, val week: List<StudyDay>, val currentCourseId: Int, val currentUnitNumber: Int)
