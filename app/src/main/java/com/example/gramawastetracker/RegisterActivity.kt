package com.example.gramawastetracker

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : ComponentActivity() {

    private lateinit var nameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var villageInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nameInput = findViewById(R.id.nameInput)
        phoneInput = findViewById(R.id.phoneInput)
        villageInput = findViewById(R.id.villageInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)

        findViewById<TextView>(R.id.backToLoginBtn).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.registerBtn).setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name = nameInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val village = villageInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
            return
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show()
            return
        }

        if (village.isEmpty()) {
            Toast.makeText(this, "Enter village name", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        findViewById<TextView>(R.id.registerBtn).isEnabled = false
        findViewById<TextView>(R.id.registerBtn).text = "CREATING ACCOUNT..."

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid

                if (userId == null) {
                    Toast.makeText(this, "User creation failed", Toast.LENGTH_SHORT).show()
                    resetButton()
                    return@addOnSuccessListener
                }

                val userData = hashMapOf(
                    "uid" to userId,
                    "name" to name,
                    "phone" to phone,
                    "village" to village,
                    "email" to email,
                    "role" to "Citizen",
                    "createdAt" to System.currentTimeMillis()
                )

                db.collection("users")
                    .document(userId)
                    .set(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_LONG).show()

                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Profile save failed: ${e.message}", Toast.LENGTH_LONG).show()
                        resetButton()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
                resetButton()
            }
    }

    private fun resetButton() {
        findViewById<TextView>(R.id.registerBtn).isEnabled = true
        findViewById<TextView>(R.id.registerBtn).text = "REGISTER"
    }
}