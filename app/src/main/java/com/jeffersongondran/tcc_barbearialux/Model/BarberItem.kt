package com.jeffersongondran.tcc_barbearialux.Model // Pacote onde a classe está localizada

import java.io.Serializable // Importa a interface Serializable para permitir serialização <button class="citation-flag" data-index="1">


// Classe de modelo que representa um item da barbearia
data class BarberItem(
    val title: String, // Título do item (ex.: "CLUBE DE CAVALHEIROS")
    val subtitle: String, // Subtítulo do item (ex.: "Horário disponível")
    val horario: String, // Horário do serviço (ex.: "10am - 10pm")
    val imageResId: Int // ID do recurso de imagem associado ao item
) : Serializable // Implementa Serializable para permitir a passagem de objetos entre atividades ou fragments <button class="citation-flag" data-index="1">