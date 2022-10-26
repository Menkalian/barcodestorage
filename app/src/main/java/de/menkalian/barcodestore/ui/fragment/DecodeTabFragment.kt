package de.menkalian.barcodestore.ui.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import de.menkalian.barcodestore.R
import de.menkalian.barcodestore.data.Barcode
import de.menkalian.barcodestore.data.BarcodeDatabase
import de.menkalian.barcodestore.databinding.FragmentDecodeBinding
import de.menkalian.barcodestore.decoder.BarcodeDecoder
import de.menkalian.barcodestore.decoder.DecoderManager
import de.menkalian.barcodestore.util.variables.Extras
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DecodeTabFragment : TabFragment(), AdapterView.OnItemSelectedListener {
    override fun labelTextRes() = R.string.desc_decode
    override fun labelIconRes()= R.drawable.symbol_decode

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val workScope = CoroutineScope(Dispatchers.Default)

    private var database: BarcodeDatabase? = null

    private lateinit var viewBinding: FragmentDecodeBinding
    private var clipboardManager: ClipboardManager? = null
    private lateinit var barcode: Barcode
    private lateinit var decoders: List<BarcodeDecoder>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = FragmentDecodeBinding.inflate(inflater, container, false)

        database?.close()
        database = BarcodeDatabase.createDatabase(inflater.context.applicationContext)

        clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        viewBinding.copyDecoded.setOnClickListener {
            clipboardManager?.setPrimaryClip(
                ClipData.newPlainText("barcode content", viewBinding.inputDecoderResField.text)
            )
            Toast.makeText(it.context, R.string.msg_copy, Toast.LENGTH_LONG).show()
        }

        workScope.launch {
            val barcodeId = arguments?.getLong(Extras.Barcode.ID) ?: -1
            barcode = database!!.barcodeAccess().getBarcodeById(barcodeId)
            decoders = DecoderManager.decoders.filter { it.canDecode(barcode) }

            mainScope.launch {
                val decoderTexts = decoders.map { context?.getString(it.nameRes()) ?: "" }
                viewBinding.inputDecoderField.adapter = ArrayAdapter(inflater.context, android.R.layout.simple_spinner_dropdown_item, decoderTexts)
                viewBinding.inputDecoderField.onItemSelectedListener = this@DecodeTabFragment
            }
        }

        return viewBinding.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val decoder = decoders[position]
        workScope.launch {
            val result = decoder.decode(barcode)

            mainScope.launch {
                viewBinding.inputDecoderResField.setText(result, TextView.BufferType.SPANNABLE)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}