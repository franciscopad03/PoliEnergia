package com.example.polienergia.consumption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.polienergia.databinding.ItemApplianceBinding

class ConsumptionAdapter(
    private val appliances: List<Appliance>,
    private val onApplianceChanged: (Appliance) -> Unit
) : RecyclerView.Adapter<ConsumptionAdapter.ApplianceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplianceViewHolder {
        val binding = ItemApplianceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApplianceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApplianceViewHolder, position: Int) {
        holder.bind(appliances[position])
    }

    override fun getItemCount(): Int = appliances.size

    inner class ApplianceViewHolder(private val binding: ItemApplianceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context

        fun bind(appliance: Appliance) {
            binding.applianceIcon.setImageResource(appliance.imageRes)
            binding.applianceName.text = context.getString(appliance.name)
            binding.appliancePower.text = "${appliance.powerConsumption} W"
            binding.quantityText.text = appliance.quantity.toString()

            // Set initial state without triggering listeners
            binding.applianceSwitch.setOnCheckedChangeListener(null)
            binding.applianceSwitch.isChecked = appliance.isOn

            // Listeners
            binding.applianceSwitch.setOnCheckedChangeListener { _, isChecked ->
                appliance.isOn = isChecked
                onApplianceChanged(appliance)
            }

            binding.increaseButton.setOnClickListener {
                appliance.quantity++
                binding.quantityText.text = appliance.quantity.toString()
                onApplianceChanged(appliance)
            }

            binding.decreaseButton.setOnClickListener {
                if (appliance.quantity > 0) {
                    appliance.quantity--
                    binding.quantityText.text = appliance.quantity.toString()
                    onApplianceChanged(appliance)
                }
            }
        }
    }
}
