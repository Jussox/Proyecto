package cl.duoc.bitacora2.ui.theme.usuarios

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.AppDatabase
import cl.duoc.bitacora2.data.UsuarioRepository
import cl.duoc.bitacora2.model.Usuario
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class UsuariosViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val database =
        AppDatabase.getInstance(application)
    private val repository =
        UsuarioRepository(database.usuarioDao())

    val usuarios: StateFlow<List<Usuario>> =
        repository
            .obtenerUsuarios()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}