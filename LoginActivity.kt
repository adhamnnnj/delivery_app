package com.example.myapplicationsem7

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var tvRedirectSignUp: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        tvRedirectSignUp = findViewById(R.id.tvRedirectSignUp)
        etEmail = findViewById(R.id.etEmail)
        etPass = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Handle login button
        btnLogin.setOnClickListener {
            login()
        }

        // Redirect to registration screen
        tvRedirectSignUp.setOnClickListener {
            val intent = Intent(this, registration::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login() {
        val email = etEmail.text.toString().trim()
        val pass = etPass.text.toString().trim()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(applicationContext, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid

                    if (userId != null) {
                        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                        // Record or update data in Realtime Database
                        val userData = mapOf(
                            "email" to user.email,
                            "lastLogin" to System.currentTimeMillis().toString()
                        )

                        userRef.updateChildren(userData)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(applicationContext, "Successfully Logged In!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(applicationContext, "Login success, but failed to update database.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }

                    // Redirect to next activity (optional)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
