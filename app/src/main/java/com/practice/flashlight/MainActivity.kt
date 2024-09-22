package com.practice.flashlight

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() , SensorEventListener {

    private lateinit var flashlightImage:ImageView
    private lateinit var imageBtnFlashlight:ImageButton
    private lateinit var camManager:CameraManager
    private lateinit var camId:String
    private var isFlashOn = false

    private var sensorManager: SensorManager? = null
    private var sensorAccelerometer:Sensor? = null
    private var lastTime:Long = 0
    private var shakeThreshold:Float = 40.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        if (sensorManager != null) {
            sensorAccelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager!!.registerListener(this,sensorAccelerometer,SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Toast.makeText(this,"Sensor Detection Failure!",Toast.LENGTH_SHORT).show()
        }

        imageBtnFlashlight.setOnClickListener {
            if (!isFlashOn) {
                flashlightImage.setImageResource(R.drawable.flash_on_image)
                imageBtnFlashlight.setImageResource(R.drawable.btn_on)
                turnFlashlight(!isFlashOn)
                 isFlashOn = true
            }
            else {
                flashlightImage.setImageResource(R.drawable.flashlight_image)
                imageBtnFlashlight.setImageResource(R.drawable.btn_off)
                turnFlashlight(!isFlashOn)
                isFlashOn = false
            }
        }
    }

    private fun initialize() {
        flashlightImage = findViewById(R.id.flashlightImage)
        imageBtnFlashlight = findViewById(R.id.imageBtnFlashlight)

        camManager = getSystemService(CAMERA_SERVICE) as CameraManager
        camId = camManager.cameraIdList[0]
    }

    private fun turnFlashlight(flash: Boolean) {
        try {
            camManager.setTorchMode(camId,flash)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val acceleration = sqrt((x*x + y*y + z*z).toDouble()) - SensorManager.GRAVITY_EARTH
            val currentTime = System.currentTimeMillis()

            if (acceleration > shakeThreshold && (currentTime - lastTime) > 1000) {
                lastTime = currentTime
                imageBtnFlashlight.performClick()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    fun ok () {

    }
}