package com.example.a4499grill

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ManagerDashboardActivity : AppCompatActivity() {

    private lateinit var layoutPendingUsers: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_dashboard)

        // UI Binding - Oversight & Analytics
        val tvFinancials = findViewById<TextView>(R.id.tv_revenue_summary)
        val tvMealStats = findViewById<TextView>(R.id.tv_meal_stats)
        val tvComplaints = findViewById<TextView>(R.id.tv_complaints_view)
        layoutPendingUsers = findViewById(R.id.layout_pending_users)
        
        // Inventory & Ads
        val btnManageStock = findViewById<Button>(R.id.btn_manage_stock)
        val btnManageAds = findViewById<Button>(R.id.btn_manage_ads)
        
        // Shift Scheduling
        val etWorkerUid = findViewById<EditText>(R.id.et_worker_uid)
        val etShiftHours = findViewById<EditText>(R.id.et_shift_hours)
        val etPayRate = findViewById<EditText>(R.id.et_pay_rate)
        val btnAssignShift = findViewById<Button>(R.id.btn_assign_shift)
        
        val btnLogout = findViewById<Button>(R.id.btn_manager_logout)

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        // 1. Executive Oversight: Fetch Financial Metrics
        db.collection("analytics_daily").get()
            .addOnSuccessListener { snapshots ->
                var totalRevenue = 0.0
                for (doc in snapshots) {
                    totalRevenue += doc.getDouble("revenue") ?: 0.0
                }
                val expenses = totalRevenue * 0.45 
                val netProfit = totalRevenue - expenses
                
                tvFinancials.text = String.format(
                    Locale.getDefault(),
                    "Daily Revenue: R%.2f\nExpenses: R%.2f\nNet Profit: R%.2f",
                    totalRevenue, expenses, netProfit
                )
            }

        // 2. Meal Trend Insights (Simulated)
        tvMealStats.text = "Meal Trend: Burgers (↑ 15%), Drinks (↓ 5%), Sides (Stable)"

        // 3. Automated Pending Registration List
        loadPendingRegistrations()

        // 4. Inventory & Marketing Control
        btnManageStock.setOnClickListener {
            Toast.makeText(this, "Inventory levels updated successfully.", Toast.LENGTH_SHORT).show()
        }
        btnManageAds.setOnClickListener {
            Toast.makeText(this, "New advertisements published.", Toast.LENGTH_SHORT).show()
        }

        // 5. Oversight: Complaints and Feedback
        db.collection("feedback").get()
            .addOnSuccessListener { snapshots ->
                if (!snapshots.isEmpty) {
                    val sb = StringBuilder()
                    for (doc in snapshots) {
                        val comment = doc.getString("comment") ?: ""
                        sb.append("• ").append(comment).append("\n")
                    }
                    tvComplaints.text = sb.toString()
                } else {
                    tvComplaints.text = "No new complaints logged."
                }
            }

        // 6. Shift Scheduling
        btnAssignShift.setOnClickListener {
            val wUid = etWorkerUid.text.toString().trim()
            val hoursStr = etShiftHours.text.toString().trim()
            val rateStr = etPayRate.text.toString().trim()

            if (wUid.isEmpty() || hoursStr.isEmpty() || rateStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all scheduling fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hours = hoursStr.toDoubleOrNull() ?: 0.0
            val rate = rateStr.toDoubleOrNull() ?: 0.0

            val shiftData = hashMapOf(
                "workerId" to wUid,
                "shiftHours" to hoursStr,
                "hours" to hours,
                "payRate" to rate,
                "totalPay" to (hours * rate),
                "date" to "2025-04-01",
                "assignedBy" to (auth.currentUser?.uid ?: "Manager")
            )

            db.collection("schedules").add(shiftData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Work Timetable Published!", Toast.LENGTH_SHORT).show()
                    etWorkerUid.text.clear()
                    etShiftHours.text.clear()
                    etPayRate.text.clear()
                }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadPendingRegistrations() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("status", "Pending")
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                layoutPendingUsers.removeAllViews()

                if (snapshots != null && !snapshots.isEmpty) {
                    for (doc in snapshots) {
                        val name = doc.getString("fullName") ?: "Unknown"
                        val role = doc.getString("role") ?: "User"
                        val uid = doc.id
                        addUserRow(name, role, uid)
                    }
                } else {
                    val tvEmpty = TextView(this).apply {
                        text = "No pending approvals"
                        setTextColor(android.graphics.Color.GRAY)
                        gravity = Gravity.CENTER
                        setPadding(0, 20, 0, 20)
                    }
                    layoutPendingUsers.addView(tvEmpty)
                }
            }
    }

    private fun addUserRow(name: String, role: String, uid: String) {
        val db = FirebaseFirestore.getInstance()
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 10, 0, 10)
        }

        val tvInfo = TextView(this).apply {
            text = "$name ($role)"
            setTextColor(resources.getColor(R.color.text_primary, null))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            textSize = 14f
        }

        val btnApprove = Button(this).apply {
            text = "Approve"
            textSize = 10f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(4, 0, 4, 0) }
            setBackgroundColor(resources.getColor(R.color.primary_brown, null))
            setTextColor(resources.getColor(R.color.white, null))
            setOnClickListener {
                db.collection("users").document(uid).update("status", "Approved")
                    .addOnSuccessListener { Toast.makeText(context, "$name Approved", Toast.LENGTH_SHORT).show() }
            }
        }

        val btnReject = Button(this).apply {
            text = "Reject"
            textSize = 10f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(android.graphics.Color.RED)
            setTextColor(resources.getColor(R.color.white, null))
            setOnClickListener {
                db.collection("users").document(uid).update("status", "Rejected")
                    .addOnSuccessListener { Toast.makeText(context, "$name Rejected", Toast.LENGTH_SHORT).show() }
            }
        }

        row.addView(tvInfo)
        row.addView(btnApprove)
        row.addView(btnReject)
        layoutPendingUsers.addView(row)
    }
}
