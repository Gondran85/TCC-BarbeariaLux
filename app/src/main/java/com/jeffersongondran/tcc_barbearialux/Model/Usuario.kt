/**
 * Modelo de dados para representar um usuário no sistema
 * Usado para armazenar informações básicas do cliente da barbearia
 */
package com.jeffersongondran.luxconnect.Model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

/**
 * Classe de dados que representa um usuário/cliente da barbearia
 *
 * @property id Identificador único do usuário no Firestore (gerado automaticamente)
 * @property nome Nome completo do usuário
 * @property email Email do usuário para login e comunicação
 * @property telefone Telefone para contato (opcional)
 * @property dataCadastro Data e hora do cadastro do usuário
 */
data class Usuario(
    @DocumentId
    val id: String = "", // ID gerado automaticamente pelo Firestore
    val nome: String = "",
    val email: String = "",
    val telefone: String? = null, // Campo opcional
    val dataCadastro: Timestamp = Timestamp.now()
) : Serializable {

    /**
     * Construtor vazio necessário para deserialização do Firestore
     * O Firestore precisa deste construtor para criar objetos a partir dos dados do banco
     */
    constructor() : this("", "", "", null, Timestamp.now())

    /**
     * Converte o objeto Usuario para um Map para salvar no Firestore
     * Remove o campo 'id' pois ele é gerenciado automaticamente pelo Firestore
     */
    fun paraMap(): Map<String, Any?> {
        return mapOf(
            "nome" to nome,
            "email" to email,
            "telefone" to telefone,
            "dataCadastro" to dataCadastro
        )
    }
}
