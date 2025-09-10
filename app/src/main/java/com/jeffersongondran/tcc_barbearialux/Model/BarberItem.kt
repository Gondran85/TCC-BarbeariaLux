/**
 * Pacote que contém as classes de modelo de dados da aplicação
 * Estas classes representam as entidades de negócio do sistema
 */
package com.jeffersongondran.luxconnect.Model

import java.io.Serializable

/**
 * Classe de dados que representa um item/serviço da barbearia
 *
 * Esta classe é usada para armazenar informações sobre os serviços oferecidos
 * pela barbearia, como nome do serviço, horários de funcionamento e imagem.
 *
 * A implementação de Serializable permite que objetos desta classe sejam
 * passados entre diferentes telas (Activities/Fragments) do aplicativo.
 *
 * @property nomeDoServico Nome do serviço oferecido (ex: "Corte de Cabelo")
 * @property descricaoDoServico Descrição ou subtítulo do serviço
 * @property horarioFuncionamento Horário em que o serviço está disponível
 * @property imagemDoServico ID do recurso de imagem que representa o serviço
 */
data class BarberItem(
    val nomeDoServico: String,
    val descricaoDoServico: String,
    val horarioFuncionamento: String,
    val imagemDoServico: Int
) : Serializable {

    /**
     * Construtor secundário para facilitar a criação de objetos
     * quando nem todos os dados estão disponíveis
     */
    constructor() : this(
        nomeDoServico = "",
        descricaoDoServico = "",
        horarioFuncionamento = "",
        imagemDoServico = 0
    )

    /**
     * Verifica se o item possui dados válidos
     * @return true se todos os campos obrigatórios estão preenchidos
     */
    fun isValido(): Boolean {
        return nomeDoServico.isNotBlank() &&
               descricaoDoServico.isNotBlank() &&
               horarioFuncionamento.isNotBlank() &&
               imagemDoServico != 0
    }

    /**
     * Retorna uma representação em texto do objeto para facilitar debug
     * @return String formatada com as informações do serviço
     */
    override fun toString(): String {
        return "Serviço: $nomeDoServico | Descrição: $descricaoDoServico | Horário: $horarioFuncionamento"
    }
}
