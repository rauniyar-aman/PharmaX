package com.example.pharmax.repo

import com.example.pharmax.model.UserModel

interface UserRepo {

    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String, UserModel?) -> Unit
    )

    fun sendPasswordResetEmail(
        email: String,
        callback: (Boolean, String) -> Unit
    )

    fun addUser(
        uid: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun rollbackCurrentUserRegistration()

    fun checkPhoneExists(phone: String, callback: (Boolean) -> Unit)

    fun sendVerificationEmail(callback: (Boolean, String) -> Unit)

    fun resendVerificationEmail(email: String, password: String, callback: (Boolean, String) -> Unit)

    fun signOutSilently()

    fun getCurrentUser(callback: (Boolean, UserModel?) -> Unit)

    fun logOut(callback: (Boolean, String) -> Unit)
}
