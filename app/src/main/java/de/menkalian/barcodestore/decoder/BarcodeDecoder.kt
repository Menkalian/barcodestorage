package de.menkalian.barcodestore.decoder

import androidx.annotation.StringRes
import de.menkalian.barcodestore.data.Barcode

interface BarcodeDecoder {
    @StringRes
    fun nameRes(): Int

    fun canDecode(barcode: Barcode): Boolean

    fun decode(barcode: Barcode): String
}