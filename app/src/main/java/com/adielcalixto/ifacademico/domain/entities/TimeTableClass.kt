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
            val courseNumber = eachWord.last().takeIf { it.matches(Regex("^(X{0,3})(IX|IV|V?I{0,3})$")) } ?: ""

            val withoutPreposition = eachWord.filter { s ->  s.count() > 3}
            val justFirstLetter: List<Char> = withoutPreposition.map { it[0] }

            return justFirstLetter.joinToString("") { it.uppercase() } + courseNumber
        }
}