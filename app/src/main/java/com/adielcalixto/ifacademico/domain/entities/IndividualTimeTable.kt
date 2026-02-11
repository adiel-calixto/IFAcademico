package com.adielcalixto.ifacademico.domain.entities

data class IndividualTimeTable (
    //Map<String -> Start Time, Map<Int -> Week Day, TimeTableCLass>>, good to make Grid TimeTable
    val classes: Map<String, Map<Int, TimeTableClass>>,
    val weekDay: Int
)