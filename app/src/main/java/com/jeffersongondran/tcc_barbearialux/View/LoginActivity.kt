package com.jeffersongondran.luxconnect.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.jeffersongondran.luxconnect.R
import com.jeffersongondran.luxconnect.databinding.ActivityLoginBinding


/**
 * Activity responsável pela tela de login da aplicação.
 *
 * Esta classe gerencia toda a lógica de autenticação do usuário,
 * incluindo validação de dados e comunicação com o Firebase.
 *
 * Principais funcionalidades:
 * - Validação de email e senha
 * - Autenticação com Firebase
 * - Navegação para outras telas
 * - Tratamento de erros de login
 */
class LoginActivity : AppCompatActivity() {

    // ===========================================
    // PROPRIEDADES DA CLASSE
    // ===========================================

    /**
     * Binding para acessar os elementos da interface de forma segura.
     * O View Binding é uma forma moderna e segura de acessar views
     * sem usar findViewById()
     */
    private lateinit var binding: ActivityLoginBinding

    /**
     * Instância do Firebase Authentication para gerenciar login/logout
     */
    private lateinit var autenticadorFirebase: FirebaseAuth

    /**
     * Constante para identificar logs desta Activity.
     * Usamos companion object para criar uma "variável estática"
     * que pode ser acessada sem instanciar a classe.
     */
    companion object {
        private const val TAG_LOG = "LoginActivity"
    }

    // ===========================================
    // CICLO DE VIDA DA ACTIVITY
    // ===========================================

    /**
     * Método chamado quando a Activity é criada.
     * É aqui que inicializamos todos os componentes necessários.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o View Binding para acessar as views
        configurarViewBinding()

        // Configura o Firebase Authentication
        inicializarFirebaseAuth()

        // Configura os listeners dos botões e campos
        configurarEventosDeClique()
    }

    // ===========================================
    // CONFIGURAÇÃO INICIAL
    // ===========================================

    /**
     * Configura o View Binding para esta Activity.
     *
     * O View Binding gera automaticamente uma classe que contém
     * referências para todas as views do layout XML.
     */
    private fun configurarViewBinding() {
        try {
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)
            Log.d(TAG_LOG, "View Binding configurado com sucesso")
        } catch (erro: Exception) {
            Log.e(TAG_LOG, "Erro ao configurar View Binding", erro)
            exibirMensagem("Erro ao carregar a tela. Tente reiniciar o app.")
        }
    }

    /**
     * Inicializa o Firebase Authentication.
     *
     * O Firebase Auth é o serviço que gerencia autenticação de usuários
     * (login, logout, criação de contas, etc.)
     */
    private fun inicializarFirebaseAuth() {
        try {
            autenticadorFirebase = FirebaseAuth.getInstance()
            Log.d(TAG_LOG, "Firebase Authentication inicializado com sucesso")
        } catch (erro: Exception) {
            Log.e(TAG_LOG, "Erro ao inicializar Firebase Auth", erro)
            exibirMensagem("Erro ao inicializar sistema de login. Verifique sua conexão.")
        }
    }

    /**
     * Configura todos os eventos de clique da tela.
     *
     * Separamos esta lógica em um método próprio para manter
     * o onCreate() limpo e organizado.
     */
    private fun configurarEventosDeClique() {
        configurarBotaoVoltar()
        configurarBotaoEntrar()
        configurarLinkEsqueceuSenha()
        configurarLinkCadastro()
        //configurarRedirecionamentoCadastro()
    }

    // ===========================================
    // CONFIGURAÇÃO DE EVENTOS DE CLIQUE
    // ===========================================

    /**
     * Configura o comportamento do botão "Voltar".
     * Quando clicado, fecha a tela atual.
     */
    private fun configurarBotaoVoltar() {
        binding.btnVoltarLogin.setOnClickListener {
            Log.d(TAG_LOG, "Usuário clicou em voltar")
            finish() // Fecha a Activity atual
        }
    }

    /**
     * Configura o comportamento do botão "Entrar".
     * Quando clicado, inicia o processo de login.
     */
    private fun configurarBotaoEntrar() {
        binding.btnEntrar.setOnClickListener {
            Log.d(TAG_LOG, "Usuário clicou em entrar")
            executarProcessoDeLogin()
        }
    }

    /**
     * Configura o link "Esqueceu a senha?".
     * Por enquanto apenas exibe uma mensagem informativa.
     */
    private fun configurarLinkEsqueceuSenha() {
        binding.forgotPasswordText.setOnClickListener {
            Log.d(TAG_LOG, "Usuário clicou em 'esqueceu a senha'")
            tratarEsqueceuSenha()
        }
    }

    /**
     * Configura o redirecionamento para a tela de cadastro.
     * Usamos um clique longo como exemplo (pode ser adaptado conforme necessário).
     */
   /* private fun configurarRedirecionamentoCadastro() {
        binding.forgotPasswordText.setOnLongClickListener {
            Log.d(TAG_LOG, "Usuário fez clique longo - redirecionando para cadastro")
            redirecionarParaTelaDeCadastro()
            true // Indica que o evento foi tratado
        }
    }*/

    /**
     * Configura o clique no texto "Não tem conta? Cadastre-se"
     * para redirecionar para a tela de cadastro (SignUpActivity).
     */
    private fun configurarLinkCadastro() {
        // Supondo que o ID do seu TextView no XML seja "naoTemContaTextView"
        binding.naoTemContaTextView?.setOnClickListener {
            Log.d(TAG_LOG, "Usuário clicou para se cadastrar, abrindo SignUpActivity")
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    // ===========================================
    // LÓGICA DE AUTENTICAÇÃO
    // ===========================================

    /**
     * Executa todo o processo de login do usuário.
     *
     * Este método coordena as etapas:
     * 1. Coletar dados dos campos
     * 2. Validar os dados
     * 3. Tentar autenticar no Firebase
     */
    private fun executarProcessoDeLogin() {
        // Coleta os dados digitados pelo usuário
        val emailDigitado = obterEmailDigitado()
        val senhaDigitada = obterSenhaDigitada()

        Log.d(TAG_LOG, "Iniciando processo de login para: $emailDigitado")

        // Valida se os dados estão corretos antes de tentar o login
        if (validarDadosDeEntrada(emailDigitado, senhaDigitada)) {
            desabilitarBotaoEntrar() // Evita múltiplos cliques
            autenticarComFirebase(emailDigitado, senhaDigitada)
        }
    }

    /**
     * Obtém o email digitado pelo usuário, removendo espaços extras.
     */
    private fun obterEmailDigitado(): String {
        return binding.editTextEmail.text.toString().trim()
    }

    /**
     * Obtém a senha digitada pelo usuário, removendo espaços extras.
     */
    private fun obterSenhaDigitada(): String {
        return binding.editTextPassword.text.toString().trim()
    }

    /**
     * Desabilita o botão "Entrar" para evitar múltiplos cliques.
     */
    private fun desabilitarBotaoEntrar() {
        binding.btnEntrar.isEnabled = false
    }

    /**
     * Reabilita o botão "Entrar".
     */
    private fun reabilitarBotaoEntrar() {
        binding.btnEntrar.isEnabled = true
    }

    /**
     * Autentica o usuário usando o Firebase Authentication.
     *
     * O Firebase fará a verificação do email e senha nos seus servidores
     * e retornará o resultado através de um callback.
     */
    private fun autenticarComFirebase(email: String, senha: String) {
        Log.d(TAG_LOG, "Enviando credenciais para o Firebase: $email")

        autenticadorFirebase.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { tarefaDeAutenticacao ->
                // Reabilita o botão independente do resultado
                reabilitarBotaoEntrar()

                if (tarefaDeAutenticacao.isSuccessful) {
                    // Login deu certo
                    Log.i(TAG_LOG, "Login realizado com sucesso para: $email")
                    tratarLoginBemSucedido()
                } else {
                    // Login falhou
                    val erro = tarefaDeAutenticacao.exception
                    Log.w(TAG_LOG, "Falha no login para: $email", erro)
                    tratarFalhaNoLogin(erro)
                }
            }
    }

    // ===========================================
    // TRATAMENTO DE RESULTADOS
    // ===========================================

    /**
     * Trata o sucesso do login.
     *
     * Quando o login é bem-sucedido:
     * 1. Mostra mensagem de sucesso
     * 2. Redireciona para a tela principal
     * 3. Fecha a tela de login
     */
    private fun tratarLoginBemSucedido() {
        val usuarioLogado = autenticadorFirebase.currentUser
        Log.i(TAG_LOG, "Usuário ${usuarioLogado?.email} está agora logado")

        exibirMensagem("Login realizado com sucesso! Bem-vindo!")
        redirecionarParaTelaPrincipal()
    }

    /**
     * Trata as falhas no processo de login.
     *
     * Analisa o tipo de erro e mostra uma mensagem apropriada para o usuário.
     */
    private fun tratarFalhaNoLogin(erro: Exception?) {
        Log.e(TAG_LOG, "Erro durante o login", erro)

        val mensagemDeErro = when (erro) {
            is FirebaseAuthInvalidUserException -> {
                // Email não existe na base de dados
                "Este email não está cadastrado. Verifique o email ou crie uma conta."
            }
            is FirebaseAuthInvalidCredentialsException -> {
                // Email ou senha incorretos
                "Email ou senha incorretos. Verifique seus dados e tente novamente."
            }
            else -> {
                // Outros tipos de erro (rede, servidor, etc.)
                Log.e(TAG_LOG, "Erro não categorizado: ${erro?.message}", erro)
                "Erro no login. Verifique sua conexão com a internet e tente novamente."
            }
        }

        exibirMensagem(mensagemDeErro)
    }

    // ===========================================
    // VALIDAÇÃO DE DADOS
    // ===========================================

    /**
     * Valida os dados de entrada do usuário (email e senha).
     *
     * Retorna true se todos os dados estão válidos,
     * false caso contrário (e mostra as mensagens de erro).
     */
    private fun validarDadosDeEntrada(email: String, senha: String): Boolean {
        var todosOsDadosEstaoValidos = true

        // Valida o campo de email
        if (!validarCampoEmail(email)) {
            todosOsDadosEstaoValidos = false
        }

        // Valida o campo de senha
        if (!validarCampoSenha(senha)) {
            todosOsDadosEstaoValidos = false
        }

        return todosOsDadosEstaoValidos
    }

    /**
     * Valida especificamente o campo de email.
     *
     * Verifica se:
     * 1. O campo não está vazio
     * 2. O formato do email é válido
     */
    private fun validarCampoEmail(email: String): Boolean {
        return when {
            email.isEmpty() -> {
                // Email está vazio
                binding.emailInputLayout.error = getString(R.string.validacao_email_obrigatorio)
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                // Email tem formato inválido
                binding.emailInputLayout.error = getString(R.string.validacao_email_invalido)
                false
            }
            else -> {
                // Email está válido
                binding.emailInputLayout.error = null
                true
            }
        }
    }

    /**
     * Valida especificamente o campo de senha.
     *
     * Por enquanto apenas verifica se não está vazio,
     * mas pode ser expandido para verificar força da senha.
     */
    private fun validarCampoSenha(senha: String): Boolean {
        return if (senha.isEmpty()) {
            binding.passwordInputLayout.error = getString(R.string.validacao_senha_obrigatoria)
            false
        } else {
            binding.passwordInputLayout.error = null
            true
        }
    }

    // ===========================================
    // NAVEGAÇÃO ENTRE TELAS
    // ===========================================

    /**
     * Redireciona o usuário para a tela principal da aplicação.
     *
     * Após o login bem-sucedido, o usuário vai para MainActivity
     * e a tela de login é fechada para evitar que ele volte para ela.
     */
    private fun redirecionarParaTelaPrincipal() {
        val intencaoParaTelaPrincipal = Intent(this, MainActivity::class.java)
        startActivity(intencaoParaTelaPrincipal)
        finish() // Remove esta tela da pilha de navegação
    }

    /**
     * Redireciona o usuário para a tela de cadastro.
     */
    private fun redirecionarParaTelaDeCadastro() {
        val intencaoParaCadastro = Intent(this, SignUpActivity::class.java)
        startActivity(intencaoParaCadastro)
        Log.d(TAG_LOG, "Redirecionando para tela de cadastro")

    }

    // ===========================================
    // FUNCIONALIDADES AUXILIARES
    // ===========================================

    /**
     * Trata o clique no link "Esqueceu a senha?".
     *
     * Por enquanto apenas mostra uma mensagem informativa,
     * mas pode ser expandido para implementar recuperação de senha.
     */
    private fun tratarEsqueceuSenha() {
        exibirMensagem("Funcionalidade de recuperação de senha em desenvolvimento!")
    }

    /**
     * Exibe uma mensagem Toast para o usuário.
     *
     * Toast é uma mensagem pequena que aparece brevemente na tela.
     * É útil para dar feedback rápido ao usuário.
     */
    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
        Log.d(TAG_LOG, "Mensagem exibida: $mensagem")
    }
}
