/**
 * ViewModel responsável por gerenciar os agendamentos do usuário
 * Gerencia criação, listagem e atualização de agendamentos
 */
package com.jeffersongondran.luxconnect.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeffersongondran.luxconnect.Model.Agendamento
import com.jeffersongondran.luxconnect.Model.StatusAgendamento
import com.jeffersongondran.luxconnect.Repository.RepositorioFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar agendamentos do usuário
 * Permite criar, listar e cancelar agendamentos
 */
class AgendamentosViewModel : ViewModel() {

    private val repositorio = RepositorioFirestore.getInstance()

    // Lista de agendamentos do usuário
    private val _agendamentos = MutableStateFlow<List<Agendamento>>(emptyList())

    /**
     * Lista de agendamentos observável pela UI
     */
    val agendamentos: StateFlow<List<Agendamento>> = _agendamentos.asStateFlow()

    // Estado de carregamento
    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    // Estado de erro
    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    // Estado de sucesso para operações
    private val _operacaoSucesso = MutableStateFlow<String?>(null)

    /**
     * Mensagem de sucesso para operações realizadas
     */
    val operacaoSucesso: StateFlow<String?> = _operacaoSucesso.asStateFlow()

    /**
     * Carrega os agendamentos de um usuário específico
     * @param usuarioId ID do usuário cujos agendamentos serão carregados
     */
    fun carregarAgendamentosDoUsuario(usuarioId: String) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null

            try {
                // Observa mudanças nos agendamentos do usuário em tempo real
                repositorio.listarAgendamentosPorUsuario(usuarioId).collect { listaAgendamentos ->
                    _agendamentos.value = listaAgendamentos
                    _carregando.value = false
                }
            } catch (exception: Exception) {
                _erro.value = "Erro ao carregar agendamentos: ${exception.message}"
                _carregando.value = false
            }
        }
    }

    /**
     * Cria um novo agendamento
     * @param agendamento Dados do agendamento a ser criado
     */
    fun criarAgendamento(agendamento: Agendamento) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null

            try {
                val resultado = repositorio.criarAgendamento(agendamento)

                if (resultado.isSuccess) {
                    _operacaoSucesso.value = "Agendamento criado com sucesso!"
                } else {
                    _erro.value = "Erro ao criar agendamento: ${resultado.exceptionOrNull()?.message}"
                }
            } catch (exception: Exception) {
                _erro.value = "Erro inesperado: ${exception.message}"
            } finally {
                _carregando.value = false
            }
        }
    }

    /**
     * Cancela um agendamento existente
     * @param agendamentoId ID do agendamento a ser cancelado
     */
    fun cancelarAgendamento(agendamentoId: String) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null

            try {
                val resultado = repositorio.cancelarAgendamento(agendamentoId)

                if (resultado.isSuccess) {
                    _operacaoSucesso.value = "Agendamento cancelado com sucesso!"
                } else {
                    _erro.value = "Erro ao cancelar agendamento: ${resultado.exceptionOrNull()?.message}"
                }
            } catch (exception: Exception) {
                _erro.value = "Erro inesperado: ${exception.message}"
            } finally {
                _carregando.value = false
            }
        }
    }

    /**
     * Atualiza o status de um agendamento
     * @param agendamentoId ID do agendamento
     * @param novoStatus Novo status a ser definido
     */
    fun atualizarStatusAgendamento(agendamentoId: String, novoStatus: StatusAgendamento) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null

            try {
                val resultado = repositorio.atualizarStatusAgendamento(agendamentoId, novoStatus)

                if (resultado.isSuccess) {
                    _operacaoSucesso.value = "Status atualizado com sucesso!"
                } else {
                    _erro.value = "Erro ao atualizar status: ${resultado.exceptionOrNull()?.message}"
                }
            } catch (exception: Exception) {
                _erro.value = "Erro inesperado: ${exception.message}"
            } finally {
                _carregando.value = false
            }
        }
    }

    /**
     * Filtra agendamentos por status
     * @param status Status para filtrar
     * @return Lista de agendamentos com o status especificado
     */
    fun filtrarPorStatus(status: StatusAgendamento): List<Agendamento> {
        return _agendamentos.value.filter { it.status == status }
    }

    /**
     * Retorna apenas agendamentos ativos (não cancelados)
     */
    fun obterAgendamentosAtivos(): List<Agendamento> {
        return _agendamentos.value.filter { it.status != StatusAgendamento.CANCELADO }
    }

    /**
     * Limpa mensagens de erro e sucesso
     */
    fun limparMensagens() {
        _erro.value = null
        _operacaoSucesso.value = null
    }
}
