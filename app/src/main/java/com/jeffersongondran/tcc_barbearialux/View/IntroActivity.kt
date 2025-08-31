package com.jeffersongondran.tcc_barbearialux.View // Pacote onde a classe está localizada

import android.content.Intent // Importa a classe Intent para navegar entre atividades <button class="citation-flag" data-index="1">
import android.os.Bundle // Importa classes necessárias para gerenciar o ciclo de vida da atividade
import androidx.appcompat.app.AppCompatActivity // Importa a classe base para atividades compatíveis com a biblioteca de suporte
import com.jeffersongondran.tcc_barbearialux.databinding.ActivityIntroBinding // Importa o binding gerado automaticamente para acessar elementos do layout
import kotlin.jvm.java

// Classe que representa a tela inicial (intro) do aplicativo
class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding // Binding para acessar os elementos do layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater) // Infla o layout usando View Binding
        setContentView(binding.root) // Define o conteúdo da atividade como o layout inflado

        // Configurar o botão "Entrar"
        binding.btnEntrar.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java)) // Navega para a tela de login <button class="citation-flag" data-index="1">
        }

        // Configurar o botão "Inscrever-se"
        binding.btnIncrever.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java)) // Navega para a tela de inscrição <button class="citation-flag" data-index="1">
        }
    }
}