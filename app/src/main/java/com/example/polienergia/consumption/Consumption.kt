package com.example.polienergia.consumption

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.polienergia.R

data class Appliance(
    @StringRes val name: Int,
    val powerConsumption: Int,
    @DrawableRes val imageRes: Int,
    var isOn: Boolean = false,
    var quantity: Int = 1
)

object ConsumptionData {
    fun getAppliances(): List<Appliance> = listOf(
        Appliance(R.string.appliance_fridge, 150, R.drawable.ic_fridge),
        Appliance(R.string.appliance_tv, 200, R.drawable.ic_tv),
        Appliance(R.string.appliance_bulb, 10, R.drawable.ic_bulb, quantity = 5),
        Appliance(R.string.appliance_microwave, 1200, R.drawable.ic_microwave),
        Appliance(R.string.appliance_laptop, 65, R.drawable.ic_laptop)
    )
}
