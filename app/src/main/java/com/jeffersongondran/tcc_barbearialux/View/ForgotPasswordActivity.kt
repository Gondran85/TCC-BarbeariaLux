// Define o pacote ao qual este arquivo pertence. Pacotes são usados para organizar o código.
package com.jeffersongondran.tcc_barbearialux.View // Pacote onde a classe está localizada

// Importa as classes necessárias de outras partes do Android e do projeto.
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.jeffersongondran.tcc_barbearialux.R
import com.jeffersongondran.tcc_barbearialux.databinding.ActivityForgotPasswordBinding



/**
 * Activity responsável por gerenciar a funcionalidade "Esqueceu a senha".
 * Permite que o usuário envie um email de recuperação de senha para o Firebase Authentication.
 */
class ForgotPasswordActivity : AppCompatActivity() {

    // ViewBinding para acessar os componentes do layout de forma segura e eficiente
    private lateinit var binding: ActivityForgotPasswordBinding

    // Instância do Firebase Authentication para gerenciar a recuperação de senha
    private lateinit var auth: FirebaseAuth

    /**
     * Método chamado quando a Activity é criada pela primeira vez.
     * Configura a UI e os listeners de clique.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewBinding
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o Firebase Authentication
        auth = FirebaseAuth.getInstance()

        setupClickListeners()
    }

    /**
     * Configura os listeners de clique para os componentes da tela.
     */
    private fun setupClickListeners() {
        // Botão voltar - retorna para a tela de login
        binding.btnVoltarForgotPassword.setOnClickListener {
            finish() // Finaliza esta activity e volta para a anterior
        }

        // Texto "Voltar para login" - também retorna para a tela de login
        binding.textVoltarLogin.setOnClickListener {
            finish()
        }

        // Botão enviar recuperação - envia email de recuperação de senha
        binding.btnEnviarRecuperacao.setOnClickListener {
            sendPasswordResetEmail()
        }
    }

    /**
     * Envia um email de recuperação de senha usando o Firebase Authentication.
     */
    private fun sendPasswordResetEmail() {
        val email = binding.editTextEmailForgotPassword.text.toString().trim()

        // Validação do campo de email
        if (email.isEmpty()) {
            showToast("Por favor, digite seu email")
            return
        }

        if (!isValidEmail(email)) {
            showToast("Por favor, digite um email válido")
            return
        }

        // Desabilita o botão para evitar múltiplos cliques
        binding.btnEnviarRecuperacao.isEnabled = false
        binding.btnEnviarRecuperacao.text = getString(R.string.enviando)

        // Envia o email de recuperação através do Firebase
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                // Reabilita o botão
                binding.btnEnviarRecuperacao.isEnabled = true
                binding.btnEnviarRecuperacao.text = getString(R.string.enviar_recuperacao)

                if (task.isSuccessful) {
                    // Sucesso - informa o usuário e volta para a tela de login
                    showToast("Email de recuperação enviado! Verifique sua caixa de entrada.")
                    finish()
                } else {
                    // Erro - informa o usuário sobre o problema
                    val errorMessage = when (task.exception?.message) {
                        "There is no user record corresponding to this identifier. The user may have been deleted." ->
                            "Email não encontrado. Verifique se está correto."
                        "The email address is badly formatted." ->
                            "Formato de email inválido."
                        else ->
                            "Erro ao enviar email de recuperação. Tente novamente."
                    }
                    showToast(errorMessage)
                }
            }
    }

    /**
     * Valida se o email está em um formato válido.
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Exibe uma mensagem Toast para o usuário.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
