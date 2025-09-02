package com.jeffersongondran.tcc_barbearialux.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jeffersongondran.tcc_barbearialux.databinding.ActivityLoginBinding
import android.widget.Toast // Import Toast for messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlin.jvm.java

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    // TAG para logging
    private companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase Auth
        initializeFirebase()

        setupUI()
        setupClickListeners()
    }

    /**
     * Inicializa o Firebase Authentication
     */
    private fun initializeFirebase() {
        try {
            auth = FirebaseAuth.getInstance()
            Log.d(TAG, "initializeFirebase: Firebase Auth inicializado com sucesso.")
        } catch (e: Exception) {
            Log.e(TAG, "initializeFirebase: Erro ao inicializar Firebase Auth.", e)
            showToast("Erro ao inicializar autenticação. Tente novamente.")
        }
    }

    private fun setupUI() {
        // Configurações iniciais da UI, se necessário
    }

    private fun setupClickListeners() {
        // Botão de voltar
        binding.btnVoltarLogin.setOnClickListener {
            finish() // Fecha a activity atual
        }

        // Botão de entrar
        binding.btnEntrar.setOnClickListener {
            performLogin()
        }

        // Link "Esqueceu da senha?"
        binding.forgotPasswordText.setOnClickListener {
            handleForgotPassword()
        }

        // Redirecionar para a tela de inscrição
        setupSignUpRedirection()
    }

    /**
     * Esta função pode ser chamada separadamente se necessário
     */
    private fun setupSignUpRedirection() {
        // Como não vemos esse elemento no layout atual, vamos adicioná-lo
        binding.forgotPasswordText.setOnLongClickListener {
            redirectToSignUp()
            true
        }
    }

    fun redirectToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun performLogin() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (validateInput(email, password)) {
            // Desabilita o botão para evitar múltiplos cliques
            binding.btnEntrar.isEnabled = false

            // Autenticação real com Firebase
            authenticateWithFirebase(email, password)
        }
    }

    /**
     * Autentica o usuário usando Firebase Authentication
     */
    private fun authenticateWithFirebase(email: String, password: String) {
        Log.d(TAG, "authenticateWithFirebase: Tentando login para $email")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Reabilita o botão
                binding.btnEntrar.isEnabled = true

                if (task.isSuccessful) {
                    // Login bem-sucedido
                    Log.i(TAG, "authenticateWithFirebase: Login bem-sucedido para $email")
                    handleLoginSuccess()
                } else {
                    // Login falhou
                    Log.w(TAG, "authenticateWithFirebase: Falha no login para $email", task.exception)
                    handleLoginFailure(task.exception)
                }
            }
    }

    /**
     * Trata o sucesso do login
     */
    private fun handleLoginSuccess() {
        val user = auth.currentUser
        Log.i(TAG, "handleLoginSuccess: Usuário ${user?.email} logado com sucesso.")

        showToast("Login realizado com sucesso!")

        // Navegar para EscolhaServicosActivity
        val intent = Intent(this, EscolhaServicoActivity::class.java)
        startActivity(intent)
        finish() // Fecha a LoginActivity para que o usuário não volte para ela
    }

    /**
     * Trata as falhas no login
     */
    private fun handleLoginFailure(exception: Exception?) {
        Log.e(TAG, "handleLoginFailure: Erro no login", exception)

        val errorMessage = when (exception) {
            is FirebaseAuthInvalidUserException ->
                "Email não cadastrado. Verifique o email ou crie uma conta."
            is FirebaseAuthInvalidCredentialsException ->
                "Email ou senha incorretos. Verifique seus dados."
            else -> {
                Log.e(TAG, "handleLoginFailure: Erro não categorizado: ${exception?.message}", exception)
                "Erro no login. Verifique sua conexão ou tente novamente."
            }
        }
        showToast(errorMessage)
    }

    /**
     * Exibe mensagem Toast
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        // Validação do email
        if (email.isEmpty()) {
            binding.emailInputLayout.error = getString(com.jeffersongondran.tcc_barbearialux.R.string.error_email_required) // Assuming you add/have this string
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = getString(com.jeffersongondran.tcc_barbearialux.R.string.error_email_invalid)
            isValid = false
        } else {
            binding.emailInputLayout.error = null
        }

        // Validação da senha
        if (password.isEmpty()) {
            binding.passwordInputLayout.error = getString(com.jeffersongondran.tcc_barbearialux.R.string.error_password_required)
            isValid = false
        } else {
            binding.passwordInputLayout.error = null
        }

        return isValid
    }

    private fun handleForgotPassword() {
        // Implementar lógica para recuperação de senha
        // Por exemplo, abrir dialog ou navegar para tela de recuperação
        Toast.makeText(this, "Funcionalidade Esqueceu a senha a ser implementada", Toast.LENGTH_SHORT).show()
    }
}
