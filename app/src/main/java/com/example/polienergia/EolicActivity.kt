package com.example.polienergia

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.polienergia.consumption.ConsumptionActivity
import com.example.polienergia.glossary.GlossaryCategory
import com.example.polienergia.glossary.GlossaryScreen
import kotlin.math.pow

class EolicActivity : AppCompatActivity() {

    // --- UI Components ---
    private lateinit var voltageValueText: TextView
    private lateinit var currentValueText: TextView
    private lateinit var windSpeedValueText: TextView
    private lateinit var bladeRadiusValueText: TextView
    private lateinit var heightValueText: TextView
    private lateinit var aspasImage: ImageView
    private lateinit var lightEffectImage: ImageView
    private lateinit var windSimImage1: ImageView
    private lateinit var windSimImage2: ImageView
    private lateinit var batteryProgressBar: ProgressBar
    private lateinit var windSpeedSlider: SeekBar
    private lateinit var bladeRadiusSlider: SeekBar
    private lateinit var heightSlider: SeekBar

    // --- Animation & State Variables ---
    private var rotationAnimator: ValueAnimator? = null
    private var windAnimator: ValueAnimator? = null
    private var batteryChargeAnimator: ValueAnimator? = null
    private var rotationSpeedPerFrame: Float = 0f
    private var areWindImagesPositioned = false
    private var currentBatteryLevel = 0f
    private var currentPowerGeneration = 0.0
    private var totalConsumptionW = 0
    private var batteryCapacity = 0

    private val EOLIC_SCREEN_KEY = "eolic"
    private val capacityLevels = listOf(50000, 100000, 200000)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eolic)

        // --- View Initialization ---
        voltageValueText = findViewById(R.id.eolic_voltage_value)
        currentValueText = findViewById(R.id.eolic_current_value)
        aspasImage = findViewById(R.id.aspas)
        lightEffectImage = findViewById(R.id.imageView2)
        windSimImage1 = findViewById(R.id.windSim)
        windSimImage2 = findViewById(R.id.windSim2)
        batteryProgressBar = findViewById(R.id.battery_progress_bar)
        windSpeedSlider = findViewById(R.id.wind_speed_slider)
        windSpeedValueText = findViewById(R.id.wind_speed_value)
        bladeRadiusSlider = findViewById(R.id.blade_radius_slider)
        bladeRadiusValueText = findViewById(R.id.blade_radius_value)
        heightSlider = findViewById(R.id.height_slider)
        heightValueText = findViewById(R.id.height_value)
        val glossaryButton: ImageButton = findViewById(R.id.glossary_info_button)

        // --- Animator Setup ---
        setupContinuousRotationAnimator()
        setupWindAnimator()
        setupBatteryAnimator()

        // --- Listeners ---
        val listener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateSimulation()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }

        windSpeedSlider.setOnSeekBarChangeListener(listener)
        bladeRadiusSlider.setOnSeekBarChangeListener(listener)
        heightSlider.setOnSeekBarChangeListener(listener)

        glossaryButton.setOnClickListener {
            val intent = Intent(this, GlossaryScreen::class.java).apply {
                putExtra("CATEGORY", GlossaryCategory.WIND)
            }
            startActivity(intent)
        }

        batteryProgressBar.setOnClickListener {
            startActivity(Intent(this, ConsumptionActivity::class.java))
        }
    }

    private fun updateSimulation() {
        val vientoBase = windSpeedSlider.progress.toDouble()
        val radio = bladeRadiusSlider.progress.toDouble()
        val altura = heightSlider.progress.toDouble()

        windSpeedValueText.text = "${vientoBase.toInt()} m/s"
        bladeRadiusValueText.text = "${radio.toInt()} m"
        heightValueText.text = "${altura.toInt()} m"

        val vientoReal = vientoBase * (altura / 10.0).pow(0.14)
        currentPowerGeneration = (0.5 * 1.225 * Math.PI * radio.pow(2) * vientoReal.pow(3) * 0.40) / 450.0
        val voltaje = vientoReal * (radio * 0.28)
        val corriente = if (voltaje > 0) currentPowerGeneration / voltaje else 0.0

        voltageValueText.text = String.format("%.2f", voltaje)
        currentValueText.text = String.format("%.2f", corriente)

        updateLightIntensity(currentPowerGeneration)
        rotationSpeedPerFrame = ((vientoReal / radio.coerceAtLeast(1.0)) * 50.0 * 2.0 * 0.1).toFloat()
        
        val scale = 0.9f + ((radio - 10.0f) / 70.0f) * 0.2f
        aspasImage.scaleX = scale.toFloat()
        aspasImage.scaleY = scale.toFloat()
    }

    private fun setupContinuousRotationAnimator() {
        rotationAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                aspasImage.rotation = (aspasImage.rotation + rotationSpeedPerFrame) % 360
            }
        }
    }

    private fun setupWindAnimator() {
        windAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                if (!areWindImagesPositioned && windSimImage1.width > 0) {
                    windSimImage2.translationX = -windSimImage1.width.toFloat()
                    areWindImagesPositioned = true
                }
                if (!areWindImagesPositioned) return@addUpdateListener

                val windSpeed = windSpeedSlider.progress.toFloat()
                val translationXAmount = windSpeed * 0.4f
                windSimImage1.translationX += translationXAmount
                windSimImage2.translationX += translationXAmount

                if (windSimImage1.translationX >= windSimImage1.width) {
                    windSimImage1.translationX -= (2 * windSimImage1.width)
                }
                if (windSimImage2.translationX >= windSimImage2.width) {
                    windSimImage2.translationX -= (2 * windSimImage2.width)
                }
            }
        }
    }

    private fun setupBatteryAnimator() {
        batteryChargeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                val simulatedHoursPerTick = 1.0f / 3600.0f
                val netPower = currentPowerGeneration - totalConsumptionW
                val energyChangeWh = netPower * simulatedHoursPerTick
                currentBatteryLevel += energyChangeWh.toFloat()
                currentBatteryLevel = currentBatteryLevel.coerceIn(0f, batteryCapacity.toFloat())
                batteryProgressBar.progress = currentBatteryLevel.toInt()
            }
        }
    }

    private fun loadUiState() {
        val savedState = StateRepository.loadUiState(this, EOLIC_SCREEN_KEY)
        windSpeedSlider.progress = savedState["wind_speed"] ?: 0
        bladeRadiusSlider.progress = savedState["blade_radius"] ?: 10
        heightSlider.progress = savedState["height"] ?: 20
    }

    private fun saveUiState() {
        val stateMap = mapOf(
            "wind_speed" to windSpeedSlider.progress,
            "blade_radius" to bladeRadiusSlider.progress,
            "height" to heightSlider.progress
        )
        StateRepository.saveUiState(this, EOLIC_SCREEN_KEY, stateMap)
    }

    private fun updateLightIntensity(power: Double) {
        val maxPower = 800.0
        val intensity = (power / maxPower).coerceIn(0.0, 1.0)
        lightEffectImage.alpha = intensity.toFloat()
    }

    private fun manageAnimators() {
        // Rotation Animator
        if (rotationSpeedPerFrame > 0.1f) {
            if (rotationAnimator?.isStarted == false) rotationAnimator?.start() else rotationAnimator?.resume()
        } else {
            rotationAnimator?.pause()
        }

        // Wind Animator
        if (windSpeedSlider.progress > 0) {
            if (windAnimator?.isStarted == false) windAnimator?.start() else windAnimator?.resume()
        } else {
            windAnimator?.pause()
        }

        // Battery Animator
        if (batteryChargeAnimator?.isStarted == false) batteryChargeAnimator?.start() else batteryChargeAnimator?.resume()
    }

    override fun onResume() {
        super.onResume()
        // 1. Load all state from repository
        currentBatteryLevel = StateRepository.getBatteryLevel(this)
        totalConsumptionW = StateRepository.getTotalConsumptionPower(this)
        val capacityLevel = StateRepository.getBatteryCapacityLevel(this)
        batteryCapacity = capacityLevels.getOrElse(capacityLevel) { 100000 }
        
        // 2. Update UI with loaded state
        batteryProgressBar.max = batteryCapacity
        batteryProgressBar.progress = currentBatteryLevel.toInt()
        loadUiState() // This sets the sliders
        
        // 3. Recalculate simulation values based on loaded state
        updateSimulation()
        
        // 4. Start or resume all animators based on the current state
        manageAnimators()
    }

    override fun onPause() {
        super.onPause()
        // 1. Save UI state
        saveUiState()
        StateRepository.saveBatteryLevel(this, currentBatteryLevel)
        StateRepository.saveGenerationPower(this, currentPowerGeneration.toFloat())
        
        // 2. Pause all animators
        rotationAnimator?.pause()
        windAnimator?.pause()
        batteryChargeAnimator?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        rotationAnimator?.cancel()
        windAnimator?.cancel()
        batteryChargeAnimator?.cancel()
    }
}