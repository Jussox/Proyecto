package cl.duoc.bitacora2.data

import kotlinx.coroutines.flow.Flow

class BitacoraRepository(
    private val dao: BitacoraDao
) {

    suspend fun guardarBitacora(bitacora: Bitacora) {
        dao.insertarBitacora(bitacora)
    }

    fun obtenerBitacoras(): Flow<List<Bitacora>> {
        return dao.obtenerBitacoras()
    }

    suspend fun publicarBitacora(id: Int, fecha: String) {
        dao.publicarBitacora(id, fecha)
    }

    suspend fun cerrarBitacora(id: Int, fecha: String) {
        dao.cerrarBitacora(id, fecha)
    }

    suspend fun guardarRegistro(registro: Registro) {
        dao.insertarRegistro(registro)
    }

    fun obtenerRegistros(): Flow<List<Registro>> {
        return dao.obtenerRegistros()
    }

    suspend fun enviarRegistro(id: Int, fecha: String) {
        dao.enviarRegistro(id, fecha)
    }

    suspend fun guardarComentario(id: Int, comentario: String, fecha: String, docente: String) {
        dao.guardarComentario(id, comentario, fecha, docente)
    }

    suspend fun marcarRevisado(id: Int) {
        dao.marcarRevisado(id)
    }

    suspend fun guardarRecordatorio(recordatorio: Recordatorio) {
        dao.insertarRecordatorio(recordatorio)
    }

    fun obtenerRecordatorios(): Flow<List<Recordatorio>> {
        return dao.obtenerRecordatorios()
    }

    suspend fun completarRecordatorio(id: Int) {
        dao.completarRecordatorio(id)
    }
}