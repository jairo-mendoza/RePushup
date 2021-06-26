package com.app.pushup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var repInput: EditText
    private lateinit var uploadButton: Button
    private lateinit var rootNode: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var dailyReference: DatabaseReference
    private lateinit var userInfo: UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Instantiate global variables
        mAuth = FirebaseAuth.getInstance()
        repInput = findViewById(R.id.repET)
        uploadButton = findViewById(R.id.uploadBtn)

        // Get date
        val dateF = SimpleDateFormat("M-dd-yyyy")
        val currentDate = dateF.format(Date())

        // Get time
        val timeF = SimpleDateFormat("hh:mm:ss")
        val currentTime = timeF.format(Date())

        val currentUser = mAuth.currentUser

        // Check if user is logged in
        if(currentUser != null) {
            // Change greeting
            changeGreeting()
        }

        uploadButton.setOnClickListener {
            var currentDailyReps: Int = 0

            if (currentUser != null) {
                // Write a message to the database
                rootNode = FirebaseDatabase.getInstance()
                reference = rootNode.getReference("User-Info")
                dailyReference = rootNode.getReference("User-Info/${currentUser.uid}/${currentDate}")

                userInfo = UserInfo(repInput.text.toString().toInt())


                var dataListener = object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Check if value exists
                        if(!snapshot.exists()) {
                            dailyReference.child("Daily-Reps").setValue(userInfo.totalReps)
                        }
                        else {
                            currentDailyReps = snapshot.value.toString().toInt()
                            currentDailyReps += userInfo.totalReps
                            dailyReference.child("Daily-Reps").setValue(currentDailyReps)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }

                dailyReference.child("Daily-Reps").addListenerForSingleValueEvent(dataListener)

                // Saves inputted data to user id
                val childPath = "${currentUser.uid}/${currentDate}/${currentTime}"
                reference.child(childPath).setValue(userInfo)
                Toast.makeText(this@MainActivity, "Uploaded Information", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun changeGreeting() {
        val greetingTextView: TextView = findViewById(R.id.greetingTV)

        // Get hour
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)

        if((hour in 0..5) || (hour in 21..24)) {
            greetingTextView.text = "Good night!"
        }
        else if(hour in 6..11) {
            greetingTextView.text = "Good morning!"
        }
        else if(hour in 12..17) {
            greetingTextView.text = "Good afternoon!"
        }
        else if(hour in 18..20) {
            greetingTextView.text = "Good evening!"
        }
        else {
            greetingTextView.text = "Hello!"
        }
    }

    fun toSettingsActivity(v: View) {
        startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
    }
}