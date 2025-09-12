/**
 * Classe Application personalizada para inicialização do Firebase
 * e configuração global da aplicação LuxConnect
 */
package com.jeffersongondran.luxconnect

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

/**
 * Classe Application responsável pela inicialização do Firebase
 * e configuração de instâncias singleton para toda a aplicação
 */
class LuxConnectApplication : Application() {

    companion object {
        /**
         * Obtém a instância do FirebaseFirestore configurada
         * Esta é uma forma segura de acessar o Firestore sem vazamentos de memória
         */
        fun obterFirestore(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Inicializa o Firebase
        inicializarFirebase()

        // Configura o Firestore com persistência offline habilitada
        // e demais ajustes globais. Isso garante que o aplicativo funcione
        // mesmo sem conexão e sincronize assim que possível.
        configurarFirestoreGlobal()
    }

    /**
     * Inicializa o FirebaseApp se ainda não foi inicializado
     * Garante que o Firebase está pronto para uso em toda a aplicação
     */
    private fun inicializarFirebase() {
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
    }

    /**
     * Aplica configurações globais do Firestore (persistência offline, etc.)
     * Observação: Em Android, a persistência offline já é habilitada por padrão,
     * mas deixamos explícito para documentação e futuras customizações.
     */
    private fun configurarFirestoreGlobal() {
        val instancia = FirebaseFirestore.getInstance()
        val configuracoes = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true) // Habilita cache offline
            .build()
        instancia.firestoreSettings = configuracoes
    }
}
