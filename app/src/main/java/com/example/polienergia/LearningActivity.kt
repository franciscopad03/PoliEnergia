package com.example.polienergia

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LearningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_learning)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val solarButton: ImageButton = findViewById(R.id.solarButton)
        solarButton.setOnClickListener {
            val intent = Intent(this, SolarActivity::class.java)
            startActivity(intent)
        }

        val eolicaButton: ImageButton = findViewById(R.id.eolicaButton)
        eolicaButton.setOnClickListener {
            val intent = Intent(this, EolicActivity::class.java)
            startActivity(intent)
        }

        val returnButton: ImageButton = findViewById(R.id.imageButton_Return)
        returnButton.setOnClickListener {
            finish()
        }
    }
}