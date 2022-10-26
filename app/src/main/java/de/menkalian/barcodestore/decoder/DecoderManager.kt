package de.menkalian.barcodestore.decoder

import de.menkalian.barcodestore.decoder.impl.Base64DataDecoder
import de.menkalian.barcodestore.decoder.impl.HexDataDecoder
import de.menkalian.barcodestore.decoder.impl.StringDecoder

object DecoderManager {
    private val innerDecoders: MutableList<BarcodeDecoder> = mutableListOf()
    val decoders: List<BarcodeDecoder>
        get() = innerDecoders

    fun initDecoders() {
        innerDecoders.add(StringDecoder())
        innerDecoders.add(HexDataDecoder())
        innerDecoders.add(Base64DataDecoder())
//        innerDecoders.add(UicDecoder())
    }
}