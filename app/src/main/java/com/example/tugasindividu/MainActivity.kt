package com.example.tugasindividu

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.hardware.Camera
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.CameraInfo
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tugasindividu.Camera.CameraView
import com.example.tugasindividu.databinding.ActivityMainBinding
import java.lang.Exception
import java.lang.Math.sqrt
import java.util.*


class MainActivity : AppCompatActivity(), SensorEventListener{

    lateinit var proximitySensor: Sensor
    lateinit var sensorManager: SensorManager
    private var mCamera: Camera?=null
    private var currentCameraId= Camera.CameraInfo.CAMERA_FACING_BACK
    private var mCameraView: CameraView?=null



    //notification
    private val CHANNEL_ID_1 ="channel_notifikasi_01"
    private val notificationId1 = 101
    var binding: ActivityMainBinding?=null


    //AccelerometeR
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private lateinit var square: TextView

    //camera
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            mCamera = Camera.open()
        } catch (e: Exception) {
            Log.d("Error", "Failed to get Camera" + e.message)
        }

        if (mCamera != null) {
            mCameraView = CameraView(this, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView((mCameraView))
        }

        @SuppressLint("MissingInflatedId", "LocalSuppress") val imageClose =
            findViewById<View>(R.id.imgClose) as ImageButton
        imageClose.setOnClickListener { view: View? -> System.exit(0) }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if (proximitySensor == null) {
            Toast.makeText(this, "No proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            sensorManager.registerListener(
                proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )

        }

        createNotificationChannel()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setUpSensorStuff()
    }

    fun changeCamera(){
        try {
            mCamera?.stopPreview()
        } catch (e: Exception){
            e.printStackTrace();
        }
        mCamera?.release()
        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT
        }else{
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK
        }

        try{
            mCamera = Camera.open(currentCameraId)
        }catch (e: Exception){
            Log.d("Error", "Failed to get Camera" + e.message)
        }
        if(mCamera != null){
            mCameraView = CameraView(this, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }
    }

    //proximity camera
    var proximitySensorEventListener: SensorEventListener?=object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                if(event.values[0]==0f){
                    if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                        changeCamera()
                    }
                }
            }
        }
    }

    //Accelerometer
    fun setUpSensorStuff(){
        sensorManager= getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also{
                accelerometer -> sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_FASTEST,
            SensorManager.SENSOR_DELAY_FASTEST
        ) }

    }
    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            // Fetching x,y,z values
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration

            // Getting current accelerations
            // with the help of fetched x,y,z values
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            // Display a Toast message if
            // acceleration value is over 12
            if (acceleration > 12) {
                sendNotifiaction1()

            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }



        //notifikation
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_0_1){
            val name="Modul89_A_10781_PROJECT2 "
            val descriptionText ="Selamat anda sudah berhasil mengerjakan Modul 8 dan 9"

            val channel1= NotificationChannel(CHANNEL_ID_1, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description= descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }

    private fun sendNotifiaction1() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentText("Selamat anda sudah berhasil mengerjakan Modul 8 dan 9 ")
            .setPriority(NotificationCompat.PRIORITY_LOW)
        with(NotificationManagerCompat.from(this)){
            notify(notificationId1, builder.build())
        }
    }
}