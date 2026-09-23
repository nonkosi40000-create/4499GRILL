package com.example.a4499grill

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ItemDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        val itemId = intent.getStringExtra("ITEM_ID") ?: "default_id"
        val itemName = intent.getStringExtra("ITEM_NAME") ?: "Double Cheese Smash"
        val itemPrice = intent.getDoubleExtra("ITEM_PRICE", 78.99)
        val itemImage = intent.getIntExtra("ITEM_IMAGE", R.drawable.food_burger_hero)

        val tvName = findViewById<TextView>(R.id.tv_item_name)
        val tvPrice = findViewById<TextView>(R.id.tv_item_price)
        val tvQuantity = findViewById<TextView>(R.id.tv_quantity)
        val btnPlus = findViewById<ImageView>(R.id.btn_plus)
        val btnMinus = findViewById<ImageView>(R.id.btn_minus)
        val btnBack = findViewById<View>(R.id.btn_back_container)
        val btnAdd = findViewById<View>(R.id.btn_add_to_basket)
        val ivHero = findViewById<ImageView>(R.id.iv_item_hero)

        tvName.text = itemName
        tvPrice.text = "R${String.format("%.2f", itemPrice)}"

        if (ivHero != null) {
            Glide.with(this)
                .load(itemImage)
                .into(ivHero)
        }

        var quantity = 1

        btnPlus?.setOnClickListener {
            quantity++
            tvQuantity?.text = quantity.toString()
        }

        btnMinus?.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQuantity?.text = quantity.toString()
            }
        }

        btnBack?.setOnClickListener {
            finish()
        }

        btnAdd?.setOnClickListener {
            CartManager.addItem(CartItem(
                id = itemId,
                name = itemName,
                price = itemPrice,
                quantity = quantity,
                imageUrl = itemImage
            ))
            
            Toast.makeText(this, "$quantity $itemName(s) added to basket", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
