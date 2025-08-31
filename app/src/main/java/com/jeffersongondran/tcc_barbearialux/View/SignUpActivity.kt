package com.jeffersongondran.tcc_barbearialux.View// Pacote onde a classe está localizada

import android.content.Intent // Importa a classe Intent para navegar entre atividades <button class="citation-flag" data-index="1">
import android.os.Bundle // Importa classes necessárias para gerenciar o ciclo de vida da atividade
import android.widget.Toast // Importa a classe Toast para exibir mensagens curtas na tela <button class="citation-flag" data-index="6">
import androidx.appcompat.app.AppCompatActivity // Importa a classe base para atividades compatíveis com a biblioteca de suporte
import com.google.firebase.auth.FirebaseAuth // Importa a classe FirebaseAuth para autenticação com Firebase <button class="citation-flag" data-index="3">
import kotlin.jvm.java
import com.jeffersongondran.tcc_barbearialux.databinding.ActivitySignUpBinding // Importa o binding gerado automaticamente para acessar elementos do layout


// Classe que representa a tela de inscrição (sign-up) do aplicativo
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding // Binding para acessar os elementos do layout
    private lateinit var auth: FirebaseAuth // Instância do Firebase Authentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater) // Infla o layout usando View Binding
        setContentView(binding.root) // Define o conteúdo da atividade como o layout inflado

        // Configurar o botão "Voltar"
        binding.btnVoltarSign.setOnClickListener {
            finish() // Fecha a atividade atual e retorna à tela anterior
        }

        // Inicializar o Firebase Auth
        auth = FirebaseAuth.getInstance() // Obtém a instância do Firebase Authentication <button class="citation-flag" data-index="3">

        // Configurar o botão de inscrição
        binding.btnIncrever.setOnClickListener {
            val email = binding.etEmail.text.toString() // Obtém o e-mail digitado pelo usuário
            val password = binding.etSenha.text.toString() // Obtém a senha digitada pelo usuário

            // Validar os campos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show() // Exibe uma mensagem de erro caso algum campo esteja vazio <button class="citation-flag" data-index="6">
                return@setOnClickListener // Encerra a execução do listener
            }

            // Criar conta no Firebase
            auth.createUserWithEmailAndPassword(email, password) // Tenta criar uma nova conta com o e-mail e senha fornecidos <button class="citation-flag" data-index="3">
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Autenticação bem-sucedida
                        val user = auth.currentUser // Obtém o usuário atualmente autenticado
                        Toast.makeText(
                            this@SignUpActivity,
                            "Cadastro realizado com sucesso! Bem-vindo: ${user?.email}",
                            Toast.LENGTH_SHORT
                        ).show() // Exibe uma mensagem de sucesso com o e-mail do usuário <button class="citation-flag" data-index="6">
                        // Navegar para a MainActivity
                        startActivity(Intent(this@SignUpActivity, MainActivity::class.java)) // Navega para a MainActivity <button class="citation-flag" data-index="1">
                        finish() // Finaliza a atividade atual
                    } else {
                        // Autenticação falhou
                        Toast.makeText(
                            this@SignUpActivity,
                            "Falha no cadastro: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show() // Exibe uma mensagem de erro com detalhes <button class="citation-flag" data-index="6">
                    }
                }
        }
    }
}