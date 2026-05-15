package com.example.gramawastetracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

class MainActivity : ComponentActivity() {

    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        credentialManager = CredentialManager.create(this)

        findViewById<Button>(R.id.btnGoogleSignIn).setOnClickListener {
            signInWithGoogle()
        }

        findViewById<TextView>(R.id.registerText).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signInWithGoogle() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .setNonce(generateNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = request
                )

                val googleCredential =
                    GoogleIdTokenCredential.createFrom(result.credential.data)

                val userName = googleCredential.displayName ?: "Citizen"
                val userEmail = googleCredential.id

                getSharedPreferences("user_profile", MODE_PRIVATE)
                    .edit()
                    .putString("name", userName)
                    .putString("email", userEmail)
                    .putBoolean("logged_in", true)
                    .apply()

                Toast.makeText(
                    this@MainActivity,
                    "Login Success",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                intent.putExtra("user_name", userName)
                intent.putExtra("user_email", userEmail)
                startActivity(intent)
                finish()

            } catch (e: GetCredentialException) {
                Log.e("GOOGLE_LOGIN", "Credential error", e)

                Toast.makeText(
                    this@MainActivity,
                    e.javaClass.simpleName + "\n" + e.message,
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                Log.e("GOOGLE_LOGIN", "Unknown error", e)

                Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.joinToString("") { "%02x".format(it) }
    }
}