package com.app.pushup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FinishPushupActivity : AppCompatActivity() {
    private lateinit var rootNode: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_pushup)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@FinishPushupActivity, MainActivity::class.java))
    }
}