package com.jeffersongondran.tcc_barbearialux.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jeffersongondran.tcc_barbearialux.Model.BarberItem
import com.jeffersongondran.tcc_barbearialux.databinding.ItemBarberBinding

/**
 * Adapter responsável por exibir uma lista de serviços de barbearia no RecyclerView.
 *
 * Este adapter segue o padrão ViewHolder para otimizar a performance da lista,
 * reutilizando as views conforme o usuário faz scroll.
 *
 * @param listaItensBarberaria Lista contendo todos os itens de serviços da barbearia
 * @param acaoCliqueItem Função que será executada quando um item da lista for clicado
 */
class BarberAdapter(
    private val listaItensBarberaria: List<BarberItem>,
    private val acaoCliqueItem: (BarberItem) -> Unit
) : RecyclerView.Adapter<BarberAdapter.BarberViewHolder>() {

    /**
     * ViewHolder interno que mantém as referências das views de cada item da lista.
     *
     * O ViewHolder é uma classe que "segura" as views de um item específico,
     * evitando que o sistema precise procurar essas views repetidas vezes.
     * Isso melhora significativamente a performance da lista.
     */
    inner class BarberViewHolder(
        private val bindingDoItem: ItemBarberBinding
    ) : RecyclerView.ViewHolder(bindingDoItem.root) {

        init {
            // Configuramos o clique no item assim que o ViewHolder é criado
            configurarCliqueNoItem()
        }

        /**
         * Configura o que acontece quando o usuário clica em um item da lista.
         *
         * Verificamos se a posição é válida antes de executar a ação,
         * pois durante animações a posição pode ser inválida.
         */
        private fun configurarCliqueNoItem() {
            bindingDoItem.root.setOnClickListener {
                val posicaoAtual = adapterPosition

                if (posicaoAtual != RecyclerView.NO_POSITION) {
                    val itemClicado = listaItensBarberaria[posicaoAtual]
                    acaoCliqueItem(itemClicado)
                }
            }
        }

        /**
         * Vincula os dados de um BarberItem específico às views do layout.
         *
         * Este método é chamado toda vez que um novo item precisa ser exibido
         * ou quando um item existente precisa ser atualizado.
         *
         * @param itemBarberaria O item contendo os dados a serem exibidos
         */
        fun vincularDados(itemBarberaria: BarberItem) {
            with(bindingDoItem) {
                // Definindo o nome do serviço (ex: "CORTE DE CABELO")
                tituloTextView.text = itemBarberaria.nomeDoServico

                // Definindo a descrição do serviço (ex: "Corte moderno e estiloso")
                subtituloTextView.text = itemBarberaria.descricaoDoServico

                // Definindo o horário de funcionamento (ex: "10h - 18h")
                horarioTextView.text = itemBarberaria.horarioFuncionamento

                // Definindo a imagem representativa do serviço
                imageView.setImageResource(itemBarberaria.imagemDoServico)
            }
        }
    }

    /**
     * Cria um novo ViewHolder quando o RecyclerView precisa de uma nova view.
     *
     * Este método é chamado apenas quando não há ViewHolders reutilizáveis disponíveis.
     * O RecyclerView reutiliza ViewHolders sempre que possível para economizar memória.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberViewHolder {
        // Inflamos o layout XML transformando-o em uma view utilizável
        val binding = ItemBarberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return BarberViewHolder(binding)
    }

    /**
     * Vincula os dados de um item específico a um ViewHolder existente.
     *
     * Este método é chamado toda vez que um ViewHolder precisa exibir
     * dados de um item diferente (quando o usuário faz scroll, por exemplo).
     */
    override fun onBindViewHolder(holder: BarberViewHolder, position: Int) {
        val itemNaPosicao = listaItensBarberaria[position]
        holder.vincularDados(itemNaPosicao)
    }

    /**
     * Retorna a quantidade total de itens na lista.
     *
     * O RecyclerView usa esse número para saber quantos itens precisa gerenciar
     * e para calcular o tamanho da barra de rolagem.
     */
    override fun getItemCount(): Int = listaItensBarberaria.size
}