package com.example.a4499grill

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etLocation: EditText
    private lateinit var etAddress: EditText
    private lateinit var tvNameTop: TextView
    private lateinit var ivAvatar: ImageView
    private lateinit var switchDarkMode: SwitchCompat
    
    private var selectedImageUri: Uri? = null
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            ivAvatar.setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvNameTop = findViewById(R.id.tv_profile_name)
        ivAvatar = findViewById(R.id.iv_profile_avatar)
        
        etFullName = findViewById(R.id.et_full_name)
        etEmail = findViewById(R.id.et_email)
        etPhone = findViewById(R.id.et_phone)
        etLocation = findViewById(R.id.et_location)
        etAddress = findViewById(R.id.et_address)
        switchDarkMode = findViewById(R.id.switch_dark_mode)

        // Ensure text font color is black when viewing or typing words
        val blackColor = android.graphics.Color.BLACK
        etFullName.setTextColor(blackColor)
        etEmail.setTextColor(blackColor)
        etPhone.setTextColor(blackColor)
        etLocation.setTextColor(blackColor)
        etAddress.setTextColor(blackColor)

        // Setup Dark Mode Switch
        val sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("DARK_MODE", false)
        switchDarkMode.isChecked = isDarkMode

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("DARK_MODE", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val uid = auth.currentUser?.uid ?: ""

        // Fetch and display user details
        if (uid.isNotEmpty()) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val fullName = doc.getString("fullName") ?: ""
                        val email = doc.getString("email") ?: ""
                        val phone = doc.getString("phoneNumber") ?: ""
                        val location = doc.getString("location") ?: ""
                        val address = doc.getString("address") ?: ""
                        val avatarUrl = doc.getString("profilePictureUrl") ?: ""

                        tvNameTop.text = fullName.ifEmpty { "User Name" }
                        etFullName.setText(fullName)
                        etEmail.setText(email)
                        etPhone.setText(phone)
                        etLocation.setText(location)
                        etAddress.setText(address)

                        if (avatarUrl.isNotEmpty()) {
                            Glide.with(this).load(avatarUrl).into(ivAvatar)
                        }
                    }
                }
        }

        // Make profile picture uploadable/clickable
        ivAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        // Save / Update profile details
        findViewById<View>(R.id.btn_update_profile)?.setOnClickListener {
            val updatedName = etFullName.text.toString().trim()
            val updatedPhone = etPhone.text.toString().trim()
            val updatedLocation = etLocation.text.toString().trim()
            val updatedAddress = etAddress.text.toString().trim()

            if (uid.isNotEmpty()) {
                val updates = hashMapOf<String, Any>(
                    "fullName" to updatedName,
                    "phoneNumber" to updatedPhone,
                    "location" to updatedLocation,
                    "address" to updatedAddress
                )
                
                // If we have a local URI, we should ideally upload it to Firebase Storage.
                // However, following the instruction not to restore rolled back Firebase Storage changes,
                // I will just save the URI string for now as requested by the current content logic.
                selectedImageUri?.let { uri ->
                    updates["profilePictureUrl"] = uri.toString()
                }

                db.collection("users").document(uid).update(updates)
                    .addOnSuccessListener {
                        tvNameTop.text = updatedName
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // Sign Out function
        findViewById<View>(R.id.btn_logout)?.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        // Bottom Navigation
        findViewById<View>(R.id.nav_explore_btn)?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.nav_orders_btn)?.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.nav_community_btn)?.setOnClickListener {
            startActivity(Intent(this, CommunityActivity::class.java))
            finish()
        }
    }
}
