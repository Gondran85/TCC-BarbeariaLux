/**
 * Classe com exemplos práticos de uso do sistema Firebase Firestore
 * Demonstra como utilizar as ViewModels e repositório na prática
 */
package com.jeffersongondran.luxconnect.Utils

import com.google.firebase.Timestamp
import com.jeffersongondran.luxconnect.Model.Agendamento
import com.jeffersongondran.luxconnect.Model.Salao
import com.jeffersongondran.luxconnect.Model.StatusAgendamento
import com.jeffersongondran.luxconnect.Model.Usuario
import com.jeffersongondran.luxconnect.Repository.RepositorioFirestore
import com.jeffersongondran.luxconnect.Viewmodel.AgendamentosViewModel
import com.jeffersongondran.luxconnect.Viewmodel.SaloesViewModel
import com.jeffersongondran.luxconnect.Viewmodel.UsuarioViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

/**
 * Classe com exemplos práticos de como usar o sistema de agendamentos
 * Serve como guia de implementação para outros desenvolvedores
 */
class ExemplosDeUso {

    companion object {

        /**
         * EXEMPLO 1: Como observar salões em tempo real
         * Este exemplo mostra como usar a SaloesViewModel para observar
         * mudanças na lista de salões automaticamente
         */
        fun exemploObservarSaloes(viewModel: SaloesViewModel) {
            // Crie um escopo de coroutine na sua Activity/Fragment
            val scope = CoroutineScope(Dispatchers.Main)

            scope.launch {
                // Observa mudanças na lista de salões
                viewModel.saloes.collect { listaSaloes ->
                    // Esta função será chamada sempre que a lista mudar no Firestore
                    println("Lista atualizada com ${listaSaloes.size} salões:")

                    listaSaloes.forEach { salao ->
                        println("- ${salao.nome} (${salao.servicos.size} serviços)")
                    }
                }
            }

            // Observa estado de carregamento
            scope.launch {
                viewModel.carregando.collect { carregando ->
                    if (carregando) {
                        println("Carregando salões...")
                        // Mostrar indicador de carregamento na UI
                    } else {
                        println("Carregamento concluído")
                        // Esconder indicador de carregamento
                    }
                }
            }

            // Observa erros
            scope.launch {
                viewModel.erro.collect { mensagemErro ->
                    mensagemErro?.let {
                        println("Erro: $it")
                        // Mostrar erro para o usuário
                    }
                }
            }
        }

        /**
         * EXEMPLO 2: Como criar um agendamento
         * Demonstra o processo completo de criação de um agendamento
         */
        fun exemploCriarAgendamento(
            agendamentosViewModel: AgendamentosViewModel,
            usuarioId: String,
            salaoId: String
        ) {
            // Cria os dados do agendamento
            val novoAgendamento = Agendamento(
                usuarioId = usuarioId,
                salaoId = salaoId,
                tipoServico = "Corte de Cabelo",
                horario = criarTimestampParaProximaSegunda(14, 30), // Segunda às 14:30
                observacoes = "Gostaria de um corte social moderno",
                preco = 35.0
            )

            // Cria o agendamento usando a ViewModel
            agendamentosViewModel.criarAgendamento(novoAgendamento)

            // Observa o resultado da operação
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                agendamentosViewModel.operacaoSucesso.collect { mensagem ->
                    mensagem?.let {
                        println("Sucesso: $it")
                        // Mostrar mensagem de sucesso para o usuário
                        // Navegar para tela de confirmação
                    }
                }
            }

            scope.launch {
                agendamentosViewModel.erro.collect { erro ->
                    erro?.let {
                        println("Erro ao criar agendamento: $it")
                        // Mostrar erro para o usuário
                    }
                }
            }
        }

        /**
         * EXEMPLO 3: Como observar agendamentos do usuário
         * Mostra como listar e filtrar agendamentos em tempo real
         */
        fun exemploObservarAgendamentosUsuario(
            agendamentosViewModel: AgendamentosViewModel,
            usuarioId: String
        ) {
            // Carrega agendamentos do usuário
            agendamentosViewModel.carregarAgendamentosDoUsuario(usuarioId)

            val scope = CoroutineScope(Dispatchers.Main)

            // Observa todos os agendamentos
            scope.launch {
                agendamentosViewModel.agendamentos.collect { agendamentos ->
                    println("Agendamentos do usuário (${agendamentos.size}):")

                    agendamentos.forEach { agendamento ->
                        println("- ${agendamento.tipoServico} em ${formatarData(agendamento.horario)} - Status: ${agendamento.status}")
                    }

                    // Filtra apenas agendamentos ativos
                    val agendamentosAtivos = agendamentosViewModel.obterAgendamentosAtivos()
                    println("Agendamentos ativos: ${agendamentosAtivos.size}")

                    // Filtra por status específico
                    val agendados = agendamentosViewModel.filtrarPorStatus(StatusAgendamento.AGENDADO)
                    println("Agendamentos confirmados: ${agendados.size}")
                }
            }
        }

        /**
         * EXEMPLO 4: Como buscar e filtrar salões
         * Demonstra funcionalidades de busca e filtro
         */
        fun exemploBuscarSaloes(viewModel: SaloesViewModel) {
            val scope = CoroutineScope(Dispatchers.Main)

            scope.launch {
                viewModel.saloes.collect { saloes ->
                    // Busca salões por nome
                    val saloesBuscados = viewModel.buscarSaloesPorNome("Barbearia")
                    println("Salões com 'Barbearia' no nome: ${saloesBuscados.size}")

                    // Filtra salões que oferecem serviço específico
                    val saloesComCorte = viewModel.filtrarSaloesPorServico("Corte de Cabelo")
                    println("Salões que fazem corte: ${saloesComCorte.size}")

                    // Exemplo de uso prático na UI
                    saloesComCorte.forEach { salao ->
                        println("${salao.nome} - ${salao.horarioFuncionamento}")
                        println("Serviços: ${salao.servicos.joinToString(", ")}")
                        println("Avaliação: ${salao.avaliacaoMedia}/5.0")
                        println("---")
                    }
                }
            }
        }

        /**
         * EXEMPLO 5: Como gerenciar usuário
         * Demonstra cadastro e atualização de perfil
         */
        fun exemploGerenciarUsuario(usuarioViewModel: UsuarioViewModel) {
            // Cadastra um novo usuário
            val novoUsuario = Usuario(
                nome = "João Silva",
                email = "joao.silva@email.com",
                telefone = "(11) 99999-9999"
            )

            usuarioViewModel.cadastrarUsuario(novoUsuario)

            val scope = CoroutineScope(Dispatchers.Main)

            // Observa o usuário logado
            scope.launch {
                usuarioViewModel.usuarioLogado.collect { usuario ->
                    usuario?.let {
                        println("Usuário logado: ${it.nome}")
                        println("Email: ${it.email}")
                        println("Telefone: ${it.telefone}")

                        // Exemplo de atualização de perfil
                        val dadosParaAtualizar = mapOf(
                            "telefone" to "(11) 88888-8888",
                            "nome" to "João Silva Santos"
                        )

                        // Atualiza depois de 5 segundos (apenas exemplo)
                        // usuarioViewModel.atualizarPerfil(dadosParaAtualizar)
                    }
                }
            }

            // Observa operações de sucesso
            scope.launch {
                usuarioViewModel.operacaoSucesso.collect { mensagem ->
                    mensagem?.let {
                        println("Operação realizada: $it")
                    }
                }
            }
        }

        /**
         * EXEMPLO 6: Como cancelar um agendamento
         * Demonstra como cancelar agendamentos existentes
         */
        fun exemploCancelarAgendamento(
            agendamentosViewModel: AgendamentosViewModel,
            agendamentoId: String
        ) {
            // Cancela o agendamento
            agendamentosViewModel.cancelarAgendamento(agendamentoId)

            val scope = CoroutineScope(Dispatchers.Main)

            // Observa o resultado
            scope.launch {
                agendamentosViewModel.operacaoSucesso.collect { mensagem ->
                    mensagem?.let {
                        println("Agendamento cancelado: $it")
                        // Atualizar UI para refletir o cancelamento
                    }
                }
            }
        }

        // FUNÇÕES UTILITÁRIAS

        /**
         * Cria um timestamp para a próxima segunda-feira no horário especificado
         * @param hora Hora do dia (0-23)
         * @param minuto Minuto da hora (0-59)
         * @return Timestamp para o horário especificado
         */
        private fun criarTimestampParaProximaSegunda(hora: Int, minuto: Int): Timestamp {
            val calendario = Calendar.getInstance()

            // Encontra a próxima segunda-feira
            val diasAteSegunda = (Calendar.MONDAY - calendario.get(Calendar.DAY_OF_WEEK) + 7) % 7
            if (diasAteSegunda == 0) {
                calendario.add(Calendar.DAY_OF_MONTH, 7) // Próxima segunda se hoje já for segunda
            } else {
                calendario.add(Calendar.DAY_OF_MONTH, diasAteSegunda)
            }

            // Define o horário
            calendario.set(Calendar.HOUR_OF_DAY, hora)
            calendario.set(Calendar.MINUTE, minuto)
            calendario.set(Calendar.SECOND, 0)
            calendario.set(Calendar.MILLISECOND, 0)

            return Timestamp(calendario.time)
        }

        /**
         * Formata um Timestamp para string legível
         * @param timestamp Timestamp a ser formatado
         * @return String formatada com data e hora
         */
        private fun formatarData(timestamp: Timestamp): String {
            val formato = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return formato.format(timestamp.toDate())
        }
    }
}
