/**
 * Classe Application personalizada para inicialização do Firebase
 * e configuração global da aplicação LuxConnect
 */
package com.jeffersongondran.luxconnect

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

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
    }

    /**
     * Inicializa o FirebaseApp se ainda não foi inicializado
     * Garante que o Firebase está pronto para uso em toda a aplicação
     */
    private fun inicializarFirebase() {
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

        // O cache offline agora é habilitado por padrão no Firestore
        // Não é mais necessário configurar manualmente
    }
}
