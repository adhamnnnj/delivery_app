package com.example.myapplicationsem7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class registration : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etConfPass: EditText
    private lateinit var etPass: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvRedirectLogin: TextView
    private lateinit var rgUserType: RadioGroup
    private lateinit var rbBuyer: RadioButton
    private lateinit var rbSeller: RadioButton

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_activity)

        auth = FirebaseAuth.getInstance()

        // Initialize views
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etConfPass = findViewById(R.id.etConfirmPassword)
        etPass = findViewById(R.id.etPassword)
        btnSignUp = findViewById(R.id.btnRegister)
        tvRedirectLogin = findViewById(R.id.tvLoginRedirect)
        rgUserType = findViewById(R.id.rgUserType)
        rbBuyer = findViewById(R.id.rbBuyer)
        rbSeller = findViewById(R.id.rbSeller)

        btnSignUp.setOnClickListener {
            signUpUser()
        }

        tvRedirectLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUpUser() {
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val pass = etPass.text.toString().trim()
        val confirmPassword = etConfPass.text.toString().trim()
        val userType = when {
            rbBuyer.isChecked -> "Buyer"
            rbSeller.isChecked -> "Seller"
            else -> ""
        }

        // Validate fields
        if (email.isEmpty() || phone.isEmpty() || pass.isEmpty() || confirmPassword.isEmpty() || userType.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        // Create user in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userMap = mapOf(
                        "email" to email,
                        "phone" to phone,
                        "userType" to userType
                    )

                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(userId)
                        .setValue(userMap)
                        .addOnCompleteListener { saveTask ->
                            if (saveTask.isSuccessful) {
                                Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show()

                                // Redirect to Login screen after saving data
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()

                            } else {
                                Toast.makeText(this, "Failed to save data: ${saveTask.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }

                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
