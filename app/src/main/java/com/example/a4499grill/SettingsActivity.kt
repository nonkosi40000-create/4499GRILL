package com.example.a4499grill

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val switchDarkMode = findViewById<MaterialSwitch>(R.id.switch_dark_mode)
        val etLocation = findViewById<EditText>(R.id.et_settings_location)
        val etAddress = findViewById<EditText>(R.id.et_settings_address)
        val btnSave = findViewById<Button>(R.id.btn_save_settings)

        val sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("DARK_MODE", true)
        switchDarkMode.isChecked = isDarkMode

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("DARK_MODE", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: ""

        if (uid.isNotEmpty()) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        etLocation.setText(doc.getString("location") ?: "")
                        etAddress.setText(doc.getString("address") ?: "")
                    }
                }
        }

        btnSave.setOnClickListener {
            val location = etLocation.text.toString().trim()
            val address = etAddress.text.toString().trim()

            if (uid.isNotEmpty()) {
                val updates = hashMapOf<String, Any>(
                    "location" to location,
                    "address" to address
                )
                db.collection("users").document(uid).update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Preferences Saved Successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
