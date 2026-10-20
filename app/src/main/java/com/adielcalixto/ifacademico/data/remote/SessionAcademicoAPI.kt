package com.adielcalixto.ifacademico.data.remote

import com.adielcalixto.ifacademico.data.remote.dto.*
import com.adielcalixto.ifacademico.data.session.SessionCoordinator
import com.adielcalixto.ifacademico.data.session.SessionTicket
import java.time.LocalDate
import java.time.LocalDateTime

/** All protected endpoints share one gate, one recovery and one retry budget. */
class SessionAcademicoAPI(private val raw: AcademicoAPI, private val session: SessionCoordinator) :
    AcademicoAPI {
    override suspend fun login(data: LoginDTO) = raw.login(data)
    override suspend fun getStudent(ticket: SessionTicket?) =
        session.protectedRequest { raw.getStudent(it) }

    override suspend fun getDiaries(
        periodNumber: Int,
        periodYear: Int,
        registrationId: Int,
        dateTime: LocalDateTime?,
        ticket: SessionTicket?
    ) =
        session.protectedRequest {
            raw.getDiaries(
                periodNumber,
                periodYear,
                registrationId,
                dateTime,
                it
            )
        }

    override suspend fun getIndividualTimeTable(
        periodYear: Int,
        priodNumber: Int,
        ticket: SessionTicket?
    ) =
        session.protectedRequest { raw.getIndividualTimeTable(periodYear, priodNumber, it) }

    override suspend fun getExams(diaryId: Int, registrationId: Int, ticket: SessionTicket?) =
        session.protectedRequest { raw.getExams(diaryId, registrationId, it) }

    override suspend fun getSpecialDates(
        startDate: LocalDate,
        endDate: LocalDate,
        ticket: SessionTicket?
    ) =
        session.protectedRequest { raw.getSpecialDates(startDate, endDate, it) }

    override suspend fun getPerformanceGraph(ticket: SessionTicket?) =
        session.protectedRequest { raw.getPerformanceGraph(it) }

    override suspend fun getTimeTable(ticket: SessionTicket?) =
        session.protectedRequest { raw.getTimeTable(it) }

    override suspend fun getPeriods(ticket: SessionTicket?) =
        session.protectedRequest { raw.getPeriods(it) }
}
