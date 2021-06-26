package com.app.pushup

import java.text.SimpleDateFormat
import java.util.*

class UserInfo(var highestReps: Int = 0) {
    var userID: String? = null
    val totalReps: Int = calcTotalReps()
    var dailyTotalReps: Int = 0
    var completedWorkout: Boolean = false

    private fun calcTotalReps(): Int {
        var i = highestReps
        var totReps = 0

        while(i > 0) {
            totReps += i
            i--
        }

        return totReps
    }
}