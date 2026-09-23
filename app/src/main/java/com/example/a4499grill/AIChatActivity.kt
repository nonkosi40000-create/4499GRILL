package com.example.a4499grill

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AIChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundColor(android.graphics.Color.BLACK)
        }

        // Back Button
        val btnBack = Button(this).apply {
            text = "Back"
            setOnClickListener {
                finish()
            }
        }
        layout.addView(btnBack)

        val tvTitle = TextView(this).apply {
            text = "4499 Grill AI Assistant"
            setTextColor(android.graphics.Color.WHITE)
            textSize = 20f
            setPadding(0, 16, 0, 16)
        }
        layout.addView(tvTitle)

        val tvChatLog = TextView(this).apply {
            text = "AI: Hello! What are you craving today? I can recommend premium flame-grilled options!\n"
            setTextColor(android.graphics.Color.GRAY)
            textSize = 14f
            setPadding(0, 16, 0, 16)
        }
        layout.addView(tvChatLog)

        val etPrompt = EditText(this).apply {
            hint = "Ask our AI something..."
            setHintTextColor(android.graphics.Color.GRAY)
            setTextColor(android.graphics.Color.WHITE)
        }
        layout.addView(etPrompt)

        val btnSend = Button(this).apply {
            text = "Ask AI"
            setOnClickListener {
                val input = etPrompt.text.toString().trim()
                if (input.isNotEmpty()) {
                    tvChatLog.append("\nYou: $input")
                    tvChatLog.append("\nAI: That sounds delicious! We highly recommend trying our customized Double Cheese Smash with signature BBQ sauce or Truffle Mushroom Swiss burger today! 🔥")
                    etPrompt.text.clear()
                } else {
                    Toast.makeText(this@AIChatActivity, "Please type a question", Toast.LENGTH_SHORT).show()
                }
            }
        }
        layout.addView(btnSend)

        setContentView(layout)
    }
}
