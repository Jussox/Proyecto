package cl.duoc.bitacora2.ui.theme.comun

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically

const val DURACION_FADE_IN = 250
const val DURACION_EXPAND = 300
const val DURACION_FADE_OUT = 200
const val DURACION_SHRINK = 250
const val DURACION_CROSSFADE = 300

val animacionEntradaExpandir: EnterTransition =
    fadeIn(animationSpec = tween(DURACION_FADE_IN)) +
            expandVertically(animationSpec = tween(DURACION_EXPAND))

val animacionSalidaContraer: ExitTransition =
    fadeOut(animationSpec = tween(DURACION_FADE_OUT)) +
            shrinkVertically(animationSpec = tween(DURACION_SHRINK))
