package cl.duoc.bitacora2.ui.theme.Docente

import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.data.UsuarioPrueba

data class DocenteParticipacionUiState(
    val bitacoras: List<Bitacora> = emptyList(),
    val seleccionada: Int = 0,
    val bitacora: Bitacora? = null,
    val nombreDocente: String = "",
    val estudiantes: List<UsuarioPrueba> = emptyList(),
    val participantes: List<UsuarioPrueba> = emptyList(),
    val sinParticipacion: List<UsuarioPrueba> = emptyList(),
    val enviados: List<Registro> = emptyList(),
    val pendientes: Int = 0,
    val revisados: Int = 0
)
