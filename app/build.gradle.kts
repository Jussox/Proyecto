plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

android {
    namespace = "cl.duoc.bitacora2"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cl.duoc.bitacora2"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.animation.core)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.room.runtime)
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    ksp(libs.androidx.room.compiler)
}

kover {
    reports {
        filters {
            includes {
                classes(
                    // Lógica pura de validaciones de formularios y reglas de negocio
                    "cl.duoc.bitacora2.util.Validaciones*",
                    // Utilidades de traducción de errores HTTP y de red
                    "cl.duoc.bitacora2.util.ErrorUtils*",
                    // Gestión de sesión activa en memoria
                    "cl.duoc.bitacora2.util.Sesion*",
                    // Repositorio remoto que procesa y valida respuestas de la API REST
                    "cl.duoc.bitacora2.data.remoto.RemotoRepository*",
                    // ViewModels de autenticación
                    "cl.duoc.bitacora2.ui.theme.login.*ViewModel*",
                    // ViewModels del rol Docente (nombre de paquete real con D mayúscula)
                    "cl.duoc.bitacora2.ui.theme.Docente.*ViewModel*",
                    // ViewModels del rol Estudiante (nombre de paquete real con E mayúscula)
                    "cl.duoc.bitacora2.ui.theme.Estudiante.*ViewModel*"
                )
            }
        }
    }
}