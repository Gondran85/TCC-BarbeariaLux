import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.jeffersongondran.tcc_barbearialux.View.MainActivity

// Exemplo de como configurar o botão btnVoltarHome em qualquer Activity

class ExemploActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExemploBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExemploBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // Configurar o botão de voltar para MainActivity
        binding.btnVoltarHome.setOnClickListener {
            voltarParaMainActivity()
        }

        // Configurar quando mostrar o botão (opcional)
        // Se esta não for a MainActivity, mostrar o botão
        if (this !is MainActivity) {
            binding.btnVoltarHome.visibility = View.VISIBLE
        }
    }

    private fun voltarParaMainActivity() {
        // Opção 1: Navegar para MainActivity (recomendado)
        val intent = Intent(this, MainActivity::class.java).apply {
            // Limpa a pilha de activities e faz MainActivity ser a única
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish() // Fecha a activity atual
    }

    // Opção 2: Alternativa usando onBackPressed (mais simples)
    private fun voltarParaTelAnterior() {
        onBackPressedDispatcher.onBackPressed() // Fecha a atividade ou volta para a tela anterior
    }

    // Opção 3: Para navegação com Animation (opcional)
    private fun voltarParaMainActivityComAnimacao() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)

        // Adicionar animação personalizada
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}
