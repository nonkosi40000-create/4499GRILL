package com.example.a4499grill

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
            setBackgroundColor(android.graphics.Color.BLACK)
        }

        val tvTitle = TextView(this).apply {
            text = "Admin Global Control Panel"
            setTextColor(android.graphics.Color.WHITE)
            textSize = 22f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        layout.addView(tvTitle)

        val btnSignOut = Button(this).apply {
            text = "Sign Out"
            setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@AdminDashboardActivity, MainActivity::class.java))
                finish()
            }
        }
        layout.addView(btnSignOut)

        setContentView(layout)
    }
}
