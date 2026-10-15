
package cl.duoc.bitacora2

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.bitacora2.ui.theme.Docente.DocenteScreen
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
                    // Pendiente de conectar
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

        composable("docente") {
            DocenteScreen(
                nombre = "Docente",
                aportesPendientes = 0,
                recordatoriosPendientes = 0,
                onCursos = {
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
