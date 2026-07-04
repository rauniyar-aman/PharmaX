package com.example.pharmax.model

data class UserModel(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "user",
    val profileImageUrl: String = "",
    val darkMode: Boolean = false
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "role" to role,
            "profileImageUrl" to profileImageUrl,
            "darkMode" to darkMode
        )
    }
}
