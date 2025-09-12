package com.jeffersongondran.luxconnect.Viewmodel

/**
 * ViewModel responsável por gerenciar a tela de busca de salões.
 * Implementa debounce no termo de busca e expõe resultados em tempo real.
 */
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeffersongondran.luxconnect.Model.Salao
import com.jeffersongondran.luxconnect.Repository.RepositorioFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ProcurarViewModel : ViewModel() {

    private val repositorio = RepositorioFirestore.getInstance()

    // Termo de busca digitado pelo usuário
    private val _termoBusca = MutableStateFlow("")
    val termoBusca: StateFlow<String> = _termoBusca.asStateFlow()

    // Estado de carregamento e erro para a UI
    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    // Resultados da busca em tempo real (com debounce)
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val resultados: StateFlow<List<Salao>> = _termoBusca
        .debounce(350L) // Aguarda o usuário parar de digitar
        .distinctUntilChanged()
        .flatMapLatest { termo ->
            if (termo.isBlank()) flowOf(emptyList()) else repositorio.buscarSaloes(termo)
        }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    /**
     * Atualiza o termo de busca.
     */
    fun atualizarTermo(termo: String) {
        _termoBusca.value = termo
    }

    /**
     * Reexecuta a busca atual (útil para pull-to-refresh).
     */
    fun recarregar() {
        _termoBusca.update { it }
    }
}
