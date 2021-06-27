package com.app.pushup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class PushupActivity : AppCompatActivity() {
    private lateinit var pushupCountTextView: TextView
    private lateinit var finishSetBtn: Button
    private lateinit var receivedIntent: Intent
    private var currentReps = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pushup)

        receivedIntent = intent
        currentReps = receivedIntent.getIntExtra("currReps", 0)

        // Initialize global variables
        pushupCountTextView = findViewById(R.id.pushupCountTV)
        finishSetBtn = findViewById(R.id.finishPushupSetBtn)

        // Set up pushup info
        pushupCountTextView.text = "x$currentReps"

        // On click listener for finish set button
        finishSetBtn.setOnClickListener {
            if(currentReps > 1) {
                val restIntent = Intent(this@PushupActivity, RestActivity::class.java)
                restIntent.putExtra("currentReps", currentReps)
                startActivity(restIntent)
                finish()
            }
            else {
                val finishIntent = Intent(this@PushupActivity, MainActivity::class.java)
                finishIntent.putExtra("currentReps", currentReps)
                startActivity(finishIntent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@PushupActivity, MainActivity::class.java))
    }
}