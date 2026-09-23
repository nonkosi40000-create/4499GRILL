package com.example.a4499grill

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class OrderStatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_status)
        
        window.statusBarColor = getColor(R.color.screen_background)

        // Back action button
        findViewById<View>(R.id.btn_back_container)?.setOnClickListener {
            finish()
        }

        // Contact Support Interaction mapping to Manager Phone Number (+27 82 555 4499)
        findViewById<View>(R.id.btn_support)?.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+27825554499")
            }
            startActivity(intent)
        }

        // Tip parameters interaction indicators
        val tipViews = listOf(
            R.string.tip_10, R.string.tip_15, R.string.tip_20, R.string.tip_custom
        )
        
        // Mock feedback on order billing tracking sheet parameters
        Toast.makeText(this, "Order tracked successfully. Total paid successfully via payment gateway.", Toast.LENGTH_SHORT).show()

        // Bottom Navigation handlers
        findViewById<View>(R.id.nav_explore_btn)?.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
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
}
