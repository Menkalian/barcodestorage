package de.menkalian.barcodestore.ui

import de.menkalian.barcodestore.R
import de.menkalian.barcodestore.ui.shared.MenuActivity

class AboutActivity : MenuActivity(R.id.mnu_about) {
    override fun setRootLayout() {
        log.debug("Showing %q-Screen", "About")
        setContentView(R.layout.activity_about)
    }
}