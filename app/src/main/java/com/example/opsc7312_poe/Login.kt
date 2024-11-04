package com.example.opsc7312_poe

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager.Authenticators
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.common.SignInButton
import java.util.concurrent.Executor
import java.util.Locale
import android.content.res.Configuration
import android.preference.PreferenceManager

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load saved language setting before setting the content view
        loadLocale()
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize Firebase authentication
        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Google Sign-In Options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set up biometric authentication for fingerprint and facial recognition
        setupBiometricAuthentication()

        // Sign-up link listener
        findViewById<TextView>(R.id.signUpLink).setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        // Sign-in button listener
        findViewById<TextView>(R.id.signInButton).setOnClickListener {
            val email = findViewById<TextView>(R.id.emailEditText).text.toString().trim()
            val password = findViewById<TextView>(R.id.passwordEditText).text.toString().trim()
            if (validateInputs(email, password)) {
                signInUser(email, password)
            }
        }

        // Google Sign-In button listener
        findViewById<SignInButton>(R.id.googleSignInButton).setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun loadLocale() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val languageCode = sharedPreferences.getString("language", "en") ?: "en"
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale) // For RTL languages
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setupBiometricAuthentication() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Device supports biometrics with strong authentication
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, "No biometric features available on this device", Toast.LENGTH_SHORT).show()
                return
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, "Biometric hardware is not available", Toast.LENGTH_SHORT).show()
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(this, "No biometrics enrolled. Please enroll at least one biometric option.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Executor for running the BiometricPrompt
        val executor: Executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@Login, Home::class.java))
                finish()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }
        })

        // Configure the biometric prompt without setting a negative button
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login with Biometrics")
            .setSubtitle("Use your face or fingerprint to login")
            .setAllowedAuthenticators(Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL)
            .build()

        // Set up TextView click listener for biometric authentication
        findViewById<TextView>(R.id.fingerprintLoginTextView).setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
