package com.jeffersongondran.tcc_barbearialux.View

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jeffersongondran.tcc_barbearialux.Viewmodel.EscolhaServicoViewModel
import com.jeffersongondran.tcc_barbearialux.R
import com.jeffersongondran.tcc_barbearialux.databinding.ActivityEscolhaServicoBinding

/**
 * Activity responsável por gerenciar a tela de escolha de serviços da barbearia.
 *
 * Esta classe permite ao usuário:
 * - Visualizar informações da barbearia
 * - Selecionar um tipo de serviço (corte, barba, etc.)
 * - Escolher um horário disponível
 * - Confirmar o agendamento
 */
class EscolhaServicoActivity : AppCompatActivity() {

    // Binding para acessar os elementos visuais do layout de forma segura
    private lateinit var binding: ActivityEscolhaServicoBinding

    // ViewModel que gerencia os dados e lógica de negócio desta tela
    private val viewModel: EscolhaServicoViewModel by viewModels()

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
                // Log para acompanhar qual serviço foi selecionado (útil para debug)
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
            R.id.corteCabeloRadioButton -> getString(R.string.servico_nome_corte_cabelo)
            R.id.corteBarbaRadioButton -> getString(R.string.servico_nome_barba)
            R.id.cabeloBarbaRadioButton -> getString(R.string.servico_nome_cabelo_e_barba)
            R.id.lavagemCabeloRadioButton -> getString(R.string.servico_nome_lavagem_cabelo)
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
        // Obtém o serviço que o usuário selecionou
        val servicoEscolhido = obterServicoSelecionadoPeloUsuario()

        // Obtém o horário que o usuário escolheu no spinner
        val horarioEscolhido = obterHorarioSelecionadoPeloUsuario()

        // Verifica se tanto o serviço quanto o horário foram selecionados
        if (dadosDoAgendamentoSaoValidos(servicoEscolhido, horarioEscolhido)) {
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
        // Mostra uma mensagem de confirmação para o usuário
        Toast.makeText(
            this,
            getString(R.string.agendamento_confirmado, nomeDoServico, horarioEscolhido),
            Toast.LENGTH_LONG
        ).show()

        // Registra a confirmação no log para acompanhamento interno
        println("Agendamento confirmado com sucesso: $nomeDoServico às $horarioEscolhido")
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
        // NOTA PARA DESENVOLVEDOR:
        // Este método precisa ser implementado conforme a estrutura real da classe de dados da barbearia.
        // Substitua 'Any' pelo tipo específico da sua classe de dados (ex: BarberShop, Servico, etc.)

        // Exemplo de implementação com uma classe de dados conhecida:
        /*
        when (dadosDaBarbearia) {
            is BarberShop -> {
                // Preenche os campos da interface com os dados da barbearia
                binding.nomeBarbeariaTextView.text = dadosDaBarbearia.nome
                binding.descricaoTextView.text = dadosDaBarbearia.descricao
                binding.horarioFuncionamentoTextView.text = dadosDaBarbearia.horarioFuncionamento
                binding.enderecoTextView.text = dadosDaBarbearia.endereco
            }
            is Servico -> {
                // Se for um objeto de serviço específico
                binding.nomeServicoTextView.text = dadosDaBarbearia.nome
                binding.precoTextView.text = dadosDaBarbearia.preco
                binding.duracaoTextView.text = dadosDaBarbearia.duracao
            }
            else -> {
                // Log para indicar que o tipo não foi reconhecido
                println("Tipo de dados não reconhecido: ${dadosDaBarbearia::class.simpleName}")
            }
        }
        */

        // Implementação temporária para evitar warning de parâmetro não utilizado
        println("Dados recebidos: $dadosDaBarbearia")
    }
}
