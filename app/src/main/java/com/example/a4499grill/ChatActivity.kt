package com.example.a4499grill

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val orderId = intent.getStringExtra("ORDER_ID") ?: "global_support"
        val tvHeader = findViewById<TextView>(R.id.tv_chat_header)
        val tvMessages = findViewById<TextView>(R.id.tv_chat_messages)
        val etInput = findViewById<EditText>(R.id.et_chat_input)
        val btnSend = findViewById<Button>(R.id.btn_chat_send)

        tvHeader.text = "Order Support Chat (ID: $orderId)"

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUid = auth.currentUser?.uid ?: "Anonymous"

        // Real-time listener for messaging stream
        db.collection("orders").document(orderId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Chat error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val sb = StringBuilder()
                    for (doc in snapshots) {
                        val sender = doc.getString("senderName") ?: "User"
                        val msg = doc.getString("messageText") ?: ""
                        sb.append("[").append(sender).append("]: ").append(msg).append("\n\n")
                    }
                    tvMessages.text = sb.toString()
                }
            }

        btnSend.setOnClickListener {
            val text = etInput.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            val msgMap = hashMapOf(
                "senderId" to currentUid,
                "senderName" to (if (currentUid == "Anonymous") "Anonymous" else "User"),
                "messageText" to text,
                "timestamp" to com.google.firebase.Timestamp.now()
            )

            db.collection("orders").document(orderId).collection("messages").add(msgMap)
                .addOnSuccessListener {
                    etInput.text.clear()
                }
        }
    }
}
