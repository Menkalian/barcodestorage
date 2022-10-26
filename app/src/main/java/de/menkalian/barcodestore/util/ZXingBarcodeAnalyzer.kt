package de.menkalian.barcodestore.util

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.Result
import com.google.zxing.aztec.AztecReader
import com.google.zxing.common.HybridBinarizer
import de.menkalian.vela.loglib.LoggerFactory

class ZXingBarcodeAnalyzer(private val listener: (BarcodeFormat, String?, ByteArray?) -> Unit) : ImageAnalysis.Analyzer {
    private val analyzer = MultiFormatReader()
    private val aztecAnalyzer = AztecReader()
    private val log = LoggerFactory.getFactory().getLogger(this)

    private val lastResLock = Any()
    private var lastRes: Result? = null
    private var lastResCount = 0

    override fun analyze(image: ImageProxy) {
        val imgDataBuffer = image.planes[0].buffer
        val imgData = ByteArray(imgDataBuffer.capacity())
        imgDataBuffer.get(imgData)

        val source = PlanarYUVLuminanceSource(
            imgData,
            image.width, image.height,
            0, 0,
            image.width, image.height,
            false
        )
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        try {
            val res = aztecAnalyzer.decode(binaryBitmap)
            log.debug("Recognized AZTEC barcode: ${res.barcodeFormat} $res.")
            listener(res.barcodeFormat, res.text, res.rawBytes)
        } catch (ex: NotFoundException) {
            // ignore
        } catch (ex: Exception) {
            log.error("Error in Barcode detection: %lx.", ex)
        }
        try {
            val res = analyzer.decode(binaryBitmap)
            log.debug("Decode result: ${res.barcodeFormat} $res.")
            synchronized(lastResLock) {
                if (
                    (res.barcodeFormat == lastRes?.barcodeFormat)
                    && (res.text == lastRes?.text)
                    && (res.rawBytes?.contentEquals(lastRes?.rawBytes) != false)
                ) {
                    lastResCount++
                    if (lastResCount == 5) {
                        log.debug("Recognized barcode: ${res.barcodeFormat} $res.")
                        listener(res.barcodeFormat, res.text, res.rawBytes)
                    }
                } else {
                    lastRes = res
                    lastResCount = 0
                }
            }
        } catch (ex: NotFoundException) {
            // ignore
        } catch (ex: Exception) {
            log.error("Error in Barcode detection: %lx.", ex)
        }
        image.close()
    }
}