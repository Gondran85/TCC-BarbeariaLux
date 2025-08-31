package com.jeffersongondran.tcc_barbearialux.View // Pacote onde a classe está localizada

import android.content.Intent // Importa a classe Intent para navegar entre atividades <button class="citation-flag" data-index="1">
import android.os.Bundle // Importa classes necessárias para gerenciar o ciclo de vida da atividade
import androidx.activity.viewModels // Importa a função viewModels para inicializar ViewModel <button class="citation-flag" data-index="5">
import androidx.appcompat.app.AppCompatActivity // Importa a classe base para atividades compatíveis com a biblioteca de suporte
import androidx.recyclerview.widget.LinearLayoutManager // Importa o LinearLayoutManager para configurar o RecyclerView
import com.google.firebase.FirebaseApp // Importa a classe FirebaseApp para inicializar o Firebase <button class="citation-flag" data-index="3">
import com.jeffersongondran.tcc_barbearialux.Adapter.BarberAdapter // Importa o adapter personalizado para o RecyclerView
import com.jeffersongondran.tcc_barbearialux.Viewmodel.MainViewModel // Importa o ViewModel associado a esta atividade
import com.jeffersongondran.tcc_barbearialux.databinding.ActivityMainBinding // Importa o binding gerado automaticamente para acessar elementos do layout
import kotlin.jvm.java

// Classe que representa a tela principal (MainActivity) do aplicativo
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // Binding para acessar os elementos do layout
    private val viewModel: MainViewModel by viewModels() // Inicializa o ViewModel usando a biblioteca AndroidX <button class="citation-flag" data-index="5">

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this) // Inicializa o Firebase no aplicativo <button class="citation-flag" data-index="3">
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Infla o layout usando View Binding
        setContentView(binding.root) // Define o conteúdo da atividade como o layout inflado

        // Configurar o botão "Card" para navegar para a tela de escolha de serviço
        binding.btnCard.setOnClickListener {
            startActivity(Intent(this, EscolhaServicoActivity::class.java)) // Navega para a EscolhaServicoActivity <button class="citation-flag" data-index="1">
        }

        // Configurar o botão de voltar
        binding.btnVoltarHome.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Fecha a atividade ou volta para a tela anterior <button class="citation-flag" data-index="4">
        }

        setupRecyclerView() // Configura o RecyclerView
        observeViewModel() // Observa mudanças nos dados fornecidos pelo ViewModel
    }

    // Método para configurar o RecyclerView
    private fun setupRecyclerView() {
        // Configura o LinearLayoutManager para orientação horizontal
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.setHasFixedSize(true) // Otimiza o desempenho ao informar que o tamanho do RecyclerView não muda
    }

    // Método para observar mudanças nos dados fornecidos pelo ViewModel
    private fun observeViewModel() {
        viewModel.barberItems.observe(this) { items ->
            // Atualiza o adapter do RecyclerView com os itens observados
            binding.recyclerView.adapter = BarberAdapter(items) { clickedItem ->
                // Navegar para a EscolhaServicoActivity ao clicar em um item
                val intent = Intent(this, EscolhaServicoActivity::class.java).apply {
                    putExtra(
                        "BARBER_ITEM",
                        clickedItem
                    ) // Passa os dados do item clicado para a próxima tela
                }
                startActivity(intent) // Inicia a nova atividade <button class="citation-flag" data-index="1">
            }
        }
    }
}