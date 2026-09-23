package com.example.a4499grill

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class CartActivity : AppCompatActivity() {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        rvCart = findViewById(R.id.rv_cart_items)
        tvTotal = findViewById(R.id.tv_cart_total)
        btnCheckout = findViewById(R.id.btn_checkout)

        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }

        rvCart.layoutManager = LinearLayoutManager(this)
        val adapter = CartAdapter(CartManager.getItems())
        rvCart.adapter = adapter

        updateTotal()

        btnCheckout.setOnClickListener {
            if (CartManager.isEmpty()) {
                Toast.makeText(this, "Basket is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            placeOrder()
        }
    }

    private fun updateTotal() {
        tvTotal.text = "R${String.format("%.2f", CartManager.getTotal())}"
    }

    private fun placeOrder() {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid ?: return

        val orderId = UUID.randomUUID().toString()
        val orderNumber = (1000..9999).random().toString()
        
        val order = Order(
            orderId = orderId,
            userId = userId,
            items = CartManager.getItems().toList(),
            totalAmount = CartManager.getTotal(),
            orderNumber = orderNumber
        )

        db.collection("orders").document(orderId).set(order)
            .addOnSuccessListener {
                CartManager.clear()
                Toast.makeText(this, "Order placed! Proceed to payment.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, OrderStatusActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
            }
    }

    inner class CartAdapter(private val items: List<CartItem>) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(android.R.id.text1)
            val price: TextView = view.findViewById(android.R.id.text2)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.name.text = "${item.quantity}x ${item.name}"
            holder.name.setTextColor(android.graphics.Color.BLACK)
            holder.price.text = "R${String.format("%.2f", item.price * item.quantity)}"
            holder.price.setTextColor(android.graphics.Color.GRAY)
        }

        override fun getItemCount() = items.size
    }
}
