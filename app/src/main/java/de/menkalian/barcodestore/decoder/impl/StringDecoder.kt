package de.menkalian.barcodestore.decoder.impl

import de.menkalian.barcodestore.R
import de.menkalian.barcodestore.data.Barcode
import de.menkalian.barcodestore.decoder.BarcodeDecoder

class StringDecoder : BarcodeDecoder {
    override fun nameRes() = R.string.decoder_string

    override fun canDecode(barcode: Barcode)= true

    override fun decode(barcode: Barcode): String {
        return barcode.data.decodeToString()
    }
}