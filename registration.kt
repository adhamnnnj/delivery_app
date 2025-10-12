package com.example.myapplicationsem7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

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

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Bind views
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
        val email = etEmail.text.toString()
        val phone = etPhone.text.toString()
        val pass = etPass.text.toString()
        val confirmPassword = etConfPass.text.toString()
        val userType = if (rbBuyer.isChecked) "Buyer" else "Seller"

        if (email.isBlank() || pass.isBlank() || confirmPassword.isBlank() || phone.isBlank()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Create Firebase user
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Sign Up Successful as $userType!", Toast.LENGTH_SHORT).show()

                    // (Optional) Save phone and user type to Firestore later

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
