package org.ambatujudge.legaloo

import org.ambatujudge.legaloo.data.Course
import org.ambatujudge.legaloo.data.LessonDto
import org.ambatujudge.legaloo.data.LevelState
import org.ambatujudge.legaloo.data.LevelType
import org.ambatujudge.legaloo.data.StepContent
import org.ambatujudge.legaloo.data.UserProgress
import org.ambatujudge.legaloo.data.remote.ApiClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Samples copied from the live backend responses. */
class ApiParsingTest {
    private val json = ApiClient.json

    @Test fun parsesCourses() {
        val body = """[{"id":1,"number":1,"title":"Hak dan Kewajiban","progress":0.4,"unitLabel":"KURSUS 1, UNIT 2","isCurrent":true,
            "levels":[{"id":1,"type":"LESSON","state":"COMPLETED","title":"Apa Itu Hak?"},{"id":5,"type":"PRACTICE","state":"LOCKED","title":"Latihan"}]}]"""
        val course = json.decodeFromString<List<Course>>(body).single()
        assertEquals(0.4f, course.progress)
        assertEquals(LevelState.COMPLETED, course.levels[0].state)
        assertEquals(LevelType.PRACTICE, course.levels[1].type)
    }

    @Test fun parsesProgressIgnoringUnknownKeys() {
        val body = """{"userName":"Budi Santoso","streakCount":7,"minutesStudied":6,"dailyGoalMinutes":10,"extra":1,
            "week":[{"label":"S","completed":true}],"currentCourseId":1,"currentUnitNumber":2}"""
        val p = json.decodeFromString<UserProgress>(body)
        assertEquals(7, p.streakCount)
        assertTrue(p.week[0].completed)
    }

    @Test fun parsesLessonStepsByKind() {
        val body = """{"courseId":4,"levelId":1,"type":"LESSON","title":"Rambu","steps":[
            {"position":2,"kind":"EXAMPLES","modeLabel":"MODE BACA","title":"Menyeberang","mascotMessage":"m",
             "content":{"examples":[{"tone":"GOOD","label":"CONTOH BAIK","caption":"c","illustration":"CROSSING_GREEN"},{"tone":"BAD","label":"B","caption":"d"}]}},
            {"position":1,"kind":"READ","modeLabel":"MODE BACA","title":"Rambu","mascotMessage":"Ayo","content":{"paragraphs":["a","b"]}},
            {"position":4,"kind":"VIDEO","modeLabel":"X","title":null,"mascotMessage":"","content":{}},
            {"position":3,"kind":"QUIZ","modeLabel":"MODE KUIS","title":null,"mascotMessage":"Pilih",
             "content":{"options":[{"text":"x","correct":false},{"text":"y","correct":true}],"question":"q","feedbackWrong":"w","feedbackCorrect":"c"}}]}"""
        val lesson = json.decodeFromString<LessonDto>(body).toLesson()
        assertEquals(3, lesson.steps.size) // unknown kind dropped
        assertEquals(listOf("a", "b"), (lesson.steps[0].content as StepContent.Read).paragraphs)
        val examples = (lesson.steps[1].content as StepContent.Examples).examples
        assertNull(examples[1].illustration)
        val quiz = lesson.steps[2].content as StepContent.Quiz
        assertNull(lesson.steps[2].title)
        assertTrue(quiz.options[1].correct)
    }
}
