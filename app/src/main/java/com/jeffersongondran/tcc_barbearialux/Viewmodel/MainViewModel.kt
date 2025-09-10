package com.jeffersongondran.luxconnect.Viewmodel // Pacote onde a classe está localizada

import androidx.lifecycle.LiveData // Importa LiveData para observar mudanças nos dados
import androidx.lifecycle.MutableLiveData // Importa MutableLiveData para atualizar dados de forma segura
import androidx.lifecycle.ViewModel // Importa ViewModel para gerenciar dados relacionados à UI
import com.jeffersongondran.luxconnect.Model.BarberItem
import com.jeffersongondran.luxconnect.Repository.BarberRepository


// Classe ViewModel que gerencia os dados para a tela principal (MainActivity)
class MainViewModel : ViewModel() {
    private val _barberItems = MutableLiveData<List<BarberItem>>() // Dados mutáveis privados para atualização segura
    val barberItems: LiveData<List<BarberItem>> get() = _barberItems // Dados públicos observáveis para a UI

    init {
        loadBarberItems() // Carrega os dados iniciais ao inicializar o ViewModel
    }

    // Método privado para carregar os itens da barbearia
    private fun loadBarberItems() {
        val repository = BarberRepository() // Instância do repositório para fornecer dados
        _barberItems.value = repository.getBarberItems() // Atualiza os dados com os itens fornecidos pelo repositório
    }
}