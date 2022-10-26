package de.menkalian.barcodestore.ui.shared

import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.menkalian.barcodestore.R
import de.menkalian.barcodestore.ui.AboutActivity
import de.menkalian.barcodestore.ui.GalleryActivity
import de.menkalian.barcodestore.ui.ScannerActivity
import de.menkalian.vela.loglib.Logger
import de.menkalian.vela.loglib.LoggerFactory

abstract class MenuActivity(@IdRes private val menuItemId: Int) : AppCompatActivity() {
    protected val log: Logger = LoggerFactory.getFactory().getLogger("UI")
    private lateinit var menuBinding: BottomNavigationView

    abstract fun setRootLayout()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRootLayout()

        menuBinding = findViewById(R.id.bottomnav_main)
        menuBinding.setOnItemSelectedListener {
            log.info("Listener Fired")
            when (it.itemId) {
                R.id.mnu_scanner  -> switchActivity(ScannerActivity::class.java)
                R.id.mnu_gallery  -> switchActivity(GalleryActivity::class.java)
                R.id.mnu_about    -> switchActivity(AboutActivity::class.java)
//                R.id.mnu_settings -> switchActivity(SettingsActivity::class.java)
                else              -> {
                    log.warn("Unsupported Item in Main-Menu selected: $it (ID: ${it.itemId})")
                    false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        menuBinding.selectedItemId = menuItemId
    }

    private fun switchActivity(activityClass: Class<*>): Boolean {
        if (activityClass == this::class.java)
            return true

        val intent = Intent(this, activityClass)
        startActivity(intent)
        return true
    }
}