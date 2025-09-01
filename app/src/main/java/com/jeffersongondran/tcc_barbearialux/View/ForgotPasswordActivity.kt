// Define o pacote ao qual este arquivo pertence. Pacotes são usados para organizar o código.
package com.jeffersongondran.tcc_barbearialux.View // Pacote onde a classe está localizada

// Importa as classes necessárias de outras partes do Android e do projeto.
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AnimationUtils
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
        setupEmailValidation()
        animateCardEntrance()
    }

    /**
     * Configura os listeners de clique para os componentes da tela.
     */
    private fun setupClickListeners() {
        // Configura o botão de voltar na toolbar
        binding.toolbar.setNavigationOnClickListener {
            animateCardExit {
                finish() // Finaliza esta activity e volta para a anterior
            }
        }

        // Texto "Voltar para login" - também retorna para a tela de login
        binding.textVoltarLogin.setOnClickListener {
            animateCardExit {
                finish()
            }
        }

        // Botão enviar recuperação - envia email de recuperação de senha
        binding.btnEnviarRecuperacao.setOnClickListener {
            sendPasswordResetEmail()
        }
    }

    /**
     * Configura validação em tempo real do campo de email
     */
    private fun setupEmailValidation() {
        binding.editTextEmailForgotPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()

                if (email.isNotEmpty()) {
                    if (isValidEmail(email)) {
                        // Email válido - remove erro e habilita botão
                        binding.textInputLayoutEmail.error = null
                        binding.btnEnviarRecuperacao.isEnabled = true
                        binding.btnEnviarRecuperacao.alpha = 1.0f
                    } else {
                        // Email inválido - mostra erro e desabilita botão
                        binding.textInputLayoutEmail.error = "Formato de email inválido"
                        binding.btnEnviarRecuperacao.isEnabled = false
                        binding.btnEnviarRecuperacao.alpha = 0.6f
                    }
                } else {
                    // Campo vazio - remove erro mas desabilita botão
                    binding.textInputLayoutEmail.error = null
                    binding.btnEnviarRecuperacao.isEnabled = false
                    binding.btnEnviarRecuperacao.alpha = 0.6f
                }
            }
        })
    }

    /**
     * Anima a entrada do card principal
     */
    private fun animateCardEntrance() {
        try {
            val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.card_slide_in)
            binding.mainContentCard?.startAnimation(slideInAnimation)
        } catch (_: Exception) {
            // Se a animação não existir, continua sem ela
        }
    }

    /**
     * Anima a saída do card antes de finalizar a activity
     */
    private fun animateCardExit(onComplete: () -> Unit) {
        try {
            binding.mainContentCard?.animate()
                ?.alpha(0f)
                ?.translationY(-50f)
                ?.setDuration(300)
                ?.withEndAction(onComplete)
                ?.start()
        } catch (_: Exception) {
            // Se a animação falhar, executa a ação diretamente
            onComplete()
        }
    }

    /**
     * Envia um email de recuperação de senha usando o Firebase Authentication.
     */
    private fun sendPasswordResetEmail() {
        val email = binding.editTextEmailForgotPassword.text.toString().trim()

        // Validação do campo de email
        if (email.isEmpty()) {
            binding.textInputLayoutEmail.error = "Por favor, digite seu email"
            showToast("Por favor, digite seu email")
            return
        }

        if (!isValidEmail(email)) {
            binding.textInputLayoutEmail.error = "Por favor, digite um email válido"
            showToast("Por favor, digite um email válido")
            return
        }

        // Remove qualquer erro anterior
        binding.textInputLayoutEmail.error = null

        // Feedback visual durante o carregamento
        showLoadingState(true)

        // Envia o email de recuperação através do Firebase
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                // Remove o estado de carregamento
                showLoadingState(false)

                if (task.isSuccessful) {
                    // Sucesso - anima o ícone e informa o usuário
                    animateSuccessState()
                    showToast("Email de recuperação enviado! Verifique sua caixa de entrada.")

                    // Aguarda a animação antes de voltar
                    binding.mainContentCard?.postDelayed({
                        animateCardExit { finish() }
                    }, 1500)
                } else {
                    // Erro - anima o erro e informa o usuário
                    animateErrorState()
                    val errorMessage = getFirebaseErrorMessage(task.exception?.message)
                    binding.textInputLayoutEmail.error = errorMessage
                    showToast(errorMessage)
                }
            }
    }

    /**
     * Mostra/esconde o estado de carregamento
     */
    private fun showLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.btnEnviarRecuperacao.isEnabled = false
            binding.btnEnviarRecuperacao.text = getString(R.string.enviando)
            binding.btnEnviarRecuperacao.alpha = 0.7f

            // Anima o ícone para indicar carregamento
            binding.iconForgotPassword?.animate()
                ?.rotation(360f)
                ?.setDuration(1000)
                ?.start()
        } else {
            binding.btnEnviarRecuperacao.isEnabled = true
            binding.btnEnviarRecuperacao.text = getString(R.string.enviar_recuperacao)
            binding.btnEnviarRecuperacao.alpha = 1.0f

            // Para a animação do ícone
            binding.iconForgotPassword?.animate()
                ?.rotation(0f)
                ?.setDuration(300)
                ?.start()
        }
    }

    /**
     * Anima o estado de sucesso
     */
    private fun animateSuccessState() {
        // Anima o ícone com efeito de sucesso
        binding.iconForgotPassword?.animate()
            ?.scaleX(1.2f)
            ?.scaleY(1.2f)
            ?.setDuration(200)
            ?.withEndAction {
                binding.iconForgotPassword?.animate()
                    ?.scaleX(1.0f)
                    ?.scaleY(1.0f)
                    ?.setDuration(200)
                    ?.start()
            }
            ?.start()

        // Anima o botão com feedback de sucesso
        binding.btnEnviarRecuperacao.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                binding.btnEnviarRecuperacao.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    /**
     * Anima o estado de erro
     */
    private fun animateErrorState() {
        // Shake animation no campo de email
        binding.textInputLayoutEmail.animate()
            .translationX(-10f)
            .setDuration(50)
            .withEndAction {
                binding.textInputLayoutEmail.animate()
                    .translationX(10f)
                    .setDuration(50)
                    .withEndAction {
                        binding.textInputLayoutEmail.animate()
                            .translationX(0f)
                            .setDuration(50)
                            .start()
                    }
                    .start()
            }
            .start()
    }

    /**
     * Retorna mensagem de erro amigável baseada na exceção do Firebase
     */
    private fun getFirebaseErrorMessage(firebaseError: String?): String {
        return when (firebaseError) {
            "There is no user record corresponding to this identifier. The user may have been deleted." ->
                "Email não encontrado. Verifique se está correto."
            "The email address is badly formatted." ->
                "Formato de email inválido."
            "A network error (such as timeout, interrupted connection or unreachable host) has occurred." ->
                "Erro de conexão. Verifique sua internet."
            "Too many requests. Please try again later." ->
                "Muitas tentativas. Tente novamente em alguns minutos."
            else ->
                "Erro ao enviar email de recuperação. Tente novamente."
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
