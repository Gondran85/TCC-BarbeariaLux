/**
 * Repositório responsável por todas as operações de banco de dados com Firebase Firestore
 * Implementa operações CRUD para usuários, salões e agendamentos
 */
package com.jeffersongondran.luxconnect.Repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jeffersongondran.luxconnect.LuxConnectApplication
import com.jeffersongondran.luxconnect.Model.Agendamento
import com.jeffersongondran.luxconnect.Model.Salao
import com.jeffersongondran.luxconnect.Model.StatusAgendamento
import com.jeffersongondran.luxconnect.Model.Usuario
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Classe responsável por gerenciar todas as operações com o Firebase Firestore
 * Utiliza padrão singleton e operações assíncronas com coroutines
 */
class RepositorioFirestore {

    companion object {
        // Nomes das coleções no Firestore
        private const val COLECAO_USUARIOS = "usuarios"
        private const val COLECAO_SALOES = "saloes"
        private const val COLECAO_AGENDAMENTOS = "agendamentos"

        // Instância singleton do repositório
        @Volatile
        private var INSTANCE: RepositorioFirestore? = null

        /**
         * Retorna a instância singleton do repositório
         */
        fun getInstance(): RepositorioFirestore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RepositorioFirestore().also { INSTANCE = it }
            }
        }
    }

    // Instância do Firestore obtida de forma segura
    private val firestore: FirebaseFirestore = LuxConnectApplication.obterFirestore()

    // OPERAÇÕES COM USUÁRIOS

    /**
     * Adiciona um novo usuário ao Firestore
     * @param usuario Objeto Usuario a ser salvo
     * @return Result<String> com o ID do usuário criado ou erro
     */
    suspend fun adicionarUsuario(usuario: Usuario): Result<String> {
        return try {
            // Adiciona o usuário à coleção e aguarda a operação
            val documentReference = firestore.collection(COLECAO_USUARIOS)
                .add(usuario.paraMap())
                .await()

            // Retorna sucesso com o ID do documento criado
            Result.success(documentReference.id)
        } catch (exception: Exception) {
            // Retorna erro em caso de falha
            Result.failure(exception)
        }
    }

    /**
     * Busca um usuário pelo ID
     * @param usuarioId ID do usuário a ser buscado
     * @return Result<Usuario?> com o usuário encontrado ou null se não existir
     */
    suspend fun buscarUsuarioPorId(usuarioId: String): Result<Usuario?> {
        return try {
            val documento = firestore.collection(COLECAO_USUARIOS)
                .document(usuarioId)
                .get()
                .await()

            // Converte o documento para objeto Usuario
            val usuario = documento.toObject(Usuario::class.java)
            Result.success(usuario)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    /**
     * Atualiza as informações de um usuário
     * @param usuarioId ID do usuário a ser atualizado
     * @param dadosAtualizados Map com os campos a serem atualizados
     * @return Result<Unit> indicando sucesso ou erro
     */
    suspend fun atualizarUsuario(usuarioId: String, dadosAtualizados: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection(COLECAO_USUARIOS)
                .document(usuarioId)
                .update(dadosAtualizados)
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    // OPERAÇÕES COM SALÕES

    /**
     * Observa todos os salões ativos em tempo real
     * @return Flow<List<Salao>> que emite a lista atualizada de salões
     */
    fun listarSaloes(): Flow<List<Salao>> = callbackFlow {
        // Cria um listener para mudanças na coleção de salões
        val listener = firestore.collection(COLECAO_SALOES)
            .whereEqualTo("ativo", true) // Filtra apenas salões ativos
            .orderBy("nome") // Ordena por nome
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Em caso de erro, fecha o flow com exceção
                    close(error)
                    return@addSnapshotListener
                }

                // Converte os documentos para objetos Salao
                val saloes = snapshot?.documents?.mapNotNull { documento ->
                    documento.toObject(Salao::class.java)?.copy(id = documento.id)
                } ?: emptyList()

                // Emite a lista atualizada
                trySend(saloes)
            }

        // Remove o listener quando o flow é cancelado
        awaitClose { listener.remove() }
    }

    /**
     * Busca um salão pelo ID
     * @param salaoId ID do salão a ser buscado
     * @return Result<Salao?> com o salão encontrado ou null
     */
    suspend fun buscarSalaoPorId(salaoId: String): Result<Salao?> {
        return try {
            val documento = firestore.collection(COLECAO_SALOES)
                .document(salaoId)
                .get()
                .await()

            val salao = documento.toObject(Salao::class.java)?.copy(id = documento.id)
            Result.success(salao)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    /**
     * Adiciona um novo salão (geralmente usado apenas por administradores)
     * @param salao Objeto Salao a ser salvo
     * @return Result<String> com o ID do salão criado
     */
    suspend fun adicionarSalao(salao: Salao): Result<String> {
        return try {
            val documentReference = firestore.collection(COLECAO_SALOES)
                .add(salao.paraMap())
                .await()
            Result.success(documentReference.id)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    // OPERAÇÕES COM AGENDAMENTOS

    /**
     * Cria um novo agendamento
     * @param agendamento Objeto Agendamento a ser salvo
     * @return Result<String> com o ID do agendamento criado
     */
    suspend fun criarAgendamento(agendamento: Agendamento): Result<String> {
        return try {
            val documentReference = firestore.collection(COLECAO_AGENDAMENTOS)
                .add(agendamento.paraMap())
                .await()
            Result.success(documentReference.id)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    /**
     * Observa os agendamentos de um usuário em tempo real
     * @param usuarioId ID do usuário
     * @return Flow<List<Agendamento>> que emite a lista atualizada de agendamentos
     */
    fun listarAgendamentosPorUsuario(usuarioId: String): Flow<List<Agendamento>> = callbackFlow {
        val listener = firestore.collection(COLECAO_AGENDAMENTOS)
            .whereEqualTo("usuarioId", usuarioId)
            .orderBy("horario", Query.Direction.DESCENDING) // Mais recentes primeiro
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                // Converte documentos para objetos Agendamento
                val agendamentos = snapshot?.documents?.mapNotNull { documento ->
                    try {
                        documento.toObject(Agendamento::class.java)?.copy(id = documento.id)
                    } catch (e: Exception) {
                        null // Ignora documentos com erro de conversão
                    }
                } ?: emptyList()

                trySend(agendamentos)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Observa os agendamentos de um salão em tempo real
     * @param salaoId ID do salão
     * @return Flow<List<Agendamento>> que emite a lista atualizada de agendamentos
     */
    fun listarAgendamentosPorSalao(salaoId: String): Flow<List<Agendamento>> = callbackFlow {
        val listener = firestore.collection(COLECAO_AGENDAMENTOS)
            .whereEqualTo("salaoId", salaoId)
            .orderBy("horario")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val agendamentos = snapshot?.documents?.mapNotNull { documento ->
                    try {
                        documento.toObject(Agendamento::class.java)?.copy(id = documento.id)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(agendamentos)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Atualiza o status de um agendamento
     * @param agendamentoId ID do agendamento
     * @param novoStatus Novo status a ser definido
     * @return Result<Unit> indicando sucesso ou erro
     */
    suspend fun atualizarStatusAgendamento(agendamentoId: String, novoStatus: StatusAgendamento): Result<Unit> {
        return try {
            firestore.collection(COLECAO_AGENDAMENTOS)
                .document(agendamentoId)
                .update("status", novoStatus.name)
                .await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    /**
     * Cancela um agendamento (atualiza status para CANCELADO)
     * @param agendamentoId ID do agendamento a ser cancelado
     * @return Result<Unit> indicando sucesso ou erro
     */
    suspend fun cancelarAgendamento(agendamentoId: String): Result<Unit> {
        return atualizarStatusAgendamento(agendamentoId, StatusAgendamento.CANCELADO)
    }

    /**
     * Busca um agendamento pelo ID
     * @param agendamentoId ID do agendamento
     * @return Result<Agendamento?> com o agendamento encontrado ou null
     */
    suspend fun buscarAgendamentoPorId(agendamentoId: String): Result<Agendamento?> {
        return try {
            val documento = firestore.collection(COLECAO_AGENDAMENTOS)
                .document(agendamentoId)
                .get()
                .await()

            val agendamento = documento.toObject(Agendamento::class.java)?.copy(id = documento.id)
            Result.success(agendamento)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
