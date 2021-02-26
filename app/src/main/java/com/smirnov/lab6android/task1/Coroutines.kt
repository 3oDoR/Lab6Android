package com.smirnov.lab6android.task1

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smirnov.lab6android.R
import kotlinx.android.synthetic.main.task1.*
import kotlinx.coroutines.*

class Coroutines : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private lateinit var coroutine: Job
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task1)

    }

    override fun onStart() {
        super.onStart()
        coroutine = scope.launch {
            while (true) {
                delay(1000)
                textSecondsElapsed.text = "Seconds elapsed: " + secondsElapsed++
            }
        }
    }

    override fun onStop() {
        super.onStop()
        coroutine.cancel()
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
