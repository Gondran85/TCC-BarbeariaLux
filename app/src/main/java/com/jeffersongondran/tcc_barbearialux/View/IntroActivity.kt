package com.jeffersongondran.tcc_barbearialux.View // Pacote onde a classe está localizada

import android.content.Context // Necessário para o exemplo de SharedPreferences na função usuarioEstaRegistrado
import android.content.Intent // Importa a classe Intent para navegar entre atividades
import android.os.Bundle // Importa classes necessárias para gerenciar o ciclo de vida da atividade
import android.widget.Toast // Importa Toast para exibir mensagens
import androidx.appcompat.app.AppCompatActivity // Importa a classe base para atividades compatíveis com a biblioteca de suporte
import com.jeffersongondran.tcc_barbearialux.databinding.ActivityIntroBinding // Importa o binding gerado automaticamente para acessar elementos do layout
import kotlin.jvm.java

// Classe que representa a tela inicial (intro) do aplicativo
class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding // Binding para acessar os elementos do layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater) // Infla o layout usando View Binding
        setContentView(binding.root) // Define o conteúdo da atividade como o layout inflado

        // Configurar o botão "Entrar"
        binding.btnEntrar.setOnClickListener {
            // Verifica se o usuário já possui registro
            if (usuarioEstaRegistrado()) {
                // Se registrado, navega para a tela de login.
                // Certifique-se de que a Activity LoginActivity exista em seu projeto.
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Se não registrado, exibe um aviso e então navega para a tela de inscrição
                Toast.makeText(this, "Por favor, realize o registro primeiro para continuar.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java) // Navega para a tela de inscrição
                startActivity(intent)
            }
        }

        // Configurar o botão "Inscrever-se"
        binding.btnInscrever.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java)) // Navega para a tela de inscrição
        }
    }

    // Função para verificar o estado de registro do usuário.
    // Esta é uma implementação de EXEMPLO. Você DEVE adaptá-la à sua lógica de autenticação real.
    private fun usuarioEstaRegistrado(): Boolean {
        // Lógica para verificar se o usuário está registrado.
        // Exemplo: Verificar SharedPreferences, um banco de dados, um token de autenticação, etc.
        //
        // Exemplo utilizando SharedPreferences:
        // val sharedPreferences = getSharedPreferences("DADOS_USUARIO", Context.MODE_PRIVATE)
        // return sharedPreferences.getBoolean("USUARIO_LOGADO", false)
        //
        // Por enquanto, esta função retorna '''false''' para simular que o usuário não está registrado.
        // SUBSTITUA esta linha pela sua lógica de verificação real.
        return false // Altere para '''true''' para testar o fluxo de usuário registrado.
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
