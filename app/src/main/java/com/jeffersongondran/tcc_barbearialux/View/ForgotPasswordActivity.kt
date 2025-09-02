// Define o pacote ao qual este arquivo pertence. Pacotes são usados para organizar o código.
package com.jeffersongondran.tcc_barbearialux.View

// Importa as classes necessárias de outras partes do Android e do projeto.
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.jeffersongondran.tcc_barbearialux.databinding.ActivityForgotPasswordBinding

/**
 * Tela responsável pela recuperação de senha do usuário.
 *
 * Esta Activity permite que o usuário digite seu email e receba
 * um link para redefinir sua senha via Firebase Authentication.
 *
 * Funcionalidades principais:
 * - Validação de email
 * - Envio de email de recuperação
 * - Feedback visual para o usuário
 * - Animações sutis para melhor experiência
 */
class ForgotPasswordActivity : AppCompatActivity() {

    // ========== CONSTANTES ==========
    companion object {
        // Tempo em milissegundos para animações rápidas
        private const val ANIMATION_DURATION_FAST = 100L

        // Tempo em milissegundos para animações médias
        private const val ANIMATION_DURATION_MEDIUM = 300L

        // Distância do efeito "shake" em pixels
        private const val SHAKE_DISTANCE = 10f

        // Escala normal dos elementos (100%)
        private const val SCALE_NORMAL = 1.0f

        // Escala reduzida para efeito de "press" (95%)
        private const val SCALE_PRESSED = 0.95f

        // Delay antes de fechar a tela após sucesso (2 segundos)
        private const val SUCCESS_DELAY_MS = 2000L

        // Textos das mensagens
        private const val MSG_EMAIL_REQUIRED = "Digite seu email"
        private const val MSG_EMAIL_INVALID = "Email inválido"
        private const val MSG_SUCCESS = "Email de recuperação enviado! Verifique sua caixa de entrada."
        private const val MSG_EMAIL_NOT_FOUND = "Email não encontrado"
        private const val MSG_GENERIC_ERROR = "Erro ao enviar email. Tente novamente."

        // Textos dos botões
        private const val BTN_TEXT_NORMAL = "Enviar Recuperação"
        private const val BTN_TEXT_LOADING = "Enviando..."
    }

    // ========== PROPRIEDADES ==========
    /**
     * Binding para acessar os elementos da interface de forma segura
     */
    private lateinit var binding: ActivityForgotPasswordBinding

    /**
     * Instância do Firebase Authentication para gerenciar autenticação
     */
    private lateinit var firebaseAuth: FirebaseAuth

    // ========== CICLO DE VIDA DA ACTIVITY ==========
    /**
     * Método chamado quando a Activity é criada pela primeira vez.
     *
     * Aqui inicializamos:
     * - O binding para acessar a interface
     * - O Firebase Authentication
     * - Os listeners dos elementos da tela
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla o layout e configura o binding
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa os componentes necessários
        initializeComponents()

        // Configura os listeners dos elementos da interface
        setupUserInteractions()
    }

    // ========== INICIALIZAÇÃO ==========
    /**
     * Inicializa os componentes necessários para o funcionamento da tela.
     *
     * Separamos a inicialização em um método próprio para deixar
     * o onCreate mais limpo e focado apenas no essencial.
     */
    private fun initializeComponents() {
        // Obtém a instância do Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
    }

    /**
     * Configura todos os listeners (eventos de clique) da interface.
     *
     * Listeners são "ouvintes" que ficam esperando o usuário
     * interagir com os elementos da tela (tocar, clicar, etc.)
     */
    private fun setupUserInteractions() {
        // Configura o botão de voltar na toolbar (barra superior)
        binding.toolbar.setNavigationOnClickListener {
            closeScreen()
        }

        // Configura o texto clicável "Voltar para login"
        binding.textVoltarLogin.setOnClickListener {
            closeScreen()
        }

        // Configura o botão principal de enviar recuperação
        binding.btnEnviarRecuperacao.setOnClickListener {
            handlePasswordRecovery()
        }
    }

    // ========== LÓGICA PRINCIPAL ==========
    /**
     * Método principal que coordena todo o processo de recuperação de senha.
     *
     * Este método segue o padrão de "função orquestradora":
     * - Não faz o trabalho pesado diretamente
     * - Coordena outros métodos menores e específicos
     * - Fica fácil de ler e entender o fluxo principal
     */
    private fun handlePasswordRecovery() {
        // 1. Obtém o email digitado pelo usuário
        val userEmail = getUserEmail()

        // 2. Valida se o email é válido
        if (!isEmailValid(userEmail)) {
            return // Se inválido, para a execução aqui
        }

        // 3. Remove qualquer mensagem de erro anterior
        clearEmailError()

        // 4. Mostra estado de carregamento
        showLoadingState()

        // 5. Envia o email de recuperação
        sendPasswordResetEmail(userEmail)
    }

    /**
     * Obtém o email digitado pelo usuário no campo de texto.
     *
     * O .trim() remove espaços em branco no início e fim,
     * evitando problemas com emails que tenham espaços acidentais.
     */
    private fun getUserEmail(): String {
        return binding.editTextEmailForgotPassword.text.toString().trim()
    }

    /**
     * Valida se o email fornecido é válido.
     *
     * Verifica duas condições:
     * 1. Se não está vazio
     * 2. Se tem formato válido de email
     *
     * @param email O email a ser validado
     * @return true se válido, false caso contrário
     */
    private fun isEmailValid(email: String): Boolean {
        // Verifica se o email está vazio
        if (email.isEmpty()) {
            showEmailError(MSG_EMAIL_REQUIRED)
            animateEmailFieldError()
            return false
        }

        // Verifica se o email tem formato válido usando padrão do Android
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showEmailError(MSG_EMAIL_INVALID)
            animateEmailFieldError()
            return false
        }

        return true
    }

    /**
     * Envia o email de recuperação de senha usando Firebase.
     *
     * @param email O email para onde enviar a recuperação
     */
    private fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                // Sempre volta ao estado normal do botão
                hideLoadingState()

                if (task.isSuccessful) {
                    handlePasswordResetSuccess()
                } else {
                    handlePasswordResetError(task.exception)
                }
            }
    }

    // ========== TRATAMENTO DE SUCESSO E ERRO ==========
    /**
     * Trata o sucesso no envio do email de recuperação.
     *
     * Mostra mensagem de sucesso e programa o fechamento da tela.
     */
    private fun handlePasswordResetSuccess() {
        // Mostra mensagem de sucesso para o usuário
        showSuccessMessage(MSG_SUCCESS)

        // Anima o botão para dar feedback visual
        animateButtonSuccess()

        // Programa o fechamento da tela após um delay
        scheduleScreenClose()
    }

    /**
     * Trata erros que podem ocorrer ao enviar email de recuperação.
     *
     * @param exception A exceção retornada pelo Firebase
     */
    private fun handlePasswordResetError(exception: Exception?) {
        val errorMessage = getErrorMessage(exception)
        showErrorMessage(errorMessage)
        animateEmailFieldError()
    }

    /**
     * Converte exceções do Firebase em mensagens amigáveis para o usuário.
     *
     * @param exception A exceção a ser traduzida
     * @return Mensagem de erro em português
     */
    private fun getErrorMessage(exception: Exception?): String {
        return when (exception?.message) {
            "There is no user record corresponding to this identifier. The user may have been deleted." ->
                MSG_EMAIL_NOT_FOUND
            "The email address is badly formatted." ->
                MSG_EMAIL_INVALID
            else ->
                MSG_GENERIC_ERROR
        }
    }

    // ========== INTERFACE DO USUÁRIO ==========
    /**
     * Mostra erro no campo de email.
     *
     * @param message Mensagem de erro a ser exibida
     */
    private fun showEmailError(message: String) {
        binding.textInputLayoutEmail.error = message
    }

    /**
     * Remove qualquer erro do campo de email.
     */
    private fun clearEmailError() {
        binding.textInputLayoutEmail.error = null
    }

    /**
     * Mostra o estado de carregamento no botão.
     *
     * Desabilita o botão e muda o texto para indicar que
     * algo está sendo processado.
     */
    private fun showLoadingState() {
        binding.btnEnviarRecuperacao.apply {
            isEnabled = false
            text = BTN_TEXT_LOADING
        }
    }

    /**
     * Esconde o estado de carregamento e volta ao normal.
     */
    private fun hideLoadingState() {
        binding.btnEnviarRecuperacao.apply {
            isEnabled = true
            text = BTN_TEXT_NORMAL
        }
    }

    /**
     * Mostra mensagem de sucesso para o usuário.
     *
     * @param message Mensagem a ser exibida
     */
    private fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Mostra mensagem de erro para o usuário.
     *
     * @param message Mensagem de erro a ser exibida
     */
    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // ========== ANIMAÇÕES ==========
    /**
     * Anima o campo de email com efeito "shake" para indicar erro.
     *
     * O efeito shake chama atenção do usuário para o campo com problema
     * de forma sutil e não agressiva.
     */
    private fun animateEmailFieldError() {
        val emailField = binding.textInputLayoutEmail

        emailField.animate()
            .translationX(-SHAKE_DISTANCE)
            .setDuration(ANIMATION_DURATION_FAST)
            .withEndAction {
                emailField.animate()
                    .translationX(SHAKE_DISTANCE)
                    .setDuration(ANIMATION_DURATION_FAST)
                    .withEndAction {
                        emailField.animate()
                            .translationX(0f)
                            .setDuration(ANIMATION_DURATION_FAST)
                            .start()
                    }
                    .start()
            }
            .start()
    }

    /**
     * Anima o botão com efeito de "pulse" para indicar sucesso.
     *
     * O botão diminui ligeiramente e volta ao tamanho normal,
     * dando feedback visual de que a ação foi bem-sucedida.
     */
    private fun animateButtonSuccess() {
        val button = binding.btnEnviarRecuperacao

        button.animate()
            .scaleX(SCALE_PRESSED)
            .scaleY(SCALE_PRESSED)
            .setDuration(ANIMATION_DURATION_FAST)
            .withEndAction {
                button.animate()
                    .scaleX(SCALE_NORMAL)
                    .scaleY(SCALE_NORMAL)
                    .setDuration(ANIMATION_DURATION_FAST)
                    .start()
            }
            .start()
    }

    // ========== NAVEGAÇÃO ==========
    /**
     * Programa o fechamento da tela após o sucesso.
     *
     * Dá tempo suficiente para o usuário ler a mensagem de sucesso
     * antes de voltar para a tela anterior.
     */
    private fun scheduleScreenClose() {
        binding.root.postDelayed({
            closeScreen()
        }, SUCCESS_DELAY_MS)
    }

    /**
     * Fecha a tela atual e volta para a anterior.
     *
     * O finish() encerra esta Activity e retorna para
     * a Activity anterior na pilha de navegação.
     */
    private fun closeScreen() {
        finish()
    }
}
