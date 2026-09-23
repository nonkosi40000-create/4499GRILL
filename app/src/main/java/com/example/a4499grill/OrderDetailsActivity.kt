package com.example.a4499grill

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var tvOrderNumber: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvPaymentStatus: TextView
    private lateinit var tvOrderDate: TextView
    private lateinit var tvTotal: TextView
    private lateinit var rvItems: RecyclerView
    private lateinit var btnPayNow: Button
    private lateinit var btnTrackOrder: Button
    private val db = FirebaseFirestore.getInstance()
    private var orderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
        
        window.statusBarColor = getColor(R.color.screen_background)

        orderId = intent.getStringExtra("ORDER_ID")
        
        tvOrderNumber = findViewById(R.id.tv_order_number_detail)
        tvStatus = findViewById(R.id.tv_order_status_detail)
        tvPaymentStatus = findViewById(R.id.tv_payment_status_detail)
        tvOrderDate = findViewById(R.id.tv_order_date_detail)
        tvTotal = findViewById(R.id.tv_total_detail)
        rvItems = findViewById(R.id.rv_order_items_detail)
        btnPayNow = findViewById(R.id.btn_pay_now)
        btnTrackOrder = findViewById(R.id.btn_track_order)

        findViewById<View>(R.id.btn_back_container).setOnClickListener { finish() }

        if (orderId != null) {
            fetchOrderDetails(orderId!!)
        } else {
            Toast.makeText(this, "Order ID missing", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnPayNow.setOnClickListener {
            processPayment()
        }

        btnTrackOrder.setOnClickListener {
            val intent = Intent(this, OrderStatusActivity::class.java)
            intent.putExtra("ORDER_ID", orderId)
            startActivity(intent)
        }
    }

    private fun fetchOrderDetails(id: String) {
        db.collection("orders").document(id)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener

                val order = snapshot.toObject(Order::class.java) ?: return@addSnapshotListener
                
                tvOrderNumber.text = "Order #${order.orderNumber}"
                tvStatus.text = "Status: ${order.orderStatus}"
                tvPaymentStatus.text = "Payment: ${order.paymentStatus}"
                
                val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                tvOrderDate.text = "Date: ${sdf.format(order.orderDate.toDate())}"
                
                tvTotal.text = "Total: R${String.format("%.2f", order.totalAmount)}"
                
                if (order.paymentStatus == "Awaiting Payment") {
                    btnPayNow.visibility = View.VISIBLE
                    btnTrackOrder.visibility = View.GONE
                } else {
                    btnPayNow.visibility = View.GONE
                    btnTrackOrder.visibility = View.VISIBLE
                }

                rvItems.layoutManager = LinearLayoutManager(this)
                rvItems.adapter = ItemsAdapter(order.items)
            }
    }

    private fun processPayment() {
        orderId?.let { id ->
            btnPayNow.isEnabled = false
            db.collection("orders").document(id)
                .update("paymentStatus", "Paid")
                .addOnSuccessListener {
                    Toast.makeText(this, "Payment Successful! Your order is being processed.", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    btnPayNow.isEnabled = true
                    Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    inner class ItemsAdapter(private val items: List<CartItem>) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val text: TextView = view.findViewById(android.R.id.text1)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.text.text = "${item.quantity}x ${item.name} - R${String.format("%.2f", item.price * item.quantity)}"
            holder.text.setTextColor(android.graphics.Color.BLACK)
        }
        override fun getItemCount() = items.size
    }
}
