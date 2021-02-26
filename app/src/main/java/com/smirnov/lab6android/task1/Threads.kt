package com.smirnov.lab6android.task1

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smirnov.lab6android.R
import kotlinx.android.synthetic.main.task1.*


class Threads : AppCompatActivity() {
    var secondsElapsed: Int = 0
    var work = true
    var backgroundThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task1)
    }

    override fun onResume() {
        super.onResume()

        backgroundThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                Thread.sleep(1000)
                
                textSecondsElapsed.post {
                    textSecondsElapsed.text = "Seconds elapsed: " + secondsElapsed++
                }
                if (!work) {
                    Thread.currentThread().interrupt()
                }
            }
        }
        backgroundThread!!.start()
        work = true

    }

    override fun onPause() {
        super.onPause()
        work = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("seconds", secondsElapsed)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        secondsElapsed = savedInstanceState.getInt("seconds")
        textSecondsElapsed.post {
            textSecondsElapsed.text = "Seconds elapsed: $secondsElapsed"
        }
    }
}