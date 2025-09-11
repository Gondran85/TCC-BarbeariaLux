package com.jeffersongondran.luxconnect.View

// Importações necessárias para o funcionamento da Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore
import com.jeffersongondran.luxconnect.Adapter.BarberAdapter
import com.jeffersongondran.luxconnect.Model.BarberItem
import com.jeffersongondran.luxconnect.Viewmodel.MainViewModel
import com.jeffersongondran.luxconnect.databinding.ActivityMainBinding
import java.io.Serializable

/**
 * MainActivity: Tela principal do aplicativo de barbearia
 *
 * Esta classe é responsável por:
 * - Exibir a lista de barbeiros disponíveis
 * - Permitir navegação para a tela de escolha de serviços
 * - Gerenciar a interface principal do usuário
 * - Implementar funcionalidade de pesquisa em tempo real
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
        configurarCampoPesquisa()
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
        configurarBotaoVoltarLogin()
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
     * Configura o botão de logout/voltar ao login
     * Quando clicado, leva o usuário de volta para a tela de login
     */
    private fun configurarBotaoVoltarLogin() {
        interfaceBinding.btnVoltarLogin?.setOnClickListener {
            voltarParaLogin()
        }
    }

    /**
     * Configura o campo de pesquisa com funcionalidade de filtragem em tempo real
     */
    private fun configurarCampoPesquisa() {
        interfaceBinding.editTextPesquisa.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Não precisamos fazer nada antes da mudança do texto
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Não precisamos fazer nada durante a mudança do texto
            }

            override fun afterTextChanged(s: Editable?) {
                // Após o usuário digitar, filtra a lista de barbearias
                val textoPesquisa = s?.toString() ?: ""
                telaInicialViewModel.pesquisarBarbearias(textoPesquisa)
            }
        })
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
     * Volta para a tela de login
     * Método responsável por fazer o logout do usuário e navegar para a LoginActivity
     */
    private fun voltarParaLogin() {
        // Cria intent para navegar para a tela de login
        val intentLogin = Intent(this, LoginActivity::class.java)

        // Limpa toda a pilha de activities (FLAG_CLEAR_TASK | FLAG_NEW_TASK)
        // Isso garante que o usuário não possa voltar para MainActivity pressionando voltar
        intentLogin.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Inicia a LoginActivity
        startActivity(intentLogin)

        // Finaliza a MainActivity atual
        finish()
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

        // Observa o nome do usuário e atualiza a mensagem de boas-vindas
        telaInicialViewModel.userName.observe(this) { nomeUsuario ->
            atualizarMensagemBoasVindas(nomeUsuario)
        }
    }

    /**
     * Atualiza a mensagem de boas-vindas com o nome do usuário
     * @param nomeUsuario Nome do usuário a ser exibido
     */
    private fun atualizarMensagemBoasVindas(nomeUsuario: String) {
        val mensagemPersonalizada = "Bem-vindo, $nomeUsuario!"
        interfaceBinding.textWelcome.text = mensagemPersonalizada

        Log.d("MainActivity", "Mensagem de boas-vindas atualizada para: $mensagemPersonalizada")
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