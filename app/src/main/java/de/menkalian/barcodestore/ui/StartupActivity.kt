package de.menkalian.barcodestore.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.menkalian.barcodestore.R
import de.menkalian.vela.loglib.Logger
import de.menkalian.vela.loglib.LoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartupActivity : AppCompatActivity() {
    private val log: Logger = LoggerFactory.getFactory().getLogger("UI")
    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log.info("Starting BarcodeStore...")
        setContentView(R.layout.activity_startup)

        mainScope.launch {
            // Give a little time to render start screen
            delay(100)

            // Start matching activity
            val intent = Intent(this@StartupActivity, ScannerActivity::class.java)
            startActivity(intent)
        }
    }
}