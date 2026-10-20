package com.adielcalixto.ifacademico.data.remote

import com.adielcalixto.ifacademico.data.remote.dto.DiaryDTO
import com.adielcalixto.ifacademico.data.remote.dto.ExamDTO
import com.adielcalixto.ifacademico.data.remote.dto.IndividualTimeTableDTO
import com.adielcalixto.ifacademico.data.remote.dto.LoginDTO
import com.adielcalixto.ifacademico.data.remote.dto.PerformanceGraphDTO
import com.adielcalixto.ifacademico.data.remote.dto.PeriodDTO
import com.adielcalixto.ifacademico.data.remote.dto.SpecialDateDTO
import com.adielcalixto.ifacademico.data.remote.dto.StudentDTO
import com.adielcalixto.ifacademico.data.remote.dto.TimeTableDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Tag
import com.adielcalixto.ifacademico.data.session.SessionTicket
import java.time.LocalDate
import java.time.LocalDateTime

interface AcademicoAPI {
    @POST("autenticacao/signin")
    suspend fun login(@Body data: LoginDTO): Response<ResponseBody>

    companion object {
        const val BASE_URL = "https://novo.qacademico.ifce.edu.br/webapp/api/"
    }

    @GET("autenticacao/usuario-autenticado")
    suspend fun getStudent(@Tag ticket: SessionTicket? = null): Response<StudentDTO>

    @GET("diarios/aluno/diarios?opcao=0")
    suspend fun getDiaries(
        @Query("periodoLetivo") periodNumber: Int,
        @Query("anoLetivo") periodYear: Int,
        @Query("idMatricula") registrationId: Int,
        @Query("ativosNaData") dateTime: LocalDateTime?,
        @Tag ticket: SessionTicket? = null
    ): Response<List<DiaryDTO>>

    @GET("horario-individual/obter-horario")
    suspend fun getIndividualTimeTable(
        @Query("anoLet") periodYear: Int,
        @Query("periodoLet") priodNumber: Int,
        @Tag ticket: SessionTicket? = null
    ): Response<IndividualTimeTableDTO>

    @GET("diarios/aluno/diarios/{id}/avaliacoes")
    suspend fun getExams(
        @Path("id") diaryId: Int,
        @Query("idMatricula") registrationId: Int,
        @Tag ticket: SessionTicket? = null
    ): Response<List<ExamDTO>>

    @GET("calendario-academico/aluno/obter-datas-especiais-semestrais?aulas=false")
    suspend fun getSpecialDates(
        @Query("inicio") startDate: LocalDate,
        @Query("fim") endDate: LocalDate,
        @Tag ticket: SessionTicket? = null
    ): Response<List<SpecialDateDTO>>

    @GET("dashboard/aluno/grafico-rendimento")
    suspend fun getPerformanceGraph(@Tag ticket: SessionTicket? = null): Response<PerformanceGraphDTO>

    @GET("dashboard/comum/horarios")
    suspend fun getTimeTable(@Tag ticket: SessionTicket? = null): Response<TimeTableDTO>

    @GET("diarios/aluno/periodos")
    suspend fun getPeriods(@Tag ticket: SessionTicket? = null): Response<List<PeriodDTO>>
}