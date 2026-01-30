package com.example.polienergia

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.polienergia.consumption.ConsumptionActivity
import com.example.polienergia.glossary.GlossaryCategory
import com.example.polienergia.glossary.GlossaryScreen
import kotlin.math.sin
import kotlin.math.sqrt

class SolarActivity : AppCompatActivity() {

    // Simulation constants
    private val V_MAX_IDEAL = 40.0f
    private val I_MAX_IDEAL = 10.0f
    private val HORA_AMANECER = 6.0f
    private val HORA_ANOCHECER = 18.0f

    // Simulation variables
    private var weatherFactor = 0.6f
    private var currentHour = 0f
    private var isCircuitOn = true
    private var currentBatteryLevel = 0f
    private var currentPowerGeneration = 0f
    private var totalConsumptionW = 0
    private var batteryCapacity = 0

    // UI Components
    private lateinit var voltageTextView: TextView
    private lateinit var currentTextView: TextView
    private lateinit var bulbEffect: ImageView
    private lateinit var timeLabel: TextView
    private lateinit var batteryProgressBar: ProgressBar
    private lateinit var timeSeekBar: SeekBar
    private lateinit var weatherSeekBar: SeekBar
    private lateinit var circuitSwitch: Switch

    // Animator
    private var batteryChargeAnimator: ValueAnimator? = null

    private val SOLAR_SCREEN_KEY = "solar"
    private val capacityLevels = listOf(50000, 100000, 200000)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solar)

        // Initialize UI components
        timeLabel = findViewById(R.id.time_label)
        voltageTextView = findViewById(R.id.voltage_value)
        currentTextView = findViewById(R.id.current_value)
        bulbEffect = findViewById(R.id.imageView2)
        batteryProgressBar = findViewById(R.id.battery_progress_bar)
        timeSeekBar = findViewById(R.id.seekBar)
        val dayNightImage: ImageView = findViewById(R.id.day_night_image)
        weatherSeekBar = findViewById(R.id.seekBar2)
        circuitSwitch = findViewById(R.id.switch1)
        val glossaryButton: ImageButton = findViewById(R.id.glossary_info_button)

        setupListeners(dayNightImage, glossaryButton)
        setupBatteryAnimator()

        // Set initial state from repository
        loadUiState()
        updateSimulation(timeSeekBar.progress)
    }

    private fun setupListeners(dayNightImage: ImageView, glossaryButton: ImageButton) {
        timeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val rotation = 150.0f + (progress / 1440.0f) * 360.0f
                dayNightImage.rotation = rotation
                updateSimulation(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        weatherSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                weatherFactor = when (progress) {
                    0 -> 1.0f  // Sunny
                    1 -> 0.6f  // Normal
                    2 -> 0.2f  // Cloudy
                    else -> 0.6f
                }
                updateSimulation(timeSeekBar.progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        circuitSwitch.setOnCheckedChangeListener { _, isChecked ->
            isCircuitOn = isChecked
            updateSimulation(timeSeekBar.progress)
        }

        glossaryButton.setOnClickListener {
            val intent = Intent(this, GlossaryScreen::class.java).apply {
                putExtra("CATEGORY", GlossaryCategory.SOLAR)
            }
            startActivity(intent)
        }

        batteryProgressBar.setOnClickListener {
            startActivity(Intent(this, ConsumptionActivity::class.java))
        }
    }

    private fun setupBatteryAnimator() {
        batteryChargeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000 // Update every second
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                val simulatedHoursPerTick = 1.0f / 3600.0f // 1 second of real time = 1 second of simulated time
                
                val netPower = currentPowerGeneration - totalConsumptionW
                val energyChangeWh = netPower * simulatedHoursPerTick

                currentBatteryLevel += energyChangeWh
                currentBatteryLevel = currentBatteryLevel.coerceIn(0f, batteryCapacity.toFloat())
                batteryProgressBar.progress = currentBatteryLevel.toInt()
            }
        }
    }

    private fun updateSimulation(timeProgress: Int) {
        currentHour = timeProgress / 60f
        val hours = timeProgress / 60
        val minutes = timeProgress % 60
        timeLabel.text = String.format("%02d:%02d", hours, minutes)

        if (!isCircuitOn) {
            voltageTextView.text = "0.00"
            currentTextView.text = "0.00"
            bulbEffect.alpha = 0f
            currentPowerGeneration = 0f
            return
        }

        val factorHora = if (currentHour < HORA_AMANECER || currentHour > HORA_ANOCHECER) {
            0.0f
        } else {
            sin((currentHour - HORA_AMANECER) * Math.PI / (HORA_ANOCHECER - HORA_AMANECER)).toFloat()
        }

        val factorIrradiancia = factorHora * weatherFactor
        val corrienteMostrada = I_MAX_IDEAL * factorIrradiancia
        val voltajeMostrado = if(factorHora == 0.0f) 0.0f else (V_MAX_IDEAL * 0.90f) + (V_MAX_IDEAL * 0.10f * factorIrradiancia)

        currentPowerGeneration = voltajeMostrado * corrienteMostrada
        voltageTextView.text = String.format("%.2f", voltajeMostrado)
        currentTextView.text = String.format("%.2f", corrienteMostrada)

        val normalizedCurrent = corrienteMostrada / I_MAX_IDEAL
        bulbEffect.alpha = sqrt(normalizedCurrent)
    }

    private fun loadUiState() {
        val savedState = StateRepository.loadUiState(this, SOLAR_SCREEN_KEY)
        timeSeekBar.progress = savedState["time"] ?: 0
        weatherSeekBar.progress = savedState["weather"] ?: 1
        circuitSwitch.isChecked = (savedState["circuit"] ?: 1) == 1
    }

    private fun saveUiState() {
        val stateMap = mapOf(
            "time" to timeSeekBar.progress,
            "weather" to weatherSeekBar.progress,
            "circuit" to if (circuitSwitch.isChecked) 1 else 0
        )
        StateRepository.saveUiState(this, SOLAR_SCREEN_KEY, stateMap)
    }

    override fun onResume() {
        super.onResume()
        // Load state from repository
        currentBatteryLevel = StateRepository.getBatteryLevel(this)
        totalConsumptionW = StateRepository.getTotalConsumptionPower(this)
        val capacityLevel = StateRepository.getBatteryCapacityLevel(this)
        batteryCapacity = capacityLevels.getOrElse(capacityLevel) { 100000 }
        batteryProgressBar.max = batteryCapacity
        batteryProgressBar.progress = currentBatteryLevel.toInt()
        loadUiState()
        batteryChargeAnimator?.start()
    }

    override fun onPause() {
        super.onPause()
        // Save state to repository
        saveUiState()
        StateRepository.saveBatteryLevel(this, currentBatteryLevel)
        StateRepository.saveGenerationPower(this, currentPowerGeneration)
        batteryChargeAnimator?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        batteryChargeAnimator?.cancel()
    }
}