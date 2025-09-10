package com.jeffersongondran.luxconnect.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.jeffersongondran.luxconnect.R
import com.jeffersongondran.luxconnect.databinding.ActivitySignUpBinding

/**
 * `SignUpActivity` é responsável pelo processo de cadastro de novos usuários.
 * Utiliza o Firebase Authentication para gerenciar a criação e autenticação de contas.
 */
class SignUpActivity : AppCompatActivity() {

    // Instância do ViewBinding para interagir com os elementos da UI de forma segura.
    private lateinit var binding: ActivitySignUpBinding
    // Instância do FirebaseAuth para interagir com os serviços de autenticação do Firebase.
    private lateinit var auth: FirebaseAuth

    // TAG para logging, facilitando a depuração e rastreamento de eventos.
    private companion object {
        private const val TAG = "SignUpActivity"
    }

    /**
     * Chamado quando a Activity está sendo criada.
     * É aqui que a maior parte da inicialização deve acontecer: chamar setContentView(int)
     * para inflar o layout da Activity, usar findViewById(int) para interagir programaticamente
     * com widgets na UI, e configurar listeners.
     *
     * @param savedInstanceState Se a Activity está sendo recriada após ter sido
     * previamente destruída, este Bundle contém o estado mais recentemente fornecido
     * por onSaveInstanceState(Bundle). Caso contrário, é nulo.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla o layout usando ViewBinding e define como o conteúdo da Activity.
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: SignUpActivity iniciada.")

        // Inicializa os serviços do Firebase Authentication.
        initializeFirebase()
        // Configura os listeners de clique para os botões da interface.
        setupClickListeners()
    }

    /**
     * Inicializa a instância do Firebase Authentication.
     * Trata exceções que podem ocorrer durante a inicialização.
     */
    private fun initializeFirebase() {
        try {
            auth = FirebaseAuth.getInstance()
            Log.d(TAG, "initializeFirebase: Firebase Auth inicializado com sucesso.")
        } catch (e: Exception) {
            Log.e(TAG, "initializeFirebase: Erro ao inicializar Firebase Auth.", e)
            // Informa ao usuário sobre um erro interno que impede a funcionalidade.
            showToast("Erro crítico ao inicializar. Por favor, tente novamente mais tarde.")
            // Considerar desabilitar funcionalidades ou fechar a activity se o Auth é crucial.
        }
    }

    /**
     * Configura os listeners de clique para os elementos interativos da UI,
     * como botões de voltar e de inscrever.
     */
    private fun setupClickListeners() {
        // Configura o listener para o botão de voltar.
        binding.btnVoltarSignUp.setOnClickListener {
            Log.d(TAG, "setupClickListeners: Botão 'Voltar' pressionado.")
            finish() // Encerra a Activity atual, retornando à tela anterior na pilha.
        }

        // Configura o listener para o botão de realizar o cadastro.
        binding.btnIncrever.setOnClickListener {
            Log.d(TAG, "setupClickListeners: Botão 'Inscrever' pressionado.")
            performSignUp() // Chama o método para iniciar o processo de cadastro.
        }
    }

    /**
     * Orquestra o processo de cadastro do usuário.
     * Obtém os dados do formulário, valida-os e, se válidos,
     * tenta criar uma conta no Firebase.
     */
    private fun performSignUp() {
        // Extrai e normaliza o email e a senha dos campos de texto.
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etSenha.text.toString().trim()

        Log.d(TAG, "performSignUp: Tentativa de cadastro para email: $email")

        // Valida os dados de entrada antes de prosseguir.
        if (!validateInput(email, password)) {
            Log.w(TAG, "performSignUp: Validação de entrada falhou.")
            return // Interrompe o processo se a validação falhar.
        }

        // Prossegue com a criação da conta no Firebase.
        createFirebaseAccount(email, password)
    }

    /**
     * Valida os campos de email e senha inseridos pelo usuário.
     * Verifica se os campos não estão vazios, se o email tem formato válido
     * e se a senha atende ao requisito mínimo de tamanho.
     *
     * @param email O email fornecido pelo usuário.
     * @param password A senha fornecida pelo usuário.
     * @return `true` se os dados são válidos, `false` caso contrário.
     */
    private fun validateInput(email: String, password: String): Boolean {
        // Verifica se algum dos campos está vazio.
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Por favor, preencha todos os campos.")
            Log.w(TAG, "validateInput: Campos de email ou senha vazios.")
            return false
        }

        // Verifica se o email possui um formato válido.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Por favor, insira um email válido.")
            Log.w(TAG, "validateInput: Email com formato inválido: $email")
            return false
        }

        // Verifica se a senha tem o comprimento mínimo de 6 caracteres.
        // O Firebase exige senhas com no mínimo 6 caracteres.
        if (password.length < 6) {
            showToast("A senha deve ter pelo menos 6 caracteres.")
            Log.w(TAG, "validateInput: Senha muito curta.")
            return false
        }

        Log.d(TAG, "validateInput: Validação de entrada bem-sucedida.")
        return true
    }

    /**
     * Tenta criar uma nova conta de usuário no Firebase Authentication
     * utilizando o email e senha fornecidos.
     *
     * @param email O email para a nova conta.
     * @param password A senha para a nova conta.
     */
    private fun createFirebaseAccount(email: String, password: String) {
        // Desabilita o botão de inscrever para prevenir múltiplos cliques durante o processo.
        binding.btnIncrever.isEnabled = false
        Log.d(TAG, "createFirebaseAccount: Iniciando criação de conta para $email.")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Reabilita o botão de inscrever após a conclusão da tentativa de cadastro.
                binding.btnIncrever.isEnabled = true

                if (task.isSuccessful) {
                    // Se o cadastro for bem-sucedido, prossegue para o fluxo de sucesso.
                    Log.i(TAG, "createFirebaseAccount: Cadastro bem-sucedido para $email.")
                    handleSignUpSuccess()
                } else {
                    // Se o cadastro falhar, trata os erros específicos.
                    Log.w(TAG, "createFirebaseAccount: Falha no cadastro para $email.", task.exception)
                    handleSignUpFailure(task.exception)
                }
            }
    }

    /**
     * Executado quando o cadastro do usuário no Firebase é bem-sucedido.
     * Informa o usuário e navega para a tela principal da aplicação.
     */
    private fun handleSignUpSuccess() {
        val user = auth.currentUser
        Log.i(TAG, "handleSignUpSuccess: Usuário ${user?.email} cadastrado e logado com sucesso.")

        showToast("Cadastro realizado com sucesso! Bem-vindo!")

        // Navega para a tela principal da aplicação.
        navigateToMainScreen()
    }

    /**
     * Trata as falhas que podem ocorrer durante o processo de cadastro no Firebase.
     * Exibe mensagens de erro específicas baseadas no tipo de exceção.
     *
     * A mensagem "Erro no cadastro: An internal error has occurred" geralmente indica
     * problemas de configuração do Firebase no projeto (ex: `google-services.json` ausente/incorreto,
     * SHA-1 não configurado no console Firebase, ou o método de autenticação por Email/Senha
     * não está habilitado no Firebase Console).
     * Verifique também a conexão com a internet.
     *
     * @param exception A exceção ocorrida durante a tentativa de cadastro.
     */
    private fun handleSignUpFailure(exception: Exception?) {
        Log.e(TAG, "handleSignUpFailure: Ocorreu uma falha no cadastro.", exception)

        val errorMessage = when (exception) {
            is FirebaseAuthWeakPasswordException ->
                "A senha é muito fraca. Use pelo menos 6 caracteres, combinando letras e números."
            is FirebaseAuthInvalidCredentialsException ->
                "O formato do email é inválido. Verifique o email digitado."
            is FirebaseAuthUserCollisionException ->
                "Este email já está cadastrado. Tente fazer login ou use um email diferente."
            else -> {
                // Mensagem genérica para outros erros, incluindo o "internal error".
                // Logar o exception?.message pode dar mais detalhes específicos do Firebase.
                Log.e(TAG, "handleSignUpFailure: Erro não categorizado: ${exception?.message}", exception)
                "Erro no cadastro. Verifique sua conexão ou tente novamente mais tarde. (${exception?.javaClass?.simpleName})"
            }
        }
        showToast(errorMessage)
    }

    /**
     * Navega o usuário para a tela principal (`MainActivity`) da aplicação.
     * Após a navegação, finaliza a `SignUpActivity` para que o usuário
     * não retorne a ela ao pressionar o botão "voltar".
     */
    private fun navigateToMainScreen() {
        Log.d(TAG, "navigateToMainScreen: Navegando para a MainActivity.")
        // Cria uma Intent para iniciar a MainActivity.
        // É preferível usar MainActivity::class.java para segurança de tipo e refatoração.
        val intent = Intent(this, MainActivity::class.java).apply {
            // Adiciona flags para limpar a pilha de atividades e iniciar MainActivity como a nova tarefa raiz.
            // Isso evita que o usuário volte para a tela de login/cadastro ao pressionar "voltar" na MainActivity.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish() // Finaliza a SignUpActivity.
    }

    /**
     * Exibe uma mensagem curta (Toast) para o usuário.
     *
     * @param message A mensagem a ser exibida.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
