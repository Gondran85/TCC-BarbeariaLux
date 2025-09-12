package com.jeffersongondran.luxconnect.View

// Importações necessárias para o funcionamento da Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
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
        configurarBottomNavigation()
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
     * Configuração do BottomNavigationView com boas práticas de UX/A11y
     * - Debounce de cliques para evitar navegação dupla
     * - Re-seleção rola ao topo da lista
     * - Tooltip e contentDescription baseadas no título do item
     * - Mantém estado visual padrão do Material
     * Observação: Projeto atual sem NavGraph/Fragments; ação local.
     */
    private fun configurarBottomNavigation() {
        val bottom = interfaceBinding.bottomNavigationView

        // Define o item padrão como "Home" se existir no menu atual
        val defaultItemId = com.jeffersongondran.luxconnect.R.id.action_lista
        if (bottom.menu.findItem(defaultItemId) != null) {
            bottom.selectedItemId = defaultItemId
        }

        // Acessibilidade: aplicar contentDescription/tooltip a partir do título do menu
        val menu = bottom.menu
        val size = menu.size()
        for (i in 0 until size) {
            val item = menu.getItem(i)
            val title = item.title
            MenuItemCompat.setContentDescription(item, title)
            MenuItemCompat.setTooltipText(item, title)
        }

        // Debounce local (sem variáveis globais)
        var lastClickAt = 0L
        val debounceMs = 500L

        bottom.setOnItemSelectedListener { item ->
            val now = SystemClock.elapsedRealtime()
            if (now - lastClickAt < debounceMs) return@setOnItemSelectedListener false
            lastClickAt = now

            // Feedback tátil sutil
            bottom.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

            when (item.itemId) {
                // Favoritos
                com.jeffersongondran.luxconnect.R.id.action_home -> {
                    exibirConteudoDinamico(
                        tag = "favoritos",
                        layoutRes = com.jeffersongondran.luxconnect.R.layout.tela_favoritos
                    )
                }
                // Procurar
                com.jeffersongondran.luxconnect.R.id.action_carrinho -> {
                    exibirConteudoDinamico(
                        tag = "procurar",
                        layoutRes = com.jeffersongondran.luxconnect.R.layout.tela_procurar
                    )
                }
                // Home
                com.jeffersongondran.luxconnect.R.id.action_lista -> {
                    exibirHome()
                }
                // Histórico
                com.jeffersongondran.luxconnect.R.id.action_search -> {
                    exibirConteudoDinamico(
                        tag = "historico",
                        layoutRes = com.jeffersongondran.luxconnect.R.layout.tela_historico
                    )
                }
                // Perfil
                com.jeffersongondran.luxconnect.R.id.action_profile -> {
                    exibirConteudoDinamico(
                        tag = "perfil",
                        layoutRes = com.jeffersongondran.luxconnect.R.layout.tela_perfil
                    )
                }
                else -> return@setOnItemSelectedListener false
            }
            true
        }

        bottom.setOnItemReselectedListener {
            rolarAoTopo()
        }
    }

    // Exibe a Home (NestedScrollView principal) e oculta o container dinâmico
    private fun exibirHome() {
        interfaceBinding.nestedScrollView.isVisible = true
        interfaceBinding.conteudoDinamico?.isVisible = false
    }

    // Infla/mostra uma tela no container dinâmico preservando o estado das demais
    private fun exibirConteudoDinamico(tag: String, layoutRes: Int) {
        val container = interfaceBinding.conteudoDinamico

        // Garante que a Home fique oculta e o container visível
        interfaceBinding.nestedScrollView.isVisible = false
        container?.isVisible = true

        // Esconde todos os filhos atuais
        container?.children?.forEach { it.visibility = View.GONE }

        // Procura uma view já inflada com a mesma tag para reaproveitar estado
        val existente = container?.findViewWithTag<View>(tag)
        if (existente != null) {
            existente.visibility = View.VISIBLE
            return
        }

        // Infla e adiciona a nova view com a tag para cache
        val inflada = LayoutInflater.from(this).inflate(layoutRes, container, false)
        inflada.tag = tag
        container?.addView(inflada, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
    }

    // Rola para o topo da tela visível (Home ou conteúdo dinâmico)
    private fun rolarAoTopo() {
        if (interfaceBinding.nestedScrollView.isVisible) {
            interfaceBinding.nestedScrollView.smoothScrollTo(0, 0)
            return
        }
        // Tenta encontrar um NestedScrollView visível dentro do container dinâmico
        val container = interfaceBinding.conteudoDinamico
        val scrollVisivel = container?.children?.firstOrNull { it.visibility == View.VISIBLE }
        if (scrollVisivel is androidx.core.widget.NestedScrollView) {
            scrollVisivel.smoothScrollTo(0, 0)
        } else {
            // Caso o layout raiz não seja NestedScrollView, tenta achar um descendente
            val candidato = scrollVisivel?.findViewById<androidx.core.widget.NestedScrollView>(
                com.jeffersongondran.luxconnect.R.id.scroll_favoritos
            ) ?: scrollVisivel?.findViewById(
                com.jeffersongondran.luxconnect.R.id.scroll_procurar
            ) ?: scrollVisivel?.findViewById(
                com.jeffersongondran.luxconnect.R.id.scroll_historico
            ) ?: scrollVisivel?.findViewById(
                com.jeffersongondran.luxconnect.R.id.scroll_perfil
            )
            candidato?.smoothScrollTo(0, 0)
        }
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
     */
    private fun configurarBotaoVoltar() {
        interfaceBinding.btnVoltarHome?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Configura o botão de logout/voltar ao login
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                val textoPesquisa = s?.toString() ?: ""
                telaInicialViewModel.pesquisarBarbearias(textoPesquisa)
            }
        })
    }

    /**
     * Navega para a tela de escolha de serviços
     */
    private fun navegarParaEscolhaServico(barbeiroSelecionado: BarberItem? = null) {
        val intencaoNavegacao = Intent(this, EscolhaServicoActivity::class.java)
        barbeiroSelecionado?.let { barbeiro: BarberItem ->
            intencaoNavegacao.putExtra(EXTRA_BARBER_ITEM, barbeiro as Serializable)
        }
        startActivity(intencaoNavegacao)
    }

    /**
     * Volta para a tela de login
     */
    private fun voltarParaLogin() {
        val intentLogin = Intent(this, LoginActivity::class.java)
        intentLogin.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentLogin)
        finish()
    }

    /**
     * Configura a lista horizontal de barbeiros (RecyclerView)
     */
    private fun configurarListaBarbeiros() {
        with(interfaceBinding.recyclerViewBarbeiros) {
            layoutManager = criarLayoutManagerHorizontal()
            setHasFixedSize(true)
        }
    }

    /**
     * Cria e configura o layout manager para exibição horizontal
     */
    private fun criarLayoutManagerHorizontal(): LinearLayoutManager {
        return LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    /**
     * Observa as mudanças nos dados fornecidos pelo ViewModel
     */
    private fun observarDadosDoViewModel() {
        telaInicialViewModel.barberItems.observe(this) { listaDeItens ->
            atualizarListaBarbeiros(listaDeItens)
        }
        telaInicialViewModel.userName.observe(this) { nomeUsuario ->
            atualizarMensagemBoasVindas(nomeUsuario)
        }
    }

    /**
     * Atualiza a mensagem de boas-vindas com o nome do usuário
     */
    private fun atualizarMensagemBoasVindas(nomeUsuario: String) {
        val mensagemPersonalizada = "Bem-vindo, $nomeUsuario!"
        interfaceBinding.textWelcome.text = mensagemPersonalizada
        Log.d("MainActivity", "Mensagem de boas-vindas atualizada para: $mensagemPersonalizada")
    }

    /**
     * Atualiza a lista de barbeiros exibida na tela
     */
    private fun atualizarListaBarbeiros(listaDeItens: List<BarberItem>) {
        val adaptadorBarbeiros = BarberAdapter(listaDeItens) { barbeiroClicado ->
            navegarParaEscolhaServico(barbeiroClicado)
        }
        interfaceBinding.recyclerViewBarbeiros.adapter = adaptadorBarbeiros
    }

}