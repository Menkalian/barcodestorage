package de.menkalian.barcodestore

import android.app.Application
import de.menkalian.barcodestore.decoder.DecoderManager
import de.menkalian.sagitta.loglib.addLogcatLogWriter
import de.menkalian.sagitta.loglib.asLogContext
import de.menkalian.vela.loglib.LogLevel
import de.menkalian.vela.loglib.LoggerFactory

class BarcodeStoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        LoggerFactory.getFactory().configure {
            addLogcatLogWriter()
            if (BuildConfig.DEBUG) {
                addTextFileLogWriter(this@BarcodeStoreApplication.asLogContext())
                withGlobalLogLevel(LogLevel.TRACE)
            } else {
                addDatabaseLogWriter(this@BarcodeStoreApplication.asLogContext())
                withGlobalLogLevel(LogLevel.DEBUG)
            }

            withMultithreadLogging(true)
            withDetermineLogOrigin(true)

            addLogger {
                withName("Barcodestore")
                withCollectLogs(true)
                withCollectedLoggersRegex(".*")
                withMinloglevel(LogLevel.INFO)
            }
            addLogger {
                withName("UserInterface")
                withClassMatchRegex("de\\.menkalian\\.barcodestore\\.ui\\..*")
                withNameMatchRegex("UI")
            }
            addLogger {
                withName("Detection")
                withClassMatchRegex("de\\.menkalian\\.barcodestore\\.detection\\..*")
                withNameMatchRegex("DETECT")
            }
        }

        DecoderManager.initDecoders()
    }
}