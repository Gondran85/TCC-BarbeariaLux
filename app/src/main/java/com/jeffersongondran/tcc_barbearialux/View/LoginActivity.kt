package com.jeffersongondran.tcc_barbearialux.View// Pacote onde a classe está localizada

import android.content.Intent // Importa a classe Intent para navegar entre atividades <button class="citation-flag" data-index="1">
import android.os.Bundle // Importa classes necessárias para gerenciar o ciclo de vida da atividade
import android.widget.Toast // Importa a classe Toast para exibir mensagens curtas na tela <button class="citation-flag" data-index="6">
import androidx.appcompat.app.AppCompatActivity // Importa a classe base para atividades compatíveis com a biblioteca de suporte
import com.google.firebase.auth.FirebaseAuth // Importa a classe FirebaseAuth para autenticação com Firebase <button class="citation-flag" data-index="3">
import kotlin.jvm.java
import com.jeffersongondran.tcc_barbearialux.databinding.ActivityLoginBinding // Importa o binding gerado automaticamente para acessar elementos do layout

// Classe que representa a tela de login do aplicativo
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding // Binding para acessar os elementos do layout
    private lateinit var auth: FirebaseAuth // Instância do Firebase Authentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater) // Infla o layout usando View Binding
        setContentView(binding.root) // Define o conteúdo da atividade como o layout inflado

        // Inicializar o Firebase Auth
        auth = FirebaseAuth.getInstance() // Obtém a instância do Firebase Authentication <button class="citation-flag" data-index="3">

        // Verificar se o usuário já está autenticado
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java)) // Navega para a MainActivity se o usuário já estiver logado <button class="citation-flag" data-index="1">
            finish() // Finaliza a atividade atual
        }

        // Configurar o botão de entrar
        binding.btnEntrar.setOnClickListener {
            val email = binding.etEmail.text.toString() // Obtém o e-mail digitado pelo usuário
            val password = binding.etSenha.text.toString() // Obtém a senha digitada pelo usuário

            // Validar os campos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show() // Exibe uma mensagem de erro caso algum campo esteja vazio <button class="citation-flag" data-index="6">
                return@setOnClickListener // Encerra a execução do listener
            }

            // Autenticar o usuário no Firebase
            auth.signInWithEmailAndPassword(email, password) // Tenta autenticar o usuário com o e-mail e senha fornecidos <button class="citation-flag" data-index="3">
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Autenticação bem-sucedida
                        Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show() // Exibe uma mensagem de sucesso <button class="citation-flag" data-index="6">
                        startActivity(Intent(this, MainActivity::class.java)) // Navega para a MainActivity <button class="citation-flag" data-index="1">
                        finish() // Finaliza a atividade atual
                    } else {
                        // Autenticação falhou
                        Toast.makeText(this, "Falha no login: ${task.exception?.message}", Toast.LENGTH_SHORT).show() // Exibe uma mensagem de erro com detalhes <button class="citation-flag" data-index="6">
                    }
                }
        }

        // Redirecionar para a tela de inscrição
        binding.naoTemContaTextView.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java)) // Navega para a tela de inscrição <button class="citation-flag" data-index="1">
        }

        // Botão voltar
        binding.btnVoltarLogin.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Fecha a atividade ou volta para a tela anterior <button class="citation-flag" data-index="4">
        }
    }
}