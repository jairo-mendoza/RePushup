package com.app.pushup

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class RestActivity : AppCompatActivity() {
    private lateinit var skipButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextUpTextView: TextView
    private lateinit var receivedIntent: Intent
    private lateinit var timer: CountDownTimer
    private var currentReps = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rest)

        // Initialize global variables
        skipButton = findViewById(R.id.skipBtn)
        prevButton = findViewById(R.id.prevBttn)
        nextUpTextView = findViewById(R.id.nextUpTV)
        receivedIntent = intent
        currentReps = receivedIntent.getIntExtra("currentReps", 0)

        // Set next up values
        nextUpTextView.text = "Next Up: Push Up x${currentReps - 1}"

        // Start the rest timer
        val countDownTime: TextView = findViewById(R.id.timerTV)
        val timeBar: ProgressBar = findViewById(R.id.timerProgress)
        timeBar.progress = 0

        // Countdown timer for resting
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countDownTime.text = ("${(millisUntilFinished / 60000)}:${(millisUntilFinished % 60000 / 1000).toString().padStart(2, '0')}")
                timeBar.progress += 1
            }

            override fun onFinish() {
                if(currentReps != 1) {
                    // Play sound
                    var mediaPlayer = MediaPlayer.create(this@RestActivity, R.raw.bell_counter)
                    mediaPlayer.start() // no need to call prepare(); create() does that for you

                    // Go to new activity
                    val intent = Intent(this@RestActivity, PushupActivity::class.java)
                    intent.putExtra("currReps", currentReps - 1)
                    startActivity(intent)
                    finish()
                }
                else {
                    val intent = Intent(this@RestActivity, FinishPushupActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }.start()

        // On click listener for skip button
        skipButton.setOnClickListener {
            timer.cancel()
            if(currentReps > 1) {
                val intent = Intent(this@RestActivity, PushupActivity::class.java)
                intent.putExtra("currReps", currentReps - 1)
                startActivity(intent)
                finish()
            }
            else {
                val intent = Intent(this@RestActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

       // On click listener for previous button
       prevButton.setOnClickListener {
           timer.cancel()
           val intent = Intent(this@RestActivity, PushupActivity::class.java)
           intent.putExtra("currReps", currentReps)
           startActivity(intent)
           finish()
       }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        timer.cancel()
        startActivity(Intent(this@RestActivity, MainActivity::class.java))
    }
}