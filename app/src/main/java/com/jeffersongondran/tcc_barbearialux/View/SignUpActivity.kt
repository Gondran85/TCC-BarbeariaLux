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
import com.jeffersongondran.luxconnect.Utils.UserPreferences
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
    // Instância para gerenciar as preferências do usuário
    private lateinit var userPreferences: UserPreferences

    // TAG para logging, facilitando a depuração e rastreamento de eventos.
    private companion object {
        private const val TAG = "SignUpActivity"
    }

    /**
     * Chamado quando a Activity está sendo criada.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla o layout usando ViewBinding e define como o conteúdo da Activity.
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: SignUpActivity iniciada.")

        // Inicializa os serviços do Firebase Authentication e UserPreferences.
        initializeFirebase()
        initializeUserPreferences()
        // Configura os listeners de clique para os botões da interface.
        setupClickListeners()
    }

    /**
     * Inicializa a instância do Firebase Authentication.
     */
    private fun initializeFirebase() {
        try {
            auth = FirebaseAuth.getInstance()
            Log.d(TAG, "initializeFirebase: Firebase Auth inicializado com sucesso.")
        } catch (e: Exception) {
            Log.e(TAG, "initializeFirebase: Erro ao inicializar Firebase Auth.", e)
            showToast("Erro crítico ao inicializar. Por favor, tente novamente mais tarde.")
        }
    }

    /**
     * Inicializa as preferências do usuário
     */
    private fun initializeUserPreferences() {
        userPreferences = UserPreferences(this)
    }

    /**
     * Configura os listeners de clique para os elementos interativos da UI.
     */
    private fun setupClickListeners() {
        // Configura o listener para o botão de voltar.
        binding.btnVoltarSignUp.setOnClickListener {
            Log.d(TAG, "setupClickListeners: Botão 'Voltar' pressionado.")
            finish()
        }

        // Configura o listener para o botão de realizar o cadastro.
        binding.btnIncrever.setOnClickListener {
            Log.d(TAG, "setupClickListeners: Botão 'Inscrever' pressionado.")
            performSignUp()
        }
    }

    /**
     * Orquestra o processo de cadastro do usuário.
     */
    private fun performSignUp() {
        // Extrai e normaliza os dados dos campos de texto.
        val name = binding.etNome.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etSenha.text.toString().trim()

        Log.d(TAG, "performSignUp: Tentativa de cadastro para nome: $name, email: $email")

        // Valida os dados de entrada antes de prosseguir.
        if (!validateInput(name, email, password)) {
            Log.w(TAG, "performSignUp: Validação de entrada falhou.")
            return
        }

        // Prossegue com a criação da conta no Firebase.
        createFirebaseAccount(name, email, password)
    }

    /**
     * Valida os campos de nome, email e senha inseridos pelo usuário.
     */
    private fun validateInput(name: String, email: String, password: String): Boolean {
        // Verifica se algum dos campos está vazio.
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Por favor, preencha todos os campos.")
            Log.w(TAG, "validateInput: Campos obrigatórios vazios.")
            return false
        }

        // Verifica se o nome tem pelo menos 2 caracteres
        if (name.length < 2) {
            showToast("O nome deve ter pelo menos 2 caracteres.")
            Log.w(TAG, "validateInput: Nome muito curto.")
            return false
        }

        // Verifica se o email possui um formato válido.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Por favor, insira um email válido.")
            Log.w(TAG, "validateInput: Email com formato inválido: $email")
            return false
        }

        // Verifica se a senha tem o comprimento mínimo de 6 caracteres.
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
     */
    private fun createFirebaseAccount(name: String, email: String, password: String) {
        // Desabilita o botão de inscrever para prevenir múltiplos cliques durante o processo.
        binding.btnIncrever.isEnabled = false
        Log.d(TAG, "createFirebaseAccount: Iniciando criação de conta para $email.")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Reabilita o botão de inscrever após a conclusão da tentativa de cadastro.
                binding.btnIncrever.isEnabled = true

                if (task.isSuccessful) {
                    // Se o cadastro for bem-sucedido, salva os dados e prossegue
                    Log.i(TAG, "createFirebaseAccount: Cadastro bem-sucedido para $email.")
                    handleSignUpSuccess(name, email)
                } else {
                    // Se o cadastro falhar, trata os erros específicos.
                    Log.w(TAG, "createFirebaseAccount: Falha no cadastro para $email.", task.exception)
                    handleSignUpFailure(task.exception)
                }
            }
    }

    /**
     * Executado quando o cadastro do usuário no Firebase é bem-sucedido.
     */
    private fun handleSignUpSuccess(name: String, email: String) {
        val user = auth.currentUser
        Log.i(TAG, "handleSignUpSuccess: Usuário ${user?.email} cadastrado e logado com sucesso.")

        // Salva os dados do usuário nas preferências
        userPreferences.saveUserData(name, email)
        Log.d(TAG, "handleSignUpSuccess: Dados do usuário salvos: nome=$name, email=$email")

        showToast("Cadastro realizado com sucesso! Bem-vindo, $name!")

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
