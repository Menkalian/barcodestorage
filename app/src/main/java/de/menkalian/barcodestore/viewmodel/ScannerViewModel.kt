package de.menkalian.barcodestore.viewmodel

import androidx.camera.core.CameraSelector
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat

class ScannerViewModel : ViewModel() {
    val cameraFacing = MutableLiveData(CameraSelector.LENS_FACING_BACK)
    val flashlight = MutableLiveData(false)

    fun switchCamera() {
        cameraFacing.value = if (cameraFacing.value == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
    }

    fun switchFlashlight() {
        flashlight.value = flashlight.value?.not() ?: true
    }
}