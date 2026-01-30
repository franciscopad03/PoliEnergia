package com.example.polienergia.glossary

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.polienergia.R

enum class GlossaryCategory {
    GENERAL,
    SOLAR,
    WIND
}

data class GlossaryTerm(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val imageRes: Int,
    val category: GlossaryCategory
)

object GlossaryData {
    private val allTerms = listOf(
        // --- A. Conceptos Generales ---
        GlossaryTerm(R.string.glossary_general_energy_title, R.string.glossary_general_energy_desc, R.drawable.ic_general_energy, GlossaryCategory.GENERAL),
        GlossaryTerm(R.string.glossary_general_power_title, R.string.glossary_general_power_desc, R.drawable.ic_general_potencial, GlossaryCategory.GENERAL),
        GlossaryTerm(R.string.glossary_general_battery_title, R.string.glossary_general_battery_desc, R.drawable.ic_general_battery, GlossaryCategory.GENERAL),
        GlossaryTerm(R.string.glossary_general_consumption_title, R.string.glossary_general_consumption_desc, R.drawable.ic_general_consume, GlossaryCategory.GENERAL),

        // --- B. Energía Solar ---
        GlossaryTerm(R.string.glossary_solar_panel_title, R.string.glossary_solar_panel_desc, R.drawable.ic_solar_panel, GlossaryCategory.SOLAR),
        GlossaryTerm(R.string.glossary_solar_irradiance_title, R.string.glossary_solar_irradiance_desc, R.drawable.ic_solar_irrad, GlossaryCategory.SOLAR),
        GlossaryTerm(R.string.glossary_solar_cloudiness_title, R.string.glossary_solar_cloudiness_desc, R.drawable.ic_solar_nubosidad, GlossaryCategory.SOLAR),
        GlossaryTerm(R.string.glossary_solar_angle_title, R.string.glossary_solar_angle_desc, R.drawable.ic_solar_angle, GlossaryCategory.SOLAR),

        // --- C. Energía Eólica ---
        GlossaryTerm(R.string.glossary_wind_turbine_title, R.string.glossary_wind_turbine_desc, R.drawable.ic_wind_aerogen, GlossaryCategory.WIND),
        GlossaryTerm(R.string.glossary_wind_speed_title, R.string.glossary_wind_speed_desc, R.drawable.ic_wind_velocity, GlossaryCategory.WIND),
        GlossaryTerm(R.string.glossary_wind_blades_title, R.string.glossary_wind_blades_desc, R.drawable.ic_wind_palas, GlossaryCategory.WIND),
        GlossaryTerm(R.string.glossary_wind_brake_title, R.string.glossary_wind_brake_desc, R.drawable.ic_wind_estop, GlossaryCategory.WIND)
    )

    fun getTerms(category: GlossaryCategory?): List<GlossaryTerm> {
        return when (category) {
            null, GlossaryCategory.GENERAL -> allTerms
            else -> allTerms.filter { it.category == category }
        }
    }
}
