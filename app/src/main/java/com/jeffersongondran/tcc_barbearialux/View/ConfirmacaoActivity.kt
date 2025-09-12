package com.jeffersongondran.luxconnect.View

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.jeffersongondran.luxconnect.Model.Agendamento
import com.jeffersongondran.luxconnect.Model.BarberItem
import com.jeffersongondran.luxconnect.Model.StatusAgendamento
import com.jeffersongondran.luxconnect.R
import com.jeffersongondran.luxconnect.Viewmodel.AgendamentosViewModel
import com.jeffersongondran.luxconnect.databinding.ActivityConfirmacaoBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Activity de CONFIRMAÇÃO: exibe os dados selecionados e confirma o agendamento salvando no Firestore.
 * Fluxo: onCreate -> lê extras -> valida -> mostra resumo -> inicia salvamento -> observa estado e atualiza UI.
 */
class ConfirmacaoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SERVICO = "EXTRA_SERVICO"
        const val EXTRA_HORARIO = "EXTRA_HORARIO"
        private const val EXTRA_BARBER_ITEM = "BARBER_ITEM" // reutiliza mesma chave usada anteriormente
    }

    private lateinit var binding: ActivityConfirmacaoBinding
    private val viewModel: AgendamentosViewModel by viewModels()

    private var servico: String? = null
    private var horarioTexto: String? = null
    private var barbeariaSelecionada: BarberItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmacaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lê dados recebidos e valida campos obrigatórios
        lerExtras()
        if (!validarDados()) {
            // Dados inválidos: informa o usuário e encerra a tela
            Snackbar.make(binding.root, getString(R.string.agendamento_erro_selecao), Snackbar.LENGTH_LONG).show()
            finish()
            return
        }

        // Preenche resumo visual antes de salvar
        preencherResumo()

        // Configura botões (inativos durante loading)
        configurarBotoes()

        // Observa estados de UI do ViewModel para atualizar a tela
        observarEstados()

        // Inicia o salvamento automaticamente
        iniciarSalvamento()
    }

    /** Lê os dados passados pela tela anterior (serviço, horário e barbearia). */
    private fun lerExtras() {
        servico = intent.getStringExtra(EXTRA_SERVICO)
        horarioTexto = intent.getStringExtra(EXTRA_HORARIO)
        barbeariaSelecionada = if (android.os.Build.VERSION.SDK_INT >= 33) {
            intent.getSerializableExtra(EXTRA_BARBER_ITEM, BarberItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_BARBER_ITEM) as? BarberItem
        }
    }

    /** Valida campos obrigatórios (serviço e horário). */
    private fun validarDados(): Boolean {
        return !servico.isNullOrBlank() && !horarioTexto.isNullOrBlank()
    }

    /** Preenche os elementos de UI com o resumo do agendamento. */
    private fun preencherResumo() {
        binding.txtTitulo.text = getString(R.string.confirmacao_titulo)
        binding.txtServicoValor.text = servico
        binding.txtHorarioValor.text = horarioTexto
        binding.txtSalaoValor.text = barbeariaSelecionada?.nomeDoServico ?: getString(R.string.barbearia_name)
    }

    /** Configura ações dos botões conforme estados. */
    private fun configurarBotoes() {
        binding.btnVerAgendamentos.setOnClickListener {
            // Volta para a tela principal (ou poderia abrir uma tela de "Meus Agendamentos" se existir)
            finish()
        }
        binding.btnTentarNovamente.setOnClickListener {
            iniciarSalvamento()
        }
        binding.btnVoltar.setOnClickListener { finish() }
    }

    /** Observa carregando/sucesso/erro para refletir na UI. */
    private fun observarEstados() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.carregando.collect { carregando ->
                        binding.progressCircular.isVisible = carregando
                        // Durante carregamento, esconde outras views de feedback
                        if (carregando) {
                            binding.layoutSucesso.isVisible = false
                            binding.layoutErro.isVisible = false
                            binding.btnVerAgendamentos.isVisible = false
                            binding.btnTentarNovamente.isVisible = false
                        }
                    }
                }
                launch {
                    viewModel.operacaoSucesso.collect { msg ->
                        if (!msg.isNullOrBlank()) {
                            binding.layoutSucesso.isVisible = true
                            binding.layoutErro.isVisible = false
                            binding.btnVerAgendamentos.isVisible = true
                            binding.btnTentarNovamente.isVisible = false
                            binding.txtMensagemSucesso.text = getString(R.string.agendado_com_sucesso)
                            Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
                            // Após sucesso, limpamos mensagem para evitar reprocessar em rotações
                            viewModel.limparMensagens()
                        }
                    }
                }
                launch {
                    viewModel.erro.collect { erro ->
                        if (!erro.isNullOrBlank()) {
                            binding.layoutSucesso.isVisible = false
                            binding.layoutErro.isVisible = true
                            binding.btnVerAgendamentos.isVisible = false
                            binding.btnTentarNovamente.isVisible = true
                            binding.txtMensagemErro.text = erro
                        }
                    }
                }
            }
        }
    }

    /** Monta o objeto Agendamento e aciona o ViewModel para salvar. */
    private fun iniciarSalvamento() {
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        if (usuarioId.isNullOrBlank()) {
            // Usuário não logado: mostra erro claro
            binding.layoutSucesso.isVisible = false
            binding.layoutErro.isVisible = true
            binding.txtMensagemErro.text = getString(R.string.erro_generico)
            Snackbar.make(binding.root, getString(R.string.erro_generico), Snackbar.LENGTH_LONG).show()
            return
        }

        val timestampHorario = montarTimestampHorario(horarioTexto!!)

        // Preenche modelo respeitando o padrão atual do projeto
        val agendamento = Agendamento(
            id = "",
            usuarioId = usuarioId,
            salaoId = "", // Não há ID de salão nessa tela; manter vazio conforme dados disponíveis
            tipoServico = servico!!,
            horario = timestampHorario,
            status = StatusAgendamento.AGENDADO,
            observacoes = null,
            preco = null,
            dataAgendamento = Timestamp.now()
        )

        // Dispara criação no Firestore pelo ViewModel existente
        viewModel.criarAgendamento(agendamento)
    }

    /**
     * Constrói um Timestamp unindo a data atual ao horário selecionado (ex.: "10:00 AM").
     * Caso o parse falhe, usa o horário atual como fallback.
     */
    private fun montarTimestampHorario(horario: String): Timestamp {
        return try {
            // Os valores do Spinner usam AM/PM em inglês; usar Locale.US
            val sdf = SimpleDateFormat("hh:mm a", Locale.US)
            val horaMinuto = sdf.parse(horario)
            val calHorario = Calendar.getInstance()
            calHorario.time = horaMinuto ?: Calendar.getInstance().time

            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, calHorario.get(Calendar.HOUR_OF_DAY))
            cal.set(Calendar.MINUTE, calHorario.get(Calendar.MINUTE))
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            Timestamp(cal.time)
        } catch (e: Exception) {
            Timestamp.now()
        }
    }
}
