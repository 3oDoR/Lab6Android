package com.smirnov.lab6android.task1

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smirnov.lab6android.R
import kotlinx.android.synthetic.main.task1.*
import java.util.concurrent.TimeUnit

import android.content.SharedPreferences


class AsyncTask : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private lateinit var asyncTask: MyAsyncTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task1)
    }

    override fun onStart() {
        asyncTask = MyAsyncTask()
        asyncTask.execute()
        super.onStart()
    }

    override fun onStop() {
        asyncTask.cancel(true)
        super.onStop()
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

    inner class MyAsyncTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            while (!isCancelled) {
                TimeUnit.SECONDS.sleep(1)
                publishProgress()
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
            textSecondsElapsed.text = "Seconds elapsed: " + secondsElapsed++
        }

    }
}