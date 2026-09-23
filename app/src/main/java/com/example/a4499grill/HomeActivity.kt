package com.example.a4499grill

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        
        val root = findViewById<View>(R.id.home_root)
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val tvWelcome = findViewById<TextView>(R.id.tv_welcome_user)
        val tvLocationValue = findViewById<TextView>(R.id.tv_location_value)
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid

        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("fullName")
                        val userLoc = document.getString("location")
                        if (!name.isNullOrEmpty()) {
                            tvWelcome.text = "Welcome, $name!"
                        }
                        if (!userLoc.isNullOrEmpty()) {
                            tvLocationValue.text = userLoc
                        }
                    }
                }
        }

        val ivHero = findViewById<ImageView>(R.id.iv_home_hero)
        val ivFavorite = findViewById<ImageView>(R.id.iv_home_favorite)

        if (ivHero != null) {
            Glide.with(this).load(R.drawable.food_burger_hero).into(ivHero)
        }
        if (ivFavorite != null) {
            Glide.with(this).load(R.drawable.food_truffle_mushroom).into(ivFavorite)
        }

        val onCategoryClickListener = View.OnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }
        findViewById<View>(R.id.cat_burgers_btn)?.setOnClickListener(onCategoryClickListener)
        findViewById<View>(R.id.cat_grill_btn)?.setOnClickListener(onCategoryClickListener)
        findViewById<View>(R.id.cat_drinks_btn)?.setOnClickListener(onCategoryClickListener)
        findViewById<View>(R.id.cat_sides_btn)?.setOnClickListener(onCategoryClickListener)
        
        findViewById<View>(R.id.btn_ai_chat_trigger)?.setOnClickListener {
            startActivity(Intent(this, AIChatActivity::class.java))
        }

        findViewById<View>(R.id.btn_view_cart)?.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<CardView>(R.id.card_burger_detail)?.setOnClickListener {
            startActivity(Intent(this, ItemDetailActivity::class.java))
        }

        findViewById<View>(R.id.nav_orders_btn)?.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }

        findViewById<View>(R.id.nav_community_btn)?.setOnClickListener {
            startActivity(Intent(this, CommunityActivity::class.java))
        }

        findViewById<View>(R.id.nav_profile_btn)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
