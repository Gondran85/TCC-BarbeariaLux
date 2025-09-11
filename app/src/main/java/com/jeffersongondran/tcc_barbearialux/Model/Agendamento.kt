/**
 * Modelo de dados para representar um agendamento no sistema
 * Usado para armazenar informações sobre os agendamentos de serviços
 */
package com.jeffersongondran.luxconnect.Model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

/**
 * Enum para representar o status do agendamento
 */
enum class StatusAgendamento {
    AGENDADO,    // Agendamento confirmado
    CONCLUIDO,   // Serviço realizado
    CANCELADO,   // Agendamento cancelado
    EM_ANDAMENTO // Serviço sendo realizado
}

/**
 * Classe de dados que representa um agendamento de serviço
 *
 * @property id Identificador único do agendamento no Firestore
 * @property usuarioId ID do usuário que fez o agendamento
 * @property salaoId ID do salão onde será realizado o serviço
 * @property tipoServico Tipo de serviço agendado (ex: "Corte de Cabelo")
 * @property horario Data e hora do agendamento
 * @property status Status atual do agendamento
 * @property observacoes Observações adicionais sobre o agendamento (opcional)
 * @property preco Preço do serviço (opcional)
 * @property dataAgendamento Data em que o agendamento foi criado
 */
data class Agendamento(
    @DocumentId
    val id: String = "",
    val usuarioId: String = "",
    val salaoId: String = "",
    val tipoServico: String = "",
    val horario: Timestamp = Timestamp.now(),
    val status: StatusAgendamento = StatusAgendamento.AGENDADO,
    val observacoes: String? = null,
    val preco: Double? = null,
    val dataAgendamento: Timestamp = Timestamp.now()
) : Serializable {

    /**
     * Construtor vazio necessário para deserialização do Firestore
     */
    constructor() : this("", "", "", "", Timestamp.now(), StatusAgendamento.AGENDADO, null, null, Timestamp.now())

    /**
     * Converte o objeto Agendamento para um Map para salvar no Firestore
     */
    fun paraMap(): Map<String, Any?> {
        return mapOf(
            "usuarioId" to usuarioId,
            "salaoId" to salaoId,
            "tipoServico" to tipoServico,
            "horario" to horario,
            "status" to status.name, // Salva o enum como string
            "observacoes" to observacoes,
            "preco" to preco,
            "dataAgendamento" to dataAgendamento
        )
    }

    /**
     * Verifica se o agendamento pode ser cancelado
     * Agendamentos só podem ser cancelados se estiverem com status AGENDADO
     */
    fun podeSerCancelado(): Boolean {
        return status == StatusAgendamento.AGENDADO
    }

    /**
     * Verifica se o agendamento já foi concluído
     */
    fun estaConcluido(): Boolean {
        return status == StatusAgendamento.CONCLUIDO
    }
}
