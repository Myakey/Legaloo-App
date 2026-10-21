package org.ambatujudge.legaloo.data

object DummyData {
    val courses: List<Course> = listOf(
        Course(1, 1, "Hak dan Kewajiban", 0.6f, "KURSUS 2/G A, UNIT 2", false, listOf(
            LevelNode(1, LevelType.LESSON, LevelState.COMPLETED),
            LevelNode(2, LevelType.LESSON, LevelState.COMPLETED),
            LevelNode(3, LevelType.LESSON, LevelState.ACTIVE),
            LevelNode(4, LevelType.LESSON, LevelState.LOCKED),
            LevelNode(5, LevelType.PRACTICE, LevelState.LOCKED)
        )),
        Course(2, 2, "Aturan di Sekolah", 0.3f, "KURSUS 2, UNIT 1", true, listOf(
            LevelNode(1, LevelType.LESSON, LevelState.COMPLETED),
            LevelNode(2, LevelType.LESSON, LevelState.ACTIVE),
            LevelNode(3, LevelType.LESSON, LevelState.LOCKED),
            LevelNode(4, LevelType.LESSON, LevelState.LOCKED),
            LevelNode(5, LevelType.PRACTICE, LevelState.LOCKED)
        )),
        Course(3, 3, "Keluarga dan Hukum", 0f, "KURSUS 3, UNIT 1", false, listOf(
            LevelNode(1, LevelType.LESSON, LevelState.ACTIVE),
            LevelNode(2, LevelType.LESSON, LevelState.LOCKED),
            LevelNode(3, LevelType.LESSON, LevelState.LOCKED),
            LevelNode(4, LevelType.LESSON, LevelState.LOCKED),
            LevelNode(5, LevelType.PRACTICE, LevelState.LOCKED)
        )),
        Course(4, 4, "Aturan Lalu Lintas", 0f, "KURSUS 4, UNIT 1", false, listOf(
            LevelNode(1, LevelType.LESSON, LevelState.ACTIVE),
            LevelNode(2, LevelType.LESSON, LevelState.LOCKED),
            LevelNode(3, LevelType.LESSON, LevelState.LOCKED),
            LevelNode(4, LevelType.PRACTICE, LevelState.LOCKED)
        ))
    )
    val userProgress = UserProgress(
        userName = "Nama", streakCount = 7, minutesStudied = 6, dailyGoalMinutes = 10,
        week = listOf(StudyDay("S",true),StudyDay("S",true),StudyDay("R",true),StudyDay("K",true),StudyDay("J",true),StudyDay("S",false),StudyDay("M",false)),
        currentCourseId = 1, currentUnitNumber = 2
    )
    val accounts: List<UserAccount> = listOf(
        UserAccount("Budi Santoso", "budi@legaloo.id", "legaloo123"),
        UserAccount("Siti Rahmawati", "siti.rahma@gmail.com", "hukum2026")
    )
}
