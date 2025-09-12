package com.jeffersongondran.luxconnect.View

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jeffersongondran.luxconnect.Viewmodel.EscolhaServicoViewModel
import com.jeffersongondran.luxconnect.R
import com.jeffersongondran.luxconnect.databinding.ActivityEscolhaServicoBinding
import com.jeffersongondran.luxconnect.Model.BarberItem

/**
 * Activity responsável por gerenciar a tela de escolha de serviços da barbearia.
 *
 * Esta classe permite ao usuário:
 * - Visualizar informações da barbearia selecionada
 * - Selecionar um tipo de serviço (corte, barba, etc.)
 * - Escolher um horário disponível
 * - Confirmar o agendamento
 */
class EscolhaServicoActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_BARBER_ITEM = "BARBER_ITEM"
    }

    // Binding para acessar os elementos visuais do layout de forma segura
    private lateinit var binding: ActivityEscolhaServicoBinding

    // ViewModel que gerencia os dados e lógica de negócio desta tela
    private val viewModel: EscolhaServicoViewModel by viewModels()

    // Mantém referência da barbearia/serviço selecionado vindo da tela anterior para repassar adiante
    private var barbeariaSelecionada: BarberItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura o binding para conectar esta activity com o layout XML
        configurarBinding()

        // Configura todos os elementos da interface do usuário
        configurarInterface()

        // Inicia a observação dos dados que vêm do ViewModel
        observarDadosDoViewModel()
    }

    /**
     * Configura o View Binding para conectar com o layout XML.
     * O View Binding é uma forma segura de acessar elementos da interface.
     */
    private fun configurarBinding() {
        binding = ActivityEscolhaServicoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Configura todos os elementos da interface do usuário.
     * Separa a configuração em métodos específicos para cada componente.
     */
    private fun configurarInterface() {
        configurarBotaoVoltar()
        configurarSelecaoDeServicos()
        configurarBotaoAgendamento()
        exibirInformacoesDaBarbearia()
    }

    /**
     * Configura o botão de voltar para retornar à tela anterior.
     * Usa o dispatcher nativo do Android para uma navegação consistente.
     */
    private fun configurarBotaoVoltar() {
        binding.btnVoltarServicos.setOnClickListener {
            // onBackPressedDispatcher é a forma moderna de lidar com o botão voltar
            onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Configura o grupo de botões de seleção de serviços (RadioGroup).
     * Cada opção representa um tipo diferente de serviço oferecido pela barbearia.
     */
    private fun configurarSelecaoDeServicos() {
        binding.servicoRadioGroup.setOnCheckedChangeListener { _, opcaoSelecionadaId ->
            // Identifica qual serviço foi selecionado e executa a lógica correspondente
            val nomeDoServicoSelecionado = obterNomeDoServicoSelecionado(opcaoSelecionadaId)

            if (nomeDoServicoSelecionado != null) {
                // Log para debug
                println("Serviço selecionado pelo usuário: $nomeDoServicoSelecionado")
            }
        }
    }

    /**
     * Retorna o nome do serviço baseado no ID do RadioButton selecionado.
     * Centraliza a lógica de mapeamento entre IDs e nomes dos serviços.
     *
     * @param idDaOpcaoSelecionada ID do RadioButton que foi marcado
     * @return Nome do serviço correspondente ou null se nenhum foi selecionado
     */
    private fun obterNomeDoServicoSelecionado(idDaOpcaoSelecionada: Int): String? {
        return when (idDaOpcaoSelecionada) {
            R.id.corteCabeloRadioButton -> getString(R.string.servico_corte_cabelo)
            R.id.corteBarbaRadioButton -> getString(R.string.servico_barba)
            R.id.cabeloBarbaRadioButton -> getString(R.string.servico_cabelo_barba)
            R.id.lavagemCabeloRadioButton -> getString(R.string.servico_lavagem_cabelo)
            else -> null // Retorna null quando nenhuma opção válida está selecionada
        }
    }

    /**
     * Configura o botão de agendamento que finaliza o processo de reserva.
     * Valida se o usuário selecionou um serviço e horário antes de confirmar.
     */
    private fun configurarBotaoAgendamento() {
        binding.agendarHorarioButton.setOnClickListener {
            processarSolicitacaoDeAgendamento()
        }
    }

    /**
     * Processa a solicitação de agendamento validando os dados selecionados.
     * Se tudo estiver correto, confirma a reserva. Caso contrário, mostra erro.
     */
    private fun processarSolicitacaoDeAgendamento() {
        // Coleta serviço e horário escolhidos na UI
        val servicoEscolhido = obterServicoSelecionadoPeloUsuario()
        val horarioEscolhido = obterHorarioSelecionadoPeloUsuario()

        // Validação básica dos campos obrigatórios
        if (dadosDoAgendamentoSaoValidos(servicoEscolhido, horarioEscolhido)) {
            // Navega para a tela de confirmação passando o resumo do agendamento
            confirmarAgendamento(servicoEscolhido!!, horarioEscolhido)
        } else {
            mostrarErroDeSelecao()
        }
    }

    /**
     * Obtém o serviço que está atualmente selecionado no RadioGroup.
     *
     * @return Nome do serviço selecionado ou null se nenhum estiver marcado
     */
    private fun obterServicoSelecionadoPeloUsuario(): String? {
        val idDoServicoSelecionado = binding.servicoRadioGroup.checkedRadioButtonId
        return obterNomeDoServicoSelecionado(idDoServicoSelecionado)
    }

    /**
     * Obtém o horário que está atualmente selecionado no Spinner.
     *
     * @return Horário selecionado como texto
     */
    private fun obterHorarioSelecionadoPeloUsuario(): String {
        return binding.horarioSpinner.selectedItem.toString()
    }

    /**
     * Valida se os dados necessários para o agendamento foram preenchidos.
     *
     * @param servico Nome do serviço selecionado (pode ser null)
     * @param horario Horário selecionado
     * @return true se ambos os dados são válidos, false caso contrário
     */
    private fun dadosDoAgendamentoSaoValidos(servico: String?, horario: String): Boolean {
        return servico != null && horario.isNotEmpty()
    }

    /**
     * Confirma o agendamento exibindo uma mensagem de sucesso.
     * Também registra a confirmação no log para controle interno.
     *
     * @param nomeDoServico Nome do serviço que foi agendado
     * @param horarioEscolhido Horário que foi selecionado para o agendamento
     */
    private fun confirmarAgendamento(nomeDoServico: String, horarioEscolhido: String) {
        // Inicia a ConfirmacaoActivity passando os dados necessários
        val intent = Intent(this, com.jeffersongondran.luxconnect.View.ConfirmacaoActivity::class.java)
        intent.putExtra(com.jeffersongondran.luxconnect.View.ConfirmacaoActivity.EXTRA_SERVICO, nomeDoServico)
        intent.putExtra(com.jeffersongondran.luxconnect.View.ConfirmacaoActivity.EXTRA_HORARIO, horarioEscolhido)
        // Repassa também a barbearia/serviço selecionado da tela anterior (se houver)
        barbeariaSelecionada?.let { intent.putExtra(EXTRA_BARBER_ITEM, it) }
        startActivity(intent)
    }

    /**
     * Mostra uma mensagem de erro quando o usuário não selecionou serviço ou horário.
     * Orienta o usuário sobre o que precisa ser feito.
     */
    private fun mostrarErroDeSelecao() {
        Toast.makeText(
            this,
            getString(R.string.agendamento_erro_selecao),
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Observa as mudanças nos dados fornecidos pelo ViewModel.
     * Quando novos dados chegam, atualiza automaticamente a interface.
     */
    private fun observarDadosDoViewModel() {
        viewModel.listaDeServicos.observe(this) { listaDeItens ->
            atualizarInterfaceComDadosDaBarbearia(listaDeItens)
        }
    }

    /**
     * Atualiza os elementos da interface com as informações da barbearia.
     * Pega os dados do primeiro item da lista e preenche os campos na tela.
     *
     * @param listaDeItens Lista de dados da barbearia vindos do ViewModel
     */
    private fun atualizarInterfaceComDadosDaBarbearia(listaDeItens: List<Any>) {
        // Verifica se a lista tem pelo menos um item antes de tentar acessá-lo
        if (listaDeItens.isNotEmpty()) {
            // Pega o primeiro item da lista (dados da barbearia)
            val dadosDaBarbearia = listaDeItens[0]

            // Atualiza cada campo da interface com os dados correspondentes
            preencherInformacoesDaBarbearia(dadosDaBarbearia)
        }
    }

    /**
     * Preenche os campos da interface com as informações específicas da barbearia.
     * Este método deve ser adaptado conforme a estrutura real da classe de dados.
     *
     * @param dadosDaBarbearia Objeto contendo as informações da barbearia
     */
    private fun preencherInformacoesDaBarbearia(dadosDaBarbearia: Any) {
        // Mantém implementação genérica conforme código existente
        println("Dados recebidos: $dadosDaBarbearia")
    }

    /**
     * Recebe e exibe as informações da barbearia selecionada na tela anterior.
     * Se uma barbearia foi selecionada, mostra seus dados na interface.
     */
    private fun exibirInformacoesDaBarbearia() {
        // Recebe o objeto BarberItem enviado pela MainActivity e guarda para repasse
        barbeariaSelecionada = intent.getSerializableExtra(EXTRA_BARBER_ITEM) as? BarberItem

        barbeariaSelecionada?.let { barbearia ->
            // Atualiza UI com dados disponíveis
            binding.clubeTextView.text = barbearia.nomeDoServico
            binding.descricaoTextView.text = barbearia.descricaoDoServico
            val horarioTexto = "Funcionamento: ${barbearia.horarioFuncionamento}"
            binding.abertoTextView.text = horarioTexto
            if (barbearia.imagemDoServico != 0) {
                binding.imageView15.setImageResource(barbearia.imagemDoServico)
            }
            println("Informações da barbearia carregadas: ${barbearia.nomeDoServico}")
        } ?: run {
            println("Nenhuma barbearia foi selecionada, usando valores padrão")
        }
    }
}
