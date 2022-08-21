package de.menkalian.barcodestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.camera.core.Camera
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraX
import androidx.camera.lifecycle.ProcessCameraProvider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}