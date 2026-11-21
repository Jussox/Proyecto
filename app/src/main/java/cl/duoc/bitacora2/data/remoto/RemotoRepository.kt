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
import retrofit2.HttpException
import retrofit2.Response

class RemotoRepository(
    private val api: BitacoraApi = RetrofitClient.api
) {

    private fun <T> comprobarRespuesta(response: Response<T>): T {
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        return response.body() ?: throw IllegalStateException("Cuerpo de respuesta vacío")
    }

    private fun comprobarRespuestaVacia(response: Response<Void>) {
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }

    // ========== AUTH ==========
    suspend fun login(request: LoginRequest): LoginResponse {
        return comprobarRespuesta(api.login(request))
    }

    // ========== BITÁCORAS ==========
    suspend fun obtenerBitacorasDocente(usuario: String): List<BitacoraDto> {
        return comprobarRespuesta(api.obtenerBitacorasDocente(usuario))
    }

    suspend fun obtenerBitacorasEstudiante(usuario: String): List<BitacoraDto> {
        return comprobarRespuesta(api.obtenerBitacorasEstudiante(usuario))
    }

    suspend fun obtenerBitacoraPorId(id: Long): BitacoraDto {
        return comprobarRespuesta(api.obtenerBitacoraPorId(id))
    }

    suspend fun crearBitacora(request: BitacoraRequest): BitacoraDto {
        return comprobarRespuesta(api.crearBitacora(request))
    }

    suspend fun publicarBitacora(id: Long, docente: String): BitacoraDto {
        return comprobarRespuesta(api.publicarBitacora(id, docente))
    }

    suspend fun cerrarBitacora(id: Long, docente: String): BitacoraDto {
        return comprobarRespuesta(api.cerrarBitacora(id, docente))
    }

    suspend fun editarBitacora(id: Long, docente: String, request: BitacoraEdicionRequest): BitacoraDto {
        return comprobarRespuesta(api.editarBitacora(id, docente, request))
    }

    suspend fun obtenerParticipacion(id: Long, docente: String): ParticipacionResponse {
        return comprobarRespuesta(api.obtenerParticipacion(id, docente))
    }

    suspend fun obtenerInforme(id: Long, docente: String): InformeMensualResponse {
        return comprobarRespuesta(api.obtenerInforme(id, docente))
    }

    // ========== REGISTROS ==========
    suspend fun crearRegistro(request: RegistroRequest): RegistroDto {
        return comprobarRespuesta(api.crearRegistro(request))
    }

    suspend fun actualizarRegistro(id: Long, estudiante: String, request: RegistroRequest): RegistroDto {
        return comprobarRespuesta(api.actualizarRegistro(id, estudiante, request))
    }

    suspend fun enviarRegistro(id: Long, estudiante: String): RegistroDto {
        return comprobarRespuesta(api.enviarRegistro(id, estudiante))
    }

    suspend fun eliminarRegistro(id: Long, estudiante: String) {
        comprobarRespuestaVacia(api.eliminarRegistro(id, estudiante))
    }

    suspend fun obtenerRegistrosEstudiante(usuario: String, bitacoraId: Long? = null): List<RegistroDto> {
        return comprobarRespuesta(api.obtenerRegistrosEstudiante(usuario, bitacoraId))
    }

    suspend fun obtenerRegistrosBitacora(
        bitacoraId: Long,
        docente: String,
        estudiante: String? = null,
        fecha: String? = null,
        estado: String? = null
    ): List<RegistroDto> {
        return comprobarRespuesta(api.obtenerRegistrosBitacora(bitacoraId, docente, estudiante, fecha, estado))
    }

    suspend fun obtenerRegistrosPendientes(bitacoraId: Long, docente: String): List<RegistroDto> {
        return comprobarRespuesta(api.obtenerRegistrosPendientes(bitacoraId, docente))
    }

    suspend fun agregarComentario(id: Long, docente: String, request: ComentarioRequest): RegistroDto {
        return comprobarRespuesta(api.agregarComentario(id, docente, request))
    }

    suspend fun marcarRevisado(id: Long, docente: String): RegistroDto {
        return comprobarRespuesta(api.marcarRevisado(id, docente))
    }

    // ========== RECORDATORIOS ==========
    suspend fun obtenerRecordatorios(docente: String): List<RecordatorioDto> {
        return comprobarRespuesta(api.obtenerRecordatorios(docente))
    }

    suspend fun crearRecordatorio(request: RecordatorioRequest): RecordatorioDto {
        return comprobarRespuesta(api.crearRecordatorio(request))
    }

    suspend fun completarRecordatorio(id: Long, docente: String): RecordatorioDto {
        return comprobarRespuesta(api.completarRecordatorio(id, docente))
    }

    // ========== ASIGNACIONES ==========
    suspend fun obtenerAsignacionesDocente(usuario: String): List<AsignacionDto> {
        return comprobarRespuesta(api.obtenerAsignacionesDocente(usuario))
    }

    suspend fun obtenerAsignacionesEstudiante(usuario: String): List<AsignacionDto> {
        return comprobarRespuesta(api.obtenerAsignacionesEstudiante(usuario))
    }

    // ========== DOCENTES ==========
    suspend fun obtenerDocentes(): List<DocenteDto> {
        return comprobarRespuesta(api.obtenerDocentes())
    }

    // ========== ESTUDIANTES ==========
    suspend fun obtenerEstudiantes(curso: String? = null): List<EstudianteDto> {
        return comprobarRespuesta(api.obtenerEstudiantes(curso))
    }
}
