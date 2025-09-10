package com.jeffersongondran.luxconnect.Utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Classe responsável por gerenciar as preferências do usuário
 * Utiliza SharedPreferences para persistir dados simples do usuário
 */
class UserPreferences(context: Context) {

    companion object {
        private const val PREFS_NAME = "user_preferences"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Salva o nome do usuário
     * @param name Nome do usuário a ser salvo
     */
    fun saveUserName(name: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_NAME, name)
            .apply()
    }

    /**
     * Obtém o nome do usuário salvo
     * @return Nome do usuário ou "Usuário" como padrão
     */
    fun getUserName(): String {
        return sharedPreferences.getString(KEY_USER_NAME, "Usuário") ?: "Usuário"
    }

    /**
     * Salva o email do usuário
     * @param email Email do usuário a ser salvo
     */
    fun saveUserEmail(email: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    /**
     * Obtém o email do usuário salvo
     * @return Email do usuário ou string vazia como padrão
     */
    fun getUserEmail(): String {
        return sharedPreferences.getString(KEY_USER_EMAIL, "") ?: ""
    }

    /**
     * Define o status de login do usuário
     * @param isLoggedIn true se o usuário está logado, false caso contrário
     */
    fun setUserLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            .apply()
    }

    /**
     * Verifica se o usuário está logado
     * @return true se o usuário está logado, false caso contrário
     */
    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Limpa todas as preferências do usuário
     * Usado principalmente durante o logout
     */
    fun clearUserData() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    /**
     * Salva os dados completos do usuário de uma vez
     * @param name Nome do usuário
     * @param email Email do usuário
     */
    fun saveUserData(name: String, email: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }
}
