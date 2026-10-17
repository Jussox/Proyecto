
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

import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.data.DatosPrueba
import cl.duoc.bitacora2.ui.theme.Docente.DocenteScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteBitacorasScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteCrearBitacoraScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteParticipacionScreen

import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteBitacorasScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteBitacoraScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteHistorialScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteRegistroScreen

import cl.duoc.bitacora2.ui.theme.login.LoginScreen

@Composable
fun Navegacion() {
    val navController = rememberNavController()
    val viewModel: BitacoraViewModel = viewModel()

    val bitacoras by viewModel.bitacoras.collectAsState()

    val registros by viewModel.registros.collectAsState()

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
                    // Pendiente: no hay una pantalla independiente
                    // confirmada para esta opción.
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
                aportesPendientes = 0,
                recordatoriosPendientes = 0,

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
                    // Pendiente de conectar con la pantalla de aportes.
                },

                onPendientes = {
                    // Pendiente de conectar con la pantalla de pendientes.
                },

                onParticipacion = {
                    navController.navigate("docente_participacion")
                },

                onIndicadores = {
                    navController.navigate("docente_indicadores")
                },

                onHistorial = {
                    // No hay una pantalla de historial docente
                    // confirmada para conectar aquí.
                },

                onInforme = {
                    navController.navigate("docente_informe")
                },

                onRecordatorios = {
                    // Pendiente de conectar con la pantalla
                    // de recordatorios.
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
                bitacoras = bitacoras,

                onVolver = {
                    navController.popBackStack()
                },

                onPublicar = { id ->
                    // Pendiente: guardar el cambio de estado.
                },

                onCerrar = { id ->
                    // Pendiente: guardar el cambio de estado.
                },

                onVerAportes = { id ->
                    // Pendiente de conectar con la pantalla
                    // de aportes de la bitácora seleccionada.
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
            DocenteParticipacionScreen(
                bitacoras = emptyList(),
                registros = emptyList(),
                seleccionada = 0,
                modo = "seguimiento",
                onSeleccionar = { indice ->
                    // Pendiente: conectar con los datos reales.
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // INDICADORES
        composable("docente_indicadores") {
            DocenteParticipacionScreen(
                bitacoras = emptyList(),
                registros = emptyList(),
                seleccionada = 0,
                modo = "indicadores",
                onSeleccionar = { indice ->
                    // Pendiente: conectar con los datos reales.
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // INFORME
        composable("docente_informe") {
            DocenteParticipacionScreen(
                bitacoras = emptyList(),
                registros = emptyList(),
                seleccionada = 0,
                modo = "informe",
                onSeleccionar = { indice ->
                    // Pendiente: conectar con los datos reales.
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}