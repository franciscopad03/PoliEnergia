package com.example.polienergia.glossary

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.polienergia.R
import com.example.polienergia.databinding.ActivityGlossaryBinding
import com.google.android.material.tabs.TabLayoutMediator

class GlossaryScreen : AppCompatActivity() {

    private lateinit var binding: ActivityGlossaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGlossaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Edge-to-Edge Handling ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Obtener los datos filtrados del Intent
        val category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("CATEGORY", GlossaryCategory::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("CATEGORY") as? GlossaryCategory
        }
        val terms = GlossaryData.getTerms(category)

        // 2. Configurar el ViewPager2 con nuestro Adapter
        val adapter = GlossaryAdapter(terms)
        binding.glossaryPager.adapter = adapter

        // 3. Conectar el ViewPager2 con los indicadores de puntos (TabLayout)
        TabLayoutMediator(binding.glossaryDotsIndicator, binding.glossaryPager) { tab, position ->
            // No necesitamos texto, solo los puntos
        }.attach()

        // 4. Configurar la acción del botón de cierre
        binding.glossaryCloseButton.setOnClickListener {
            finish() // Cierra esta actividad y regresa a la anterior
        }
    }
}
