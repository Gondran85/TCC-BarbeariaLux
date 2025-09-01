package com.jeffersongondran.tcc_barbearialux.View

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jeffersongondran.tcc_barbearialux.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupClickListeners()
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


    /**

     */
    fun redirectToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    fun performLogin() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()


            // Aqui você implementaria a lógica de login
            // Por exemplo, chamada para API, validação com Firebase, etc.
        }
    }

    fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        // Validação do email
        if (email.isEmpty()) {

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
    }
}

private fun LoginActivity.performLogin() {
    val email = binding.editTextEmail.text.toString().trim()
    val password = binding.editTextPassword.text.toString().trim()

    if (validateInput(email, password)) {
        // Aqui você implementaria a lógica de login
        // Por exemplo, chamada para API, validação com Firebase, etc.
    }
}

private fun LoginActivity.redirectToSignUp() {
    val intent = Intent(this, SignUpActivity::class.java)
    startActivity(intent)
}
