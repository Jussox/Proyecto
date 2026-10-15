
package cl.duoc.bitacora2

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.bitacora2.ui.theme.Docente.DocenteBitacorasScreen
import cl.duoc.bitacora2.ui.theme.Docente.DocenteScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteBitacoraScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteBitacorasScreen
import cl.duoc.bitacora2.ui.theme.Estudiante.EstudianteScreen
import cl.duoc.bitacora2.ui.theme.login.LoginScreen

@Composable
fun Navegacion() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onIrAEstudiante = {
                    navController.navigate("estudiante")
                },
                onIrADocente = {
                    navController.navigate("docente")
                }
            )
        }

        composable("estudiante") {
            EstudianteScreen(
                nombre = "Estudiante",
                curso = "5° básico",
                onCursos = {
                    // Se usa una ruta válida registrada en el NavHost
                    navController.navigate("EstudianteBitacoraScreen")
                },
                onBitacoras = {
                    // Pendiente de conectar
                },
                onHistorial = {
                    // Pendiente de conectar
                },
                onRetroalimentacion = {
                    // Pendiente de conectar
                },
                onCerrarSesion = {
                    navController.popBackStack(
                        "login",
                        inclusive = false
                    )
                }
            )
        }
        // --- PANTALLA DE LISTA DE BITÁCORAS / CURSOS ---
        composable("EstudianteBitacoraScreen") {
            EstudianteBitacorasScreen(

                titulo = "Mis cursos y asignaturas",

                bitacoras = emptyList(), // Puedes pasar tu lista de bitácoras aquí
                onVolver = {
                    navController.popBackStack()
                },
                onAbrir = { id ->
                    navController.navigate("estudiante_bitacora_detalle")
                }
            )
        }

        // --- PANTALLA DE DETALLE DE BITÁCORA ---
        composable("estudiante_bitacora_detalle") {
            EstudianteBitacoraScreen(
                bitacora = null, // Puedes pasar la bitácora seleccionada
                onVolver = {
                    navController.popBackStack()
                },
                onNuevoRegistro = {
                    // Acción para crear nuevo registro
                }
            )
        }

        composable("docente") {
            DocenteScreen(
                nombre = "Docente",
                aportesPendientes = 0,
                recordatoriosPendientes = 0,
                onCursos = {
                    navController.navigate("docente_bitacoras")

                    // Pendiente de conectar
                },
                onCrear = {
                    // Pendiente de conectar
                },
                onBitacoras = {
                    // Pendiente de conectar
                },
                onAportes = {
                    // Pendiente de conectar
                },
                onPendientes = {
                    // Pendiente de conectar
                },
                onParticipacion = {
                    // Pendiente de conectar
                },
                onIndicadores = {
                    // Pendiente de conectar
                },
                onHistorial = {
                    // Pendiente de conectar
                },
                onInforme = {
                    // Pendiente de conectar
                },
                onRecordatorios = {
                    // Pendiente de conectar
                },
                onCerrarSesion = {
                    navController.popBackStack(
                        "login",
                        inclusive = false
                    )
                }
            )
        }
    }
}
