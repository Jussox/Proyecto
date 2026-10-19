
package cl.duoc.bitacora2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cl.duoc.bitacora2.ui.theme.Docente.DocenteAportesScreen

import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.data.DatosPrueba
import cl.duoc.bitacora2.ui.theme.Docente.DocenteAportesScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteBitacorasScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteCrearBitacoraScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteParticipacionScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteRecordatoriosScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteRevisionScreen

import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteBitacorasScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteBitacoraScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteHistorialScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteRegistroScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteRetroalimentacionScreen



import cl.duoc.bitacora2.ui.theme.login.LoginScreen

@Composable
fun Navegacion() {
    val navController = rememberNavController()
    val viewModel: BitacoraViewModel = viewModel()

    val bitacoras by viewModel.bitacoras.collectAsState()

    val registros by viewModel.registros.collectAsState()

    val recordatorios by viewModel.recordatorios.collectAsState()

    val bitacorasDocente = bitacoras.filter { it.docente == viewModel.usuarioActual }
    val registrosDocente = registros.filter { r ->
        bitacorasDocente.any { it.id == r.bitacoraId }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // INICIO DE SESIÓN
        composable("login") {
            LoginScreen(
                onIrAEstudiante = { usuario ->
                    viewModel.usuarioActual = usuario
                    navController.navigate("estudiante")
                },
                onIrADocente = { usuario ->
                    viewModel.usuarioActual = usuario
                    navController.navigate("docente")
                }
            )
        }

        // MENÚ ESTUDIANTE
        composable("estudiante") {
            EstudianteScreen(
                nombre = DatosPrueba.nombreDe(viewModel.usuarioActual),
                curso = DatosPrueba.cursoDe(viewModel.usuarioActual),

                onCursos = {
                    navController.navigate("EstudianteBitacoraScreen")
                },
                onBitacoras = {
                    navController.navigate("EstudianteBitacoraScreen")
                },
                onHistorial = {
                    navController.navigate("estudiante_historial")
                },
                onRetroalimentacion = {
                    navController.navigate("estudiante_retroalimentacion")
                },
                onCerrarSesion = {
                    navController.popBackStack(
                        "login",
                        inclusive = false
                    )
                }
            )
        }

        // CURSOS / BITÁCORAS DEL ESTUDIANTE
        composable("EstudianteBitacoraScreen") {
            EstudianteBitacorasScreen(
                titulo = "Mis cursos y asignaturas",
                //muestra solo las que están publicadas para el curso del estudiante
                bitacoras = bitacoras.filter {
                    it.curso == DatosPrueba.cursoDe(viewModel.usuarioActual) &&
                            it.estado == "publicada"
                },
                onVolver = {
                    navController.popBackStack()
                },
                onAbrir = { id ->
                    navController.navigate("estudiante_bitacora_detalle/$id")
                }
            )
        }

        // DETALLE DE BITÁCORA DEL ESTUDIANTE

        composable(
            route = "estudiante_bitacora_detalle/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getInt("id")

            val bitacoraSeleccionada = bitacoras.firstOrNull {
                it.id == id
            }

            EstudianteBitacoraScreen(
                bitacora = bitacoraSeleccionada,
                onVolver = {
                    navController.popBackStack()
                },
                onNuevoRegistro = {
                    bitacoraSeleccionada?.let { bitacora ->
                        navController.navigate("estudiante_registro/${bitacora.id}")
                    }
                }
            )
        }

        // CREAR REGISTRO DE CLASE DEL ESTUDIANTE
        composable(
            route = "estudiante_registro/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getInt("id")

            val bitacoraSeleccionada = bitacoras.firstOrNull {
                it.id == id
            }

            EstudianteRegistroScreen(
                bitacora = bitacoraSeleccionada,
                estudiante = viewModel.usuarioActual,
                onVolver = {
                    navController.popBackStack()
                },
                onGuardar = { registro ->
                    viewModel.guardarRegistro(registro)
                }
            )
        }

        // HISTORIAL DE REGISTROS DEL ESTUDIANTE
        composable("estudiante_historial") {
            EstudianteHistorialScreen(
                registros = registros.filter {
                    it.estudiante == viewModel.usuarioActual
                },
                bitacoras = bitacoras,
                onVolver = {
                    navController.popBackStack()
                },
                onEditar = { registro ->
                    navController.navigate(
                        "estudiante_editar_registro/${registro.id}"
                    )
                },
                onEliminar = { id ->
                    viewModel.eliminarRegistro(id)
                }
            )
        }

        // RETROALIMENTACIÓN DEL ESTUDIANTE
        composable("estudiante_retroalimentacion") {
            EstudianteRetroalimentacionScreen(
                registros = registros.filter { it.estudiante == viewModel.usuarioActual },
                bitacoras = bitacoras,
                onVolver = { navController.popBackStack() }
            )
        }

        // EDITAR REGISTRO EXISTENTE
        composable(
            route = "estudiante_editar_registro/{registroId}",
            arguments = listOf(
                navArgument("registroId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val registroId = backStackEntry.arguments?.getInt("registroId")

            val registroExistente = registros.firstOrNull {
                it.id == registroId &&
                        it.estudiante == viewModel.usuarioActual
            }

            val bitacoraDelRegistro = bitacoras.firstOrNull {
                it.id == registroExistente?.bitacoraId
            }

            EstudianteRegistroScreen(
                bitacora = bitacoraDelRegistro,
                estudiante = viewModel.usuarioActual,
                registroExistente = registroExistente,
                onVolver = {
                    navController.popBackStack()
                },
                onGuardar = { registro ->
                    if (registro.id == 0) {
                        viewModel.guardarRegistro(registro)
                    } else {
                        viewModel.actualizarRegistro(registro)
                    }

                    navController.popBackStack()
                }
            )
        }


        // MENÚ DOCENTE
        composable("docente") {
            DocenteScreen(
                nombre = DatosPrueba.nombreDe(viewModel.usuarioActual),
                aportesPendientes = registrosDocente.count { it.estado == "pendiente" },
                recordatoriosPendientes = recordatorios.count {
                    it.docente == viewModel.usuarioActual && it.estado == "pendiente"
                },

                onCursos = {
                    navController.navigate("docente_bitacoras")
                },

                onCrear = {
                    navController.navigate("docente_crear_bitacora")
                },

                onBitacoras = {
                    navController.navigate("docente_bitacoras")
                },

                onAportes = {
                    navController.navigate("docente_aportes_general/todos")
                },

                onPendientes = {
                    navController.navigate("docente_aportes_general/pendiente")
                },

                onParticipacion = {
                    navController.navigate("docente_participacion")
                },

                onIndicadores = {
                    navController.navigate("docente_indicadores")
                },

                onHistorial = {
                    navController.navigate("docente_historial")
                },

                onInforme = {
                    navController.navigate("docente_informe")
                },

                onRecordatorios = {
                    navController.navigate("docente_recordatorios")
                },

                onCerrarSesion = {
                    navController.popBackStack(
                        "login",
                        inclusive = false
                    )
                }
            )
        }

        // BITÁCORAS DEL DOCENTE
        composable("docente_bitacoras") {
            DocenteBitacorasScreen(
                titulo = "Bitácoras mensuales",
                bitacoras = bitacorasDocente,
                onVolver = { navController.popBackStack() },
                onPublicar = { id -> viewModel.publicarBitacora(id) },
                onCerrar = { id -> viewModel.cerrarBitacora(id) },
                onVerAportes = { id ->
                    viewModel.bitacoraAportes = id
                    navController.navigate("docente_aportes")
                }
            )
        }

        // APORTES DEL DOCENTE
        composable("docente_aportes") {
            DocenteAportesScreen(
                titulo = "Aportes de estudiantes",
                registros = registros.filter { it.bitacoraId == viewModel.bitacoraAportes },
                bitacoras = bitacoras,
                estadoInicial = "todos",
                onVolver = { navController.popBackStack() },
                onAbrir = { registroId ->
                    viewModel.registroSeleccionado = registroId
                    navController.navigate("docente_revision")
                }
            )
        }

        // REVISIÓN DE UN APORTE
        composable("docente_revision") {
            val registro = registros.firstOrNull { it.id == viewModel.registroSeleccionado }
            val bitacora = bitacoras.firstOrNull { it.id == registro?.bitacoraId }

            DocenteRevisionScreen(
                registro = registro,
                bitacora = bitacora,
                onVolver = { navController.popBackStack() },
                onGuardarComentario = { id, comentario ->
                    viewModel.guardarComentario(id, comentario)
                },
                onMarcarRevisado = { id ->
                    viewModel.marcarRevisado(id)
                }
            )
        }

        // CREAR BITÁCORA
        composable("docente_crear_bitacora") {
            DocenteCrearBitacoraScreen(
                asignaciones = DatosPrueba.asignacionesDeDocente(
                    viewModel.usuarioActual
                ),
                docente = viewModel.usuarioActual,
                onVolver = {
                    navController.popBackStack()
                },
                onGuardar = { bitacora ->
                    viewModel.guardarBitacora(bitacora)
                    navController.popBackStack()
                }
            )
        }

        // SEGUIMIENTO DE PARTICIPACIÓN
        composable("docente_participacion") {
            var seleccionada by remember { mutableStateOf(0) }
            DocenteParticipacionScreen(
                bitacoras = bitacorasDocente.filter {
                    it.estado == "publicada" || it.estado == "cerrada"
                },
                registros = registrosDocente,
                seleccionada = seleccionada,
                modo = "seguimiento",
                onSeleccionar = { seleccionada = it },
                onVolver = { navController.popBackStack() }
            )
        }

        // INDICADORES
        composable("docente_indicadores") {
            var seleccionada by remember { mutableStateOf(0) }
            DocenteParticipacionScreen(
                bitacoras = bitacorasDocente.filter {
                    it.estado == "publicada" || it.estado == "cerrada"
                },
                registros = registrosDocente,
                seleccionada = seleccionada,
                modo = "indicadores",
                onSeleccionar = { seleccionada = it },
                onVolver = { navController.popBackStack() }
            )
        }

        // INFORME
        composable("docente_informe") {
            var seleccionada by remember { mutableStateOf(0) }
            DocenteParticipacionScreen(
                bitacoras = bitacorasDocente.filter {
                    it.estado == "publicada" || it.estado == "cerrada"
                },
                registros = registrosDocente,
                seleccionada = seleccionada,
                modo = "informe",
                onSeleccionar = { seleccionada = it },
                onVolver = { navController.popBackStack() }
            )
        }

        // APORTES DESDE EL MENÚ (todos o solo pendientes)
        composable(
            route = "docente_aportes_general/{estadoInicial}",
            arguments = listOf(navArgument("estadoInicial") { type = NavType.StringType })
        ) { backStackEntry ->
            val estadoInicial = backStackEntry.arguments?.getString("estadoInicial") ?: "todos"
            DocenteAportesScreen(
                titulo = if (estadoInicial == "pendiente")
                    "Aportes pendientes de revisión"
                else
                    "Aportes de estudiantes",
                registros = registrosDocente,
                bitacoras = bitacoras,
                estadoInicial = estadoInicial,
                onVolver = { navController.popBackStack() },
                onAbrir = { registroId ->
                    viewModel.registroSeleccionado = registroId
                    navController.navigate("docente_revision")
                }
            )
        }

        // HISTORIAL DE BITÁCORAS (las cerradas)
        composable("docente_historial") {
            DocenteBitacorasScreen(
                titulo = "Historial de bitácoras",
                bitacoras = bitacorasDocente.filter { it.estado == "cerrada" },
                onVolver = { navController.popBackStack() },
                onPublicar = { },
                onCerrar = { },
                onVerAportes = { id ->
                    viewModel.bitacoraAportes = id
                    navController.navigate("docente_aportes")
                }
            )
        }

        // RECORDATORIOS INTERNOS
        composable("docente_recordatorios") {
            DocenteRecordatoriosScreen(
                recordatorios = recordatorios.filter {
                    it.docente == viewModel.usuarioActual
                },
                onVolver = {
                    navController.popBackStack()
                },
                onCrear = { tipo, fecha ->
                    viewModel.crearRecordatorio(tipo, fecha)
                },
                onCompletar = { id ->
                    viewModel.completarRecordatorio(id)
                }
            )
        }

    }
}