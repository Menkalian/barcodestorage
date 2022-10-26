package de.menkalian.barcodestore.decoder.impl

import de.menkalian.barcodestore.R
import de.menkalian.barcodestore.data.Barcode
import de.menkalian.barcodestore.decoder.BarcodeDecoder

class HexDataDecoder : BarcodeDecoder {
    override fun nameRes() = R.string.decoder_hex

    override fun canDecode(barcode: Barcode) = true

    override fun decode(barcode: Barcode): String {
        return barcode.data
            .map { it.toUByte().toString(16).padStart(2, '0') }
            .chunked(8).map { it.joinToString(" ") }
            .joinToString("\n")
    }
}