package de.menkalian.barcodestore.util

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import de.menkalian.barcodestore.data.Type
import java.security.MessageDigest


fun BarcodeFormat.toDatabaseType(): Type {
    return when (this) {
        BarcodeFormat.AZTEC             -> Type.AZTEC
        BarcodeFormat.CODABAR           -> Type.CODABAR
        BarcodeFormat.CODE_39           -> Type.CODE_39
        BarcodeFormat.CODE_93           -> Type.CODE_93
        BarcodeFormat.CODE_128          -> Type.CODE_128
        BarcodeFormat.DATA_MATRIX       -> Type.DATA_MATRIX
        BarcodeFormat.EAN_8             -> Type.EAN_8
        BarcodeFormat.EAN_13            -> Type.EAN_13
        BarcodeFormat.ITF               -> Type.ITF
        BarcodeFormat.MAXICODE          -> Type.MAXI_CODE
        BarcodeFormat.PDF_417           -> Type.PDF_417
        BarcodeFormat.QR_CODE           -> Type.QR_CODE
        BarcodeFormat.RSS_14            -> Type.RSS_14
        BarcodeFormat.RSS_EXPANDED      -> Type.RSS_EXPANDED
        BarcodeFormat.UPC_A             -> Type.UPC_A
        BarcodeFormat.UPC_E             -> Type.UPC_E
        BarcodeFormat.UPC_EAN_EXTENSION -> Type.UPC_EAN_EXTENSION_2_5
    }
}

fun Type.toBarcodeFormat(): BarcodeFormat {
    return when (this) {
        Type.AZTEC                 -> BarcodeFormat.AZTEC
        Type.CODABAR               -> BarcodeFormat.CODABAR
        Type.CODE_39               -> BarcodeFormat.CODE_39
        Type.CODE_93               -> BarcodeFormat.CODE_93
        Type.CODE_128              -> BarcodeFormat.CODE_128
        Type.DATA_MATRIX           -> BarcodeFormat.DATA_MATRIX
        Type.EAN_8                 -> BarcodeFormat.EAN_8
        Type.EAN_13                -> BarcodeFormat.EAN_13
        Type.ITF                   -> BarcodeFormat.ITF
        Type.MAXI_CODE             -> BarcodeFormat.MAXICODE
        Type.PDF_417               -> BarcodeFormat.PDF_417
        Type.QR_CODE               -> BarcodeFormat.QR_CODE
        Type.RSS_14                -> BarcodeFormat.RSS_14
        Type.RSS_EXPANDED          -> BarcodeFormat.RSS_EXPANDED
        Type.UPC_A                 -> BarcodeFormat.UPC_A
        Type.UPC_E                 -> BarcodeFormat.UPC_E
        Type.UPC_EAN_EXTENSION_2_5 -> BarcodeFormat.UPC_EAN_EXTENSION
    }
}

fun ByteArray.sha256(): ByteArray {
    val md = MessageDigest.getInstance("SHA-256")
    return md.digest(this)
}

fun ByteArray.sha512(): ByteArray {
    val md = MessageDigest.getInstance("SHA-512")
    return md.digest(this)
}

fun ByteArray.toHex(): String {
    return joinToString("") { it.toUByte().toString(16).padStart(2, '0') }
}

fun BitMatrix.toImage(foreColor: Int, backColor: Int): Bitmap {
    val width: Int = getWidth()
    val height: Int = getHeight()
    val pixels = IntArray(width * height)

    for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
            pixels[offset + x] = if (get(x, y)) foreColor else backColor
        }
    }

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}
