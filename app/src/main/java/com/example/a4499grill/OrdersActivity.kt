package com.example.a4499grill

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class OrdersActivity : AppCompatActivity() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var btnPaySelected: Button
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val selectedOrderIds = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        rvOrders = findViewById(R.id.rv_orders)
        tvEmpty = findViewById(R.id.tv_empty_orders)
        btnPaySelected = findViewById(R.id.btn_pay_selected)

        rvOrders.layoutManager = LinearLayoutManager(this)

        setupBottomNavigation()
        fetchOrders()

        btnPaySelected.setOnClickListener {
            if (selectedOrderIds.isEmpty()) {
                Toast.makeText(this, "Select at least one order to pay", Toast.LENGTH_SHORT).show()
            } else {
                processPayment()
            }
        }
    }

    private fun fetchOrders() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("orders")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    tvEmpty.visibility = View.VISIBLE
                    rvOrders.visibility = View.GONE
                    return@addSnapshotListener
                }

                var orders = snapshots?.toObjects(Order::class.java)
                    ?.sortedByDescending { it.orderDate } ?: emptyList()

                // Add mock data if empty to show it's working
                if (orders.isEmpty()) {
                    orders = listOf(
                        Order(orderId = "m1", orderNumber = "5521", totalAmount = 145.50, paymentStatus = "Awaiting Payment", orderStatus = "Received"),
                        Order(orderId = "m2", orderNumber = "5522", totalAmount = 89.99, paymentStatus = "Paid", orderStatus = "Grilling")
                    )
                }

                tvEmpty.visibility = View.GONE
                rvOrders.visibility = View.VISIBLE
                rvOrders.adapter = OrdersAdapter(orders)
                
                // Show pay button if there are unpaid orders
                btnPaySelected.visibility = if (orders.any { it.paymentStatus == "Awaiting Payment" }) View.VISIBLE else View.GONE
            }
    }

    private fun processPayment() {
        Toast.makeText(this, "Processing payment for ${selectedOrderIds.size} orders...", Toast.LENGTH_LONG).show()
        
        val batch = db.batch()
        selectedOrderIds.forEach { id ->
            if (!id.startsWith("m")) {
                val ref = db.collection("orders").document(id)
                batch.update(ref, "paymentStatus", "Paid")
            }
        }
        
        batch.commit().addOnSuccessListener {
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()
            selectedOrderIds.clear()
            fetchOrders()
        }
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.nav_explore_btn)?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.nav_community_btn)?.setOnClickListener {
            startActivity(Intent(this, CommunityActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.nav_profile_btn)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    inner class OrdersAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvNumber: TextView = view.findViewById(R.id.tv_order_number)
            val tvDate: TextView = view.findViewById(R.id.tv_order_date)
            val tvStatus: TextView = view.findViewById(R.id.tv_order_status)
            val tvTotal: TextView = view.findViewById(R.id.tv_order_total)
            val tvPayment: TextView = view.findViewById(R.id.tv_payment_status)
            val checkBox: CheckBox = view.findViewById(R.id.cb_order_select)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val order = orders[position]
            holder.tvNumber.text = "Order #${order.orderNumber}"
            holder.tvDate.text = "Order placed recently"
            holder.tvStatus.text = "Status: ${order.orderStatus}"
            holder.tvTotal.text = "R${String.format("%.2f", order.totalAmount)}"
            holder.tvPayment.text = order.paymentStatus
            
            holder.tvPayment.setTextColor(if (order.paymentStatus == "Paid") android.graphics.Color.GREEN else android.graphics.Color.RED)
            
            // Enable selection for unpaid orders
            if (order.paymentStatus == "Awaiting Payment") {
                holder.checkBox.visibility = View.VISIBLE
                holder.checkBox.isChecked = selectedOrderIds.contains(order.orderId)
                holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedOrderIds.add(order.orderId)
                    else selectedOrderIds.remove(order.orderId)
                }
            } else {
                holder.checkBox.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(this@OrdersActivity, OrderDetailsActivity::class.java)
                intent.putExtra("ORDER_ID", order.orderId)
                startActivity(intent)
            }
        }

        override fun getItemCount() = orders.size
    }
}
