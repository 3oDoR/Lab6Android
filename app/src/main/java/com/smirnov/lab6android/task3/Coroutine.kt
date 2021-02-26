package com.smirnov.lab6android.task3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import com.smirnov.lab6android.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

class Coroutine : AppCompatActivity() {
    private val Scope = CoroutineScope(Dispatchers.Main)

    private fun DownloadImage(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val inputStream = URL(url).openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e("Error", e.message.orEmpty())
            e.printStackTrace()
        }

        return bitmap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            Scope.launch(Dispatchers.IO) {
                val image = DownloadImage("https://miro.medium.com/max/632/1*aJp" +
                        "-LNY8Zeb5gyCKdEWMfQ.png")
                launch(Dispatchers.Main) {
                    binding.ImageView.setImageBitmap(image)
                }
            }
        }
    }
}