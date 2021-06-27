package com.app.pushup

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var repInput: EditText
    private lateinit var startWorkoutButton: Button
    private lateinit var toSettingsButton: ImageButton
    private lateinit var rootNode: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var dailyReference: DatabaseReference
    private lateinit var userInfo: UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Instantiate global variables
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        repInput = findViewById(R.id.repET)
        startWorkoutButton = findViewById(R.id.startWrkOutBtn)
        toSettingsButton = findViewById(R.id.toSettingsBttn)

        // Set text field input filters, restricting numbers inputted
        repInput.filters = arrayOf(RepMinMaxInput("1", "100"))

        // Get date
        val dateF = SimpleDateFormat("M-dd-yyyy")
        val currentDate = dateF.format(Date())

        // Get time
        val timeF = SimpleDateFormat("hh:mm:ss")
        val currentTime = timeF.format(Date())

        val repInfoTV: TextView = findViewById(R.id.repInfoLabelTV)

        rootNode = FirebaseDatabase.getInstance()
        reference = rootNode.getReference("User-Info")
        if (currentUser != null) {
            dailyReference = rootNode.getReference("User-Info/${currentUser.uid}/${currentDate}")

            // Change greeting
            changeGreeting()


            // Check daily reps
            val dataListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Check if value exists
                    if (snapshot.exists()) {
                        repInfoTV.text = "Daily Pushup Reps: ${snapshot.value.toString()}"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }

            dailyReference.child("Daily-Reps").addListenerForSingleValueEvent(dataListener)
        }

        // Start workout button click listener
        startWorkoutButton.setOnClickListener {
            // Upload data
            var currentDailyReps: Int

            if (currentUser != null) {
                // Write a message to the database

                userInfo = UserInfo(repInput.text.toString().toInt())

                // Saves inputted data to user id
                val childPath = "${currentUser.uid}/${currentDate}/${currentTime}"
                reference.child(childPath).setValue(userInfo)

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
            }

            // Check if text field actually holds a value
            if(repInput.text.toString().isNotEmpty()) {
                val maxRep = repInput.text.toString().toInt()
                val intent: Intent = Intent(this@MainActivity, PushupActivity::class.java)
                intent.putExtra("currReps", maxRep)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this@MainActivity, "Please enter a value.", Toast.LENGTH_SHORT).show()
            }
        }

        toSettingsButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }
    }

    private fun changeGreeting() {
        val greetingTextView: TextView = findViewById(R.id.greetingTV)
        val userName = mAuth.currentUser?.displayName.toString()
        val userFirstName = userName.substring(0, userName.indexOf(' ')).trim()

        // Get hour
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)

        if((hour in 0..5) || (hour in 21..24)) {
            greetingTextView.text = "Good night, $userFirstName!"
        }
        else if(hour in 6..11) {
            greetingTextView.text = "Good morning, $userFirstName!"
        }
        else if(hour in 12..17) {
            greetingTextView.text = "Good afternoon, $userFirstName!"
        }
        else if(hour in 18..20) {
            greetingTextView.text = "Good evening, $userFirstName!"
        }
        else {
            greetingTextView.text = "Hello, $userFirstName!"
        }
    }
}