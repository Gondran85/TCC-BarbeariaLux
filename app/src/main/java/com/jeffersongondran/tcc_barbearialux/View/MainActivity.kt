package com.jeffersongondran.tcc_barbearialux.View

// Importações necessárias para o funcionamento da Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
import com.jeffersongondran.tcc_barbearialux.Adapter.BarberAdapter
import com.jeffersongondran.tcc_barbearialux.Model.BarberItem
import com.jeffersongondran.tcc_barbearialux.Viewmodel.MainViewModel
import com.jeffersongondran.tcc_barbearialux.databinding.ActivityMainBinding
import java.io.Serializable

/**
 * MainActivity: Tela principal do aplicativo de barbearia
 *
 * Esta classe é responsável por:
 * - Exibir a lista de barbeiros disponíveis
 * - Permitir navegação para a tela de escolha de serviços
 * - Gerenciar a interface principal do usuário
 *
 * Utiliza o padrão MVVM (Model-View-ViewModel) para separar
 * a lógica de negócio da interface do usuário
 */
class MainActivity : AppCompatActivity() {

    // Constantes para melhor organização e manutenção
    companion object {
        private const val EXTRA_BARBER_ITEM = "BARBER_ITEM"
    }

    // Binding para acessar os elementos da interface de forma segura
    // O lateinit indica que será inicializado antes do primeiro uso
    private lateinit var interfaceBinding: ActivityMainBinding

    // ViewModel responsável por gerenciar os dados da tela
    // O 'by viewModels()' é uma forma moderna de inicializar ViewModels
    private val telaInicialViewModel: MainViewModel by viewModels()

    /**
     * Método chamado quando a Activity é criada
     * É aqui que configuramos toda a interface e funcionalidades
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar componentes essenciais
        inicializarFirebase()
        configurarInterface()
        configurarBotoesNavegacao()
        configurarListaBarbeiros()
        observarDadosDoViewModel()
    }

    /**
     * Inicializa o Firebase - necessário para usar os serviços do Google
     * Como autenticação, banco de dados em tempo real, etc.
     */
    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this)
    }

    /**
     * Configura a interface da tela utilizando View Binding
     * View Binding é uma forma segura de acessar elementos do layout
     */
    private fun configurarInterface() {
        // Infla (carrega) o layout XML e cria o binding
        interfaceBinding = ActivityMainBinding.inflate(layoutInflater)

        // Define o conteúdo da tela como o layout carregado
        setContentView(interfaceBinding.root)
    }

    /**
     * Configura os botões de navegação da tela
     * Separamos em um método próprio para melhor organização
     */
    private fun configurarBotoesNavegacao() {
        configurarBotaoReservarAgora()
        configurarBotaoVoltar()
    }

    /**
     * Configura o botão "Reserve Agora"
     * Quando clicado, leva o usuário para a tela de escolha de serviços
     */
    private fun configurarBotaoReservarAgora() {
        interfaceBinding.btnReserveAgora.setOnClickListener {
            navegarParaEscolhaServico()
        }
    }

    /**
     * Configura o botão de voltar (se existir no layout)
     * O '?' indica que o elemento pode ser nulo (safe call)
     */
    private fun configurarBotaoVoltar() {
        interfaceBinding.btnVoltarHome?.setOnClickListener {
            // Usa o dispatcher moderno para lidar com o botão voltar
            onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Navega para a tela de escolha de serviços
     * Método separado para facilitar reutilização e testes
     */
    private fun navegarParaEscolhaServico(barbeiroSelecionado: BarberItem? = null) {
        val intencaoNavegacao = Intent(this, EscolhaServicoActivity::class.java)

        // Se um barbeiro foi selecionado, passa os dados para a próxima tela
        // BarberItem precisa implementar Serializable para ser passado entre Activities
        barbeiroSelecionado?.let { barbeiro: BarberItem ->
            intencaoNavegacao.putExtra(EXTRA_BARBER_ITEM, barbeiro as Serializable)
        }

        startActivity(intencaoNavegacao)
    }

    /**
     * Configura a lista horizontal de barbeiros (RecyclerView)
     * RecyclerView é um componente eficiente para exibir listas
     */
    private fun configurarListaBarbeiros() {
        with(interfaceBinding.recyclerViewBarbeiros) {
            // Configura o layout manager para exibição horizontal
            layoutManager = criarLayoutManagerHorizontal()

            // Otimização: informa que o tamanho da lista não mudará
            // Isso melhora a performance do RecyclerView
            setHasFixedSize(true)
        }
    }

    /**
     * Cria e configura o layout manager para exibição horizontal
     * Separado em método próprio para melhor legibilidade
     */
    private fun criarLayoutManagerHorizontal(): LinearLayoutManager {
        return LinearLayoutManager(
            this,                           // Contexto da Activity
            LinearLayoutManager.HORIZONTAL, // Orientação horizontal
            false                          // Não reverter a ordem dos itens
        )
    }

    /**
     * Observa as mudanças nos dados fornecidos pelo ViewModel
     * Quando os dados mudam, a interface é automaticamente atualizada
     */
    private fun observarDadosDoViewModel() {
        // Observa a lista de barbeiros disponíveis
        telaInicialViewModel.barberItems.observe(this) { listaDeItens ->
            atualizarListaBarbeiros(listaDeItens)
        }
    }

    /**
     * Atualiza a lista de barbeiros exibida na tela
     *
     * @param listaDeItens: Lista com os dados dos barbeiros a serem exibidos
     */
    private fun atualizarListaBarbeiros(listaDeItens: List<BarberItem>) {
        // Cria um novo adapter com os dados atualizados
        val adaptadorBarbeiros = BarberAdapter(listaDeItens) { barbeiroClicado ->
            // Quando um barbeiro é clicado, navega para escolha de serviços
            // passando os dados do barbeiro selecionado
            navegarParaEscolhaServico(barbeiroClicado)
        }

        // Atribui o novo adapter ao RecyclerView
        interfaceBinding.recyclerViewBarbeiros.adapter = adaptadorBarbeiros
    }
}