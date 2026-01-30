package com.example.polienergia.glossary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.polienergia.databinding.ActivityGlossaryBinding
import com.google.android.material.tabs.TabLayoutMediator

class GlossaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGlossaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlossaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Obtener los datos filtrados
        val category = intent.getSerializableExtra("CATEGORY") as? GlossaryCategory
        val terms = GlossaryData.getTerms(category)

        // 2. Configurar el ViewPager2 con el Adapter
        val adapter = GlossaryAdapter(terms)
        binding.glossaryPager.adapter = adapter

        // 3. Conectar el ViewPager2 con los indicadores de puntos (TabLayout)
        TabLayoutMediator(binding.glossaryDotsIndicator, binding.glossaryPager) { tab, position ->
            // No se necesita texto, solo los puntos
        }.attach()

        // 4. Configurar el botón de cierre
        binding.glossaryCloseButton.setOnClickListener {
            finish() // Cierra la actividad
        }
    }
}
