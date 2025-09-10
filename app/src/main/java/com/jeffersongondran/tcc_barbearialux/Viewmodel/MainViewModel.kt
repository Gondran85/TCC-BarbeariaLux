package com.jeffersongondran.luxconnect.Viewmodel // Pacote onde a classe está localizada

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData // Importa LiveData para observar mudanças nos dados
import androidx.lifecycle.MutableLiveData // Importa MutableLiveData para atualizar dados de forma segura
import com.jeffersongondran.luxconnect.Model.BarberItem
import com.jeffersongondran.luxconnect.Repository.BarberRepository
import com.jeffersongondran.luxconnect.Utils.UserPreferences
import java.util.Locale

// Classe ViewModel que gerencia os dados para a tela principal (MainActivity)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _barberItems = MutableLiveData<List<BarberItem>>() // Dados mutáveis privados para atualização segura
    val barberItems: LiveData<List<BarberItem>> get() = _barberItems // Dados públicos observáveis para a UI

    // LiveData para o nome do usuário
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    // Lista original de barbearias para referência na pesquisa
    private var listaOriginalBarbearias: List<BarberItem> = emptyList()

    // Instância das preferências do usuário
    private val userPreferences = UserPreferences(application.applicationContext)

    init {
        loadBarberItems() // Carrega os dados iniciais ao inicializar o ViewModel
        loadUserName() // Carrega o nome do usuário
    }

    // Método privado para carregar os itens da barbearia
    private fun loadBarberItems() {
        val repository = BarberRepository() // Instância do repositório para fornecer dados
        listaOriginalBarbearias = repository.getBarberItems() // Armazena a lista original
        _barberItems.value = listaOriginalBarbearias // Atualiza os dados com os itens fornecidos pelo repositório
    }

    /**
     * Carrega o nome do usuário das preferências
     */
    private fun loadUserName() {
        val name = userPreferences.getUserName()
        _userName.value = name
    }

    /**
     * Atualiza o nome do usuário
     * @param name Novo nome do usuário
     */
    fun updateUserName(name: String) {
        userPreferences.saveUserName(name)
        _userName.value = name
    }

    /**
     * Filtra a lista de barbearias baseado no texto de pesquisa
     * @param textoPesquisa O texto inserido pelo usuário para filtrar
     */
    fun pesquisarBarbearias(textoPesquisa: String) {
        val textoLimpo = textoPesquisa.trim()

        if (textoLimpo.isEmpty()) {
            // Se não há texto de pesquisa, mostra todas as barbearias
            _barberItems.value = listaOriginalBarbearias
        } else {
            // Filtra as barbearias que contêm o texto pesquisado no nome
            val listaFiltrada = listaOriginalBarbearias.filter { barbearia ->
                barbearia.nomeDoServico.lowercase(Locale.getDefault())
                    .contains(textoLimpo.lowercase(Locale.getDefault()))
            }
            _barberItems.value = listaFiltrada
        }
    }

    /**
     * Limpa a pesquisa e retorna à lista completa de barbearias
     */
    fun limparPesquisa() {
        _barberItems.value = listaOriginalBarbearias
    }
}