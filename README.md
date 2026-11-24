# Bitacora2 — Aplicación Android

Aplicación móvil educativa desarrollada con **Kotlin** y **Jetpack Compose**. Forma parte del proyecto académico Bitacora2, que permite a docentes administrar bitácoras de aprendizaje y a estudiantes registrar sus actividades, aprendizajes y dudas.

## Integrantes

- David Herrera
- Belen Cid

## Funcionalidades principales

- Inicio de sesión para docentes y estudiantes.
- Consulta de cursos, asignaturas y bitácoras.
- Creación y edición de bitácoras por parte del docente.
- Publicación y cierre de bitácoras.
- Creación, edición y envío de registros de aprendizaje por parte del estudiante.
- Consulta y revisión de registros, con retroalimentación del docente.
- Consulta de participación e informes.
- Gestión de recordatorios.
- Búsqueda de recursos educativos mediante la API REST de Wikipedia en español.

## Tecnologías

- Kotlin 2.2.10
- Jetpack Compose y Material 3
- Retrofit 2.11.0 y Gson
- OkHttp Logging Interceptor
- Room
- Kotlin Coroutines
- JUnit 4 y MockK
- Kover para reportes de cobertura

## Arquitectura e integración

La aplicación utiliza una organización basada en pantallas, ViewModels, estados de interfaz y repositorios. Consume los servicios REST del backend mediante Retrofit.

**Repositorio del backend:** [Agregar aquí la URL GitHub de ProyectoApp]

**Documentación general:** consultar el README del repositorio backend, donde se describen la arquitectura completa, los endpoints propios, la base de datos, la API externa y los pasos para ejecutar ambos módulos.

## Requisitos

- Android Studio compatible con la configuración Gradle del proyecto.
- Android SDK.
- Emulador Android o dispositivo físico.
- Backend Spring Boot ejecutándose y conectado a MySQL.
- Conexión a internet para la búsqueda de recursos en Wikipedia.

## Ejecutar la aplicación

1. Clonar este repositorio.
2. Abrir el proyecto en Android Studio.
3. Esperar la sincronización de Gradle.
4. Confirmar que el backend esté ejecutándose.
5. Iniciar un emulador o conectar un dispositivo Android.
6. Ejecutar la configuración `app`.

En el emulador Android, la dirección `http://10.0.2.2:8080/` permite acceder al backend que se ejecuta en el computador anfitrión. Para un dispositivo físico, configurar la IP local del computador en la aplicación y permitir la conexión en la red/firewall.

## Pruebas unitarias y cobertura

Desde la carpeta del proyecto, en Windows, ejecutar:

```powershell
.\gradlew.bat test
```

Para generar el reporte HTML de Kover:

```powershell
.\gradlew.bat koverHtmlReport
```

El porcentaje de cobertura debe informarse después de ejecutar el reporte y verificar el resultado real. No se declara aquí un porcentaje no comprobado.

## Versión de la aplicación

| Dato | Valor |
|---|---|
| Nombre | Bitacora2 |
| Application ID | `cl.duoc.bitacora2` |
| Version Name | `1.0` |
| Version Code | `1` |
| Min SDK | 24 |
| Target SDK | 37 |

Estos valores corresponden a la versión inspeccionada durante el desarrollo; comprobarlos nuevamente en la APK final de entrega.

## APK firmada y archivo de firma

La entrega requiere una APK `release` firmada digitalmente. `app-debug.apk` es una compilación de depuración y no reemplaza la APK firmada. Publicar la APK final como un archivo adjunto de una **GitHub Release**, por ejemplo `Bitacora2 v1.0`.

La pauta académica solicita también el archivo `.jks` de demostración y evidencia del proceso de firma. El almacén de claves debe conservarse como respaldo y entregarse al docente por el medio indicado. No publicar el `.jks` ni sus contraseñas en este repositorio público. Las capturas de evidencia deben ocultar contraseñas y cualquier dato secreto.

## Repositorios

- **Android (este repositorio):** repositorio de la aplicación móvil.
- **Backend Spring Boot:** [Agregar URL GitHub de ProyectoApp].

---

**Proyecto académico desarrollado por David Herrera y Belen Cid.**
