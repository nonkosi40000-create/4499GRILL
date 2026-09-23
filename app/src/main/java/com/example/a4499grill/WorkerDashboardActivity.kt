package com.example.a4499grill

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class WorkerDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_dashboard)

        val tvSchedule = findViewById<TextView>(R.id.tv_worker_schedule)
        val etItemName = findViewById<EditText>(R.id.et_menu_item_name)
        val etItemPrice = findViewById<EditText>(R.id.et_menu_item_price)
        val etItemDesc = findViewById<EditText>(R.id.et_menu_item_desc)
        val btnUpload = findViewById<Button>(R.id.btn_upload_menu)
        
        val etOrderId = findViewById<EditText>(R.id.et_notify_order_id)
        val btnNotifyStock = findViewById<Button>(R.id.btn_notify_stock_issue)
        val btnOpenChat = findViewById<Button>(R.id.btn_open_chat)
        val btnSignOut = findViewById<Button>(R.id.btn_worker_logout)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val currentUid = auth.currentUser?.uid ?: ""

        // 1. Timetable: Fetch assigned shifts and calculated pay
        if (currentUid.isNotEmpty()) {
            db.collection("schedules")
                .whereEqualTo("workerId", currentUid)
                .get()
                .addOnSuccessListener { snapshots ->
                    if (!snapshots.isEmpty) {
                        val sb = StringBuilder()
                        for (doc in snapshots) {
                            val date = doc.getString("date") ?: ""
                            val shift = doc.getString("shiftHours") ?: ""
                            val payRate = doc.getDouble("payRate") ?: 0.0
                            val totalPay = doc.getDouble("totalPay") ?: 0.0
                            
                            sb.append("Date: ").append(date)
                                .append("\nHours: ").append(shift)
                                .append("\nRate: R").append(String.format(Locale.getDefault(), "%.2f", payRate))
                                .append("\nEarned: R").append(String.format(Locale.getDefault(), "%.2f", totalPay))
                                .append("\n\n")
                        }
                        tvSchedule.text = sb.toString()
                    } else {
                        tvSchedule.text = "No shifts assigned yet."
                    }
                }
        }

        // 2. Content Creation: Upload items/ads
        btnUpload.setOnClickListener {
            val name = etItemName.text.toString().trim()
            val priceStr = etItemPrice.text.toString().trim()
            val desc = etItemDesc.text.toString().trim()

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Item Title and Price required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val itemMap = hashMapOf(
                "title" to name,
                "price" to (priceStr.toDoubleOrNull() ?: 0.0),
                "description" to desc,
                "type" to "worker_content",
                "uploadedBy" to currentUid
            )

            db.collection("content_uploads").add(itemMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Content published to Marketplace!", Toast.LENGTH_SHORT).show()
                    etItemName.text.clear()
                    etItemPrice.text.clear()
                    etItemDesc.text.clear()
                }
        }

        // 3. Customer Interaction: Out of Stock Alerts
        btnNotifyStock.setOnClickListener {
            val orderId = etOrderId.text.toString().trim()
            if (orderId.isEmpty()) {
                Toast.makeText(this, "Enter valid Order ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            db.collection("orders").document(orderId).update("status", "Out of Stock")
                .addOnSuccessListener {
                    Toast.makeText(this, "Customer notified via order tracking.", Toast.LENGTH_SHORT).show()
                    etOrderId.text.clear()
                }
        }

        // 4. Customer Interaction: Direct Chat
        btnOpenChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            // Passing a generic support ID or allowing worker to pick one
            intent.putExtra("ORDER_ID", "Global_Support")
            startActivity(intent)
        }

        btnSignOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
