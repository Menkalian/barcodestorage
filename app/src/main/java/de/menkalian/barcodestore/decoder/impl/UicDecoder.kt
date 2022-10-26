package de.menkalian.barcodestore.decoder.impl

import de.menkalian.barcodestore.R
import de.menkalian.barcodestore.data.Barcode
import de.menkalian.barcodestore.decoder.BarcodeDecoder
import org.uic.barcode.Decoder

class UicDecoder : BarcodeDecoder {
    override fun nameRes(): Int = R.string.decoder_uic

    override fun canDecode(barcode: Barcode): Boolean {
        return barcode.data.slice(0..2).toByteArray().decodeToString() == "#UT"
    }

    override fun decode(barcode: Barcode): String {
        val decoderImpl = Decoder(barcode.data)
        return decoderImpl.uicTicket.toString()
    }
}