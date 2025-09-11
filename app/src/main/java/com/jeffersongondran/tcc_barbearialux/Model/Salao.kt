/**
 * Modelo de dados para representar um salão/barbearia no sistema
 * Usado para armazenar informações sobre as barbearias disponíveis
 */
package com.jeffersongondran.luxconnect.Model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

/**
 * Classe de dados que representa um salão/barbearia
 *
 * @property id Identificador único do salão no Firestore
 * @property nome Nome do salão/barbearia
 * @property endereco Endereço completo do estabelecimento (opcional)
 * @property telefone Telefone para contato do salão
 * @property servicos Lista de serviços oferecidos pelo salão
 * @property horarioFuncionamento Horário de funcionamento do salão
 * @property avaliacaoMedia Avaliação média do salão (0.0 a 5.0)
 * @property ativo Indica se o salão está ativo/disponível para agendamentos
 * @property dataCadastro Data de cadastro do salão no sistema
 */
data class Salao(
    @DocumentId
    val id: String = "",
    val nome: String = "",
    val endereco: String? = null,
    val telefone: String = "",
    val servicos: List<String> = emptyList(), // Lista de serviços oferecidos
    val horarioFuncionamento: String = "",
    val avaliacaoMedia: Double = 0.0,
    val ativo: Boolean = true,
    val dataCadastro: Timestamp = Timestamp.now()
) : Serializable {

    /**
     * Construtor vazio necessário para deserialização do Firestore
     */
    constructor() : this("", "", null, "", emptyList(), "", 0.0, true, Timestamp.now())

    /**
     * Converte o objeto Salao para um Map para salvar no Firestore
     */
    fun paraMap(): Map<String, Any?> {
        return mapOf(
            "nome" to nome,
            "endereco" to endereco,
            "telefone" to telefone,
            "servicos" to servicos,
            "horarioFuncionamento" to horarioFuncionamento,
            "avaliacaoMedia" to avaliacaoMedia,
            "ativo" to ativo,
            "dataCadastro" to dataCadastro
        )
    }

    /**
     * Verifica se o salão oferece um serviço específico
     * @param nomeServico Nome do serviço a verificar
     * @return true se o salão oferece o serviço, false caso contrário
     */
    fun ofereceServico(nomeServico: String): Boolean {
        return servicos.any { it.equals(nomeServico, ignoreCase = true) }
    }
}
