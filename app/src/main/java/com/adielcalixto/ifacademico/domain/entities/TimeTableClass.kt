package com.adielcalixto.ifacademico.domain.entities

data class TimeTableClass (
    val id: Int,
    val className: String,
    val professorName: String,
    val academicPeriod: UByte,
    val startTime: String,
    val endTime: String,
    val classRoomName: String,
    val finished: Boolean,
    val weekDay: Int?
) {
    val acronym: String
        get() {
            val eachWord: List<String> = className.split(" ", "-")
            val saveCourseNumber = if(listOf("I", "II", "III", "IV", "V").indexOf(eachWord.last()) != -1) eachWord.last() else ""

            val removePreposition = eachWord.filter { s ->  s.count() > 3}
            val justFirstLetter: List<Char> = removePreposition.map { it.get(0) }

            return justFirstLetter.joinToString("") { it.uppercase() } + saveCourseNumber
        }
}