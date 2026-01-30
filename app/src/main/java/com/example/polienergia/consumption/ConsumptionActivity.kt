package com.example.polienergia.consumption

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.polienergia.R
import com.example.polienergia.StateRepository
import com.example.polienergia.databinding.ActivityConsumptionBinding
import kotlin.math.abs

class ConsumptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConsumptionBinding
    private lateinit var consumptionAdapter: ConsumptionAdapter
    private val appliances = ConsumptionData.getAppliances()
    private var currentBatteryLevel = 0f
    private var totalConsumptionW = 0
    private var generationPowerW = 0f
    private var batteryCapacity = 0

    private var simulationAnimator: ValueAnimator? = null
    private val CONSUMPTION_SCREEN_KEY = "consumption_v2"

    // Corrected capacity values to match 5, 10, 20 kWh
    private val capacityLevels = listOf(5000, 10000, 20000)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityConsumptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.consumptionTitle.setPadding(0, systemBars.top, 0, binding.consumptionTitle.paddingBottom)
            insets
        }

        setupRecyclerView()
        setupBatteryCapacityChips()
        setupSimulationAnimator()

        binding.returnButton.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        consumptionAdapter = ConsumptionAdapter(appliances) { _ ->
            recalculateTotalConsumption()
        }
        binding.appliancesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ConsumptionActivity)
            adapter = consumptionAdapter
        }
    }

    private fun recalculateTotalConsumption() {
        totalConsumptionW = appliances.filter { it.isOn }.sumOf { it.powerConsumption * it.quantity }
        binding.totalConsumptionValue.text = "$totalConsumptionW W"
    }

    private fun setupBatteryCapacityChips() {
        val savedCapacityLevel = StateRepository.getBatteryCapacityLevel(this)
        val chipIds = listOf(R.id.capacity_chip_small, R.id.capacity_chip_medium, R.id.capacity_chip_large)
        binding.batteryCapacityGroup.check(chipIds.getOrElse(savedCapacityLevel) { R.id.capacity_chip_medium })
        updateBatteryCapacity(savedCapacityLevel)

        binding.batteryCapacityGroup.setOnCheckedChangeListener { group, checkedId ->
            val level = when (checkedId) {
                R.id.capacity_chip_small -> 0
                R.id.capacity_chip_medium -> 1
                R.id.capacity_chip_large -> 2
                else -> 1
            }
            updateBatteryCapacity(level)
            StateRepository.saveBatteryCapacityLevel(this, level)
        }
    }

    private fun updateBatteryCapacity(level: Int) {
        batteryCapacity = capacityLevels.getOrElse(level) { 10000 } // Default to medium if out of bounds
        binding.batteryProgressBar.max = batteryCapacity
    }

    private fun setupSimulationAnimator() {
        simulationAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000 // Animator ticks every 1 second
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                val simulatedHoursPerTick = 1.0f / 3600.0f // 1 second of real time = 1 second of simulated time
                
                val netPower = generationPowerW - totalConsumptionW
                val energyChangeWh = netPower * simulatedHoursPerTick

                currentBatteryLevel += energyChangeWh
                currentBatteryLevel = currentBatteryLevel.coerceIn(0f, batteryCapacity.toFloat())
                binding.batteryProgressBar.progress = currentBatteryLevel.toInt()
                
                updateRemainingTime(netPower)
            }
        }
    }

    private fun updateRemainingTime(netPower: Float) {
        if (netPower == 0f) {
            binding.timeRemainingValue.text = "--h --m (Estable)"
            return
        }

        // Corrected the syntax error here
        var hoursRemaining: Float
        val status: String

        if (netPower > 0) { // Cargando
            val remainingCapacity = batteryCapacity - currentBatteryLevel
            hoursRemaining = if (netPower > 0) remainingCapacity / netPower else Float.POSITIVE_INFINITY
            status = "para cargar"
        } else { // Descargando
            hoursRemaining = if (netPower < 0) currentBatteryLevel / abs(netPower) else Float.POSITIVE_INFINITY
            status = "restante"
        }

        if (hoursRemaining.isInfinite() || hoursRemaining < 0) {
            binding.timeRemainingValue.text = "Calculando..."
        } else {
            val totalMinutes = (hoursRemaining * 60).toInt()
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            binding.timeRemainingValue.text = "${hours}h ${minutes}m $status"
        }
    }

    private fun loadUiState() {
        val savedState = StateRepository.loadUiState(this, CONSUMPTION_SCREEN_KEY)
        appliances.forEachIndexed { index, appliance ->
            appliance.isOn = (savedState["appliance_${index}_on"] ?: 0) == 1
            appliance.quantity = savedState["appliance_${index}_qty"] ?: 1
        }
        if (::consumptionAdapter.isInitialized) {
            consumptionAdapter.notifyDataSetChanged()
        }
        recalculateTotalConsumption()
    }

    private fun saveUiState() {
        val stateMap = mutableMapOf<String, Int>()
        appliances.forEachIndexed { index, appliance ->
            stateMap["appliance_${index}_on"] = if (appliance.isOn) 1 else 0
            stateMap["appliance_${index}_qty"] = appliance.quantity
        }
        StateRepository.saveUiState(this, CONSUMPTION_SCREEN_KEY, stateMap)
    }

    override fun onResume() {
        super.onResume()
        currentBatteryLevel = StateRepository.getBatteryLevel(this)
        generationPowerW = StateRepository.getGenerationPower(this)
        totalConsumptionW = StateRepository.getTotalConsumptionPower(this)
        
        val savedCapacityLevel = StateRepository.getBatteryCapacityLevel(this)
        updateBatteryCapacity(savedCapacityLevel)

        binding.generationValue.text = "${generationPowerW.toInt()} W"
        binding.batteryProgressBar.progress = currentBatteryLevel.toInt()
        loadUiState()
        simulationAnimator?.start()
    }

    override fun onPause() {
        super.onPause()
        saveUiState()
        StateRepository.saveBatteryLevel(this, currentBatteryLevel)
        StateRepository.saveTotalConsumptionPower(this, totalConsumptionW)
        simulationAnimator?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        simulationAnimator?.cancel()
    }
}