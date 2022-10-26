package de.menkalian.barcodestore.ui

import de.menkalian.barcodestore.R
import de.menkalian.barcodestore.ui.shared.MenuActivity

// TODO
class SettingsActivity : MenuActivity(R.id.mnu_about) {
    override fun setRootLayout() {
        log.debug("Showing %q-Screen", "Settings")
        setContentView(R.layout.activity_settings)
    }
}