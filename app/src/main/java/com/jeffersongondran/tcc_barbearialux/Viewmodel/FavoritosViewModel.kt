package com.jeffersongondran.luxconnect.Viewmodel

/**
 * ViewModel responsável por gerenciar os favoritos do usuário.
 * Observa os salões favoritados em tempo real e permite alternar favorito.
 */
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeffersongondran.luxconnect.Model.Salao
import com.jeffersongondran.luxconnect.Repository.RepositorioFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritosViewModel : ViewModel() {

    private val repositorio = RepositorioFirestore.getInstance()

    // Lista de salões favoritos
    private val _favoritos = MutableStateFlow<List<Salao>>(emptyList())
    val favoritos: StateFlow<List<Salao>> = _favoritos.asStateFlow()

    // Estado de carregamento e erro para feedback na UI
    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    /**
     * Inicia observação dos favoritos do usuário informado.
     * @param usuarioId ID do usuário atual
     */
    fun observarFavoritosDoUsuario(usuarioId: String) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null
            try {
                repositorio.observarFavoritos(usuarioId).collect { lista ->
                    _favoritos.value = lista
                    _carregando.value = false
                }
            } catch (e: Exception) {
                _erro.value = "Erro ao carregar favoritos: ${e.message}"
                _carregando.value = false
            }
        }
    }

    /**
     * Alterna o estado de favorito para um salão específico.
     */
    fun alternarFavorito(usuarioId: String, salaoId: String) {
        viewModelScope.launch {
            _erro.value = null
            val resultado = repositorio.alternarFavorito(usuarioId, salaoId)
            if (resultado.isFailure) {
                _erro.value = "Erro ao alternar favorito: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }
}

