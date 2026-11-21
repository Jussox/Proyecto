
package cl.duoc.bitacora2


import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.bitacora2.data.DatosPrueba
import cl.duoc.bitacora2.ui.theme.Docente.DocenteAportesScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteBitacorasScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteCrearBitacoraScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteParticipacionScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteRecordatoriosScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteRevisionScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteBitacoraScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteBitacorasScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteHistorialScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteRegistroScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteRetroalimentacionScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteScreen
import cl.duoc.bitacora2.ui.theme.login.LoginScreen
import cl.duoc.bitacora2.util.Sesion

import cl.duoc.bitacora2.ui.theme.Estudiante.CursosScreen
import android.net.Uri

@Composable
fun Navegacion() {
    val navController = rememberNavController()
    val viewModel: BitacoraViewModel = viewModel()

    val bitacoras by viewModel.bitacoras.collectAsState()

    val registros by viewModel.registros.collectAsState()

    val recordatorios by viewModel.recordatorios.collectAsState()

    val bitacorasDocente = bitacoras.filter { it.docente == Sesion.usuario }
    val registrosDocente = registros.filter { r ->
        bitacorasDocente.any { it.id == r.bitacoraId }
    }

    NavHost(
        navController = navController,
        startDestination = "login",

        // La pantalla nueva entra desde la derecha.
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it / 3 },
                animationSpec = tween(300)
            ) + fadeIn(
                animationSpec = tween(300)
            )
        },

        // La pantalla actual sale hacia la izquierda.
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(250)
            ) + fadeOut(
                animationSpec = tween(250)
            )
        },

        // Al volver, la pantalla anterior entra desde la izquierda.
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(300)
            ) + fadeIn(
                animationSpec = tween(300)
            )
        },

        // La pantalla actual sale hacia la derecha al regresar.
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it / 3 },
                animationSpec = tween(250)
            ) + fadeOut(
                animationSpec = tween(250)
            )
        }

    ) {
        // INICIO DE SESIÓN
           composable("login") {
               LoginScreen(
                   onIrAEstudiante = { navController.navigate("estudiante") },
                   onIrADocente = { navController.navigate("docente") }
               )
           }

           // MENÚ ESTUDIANTE
           composable("estudiante") {
               EstudianteScreen(
                   nombre = Sesion.nombre,
                   curso = Sesion.curso,

                   onCursos = {
                       navController.navigate("estudiante_cursos")
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
                   onVolver = {
                       navController.popBackStack()
                   },
                   onAbrir = { id ->
                       navController.navigate("estudiante_bitacora_detalle/$id")
                   }
               )
           }

        composable("estudiante_cursos") {
            CursosScreen(
                titulo = "Mis cursos y asignaturas",
                asignaciones = DatosPrueba.asignacionesDeEstudiante(Sesion.usuario),
                textoBoton = "Ver bitácoras",
                onVolver = { navController.popBackStack() },
                onElegir = { navController.navigate("EstudianteBitacoraScreen") }
            )
        }

           // DETALLE DE BITÁCORA DEL ESTUDIANTE

           composable(
               route = "estudiante_bitacora_detalle/{id}",
               arguments = listOf(
                   navArgument("id") {
                       type = NavType.LongType
                   }
               )
           ) { backStackEntry ->
               val id = backStackEntry.arguments?.getLong("id") ?: 0L

               EstudianteBitacoraScreen(
                   bitacoraId = id,
                   onVolver = {
                       navController.popBackStack()
                   },
                   onNuevoRegistro = {
                       navController.navigate("estudiante_registro/$id")
                   }
               )
           }

           // CREAR REGISTRO DE CLASE DEL ESTUDIANTE
           composable(
               route = "estudiante_registro/{id}",
               arguments = listOf(
                   navArgument("id") {
                       type = NavType.LongType
                   }
               )
           ) { backStackEntry ->
               val id = backStackEntry.arguments?.getLong("id")
               EstudianteRegistroScreen(
                   bitacoraId = id,
                   registroId = null,
                   onVolver = {
                       navController.popBackStack()
                   }
               )
           }

           // HISTORIAL DE REGISTROS DEL ESTUDIANTE
           composable("estudiante_historial") {
               EstudianteHistorialScreen(
                   onVolver = {
                       navController.popBackStack()
                   },
                   onEditar = { id ->
                       navController.navigate("estudiante_editar_registro/$id")
                   },
                   onVerRetroalimentacion = {
                       navController.navigate("estudiante_retroalimentacion")
                   }
               )
           }

           // RETROALIMENTACIÓN DEL ESTUDIANTE
           composable("estudiante_retroalimentacion") {
               EstudianteRetroalimentacionScreen(
                   onVolver = { navController.popBackStack() }
               )
           }

           // EDITAR REGISTRO EXISTENTE
           composable(
               route = "estudiante_editar_registro/{registroId}",
               arguments = listOf(
                   navArgument("registroId") {
                       type = NavType.LongType
                   }
               )
           ) { backStackEntry ->
               val registroId = backStackEntry.arguments?.getLong("registroId")
               EstudianteRegistroScreen(
                   bitacoraId = null,
                   registroId = registroId,
                   onVolver = {
                       navController.popBackStack()
                   }
               )
           }


           // MENÚ DOCENTE
           composable("docente") {
               DocenteScreen(
                   nombre = Sesion.nombre,
                   // TODO(Paso 6): migrar contadores de Room al backend
                   aportesPendientes = registrosDocente.count { it.estado == "pendiente" },
                   recordatoriosPendientes = recordatorios.count {
                       it.docente == Sesion.usuario && it.estado == "pendiente"
                   },

                   onCursos = {
                       navController.navigate("docente_cursos")
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


        composable("docente_cursos") {
            CursosScreen(
                titulo = "Mis cursos y asignaturas",
                asignaciones = DatosPrueba.asignacionesDeDocente(Sesion.usuario),
                textoBoton = "Ver bitácoras",
                onVolver = {
                    navController.popBackStack()
                },
                onElegir = { asignacion ->
                    navController.navigate(
                        "docente_bitacoras_curso/${Uri.encode(asignacion.curso)}/${Uri.encode(asignacion.asignatura)}"
                    )
                }
            )
        }


        // BITÁCORAS DEL DOCENTE
           composable("docente_bitacoras") {
               DocenteBitacorasScreen(
                   titulo = "Bitácoras mensuales",
                   onVolver = { navController.popBackStack() },
                   onVerAportes = { id ->
                       navController.navigate("docente_aportes/$id")
                   },
                   onEditar = { id ->
                       navController.navigate("docente_editar_bitacora/$id")
                   }
               )
           }

           // APORTES DEL DOCENTE
           composable(
                route = "docente_aportes/{bitacoraId}",
                arguments = listOf(
                    navArgument("bitacoraId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val bitacoraId = backStackEntry.arguments?.getLong("bitacoraId") ?: 0L
                DocenteAportesScreen(
                    bitacoraId = bitacoraId,
                    titulo = "Aportes de estudiantes",
                    estadoInicial = "todos",
                    onVolver = { navController.popBackStack() },
                    onAbrir = { registroId, bId ->
                        navController.navigate("docente_revision/$registroId?bitacoraId=$bId")
                    }
                )
            }

           // REVISIÓN DE UN APORTE
           composable(
                route = "docente_revision/{registroId}?bitacoraId={bitacoraId}",
                arguments = listOf(
                    navArgument("registroId") {
                        type = NavType.LongType
                    },
                    navArgument("bitacoraId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) { backStackEntry ->
                val registroId = backStackEntry.arguments?.getLong("registroId") ?: 0L
                val bitacoraId = backStackEntry.arguments?.getLong("bitacoraId")?.takeIf { it > 0L }

                DocenteRevisionScreen(
                    registroId = registroId,
                    bitacoraId = bitacoraId,
                    onVolver = { navController.popBackStack() }
                )
            }

           // CREAR BITÁCORA
           composable("docente_crear_bitacora") {
               DocenteCrearBitacoraScreen(
                   bitacoraId = null,
                   onVolver = { navController.popBackStack() }
               )
           }

           // EDITAR BITÁCORA (modo edición)
           composable(
               route = "docente_editar_bitacora/{id}",
               arguments = listOf(navArgument("id") { type = NavType.LongType })
           ) { backStackEntry ->
               val id = backStackEntry.arguments?.getLong("id")
               DocenteCrearBitacoraScreen(
                   bitacoraId = id,
                   onVolver = { navController.popBackStack() }
               )
           }

            // SEGUIMIENTO DE PARTICIPACIÓN
            composable("docente_participacion") {
                DocenteParticipacionScreen(
                    modo = "seguimiento",
                    onVolver = { navController.popBackStack() }
                )
            }

            // INDICADORES
            composable("docente_indicadores") {
                DocenteParticipacionScreen(
                    modo = "indicadores",
                    onVolver = { navController.popBackStack() }
                )
            }

            // INFORME
            composable("docente_informe") {
                DocenteParticipacionScreen(
                    modo = "informe",
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
                   bitacoraId = null,
                   titulo = if (estadoInicial == "pendiente")
                       "Aportes pendientes de revisión"
                   else
                       "Aportes de estudiantes",
                   estadoInicial = estadoInicial,
                   onVolver = { navController.popBackStack() },
                   onAbrir = { registroId, bId ->
                       navController.navigate("docente_revision/$registroId?bitacoraId=$bId")
                   }
               )
           }

           // HISTORIAL DE BITÁCORAS (las cerradas)
           composable("docente_historial") {
               DocenteBitacorasScreen(
                   titulo = "Historial de bitácoras",
                   soloCerradas = true,
                   onVolver = { navController.popBackStack() },
                   onVerAportes = { id ->
                       navController.navigate("docente_aportes/$id")
                   }
               )
           }

           // RECORDATORIOS INTERNOS
           composable("docente_recordatorios") {
               DocenteRecordatoriosScreen(
                   recordatorios = recordatorios.filter {
                       it.docente == Sesion.usuario
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
