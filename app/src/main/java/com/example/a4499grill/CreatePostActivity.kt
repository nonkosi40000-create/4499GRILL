package com.example.a4499grill

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CreatePostActivity : AppCompatActivity() {

    private lateinit var etCaption: EditText
    private lateinit var ivPostImage: ImageView
    private lateinit var pbUpload: ProgressBar
    private var selectedImageUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            ivPostImage.setImageURI(selectedImageUri)
            ivPostImage.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        etCaption = findViewById(R.id.et_post_caption)
        ivPostImage = findViewById(R.id.iv_post_image)
        pbUpload = findViewById(R.id.pb_upload)

        findViewById<View>(R.id.btn_close).setOnClickListener { finish() }
        findViewById<View>(R.id.btn_add_photo).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        findViewById<View>(R.id.btn_publish).setOnClickListener {
            publishPost()
        }
    }

    private fun publishPost() {
        val caption = etCaption.text.toString().trim()
        if (caption.isEmpty() && selectedImageUri == null) {
            Toast.makeText(this, "Please add a caption or a photo", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return
        pbUpload.visibility = View.VISIBLE
        findViewById<View>(R.id.btn_publish).isEnabled = false

        // Fetch user name first
        db.collection("users").document(userId).get().addOnSuccessListener { userDoc ->
            val userName = userDoc.getString("fullName") ?: "Anonymous"
            val userProfilePic = userDoc.getString("profilePictureUrl") ?: ""

            if (selectedImageUri != null) {
                uploadImageAndPost(userId, userName, userProfilePic, caption)
            } else {
                savePostToFirestore(userId, userName, userProfilePic, caption, emptyList())
            }
        }.addOnFailureListener {
            pbUpload.visibility = View.GONE
            findViewById<View>(R.id.btn_publish).isEnabled = true
            Toast.makeText(this, "Failed to get user info", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageAndPost(userId: String, userName: String, profilePic: String, caption: String) {
        val fileName = UUID.randomUUID().toString()
        val ref = storage.reference.child("posts/$fileName")

        ref.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url ->
                    savePostToFirestore(userId, userName, profilePic, caption, listOf(url.toString()))
                }
            }
            .addOnFailureListener {
                pbUpload.visibility = View.GONE
                findViewById<View>(R.id.btn_publish).isEnabled = true
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun savePostToFirestore(userId: String, userName: String, profilePic: String, caption: String, imageUrls: List<String>) {
        val postId = UUID.randomUUID().toString()
        val post = CommunityPost(
            postId = postId,
            userId = userId,
            userName = userName,
            userProfilePic = profilePic,
            caption = caption,
            imageUrls = imageUrls
        )

        db.collection("community").document(postId).set(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Post published!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                pbUpload.visibility = View.GONE
                findViewById<View>(R.id.btn_publish).isEnabled = true
                Toast.makeText(this, "Firestore error", Toast.LENGTH_SHORT).show()
            }
    }
}
