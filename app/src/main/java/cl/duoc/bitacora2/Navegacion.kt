
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
                   nombre = DatosPrueba.nombreDe(Sesion.usuario),
                   curso = DatosPrueba.cursoDe(Sesion.usuario),

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
                   //muestra solo las que están publicadas para el curso del estudiante
                   bitacoras = bitacoras.filter {
                       it.curso == DatosPrueba.cursoDe(Sesion.usuario) &&
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
                   estudiante = Sesion.usuario,
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
                       it.estudiante == Sesion.usuario
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
                   registros = registros.filter { it.estudiante == Sesion.usuario },
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
                           it.estudiante == Sesion.usuario
               }

               val bitacoraDelRegistro = bitacoras.firstOrNull {
                   it.id == registroExistente?.bitacoraId
               }

               EstudianteRegistroScreen(
                   bitacora = bitacoraDelRegistro,
                   estudiante = Sesion.usuario,
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
                   nombre = DatosPrueba.nombreDe(Sesion.usuario),
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
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val bitacoraId = backStackEntry.arguments?.getInt("bitacoraId") ?: 0
                DocenteAportesScreen(
                    titulo = "Aportes de estudiantes",
                    registros = registros.filter { it.bitacoraId == bitacoraId },
                    bitacoras = bitacoras,
                    estadoInicial = "todos",
                    onVolver = { navController.popBackStack() },
                    onAbrir = { registroId ->
                        navController.navigate("docente_revision/$registroId")
                    }
                )
            }

           // REVISIÓN DE UN APORTE
           composable(
                route = "docente_revision/{registroId}",
                arguments = listOf(
                    navArgument("registroId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val registroId = backStackEntry.arguments?.getInt("registroId") ?: 0
                val registro = registros.firstOrNull { it.id == registroId }
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
                   titulo = if (estadoInicial == "pendiente")
                       "Aportes pendientes de revisión"
                   else
                       "Aportes de estudiantes",
                   registros = registrosDocente,
                   bitacoras = bitacoras,
                   estadoInicial = estadoInicial,
                   onVolver = { navController.popBackStack() },
                   onAbrir = { registroId -> navController.navigate("docente_revision/$registroId") }
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
