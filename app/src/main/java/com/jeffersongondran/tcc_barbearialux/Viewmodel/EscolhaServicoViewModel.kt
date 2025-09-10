/**
 * Pacote que contém os ViewModels da aplicação
 * ViewModels são responsáveis por gerenciar os dados da interface do usuário
 */
package com.jeffersongondran.luxconnect.Viewmodel

// Importações necessárias para trabalhar com arquitetura MVVM
import androidx.lifecycle.LiveData // Para observar mudanças nos dados de forma reativa
import androidx.lifecycle.MutableLiveData // Para permitir alterações nos dados observáveis
import androidx.lifecycle.ViewModel // Classe base para ViewModels que sobrevivem a mudanças de configuração

// Importações específicas do projeto
import com.jeffersongondran.luxconnect.Model.BarberItem // Modelo de dados que representa um item de serviço
import com.jeffersongondran.luxconnect.Repository.EscolhaServicoRepository // Repositório que fornece os dados
import kotlin.collections.isNotEmpty

/**
 * ViewModel responsável por gerenciar os dados da tela de escolha de serviços da barbearia
 *
 * IMPORTANTE: O que é um ViewModel?
 * - É uma classe que armazena e gerencia dados relacionados à interface do usuário
 * - Sobrevive a mudanças de configuração (como rotação da tela)
 * - Não deve conter referências diretas para Views (Activities/Fragments)
 * - Segue o padrão MVVM (Model-View-ViewModel)
 */
class EscolhaServicoViewModel : ViewModel() {

    // === DEPENDÊNCIAS ===
    /**
     * Repositório que fornece os dados dos serviços da barbearia
     *
     * Por que usar um repositório?
     * - Separa a lógica de obtenção de dados do ViewModel
     * - Facilita testes unitários (pode ser mockado)
     * - Centraliza o acesso aos dados
     */
    private val repositorioDeServicos = EscolhaServicoRepository()

    // === DADOS PRIVADOS (MUTÁVEIS) ===
    /**
     * Lista privada e mutável dos serviços da barbearia
     *
     * Por que MutableLiveData?
     * - Permite que o ViewModel altere os dados
     * - É thread-safe (seguro para uso em múltiplas threads)
     * - O underscore (_) indica que é uma propriedade privada
     */
    private val _listaDeServicos = MutableLiveData<List<BarberItem>>()

    // === DADOS PÚBLICOS (SOMENTE LEITURA) ===
    /**
     * Lista pública e observável dos serviços da barbearia
     *
     * Por que LiveData (sem Mutable)?
     * - A UI pode apenas OBSERVAR os dados, não alterá-los
     * - Garante que apenas o ViewModel pode modificar os dados
     * - Princípio da responsabilidade única: UI observa, ViewModel gerencia
     */
    val listaDeServicos: LiveData<List<BarberItem>>
        get() = _listaDeServicos

    // === INICIALIZAÇÃO ===
    /**
     * Bloco init é executado quando o ViewModel é criado
     *
     * Por que carregar dados no init?
     * - Garante que os dados estejam disponíveis assim que o ViewModel for criado
     * - A UI já encontrará os dados prontos quando observar o LiveData
     */
    init {
        carregarServicosDisponiveis()
    }

    // === MÉTODOS PRIVADOS ===
    /**
     * Carrega os serviços disponíveis da barbearia através do repositório
     *
     * Por que este método é privado?
     * - Apenas o próprio ViewModel deve controlar quando carregar os dados
     * - Encapsula a lógica interna do ViewModel
     *
     * Como funciona:
     * 1. Chama o repositório para obter a lista de serviços
     * 2. Atualiza o MutableLiveData com os novos dados
     * 3. Automaticamente notifica todos os observadores (como a UI)
     */
    private fun carregarServicosDisponiveis() {
        // Obtém a lista de serviços do repositório e atualiza o LiveData
        _listaDeServicos.value = repositorioDeServicos.obterListaDeServicos()
    }

    // === MÉTODOS PÚBLICOS ===
    /**
     * Método público para recarregar os serviços (caso necessário)
     *
     * Quando usar?
     * - Quando o usuário fizer um "pull to refresh"
     * - Quando voltar de outra tela e quiser dados atualizados
     * - Após uma ação que possa ter alterado os dados
     */
    fun recarregarServicos() {
        carregarServicosDisponiveis()
    }

    /**
     * Método para verificar se há serviços disponíveis
     *
     * @return true se há serviços disponíveis, false caso contrário
     */
    fun possuiServicosDisponiveis(): Boolean {
        return _listaDeServicos.value?.isNotEmpty() == true
    }

    /**
     * Obtém a quantidade de serviços disponíveis
     *
     * @return número de serviços disponíveis
     */
    fun obterQuantidadeDeServicos(): Int {
        return _listaDeServicos.value?.size ?: 0
    }
}