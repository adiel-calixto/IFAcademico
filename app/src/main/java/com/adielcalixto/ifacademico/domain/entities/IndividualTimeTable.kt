package com.adielcalixto.ifacademico.domain.entities

data class IndividualTimeTable (
    val classes: Map<Int, List<TimeTableClass>>,
    val weekDay: Int
) {
    val times: List<String>
        get() {
            return classes.values
                .flatten()
                .map { it.startTime }
                .distinct()
                .sorted()
        }
}