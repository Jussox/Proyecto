package cl.duoc.bitacora2.data.remoto

import cl.duoc.bitacora2.data.remoto.dto.AsignacionDto
import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import cl.duoc.bitacora2.data.remoto.dto.BitacoraEdicionRequest
import cl.duoc.bitacora2.data.remoto.dto.BitacoraRequest
import cl.duoc.bitacora2.data.remoto.dto.ComentarioRequest
import cl.duoc.bitacora2.data.remoto.dto.DocenteDto
import cl.duoc.bitacora2.data.remoto.dto.EstudianteDto
import cl.duoc.bitacora2.data.remoto.dto.InformeMensualResponse
import cl.duoc.bitacora2.data.remoto.dto.LoginRequest
import cl.duoc.bitacora2.data.remoto.dto.LoginResponse
import cl.duoc.bitacora2.data.remoto.dto.ParticipacionResponse
import cl.duoc.bitacora2.data.remoto.dto.RecordatorioDto
import cl.duoc.bitacora2.data.remoto.dto.RecordatorioRequest
import cl.duoc.bitacora2.data.remoto.dto.RegistroDto
import cl.duoc.bitacora2.data.remoto.dto.RegistroRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BitacoraApi {

    // ========== AUTH ==========
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // ========== BITÁCORAS ==========
    @GET("bitacoras/docente/{usuario}")
    suspend fun obtenerBitacorasDocente(
        @Path("usuario") usuario: String
    ): Response<List<BitacoraDto>>

    @GET("bitacoras/estudiante/{usuario}")
    suspend fun obtenerBitacorasEstudiante(
        @Path("usuario") usuario: String
    ): Response<List<BitacoraDto>>

    @GET("bitacoras/{id}")
    suspend fun obtenerBitacoraPorId(
        @Path("id") id: Long
    ): Response<BitacoraDto>

    @POST("bitacoras")
    suspend fun crearBitacora(
        @Body request: BitacoraRequest
    ): Response<BitacoraDto>

    @PUT("bitacoras/{id}/publicar")
    suspend fun publicarBitacora(
        @Path("id") id: Long,
        @Query("docente") docente: String
    ): Response<BitacoraDto>

    @PUT("bitacoras/{id}/cerrar")
    suspend fun cerrarBitacora(
        @Path("id") id: Long,
        @Query("docente") docente: String
    ): Response<BitacoraDto>

    @PUT("bitacoras/{id}")
    suspend fun editarBitacora(
        @Path("id") id: Long,
        @Query("docente") docente: String,
        @Body request: BitacoraEdicionRequest
    ): Response<BitacoraDto>

    @GET("bitacoras/{id}/participacion")
    suspend fun obtenerParticipacion(
        @Path("id") id: Long,
        @Query("docente") docente: String
    ): Response<ParticipacionResponse>

    @GET("bitacoras/{id}/informe")
    suspend fun obtenerInforme(
        @Path("id") id: Long,
        @Query("docente") docente: String
    ): Response<InformeMensualResponse>

    // ========== REGISTROS ==========
    @POST("registros")
    suspend fun crearRegistro(
        @Body request: RegistroRequest
    ): Response<RegistroDto>

    @PUT("registros/{id}")
    suspend fun actualizarRegistro(
        @Path("id") id: Long,
        @Query("estudiante") estudiante: String,
        @Body request: RegistroRequest
    ): Response<RegistroDto>

    @PUT("registros/{id}/enviar")
    suspend fun enviarRegistro(
        @Path("id") id: Long,
        @Query("estudiante") estudiante: String
    ): Response<RegistroDto>

    @DELETE("registros/{id}")
    suspend fun eliminarRegistro(
        @Path("id") id: Long,
        @Query("estudiante") estudiante: String
    ): Response<Void>

    @GET("registros/estudiante/{usuario}")
    suspend fun obtenerRegistrosEstudiante(
        @Path("usuario") usuario: String,
        @Query("bitacoraId") bitacoraId: Long? = null
    ): Response<List<RegistroDto>>

    @GET("registros/bitacora/{bitacoraId}")
    suspend fun obtenerRegistrosBitacora(
        @Path("bitacoraId") bitacoraId: Long,
        @Query("docente") docente: String,
        @Query("estudiante") estudiante: String? = null,
        @Query("fecha") fecha: String? = null,
        @Query("estado") estado: String? = null
    ): Response<List<RegistroDto>>

    @GET("registros/bitacora/{bitacoraId}/pendientes")
    suspend fun obtenerRegistrosPendientes(
        @Path("bitacoraId") bitacoraId: Long,
        @Query("docente") docente: String
    ): Response<List<RegistroDto>>

    @PUT("registros/{id}/comentario")
    suspend fun agregarComentario(
        @Path("id") id: Long,
        @Query("docente") docente: String,
        @Body request: ComentarioRequest
    ): Response<RegistroDto>

    @PUT("registros/{id}/revisar")
    suspend fun marcarRevisado(
        @Path("id") id: Long,
        @Query("docente") docente: String
    ): Response<RegistroDto>

    // ========== RECORDATORIOS ==========
    @GET("recordatorios")
    suspend fun obtenerRecordatorios(
        @Query("docente") docente: String
    ): Response<List<RecordatorioDto>>

    @POST("recordatorios")
    suspend fun crearRecordatorio(
        @Body request: RecordatorioRequest
    ): Response<RecordatorioDto>

    @PUT("recordatorios/{id}/completar")
    suspend fun completarRecordatorio(
        @Path("id") id: Long,
        @Query("docente") docente: String
    ): Response<RecordatorioDto>

    // ========== ASIGNACIONES ==========
    @GET("asignaciones/docente/{usuario}")
    suspend fun obtenerAsignacionesDocente(
        @Path("usuario") usuario: String
    ): Response<List<AsignacionDto>>

    @GET("asignaciones/estudiante/{usuario}")
    suspend fun obtenerAsignacionesEstudiante(
        @Path("usuario") usuario: String
    ): Response<List<AsignacionDto>>

    // ========== DOCENTES ==========
    @GET("docentes")
    suspend fun obtenerDocentes(): Response<List<DocenteDto>>

    // ========== ESTUDIANTES ==========
    @GET("estudiantes")
    suspend fun obtenerEstudiantes(
        @Query("curso") curso: String? = null
    ): Response<List<EstudianteDto>>
}
