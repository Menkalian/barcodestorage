package de.menkalian.barcodestore.decoder.impl

import android.util.Base64
import de.menkalian.barcodestore.R
import de.menkalian.barcodestore.data.Barcode
import de.menkalian.barcodestore.decoder.BarcodeDecoder

class Base64DataDecoder : BarcodeDecoder {
    override fun nameRes() = R.string.decoder_base64

    override fun canDecode(barcode: Barcode)= true

    override fun decode(barcode: Barcode): String {
        return Base64.encodeToString(barcode.data, 0)
    }
}