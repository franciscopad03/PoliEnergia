package com.example.polienergia

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.polienergia.consumption.ConsumptionActivity
import com.example.polienergia.glossary.GlossaryCategory
import com.example.polienergia.glossary.GlossaryScreen

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

        // --- Navigation Buttons ---
        findViewById<ImageButton>(R.id.solarButton).setOnClickListener {
            startActivity(Intent(this, SolarActivity::class.java))
        }

        findViewById<ImageButton>(R.id.eolicaButton).setOnClickListener {
            startActivity(Intent(this, EolicActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton_Return).setOnClickListener {
            finish()
        }

        findViewById<ImageButton>(R.id.glossary_info_button).setOnClickListener {
            val intent = Intent(this, GlossaryScreen::class.java).apply {
                putExtra("CATEGORY", GlossaryCategory.GENERAL)
            }
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.consumption_button).setOnClickListener {
            startActivity(Intent(this, ConsumptionActivity::class.java))
        }
    }
}