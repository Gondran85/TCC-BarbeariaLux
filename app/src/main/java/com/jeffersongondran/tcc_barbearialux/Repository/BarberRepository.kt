package com.jeffersongondran.luxconnect.Repository

import com.jeffersongondran.luxconnect.Model.BarberItem // Importa a classe BarberItem, que representa um item da barbearia
import com.jeffersongondran.luxconnect.R // Importa os recursos de layout e imagens do projeto




// Classe responsável por fornecer dados de barbearias ou serviços
class BarberRepository {
    // Método que retorna uma lista de itens (barbearias ou serviços)
    fun getBarberItems(): List<BarberItem> {
        return listOf(
            // Cada item representa uma barbearia ou serviço com título, subtítulo, horário e imagem
            BarberItem(
                "CLUBE DE STYLE",
                "Horário disponível",
                "10am - 10pm",
                R.drawable.lucid_realism_create_a_photograph_of_a_bustling_barbershop_wit_0
            ), // Primeiro item da lista
            BarberItem(
                "CLUBE DE CABELO",
                "Horário disponível",
                "10am - 10pm",
                R.drawable.lucid_realism_a_highly_detailed_and_super_realistic_depiction_1
            ), // Segundo item da lista
            BarberItem(
                "CLUBE DE BELEZA",
                "Horário disponível",
                "10am - 10pm",
                R.drawable.lucid_realism_crie_fotos_de_um_salo_de_beleza_moderno_e_elegan_0
            ), // Terceiro item da lista
            BarberItem(
                "CLUBE DE BARBA",
                "Horário disponível",
                "10am - 10pm",
                R.drawable.lucid_realism_a_surreal_and_vibrant_cinematic_photo_of_a_barbe_2
            )  // Quarto item da lista
        ) // Retorna uma lista imutável de objetos BarberItem <button class="citation-flag" data-index="1">
    }
}