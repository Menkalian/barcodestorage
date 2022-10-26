package de.menkalian.barcodestore.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import de.menkalian.barcodestore.R
import de.menkalian.barcodestore.data.Barcode
import de.menkalian.barcodestore.data.BarcodeDatabase
import de.menkalian.barcodestore.databinding.ActivityMainBinding
import de.menkalian.barcodestore.ui.shared.MenuActivity
import de.menkalian.barcodestore.util.ZXingBarcodeAnalyzer
import de.menkalian.barcodestore.util.sha256
import de.menkalian.barcodestore.util.toDatabaseType
import de.menkalian.barcodestore.util.toHex
import de.menkalian.barcodestore.util.variables.Extras
import de.menkalian.barcodestore.viewmodel.ScannerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.concurrent.Executors

class ScannerActivity : MenuActivity(R.id.mnu_scanner) {

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val cameraExecutor = Executors.newFixedThreadPool(2)
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var scannerViewModel: ScannerViewModel
    private lateinit var database: BarcodeDatabase

    override fun setRootLayout() {
        log.debug("Showing %q-Screen", "Scanner")
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: ScannerViewModel by viewModels()
        scannerViewModel = viewModel

        database = BarcodeDatabase.createDatabase(applicationContext)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        viewBinding.cameraSwitch.setOnClickListener { viewModel.switchCamera() }
        viewBinding.cameraFlash.setOnClickListener { viewModel.switchFlashlight() }
        viewModel.flashlight.observe(this) { active ->
            viewBinding.cameraFlash.setImageResource(
                if (active) {
                    R.drawable.symbol_flash_on
                } else {
                    R.drawable.symbol_flash_off
                }
            )
        }

        log.info("ScannerActivity set up.")
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(
            {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(viewBinding.cameraPreview.surfaceProvider)
                    }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, ZXingBarcodeAnalyzer(this::storeBarcode))
                    }

                // Select back camera as a default
                scannerViewModel.cameraFacing.observe(this) {
                    val selector = CameraSelector.Builder()
                        .requireLensFacing(it)
                        .build()

                    try {
                        // Unbind use cases before rebinding
                        cameraProvider.unbindAll()

                        // Bind use cases to camera
                        val camera = cameraProvider.bindToLifecycle(
                            this, selector, preview, imageAnalyzer
                        )

                        scannerViewModel.flashlight.observe(this) {
                            camera.cameraControl.enableTorch(it)
                        }
                    } catch (ex: Exception) {
                        log.error("Could not configure camera: %lx", ex)
                    }
                }
            }, ContextCompat.getMainExecutor(this)
        )

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        database.close()
    }


    private fun storeBarcode(format: BarcodeFormat, text: String?, data: ByteArray?) {
        val bcData = text?.toByteArray(Charsets.UTF_8) ?: data ?: ByteArray(0)
        val ts = Instant.now().toEpochMilli()
        val bc = Barcode(
            null,
            generateDefaultBarcodeName(format, bcData, ts),
            format.toDatabaseType(),
            bcData,
            ts
        )

        val id = database
            .barcodeAccess()
            .insert(bc)

        mainScope.launch {
            // Start matching activity
            val intent = Intent(this@ScannerActivity, ViewBarcodeActivity::class.java)
            intent.putExtra(Extras.Barcode.ID, id)
            startActivity(intent)
        }

    }

    private fun generateDefaultBarcodeName(format: BarcodeFormat, data: ByteArray, timestamp: Long): String {
        return "${format}_${timestamp}_${data.sha256().toHex()}"
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}
