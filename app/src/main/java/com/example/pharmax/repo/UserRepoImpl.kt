package com.example.pharmax.repo

import com.example.pharmax.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImpl : UserRepo {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val ref = database.getReference("users")

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    callback(true, "Account created successfully", uid)
                } else {
                    callback(false, task.exception?.message ?: "Signup failed", "")
                }
            }
    }

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid

                if (uid.isNullOrBlank()) {
                    auth.signOut()
                    callback(false, "Login failed", null)
                    return@addOnSuccessListener
                }

                ref.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val user = snapshot.getValue(UserModel::class.java)
                            if (user != null) {
                                callback(true, "Login successful", user)
                            }
                        } else {
                            auth.signOut()
                            callback(false, "Your account has been permanently deleted", null)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        auth.signOut()
                        callback(false, error.message, null)
                    }
                })
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Login failed", null)
            }
    }

    override fun sendPasswordResetEmail(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Password reset email sent successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to send reset email")
                }
            }
    }

    override fun addUser(
        uid: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(uid).setValue(model)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Signup Successful")
                } else {
                    callback(false, task.exception?.message ?: "Failed to save user")
                }
            }
    }

    override fun rollbackCurrentUserRegistration() {
        auth.currentUser?.delete()
        auth.signOut()
    }

    override fun getCurrentUser(callback: (Boolean, UserModel?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            callback(false, null)
            return
        }
        ref.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                callback(user != null, user)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, null)
            }
        })
    }

    override fun logOut(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logout successful")
        } catch (e: Exception) {
            callback(false, e.toString())
        }
    }
}
