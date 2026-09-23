package com.example.a4499grill

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<ImageButton>(R.id.btn_back)?.setOnClickListener {
            finish()
        }

        val ivHero = findViewById<ImageView>(R.id.iv_menu_hero)
        val ivItem1 = findViewById<ImageView>(R.id.iv_menu_item_1)
        val ivItem2 = findViewById<ImageView>(R.id.iv_menu_item_2)
        val ivItem3 = findViewById<ImageView>(R.id.iv_menu_item_3)

        Glide.with(this).load(R.drawable.food_burger_hero).into(ivHero)
        Glide.with(this).load(R.drawable.food_truffle_mushroom).into(ivItem1)
        Glide.with(this).load(R.drawable.food_sticky_wings).into(ivItem2)
        Glide.with(this).load(R.drawable.food_vanilla_shake).into(ivItem3)

        findViewById<View>(R.id.item_burger_1)?.setOnClickListener {
            openDetail("burger_1", "Truffle Mushroom Swiss", 95.0, R.drawable.food_truffle_mushroom)
        }
        
        findViewById<View>(R.id.item_burger_2)?.setOnClickListener {
             openDetail("wings_1", "Sticky BBQ Wings", 65.0, R.drawable.food_sticky_wings)
        }

        findViewById<View>(R.id.item_burger_3)?.setOnClickListener {
             openDetail("shake_1", "Classic Vanilla Shake", 45.0, R.drawable.food_vanilla_shake)
        }
    }

    private fun openDetail(id: String, name: String, price: Double, imgRes: Int) {
        val intent = Intent(this, ItemDetailActivity::class.java)
        intent.putExtra("ITEM_ID", id)
        intent.putExtra("ITEM_NAME", name)
        intent.putExtra("ITEM_PRICE", price)
        intent.putExtra("ITEM_IMAGE", imgRes)
        startActivity(intent)
    }
}
