/**
 * ViewModel responsável por gerenciar dados e operações do usuário
 * Gerencia login, cadastro e perfil do usuário
 */
package com.jeffersongondran.luxconnect.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeffersongondran.luxconnect.Model.Usuario
import com.jeffersongondran.luxconnect.Repository.RepositorioFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar dados do usuário logado
 * Controla operações de cadastro, atualização e busca de usuários
 */
class UsuarioViewModel : ViewModel() {

    private val repositorio = RepositorioFirestore.getInstance()

    // Dados do usuário logado
    private val _usuarioLogado = MutableStateFlow<Usuario?>(null)

    /**
     * Dados do usuário atualmente logado
     */
    val usuarioLogado: StateFlow<Usuario?> = _usuarioLogado.asStateFlow()

    // Estado de carregamento
    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    // Estado de erro
    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    // Estado de sucesso
    private val _operacaoSucesso = MutableStateFlow<String?>(null)
    val operacaoSucesso: StateFlow<String?> = _operacaoSucesso.asStateFlow()

    /**
     * Cadastra um novo usuário no sistema
     * @param usuario Dados do usuário a ser cadastrado
     */
    fun cadastrarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null

            try {
                val resultado = repositorio.adicionarUsuario(usuario)

                if (resultado.isSuccess) {
                    val usuarioId = resultado.getOrNull()
                    // Carrega o usuário recém-criado como usuário logado
                    usuarioId?.let { id ->
                        carregarUsuario(id)
                    }
                    _operacaoSucesso.value = "Usuário cadastrado com sucesso!"
                } else {
                    _erro.value = "Erro ao cadastrar usuário: ${resultado.exceptionOrNull()?.message}"
                }
            } catch (exception: Exception) {
                _erro.value = "Erro inesperado: ${exception.message}"
            } finally {
                _carregando.value = false
            }
        }
    }

    /**
     * Carrega os dados de um usuário pelo ID
     * @param usuarioId ID do usuário a ser carregado
     */
    fun carregarUsuario(usuarioId: String) {
        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null

            try {
                val resultado = repositorio.buscarUsuarioPorId(usuarioId)

                if (resultado.isSuccess) {
                    _usuarioLogado.value = resultado.getOrNull()
                } else {
                    _erro.value = "Erro ao carregar usuário: ${resultado.exceptionOrNull()?.message}"
                }
            } catch (exception: Exception) {
                _erro.value = "Erro inesperado: ${exception.message}"
            } finally {
                _carregando.value = false
            }
        }
    }

    /**
     * Atualiza as informações do usuário logado
     * @param dadosAtualizados Map com os campos a serem atualizados
     */
    fun atualizarPerfil(dadosAtualizados: Map<String, Any>) {
        val usuarioAtual = _usuarioLogado.value
        if (usuarioAtual == null) {
            _erro.value = "Nenhum usuário logado"
            return
        }

        viewModelScope.launch {
            _carregando.value = true
            _erro.value = null

            try {
                val resultado = repositorio.atualizarUsuario(usuarioAtual.id, dadosAtualizados)

                if (resultado.isSuccess) {
                    // Recarrega os dados do usuário para refletir as mudanças
                    carregarUsuario(usuarioAtual.id)
                    _operacaoSucesso.value = "Perfil atualizado com sucesso!"
                } else {
                    _erro.value = "Erro ao atualizar perfil: ${resultado.exceptionOrNull()?.message}"
                }
            } catch (exception: Exception) {
                _erro.value = "Erro inesperado: ${exception.message}"
            } finally {
                _carregando.value = false
            }
        }
    }

    /**
     * Realiza logout do usuário (limpa dados locais)
     */
    fun logout() {
        _usuarioLogado.value = null
        _erro.value = null
        _operacaoSucesso.value = null
    }

    /**
     * Verifica se há um usuário logado
     * @return true se há usuário logado, false caso contrário
     */
    fun temUsuarioLogado(): Boolean {
        return _usuarioLogado.value != null
    }

    /**
     * Obtém o ID do usuário logado
     * @return ID do usuário ou null se não há usuário logado
     */
    fun obterIdUsuarioLogado(): String? {
        return _usuarioLogado.value?.id
    }

    /**
     * Limpa mensagens de erro e sucesso
     */
    fun limparMensagens() {
        _erro.value = null
        _operacaoSucesso.value = null
    }
}
