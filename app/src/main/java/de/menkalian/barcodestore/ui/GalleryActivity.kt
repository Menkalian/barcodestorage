package de.menkalian.barcodestore.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.MultiFormatWriter
import de.menkalian.barcodestore.R
import de.menkalian.barcodestore.data.Barcode
import de.menkalian.barcodestore.data.BarcodeDatabase
import de.menkalian.barcodestore.databinding.ActivityGalleryBinding
import de.menkalian.barcodestore.databinding.CardGalleryBinding
import de.menkalian.barcodestore.ui.shared.MenuActivity
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

class GalleryActivity : MenuActivity(R.id.mnu_gallery) {
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val workScope = CoroutineScope(Dispatchers.Default)
    private val barcodeWriter = MultiFormatWriter()

    private lateinit var viewBinding: ActivityGalleryBinding
    private lateinit var database: BarcodeDatabase

    override fun setRootLayout() {
        log.debug("Showing %q-Screen", "Gallery")
        viewBinding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = BarcodeDatabase.createDatabase(applicationContext)
        log.info("GalleryActivity set up.")
    }

    override fun onResume() {
        super.onResume()
        workScope.launch {
            val barcodes = database.barcodeAccess().getAllStoredBarcodes()
            val adapter = CardAdapter(barcodes)

            mainScope.launch {
                viewBinding.galleryList.adapter = adapter
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
    }

    inner class CardAdapter(private val data: List<Barcode>) : RecyclerView.Adapter<CardViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_gallery, parent, false)

            return CardViewHolder(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            holder.updateDisplay(data[position])
        }

    }

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val context: Context
        val viewBinding: CardGalleryBinding

        init {
            context = view.context
            viewBinding = CardGalleryBinding.bind(view)
        }

        fun updateDisplay(barcode: Barcode) {
            viewBinding.cardGalleryName.text = barcode.name
            viewBinding.cardGalleryTimestamp.text = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(barcode.timestampMillis),
                ZoneId.systemDefault()
            ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            viewBinding.cardGalleryCodeImg.setImageResource(R.drawable.symbol_image_loading)

            viewBinding.cardGallery.setOnClickListener {
                // Start matching activity
                val intent = Intent(context, ViewBarcodeActivity::class.java)
                intent.putExtra(Extras.Barcode.ID, barcode.id)
                context.startActivity(intent)
            }

            workScope.launch {
                val img = barcodeWriter.encode(
                    barcode.data.decodeToString(), barcode.type.toBarcodeFormat(),
                    1024, 1024
                ).toImage(context.getColor(R.color.black), context.getColor(R.color.white))

                mainScope.launch {
                    viewBinding.cardGalleryCodeImg.setImageBitmap(img)
                }
            }
        }
    }
}