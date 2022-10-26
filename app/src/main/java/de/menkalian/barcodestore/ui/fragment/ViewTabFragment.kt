package de.menkalian.barcodestore.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.MultiFormatWriter
import de.menkalian.barcodestore.R
import de.menkalian.barcodestore.data.Barcode
import de.menkalian.barcodestore.data.BarcodeDatabase
import de.menkalian.barcodestore.data.Type
import de.menkalian.barcodestore.databinding.FragmentViewBinding
import de.menkalian.barcodestore.util.toBarcodeFormat
import de.menkalian.barcodestore.util.toImage
import de.menkalian.barcodestore.util.variables.Extras
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ViewTabFragment() : TabFragment(), AdapterView.OnItemSelectedListener {
    override fun labelTextRes() = R.string.desc_view
    override fun labelIconRes() = R.drawable.symbol_view

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val workScope = CoroutineScope(Dispatchers.Default)
    private val barcodeWriter = MultiFormatWriter()

    private lateinit var viewBinding: FragmentViewBinding
    private var database: BarcodeDatabase? = null
    private var barcode: Barcode? = null
    private var types: Array<Type>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = FragmentViewBinding.inflate(inflater, container, false)

        database?.close()
        database = BarcodeDatabase.createDatabase(inflater.context.applicationContext)

        // Setup zoom
        viewBinding.zoomSlider.value = 1.0f
        viewBinding.zoomSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewBinding.codeImg.scaleX = value
                viewBinding.codeImg.scaleY = value
            }
        }


        viewBinding.btnRename.setOnClickListener {
            val tv = EditText(inflater.context)
            tv.setText(barcode?.name, TextView.BufferType.EDITABLE)
            MaterialAlertDialogBuilder(inflater.context)
                .setMessage(R.string.msg_change_name)
                .setView(tv)
                .setPositiveButton(R.string.option_confirm) { _, _ ->
                    barcode?.name = tv.text.toString()
                    workScope.launch {
                        barcode?.let { bc -> database?.barcodeAccess()?.update(bc) }
                        mainScope.launch {
                            updateView()
                        }
                    }
                }
                .setNegativeButton(R.string.option_decline) { _, _ -> }
                .show()
        }

        workScope.launch {
            val barcodeId = arguments?.getLong(Extras.Barcode.ID) ?: -1
            barcode = database?.barcodeAccess()?.getBarcodeById(barcodeId)
            val nnTypes = filterPossibleTypes()
            types = nnTypes

            mainScope.launch {
                viewBinding.inputFormatField.adapter = ArrayAdapter(inflater.context, android.R.layout.simple_spinner_dropdown_item, nnTypes)
                viewBinding.inputFormatField.setSelection(nnTypes.indexOf(barcode?.type).coerceAtLeast(0))
                viewBinding.inputFormatField.onItemSelectedListener = this@ViewTabFragment

                updateView()
            }
        }

        return viewBinding.root
    }

    private fun updateView() {
        viewBinding.codeName.text = barcode?.name
        viewBinding.codeTimestamp.text = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(barcode?.timestampMillis ?: 0),
            ZoneId.systemDefault()
        ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    private fun filterPossibleTypes(): Array<Type> {
        val toReturn = mutableSetOf<Type>()
        val data = barcode?.data
        val dataStr = data?.decodeToString()
        val dataNum = dataStr?.toBigIntegerOrNull()

        if (dataStr != null) {
            toReturn.add(Type.QR_CODE)
            toReturn.add(Type.AZTEC)
            toReturn.add(Type.DATA_MATRIX)
            toReturn.add(Type.CODABAR)
            toReturn.add(Type.PDF_417)

            if ((7..8).contains(dataStr.length) && dataNum != null) {
                toReturn.add(Type.EAN_8)

                if (dataStr.toCharArray()[0].let { it == '0' || it == '1' }) {
                    toReturn.add(Type.UPC_E)
                }
            }

            if ((12..13).contains(dataStr.length) && dataNum != null) {
                toReturn.add(Type.EAN_13)
            }
            if ((11..12).contains(dataStr.length) && dataNum != null) {
                toReturn.add(Type.UPC_E)
            }

            if (dataStr.length <= 80 && dataStr.length % 2 == 0 && dataNum != null) {
                toReturn.add(Type.ITF)
            }

            if (dataStr.length <= 80) {
                toReturn.add(Type.CODE_39)
                toReturn.add(Type.CODE_93)
                toReturn.add(Type.CODE_128)
            }
        }

        if (toReturn.isEmpty()) {
            toReturn.add(Type.QR_CODE)
        }
        return toReturn.toTypedArray()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val type = types!![position]

        workScope.launch {
            try {
                val img = barcodeWriter.encode(
                    barcode?.data?.decodeToString(), type.toBarcodeFormat(),
                    4096, 4096
                ).toImage(
                    context?.getColor(R.color.black) ?: 0,
                    context?.getColor(R.color.white) ?: 0xffffff
                )

                mainScope.launch {
                    viewBinding.codeImg.setImageBitmap(img)
                }
            } catch (ex: Exception) {
                mainScope.launch {
                    Toast.makeText(context, R.string.msg_error_encode, Toast.LENGTH_LONG).show()
                    viewBinding.inputFormatField.setSelection(types?.indexOf(barcode?.type)?.coerceAtLeast(0) ?: 0)
                }
                ex.printStackTrace()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        viewBinding.inputFormatField.setSelection(types?.indexOf(barcode?.type) ?: 0)
    }
}