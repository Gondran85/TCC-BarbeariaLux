/**
 * ViewModel responsável por gerenciar os dados dos salões
 * Segue o padrão MVVM para separar a lógica de negócio da interface
 */
package com.jeffersongondran.luxconnect.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeffersongondran.luxconnect.Model.Salao
import com.jeffersongondran.luxconnect.Repository.RepositorioFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar a lista de salões disponíveis
 * Observa mudanças em tempo real no Firestore e expõe os dados para a UI
 */
class SaloesViewModel : ViewModel() {

    private val repositorio = RepositorioFirestore.getInstance()

    // Estado interno da lista de salões
    private val _saloes = MutableStateFlow<List<Salao>>(emptyList())

    /**
     * Lista de salões observável pela UI
     * Atualizada automaticamente quando há mudanças no Firestore
     */
    val saloes: StateFlow<List<Salao>> = _saloes.asStateFlow()

    // Estado de carregamento
    private val _carregando = MutableStateFlow(false)

    /**
     * Indica se os dados estão sendo carregados
     */
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    // Estado de erro
    private val _erro = MutableStateFlow<String?>(null)

    /**
     * Mensagem de erro, se houver
     */
    val erro: StateFlow<String?> = _erro.asStateFlow()

    init {
        // Inicia o carregamento dos salões quando a ViewModel é criada
        carregarSaloes()
    }

    /**
     * Carrega a lista de salões do Firestore
     * Configura um listener para atualizações em tempo real
     */
    private fun carregarSaloes() {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null

            try {
                // Observa mudanças na coleção de salões
                repositorio.listarSaloes().collect { listaSaloes ->
                    _saloes.value = listaSaloes
                    _carregando.value = false
                }
            } catch (exception: Exception) {
                _erro.value = "Erro ao carregar salões: ${exception.message}"
                _carregando.value = false
            }
        }
    }

    /**
     * Filtra salões que oferecem um serviço específico
     * @param nomeServico Nome do serviço a filtrar
     * @return Lista de salões que oferecem o serviço
     */
    fun filtrarSaloesPorServico(nomeServico: String): List<Salao> {
        return _saloes.value.filter { salao ->
            salao.ofereceServico(nomeServico)
        }
    }

    /**
     * Busca salões pelo nome
     * @param termo Termo de busca
     * @return Lista de salões que contêm o termo no nome
     */
    fun buscarSaloesPorNome(termo: String): List<Salao> {
        return if (termo.isBlank()) {
            _saloes.value
        } else {
            _saloes.value.filter { salao ->
                salao.nome.contains(termo, ignoreCase = true)
            }
        }
    }

    /**
     * Limpa a mensagem de erro
     */
    fun limparErro() {
        _erro.value = null
    }

    /**
     * Recarrega a lista de salões manualmente
     */
    fun recarregar() {
        carregarSaloes()
    }
}
