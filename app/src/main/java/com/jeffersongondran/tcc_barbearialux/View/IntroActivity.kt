package com.jeffersongondran.tcc_barbearialux.View

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jeffersongondran.tcc_barbearialux.databinding.ActivityIntroBinding

/**
 * Activity responsável pela tela de introdução do aplicativo da Barbearia Lux.
 *
 * Esta tela permite ao usuário:
 * - Fazer login (se já estiver registrado)
 * - Criar uma nova conta
 * - Recuperar senha esquecida
 *
 * É a primeira tela que o usuário vê ao abrir o aplicativo.
 */
class IntroActivity : AppCompatActivity() {

    // Companion object para armazenar constantes da classe
    // É como se fosse um "static" em Java - pertence à classe, não à instância
    companion object {
        // Constantes para mensagens - facilita manutenção e tradução
        private const val MENSAGEM_REGISTRO_NECESSARIO =
            "Por favor, realize o registro primeiro para continuar."

        private const val MENSAGEM_FUNCIONALIDADE_EM_BREVE =
            "Funcionalidade em breve! 🚧"

        // Constantes para SharedPreferences - evita erros de digitação
        private const val ARQUIVO_PREFERENCIAS_USUARIO = "DADOS_USUARIO"
        private const val CHAVE_USUARIO_LOGADO = "USUARIO_LOGADO"
    }

    // ViewBinding para acessar os elementos da interface de forma segura
    // O "lateinit" significa que será inicializada antes do primeiro uso
    private lateinit var binding: ActivityIntroBinding

    /**
     * Método chamado quando a Activity é criada.
     * É aqui que configuramos a interface e os eventos de clique.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura o ViewBinding para acessar os elementos da tela
        configurarViewBinding()

        // Configura os eventos de clique dos botões
        configurarEventosDeClique()
    }

    /**
     * Configura o ViewBinding para esta Activity.
     * ViewBinding é uma forma segura de acessar elementos do layout XML.
     */
    private fun configurarViewBinding() {
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Configura todos os eventos de clique dos elementos da interface.
     * Separar em método próprio torna o código mais organizado.
     */
    private fun configurarEventosDeClique() {
        configurarBotaoEntrar()
        configurarBotaoInscrever()
        configurarTextoEsqueceuSenha()
    }

    /**
     * Configura o comportamento do botão "Entrar".
     * Verifica se o usuário está registrado e navega para a tela apropriada.
     */
    private fun configurarBotaoEntrar() {
        binding.btnEntrar.setOnClickListener {
            if (verificarSeUsuarioEstaRegistrado()) {
                // Usuário já está registrado - vai direto para o login
                navegarParaTelaDeLogin()
            } else {
                // Usuário não está registrado - mostra aviso e vai para login mesmo assim
                // (o login pode ter opção de criar conta)
                exibirMensagemRegistroNecessario()
                navegarParaTelaDeLogin()
            }
        }
    }

    /**
     * Configura o comportamento do botão "Inscrever-se".
     * Navega diretamente para a tela de criação de conta.
     */
    private fun configurarBotaoInscrever() {
        binding.btnInscrever.setOnClickListener {
            navegarParaTelaDeCadastro()
        }
    }

    /**
     * Configura o comportamento do texto "Esqueceu da senha?".
     * Exibe uma mensagem informando que a funcionalidade está em desenvolvimento.
     */
    private fun configurarTextoEsqueceuSenha() {
        binding.textEsqueceuSenha.setOnClickListener {
            exibirMensagemFuncionalidadeEmBreve()
        }
    }

    /**
     * Verifica se o usuário já está registrado no aplicativo.
     *
     * Esta implementação é um exemplo usando SharedPreferences.
     * Em um app real, você pode verificar:
     * - Token de autenticação salvo
     * - Dados em banco de dados local
     * - Cache de login válido
     *
     * @return true se o usuário estiver registrado, false caso contrário
     */
    private fun verificarSeUsuarioEstaRegistrado(): Boolean {
        // SharedPreferences é como um "arquivo de configurações" do app
        // Permite salvar dados simples que persistem entre execuções
        val preferenciasUsuario = getSharedPreferences(
            ARQUIVO_PREFERENCIAS_USUARIO,
            MODE_PRIVATE
        )

        // Busca se existe a informação de usuário logado
        // Se não existir, retorna false como padrão
        return preferenciasUsuario.getBoolean(CHAVE_USUARIO_LOGADO, false)
    }

    /**
     * Exibe uma mensagem informando que é necessário fazer registro.
     * Toast é uma mensagem rápida que aparece na tela por alguns segundos.
     */
    private fun exibirMensagemRegistroNecessario() {
        Toast.makeText(
            this,
            MENSAGEM_REGISTRO_NECESSARIO,
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Exibe uma mensagem informando que a funcionalidade está em desenvolvimento.
     * Toast é uma mensagem rápida que aparece na tela por alguns segundos.
     */
    private fun exibirMensagemFuncionalidadeEmBreve() {
        Toast.makeText(
            this,
            MENSAGEM_FUNCIONALIDADE_EM_BREVE,
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Navega para a tela de login.
     * Intent é como um "envelope" que carrega informações entre telas.
     */
    private fun navegarParaTelaDeLogin() {
        val intencaoNavegacao = Intent(this, LoginActivity::class.java)
        startActivity(intencaoNavegacao)
    }

    /**
     * Navega para a tela de cadastro/criação de conta.
     */
    private fun navegarParaTelaDeCadastro() {
        val intencaoNavegacao = Intent(this, SignUpActivity::class.java)
        startActivity(intencaoNavegacao)
    }
}

// TODO: Certifique-se de criar a Activity '''EscolhaServicosActivity.kt''' se ela ainda não existir.
// Exemplo de como poderia ser a declaração da Activity (em um novo arquivo EscolhaServicosActivity.kt):
// package com.jeffersongondran.tcc_barbearialux.View
//
// import androidx.appcompat.app.AppCompatActivity
// import android.os.Bundle
// import com.jeffersongondran.tcc_barbearialux.R
//
// class EscolhaServicosActivity : AppCompatActivity() {
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         setContentView(R.layout.activity_escolha_servicos) // Crie também o layout activity_escolha_servicos.xml
//     }
// }
