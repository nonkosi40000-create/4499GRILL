package com.example.a4499grill

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Removed Auto-login check to ensure users must sign in manually every time
        // if (auth.currentUser != null) {
        //     checkStatusAndNavigate(auth.currentUser!!.uid)
        // }

        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnSignIn = findViewById<Button>(R.id.btn_sign_in)
        val tvCreateAccount = findViewById<TextView>(R.id.tv_create_account)

        // Ensure text color is black when typing
        etEmail.setTextColor(android.graphics.Color.BLACK)
        etPassword.setTextColor(android.graphics.Color.BLACK)

        btnSignIn.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        checkStatusAndNavigate(auth.currentUser!!.uid)
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun checkStatusAndNavigate(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val status = doc.getString("status") ?: "Approved"
                    val role = doc.getString("role") ?: "Customer"

                    if (status == "Pending") {
                        Toast.makeText(this, "Your account is still pending manager approval.", Toast.LENGTH_LONG).show()
                        auth.signOut()
                    } else if (status == "Rejected") {
                        Toast.makeText(this, "Your registration was rejected by the manager.", Toast.LENGTH_LONG).show()
                        auth.signOut()
                    } else {
                        navigateToDashboard(role)
                    }
                } else {
                    navigateToDashboard("Customer")
                }
            }
            .addOnFailureListener {
                navigateToDashboard("Customer")
            }
    }

    private fun navigateToDashboard(role: String) {
        val intent = when (role) {
            "Manager" -> Intent(this, ManagerDashboardActivity::class.java)
            "Worker" -> Intent(this, WorkerDashboardActivity::class.java)
            else -> Intent(this, HomeActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
