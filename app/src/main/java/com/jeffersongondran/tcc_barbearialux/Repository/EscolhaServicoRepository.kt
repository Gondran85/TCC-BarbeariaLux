/**
 * Pacote que contém as classes responsáveis por fornecer dados para a aplicação
 * Repository Pattern: Camada de abstração entre a fonte de dados e a lógica de negócio
 */
package com.jeffersongondran.tcc_barbearialux.Repository

import com.jeffersongondran.tcc_barbearialux.Model.BarberItem
import com.jeffersongondran.tcc_barbearialux.R

/**
 * Repository responsável por gerenciar os dados dos serviços de barbearia
 *
 * Esta classe segue o padrão Repository, que tem como objetivo:
 * - Centralizar a lógica de acesso aos dados
 * - Abstrair a fonte de dados (pode vir de API, banco local, etc.)
 * - Facilitar testes unitários
 * - Manter o código organizado e limpo
 */
class EscolhaServicoRepository {

    /**
     * Objeto companion para armazenar constantes da classe
     * Equivalent ao 'static' em Java - pertence à classe, não à instância
     */
    companion object {
        // Constantes para os nomes das barbearias - facilita manutenção
        private const val NOME_CLUBE_CAVALHEIROS = "CLUBE DE CAVALHEIROS"
        private const val NOME_BARBEARIA_STYLE = "BARBEARIA STYLE"

        // Constantes para descrições padronizadas
        private const val DESCRICAO_HORARIO_DISPONIVEL = "Horário disponível"

        // Constantes para horários de funcionamento
        private const val HORARIO_FUNCIONAMENTO_PADRAO = "10am - 10pm"
    }

    /**
     * Método principal que retorna a lista completa de serviços disponíveis
     *
     * Este método é público e será chamado por outras camadas da aplicação
     * (como ViewModels ou Activities) para obter os dados dos serviços
     *
     * @return List<BarberItem> Lista imutável contendo todos os serviços disponíveis
     */
    fun obterListaDeServicos(): List<BarberItem> {
        return criarListaDeServicos()
    }

    /**
     * Método privado responsável por criar e configurar a lista de serviços
     *
     * Separar a criação da lista em um método privado oferece vantagens:
     * - Melhor organização do código
     * - Facilita futuras modificações
     * - Torna o código mais testável
     *
     * @return List<BarberItem> Lista configurada com todos os serviços
     */
    private fun criarListaDeServicos(): List<BarberItem> {
        return listOf(
            // Primeiro serviço - Clube de Cavalheiros (Imagem 1)
            criarItemServico(
                nome = NOME_CLUBE_CAVALHEIROS,
                descricao = DESCRICAO_HORARIO_DISPONIVEL,
                horario = HORARIO_FUNCIONAMENTO_PADRAO,
                imagemId = R.drawable.lucid_realism_create_a_photograph_of_a_bustling_barbershop_wit_0
            ),

            // Segundo serviço - Barbearia Style
            criarItemServico(
                nome = NOME_BARBEARIA_STYLE,
                descricao = DESCRICAO_HORARIO_DISPONIVEL,
                horario = HORARIO_FUNCIONAMENTO_PADRAO,
                imagemId = R.drawable.lucid_realism_a_vibrant_hairdressing_and_barber_salon_with_mod_3
            ),

            // Terceiro serviço - Clube de Cavalheiros (Imagem 2)
            criarItemServico(
                nome = NOME_CLUBE_CAVALHEIROS,
                descricao = DESCRICAO_HORARIO_DISPONIVEL,
                horario = HORARIO_FUNCIONAMENTO_PADRAO,
                imagemId = R.drawable.lucid_realism_a_highly_detailed_and_super_realistic_depiction_1
            ),

            // Quarto serviço - Clube de Cavalheiros (Imagem 3)
            criarItemServico(
                nome = NOME_CLUBE_CAVALHEIROS,
                descricao = DESCRICAO_HORARIO_DISPONIVEL,
                horario = HORARIO_FUNCIONAMENTO_PADRAO,
                imagemId = R.drawable.lucid_realism_a_surreal_and_vibrant_cinematic_photo_of_a_barbe_2
            )
        )
    }

    /**
     * Método auxiliar para criar um item de serviço de forma consistente
     *
     * Este método factory oferece várias vantagens:
     * - Garante que todos os itens sejam criados da mesma forma
     * - Facilita futuras validações ou transformações
     * - Melhora a legibilidade do código
     * - Reduz duplicação de código
     *
     * @param nome Nome do serviço/barbearia
     * @param descricao Descrição do serviço
     * @param horario Horário de funcionamento
     * @param imagemId ID do recurso de imagem
     * @return BarberItem Objeto configurado e pronto para uso
     */
    private fun criarItemServico(
        nome: String,
        descricao: String,
        horario: String,
        imagemId: Int
    ): BarberItem {
        return BarberItem(
            nomeDoServico = nome,
            descricaoDoServico = descricao,
            horarioFuncionamento = horario,
            imagemDoServico = imagemId
        )
    }

    /**
     * Método para obter apenas serviços de uma barbearia específica
     *
     * Este método demonstra como podemos estender a funcionalidade
     * do repository de forma limpa e organizada
     *
     * @param nomeBarbearia Nome da barbearia para filtrar
     * @return List<BarberItem> Lista filtrada de serviços
     */
    fun obterServicosPorBarbearia(nomeBarbearia: String): List<BarberItem> {
        return obterListaDeServicos().filter { servico ->
            servico.nomeDoServico.equals(nomeBarbearia, ignoreCase = true)
        }
    }

    /**
     * Método para verificar se existem serviços disponíveis
     *
     * @return Boolean true se houver pelo menos um serviço disponível
     */
    fun possuiServicosDisponiveis(): Boolean {
        return obterListaDeServicos().isNotEmpty()
    }
}