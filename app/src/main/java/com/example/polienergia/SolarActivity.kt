package com.example.polienergia

import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SolarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solar)

        val timeSeekBar: SeekBar = findViewById(R.id.seekBar)
        val timeLabel: TextView = findViewById(R.id.time_label)
        val sunImage: ImageView = findViewById(R.id.sun_image)
        val moonImage: ImageView = findViewById(R.id.moon_image)

        timeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val hours = progress / 60
                val minutes = progress % 60
                timeLabel.text = String.format("%02d:%02d", hours, minutes)

                updateSkyAnimation(progress, sunImage, moonImage)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No action needed here
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // No action needed here
            }
        })
        // Initial state
        updateSkyAnimation(timeSeekBar.progress, sunImage, moonImage)
    }

    private fun updateSkyAnimation(progress: Int, sun: ImageView, moon: ImageView) {
        val hour = progress / 60f

        // Simple linear interpolation for alpha
        // Sun alpha: 0 at night, 1 during day. Fade in/out during twilight.
        val sunAlpha = when {
            hour < 6 -> 0f // Night
            hour < 8 -> (hour - 6) / 2f // Dawn
            hour < 18 -> 1f // Day
            hour < 20 -> 1 - (hour - 18) / 2f // Dusk
            else -> 0f // Night
        }

        // Moon alpha: 1 at night, 0 during day. Fade in/out during twilight.
        val moonAlpha = 1 - sunAlpha

        sun.alpha = sunAlpha
        moon.alpha = moonAlpha
    }
}