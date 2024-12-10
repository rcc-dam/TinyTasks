package com.rcc.tinytasks.model

data class User(
    val userId: String?,
    val displayName: String?,
) {
    fun toMap(): MutableMap<String, String?> { // funcion que obtiene los pares
        return mutableMapOf(
            "user_Id" to this.userId,
            "display_name" to this.displayName,
        )
    }
}