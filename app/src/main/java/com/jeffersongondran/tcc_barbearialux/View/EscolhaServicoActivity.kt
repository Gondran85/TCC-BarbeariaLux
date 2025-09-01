package com.jeffersongondran.tcc_barbearialux.View// Pacote onde a classe está localizada

import android.os.Bundle // Importa classes necessárias para gerenciar o ciclo de vida da atividade
import android.widget.Toast // Importa a classe Toast para exibir mensagens curtas na tela
import androidx.activity.viewModels // Importa a função viewModels para inicializar ViewModel
import androidx.appcompat.app.AppCompatActivity // Importa a classe base para atividades compatíveis com a biblioteca de suporte
import com.jeffersongondran.tcc_barbearialux.Viewmodel.EscolhaServicoViewModel // Importa o ViewModel associado a esta atividade
import com.jeffersongondran.tcc_barbearialux.R // Importa o arquivo R para acessar recursos do projeto
import com.jeffersongondran.tcc_barbearialux.databinding.ActivityEscolhaServicoBinding // Importa o binding gerado automaticamente para acessar elementos do layout

// Classe que representa a tela de escolha de serviço
class EscolhaServicoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEscolhaServicoBinding // Binding para acessar os elementos do layout
    private val viewModel: EscolhaServicoViewModel by viewModels() // Inicializa o ViewModel usando a biblioteca AndroidX

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEscolhaServicoBinding.inflate(layoutInflater) // Infla o layout usando View Binding
        setContentView(binding.root) // Define o conteúdo da atividade como o layout inflado

        // Configurar o botão de voltar
        binding.btnVoltarServicos.setOnClickListener(){
            onBackPressedDispatcher.onBackPressed() // Fecha a atividade ou volta para a tela anterior
        }

        // Observar mudanças nos dados fornecidos pelo ViewModel
        viewModel.barberItems.observe(this) { items ->
            // Atualizar a UI com os dados observados
            if (items.isNotEmpty()) {
                val barberItem = items[0] // Obtém o primeiro item da lista
                binding.titleTextView.text = "Bem-vindo ao ${barberItem.title}" // Atualiza o título com o nome da barbearia
                binding.clubeTextView.text = barberItem.title // Atualiza o nome do clube
                binding.descricaoTextView.text = barberItem.subtitle // Atualiza a descrição
                binding.abertoTextView.text = barberItem.horario // Atualiza o horário de funcionamento
            }
        }

        // Configurar o RadioGroup para escolher o serviço
        binding.servicoRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.corteCabeloRadioButton -> {
                    // Lógica para Corte de Cabelo
                    println("Serviço selecionado: Corte de Cabelo") // Exibe no console o serviço selecionado
                }
                R.id.corteBarbaRadioButton -> {
                    // Lógica para Barba
                    println("Serviço selecionado: Barba") // Exibe no console o serviço selecionado
                }
                R.id.cabeloBarbaRadioButton -> {
                    // Lógica Corte cabelo e Barba
                    println("Serviço selecionado: Corte cabelo e Barba") // Exibe no console

                }
                R.id.lavagemCabeloRadioButton -> {
                    // Lógica lavagem de cabelo
                    println("Serviço selecionado: Lavagem de Cabelo") // Exibe no console
                }
//                R.id.radioBotaoCorteInfantil -> {
//                    // Lógica para Corte Infantil
//                    println("Serviço selecionado: Corte Infantil") // Exibe no console o serviço selecionado
//                }
            }
        }

        // Configurar o botão de reserva
        binding.agendarHorarioButton.setOnClickListener {
            // Obter o serviço selecionado
            val selectedService = when (binding.servicoRadioGroup.checkedRadioButtonId) {
                R.id.corteCabeloRadioButton -> "Corte de Cabelo" // Retorna o serviço correspondente ao RadioButton selecionado
                R.id.corteBarbaRadioButton -> "Barba"
                R.id.lavagemCabeloRadioButton-> "Corte lavagem de cabelo"
                R.id.cabeloBarbaRadioButton -> "Corte cabelo e Barba"
                //R.id.radioBotaoCorteTesoura -> "Corte de Tesoura"
                //R.id.radioBotaoCorteInfantil -> "Corte Infantil"
                else -> null // Retorna null se nenhum RadioButton estiver selecionado
            }

            // Obter o horário selecionado no Spinner
            val selectedTime = binding.horarioSpinner.selectedItem.toString() // Obtém o horário selecionado no Spinner

            if (selectedService != null && selectedTime.isNotEmpty()) {
                // Exibir um Toast confirmando a reserva
                Toast.makeText(
                    this,
                    "Reserva confirmada: $selectedService às $selectedTime",
                    Toast.LENGTH_LONG
                ).show()

                // Salvar ou processar a reserva (opcional)
                println("Reserva confirmada: $selectedService às $selectedTime") // Exibe no console a reserva confirmada
            } else {
                // Tratar caso nenhum serviço ou horário seja selecionado
                Toast.makeText(
                    this,
                    "Erro: Serviço ou horário não selecionado!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
