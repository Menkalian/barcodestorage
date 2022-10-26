package de.menkalian.barcodestore.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Barcode::class], version = 1)
abstract class BarcodeDatabase : RoomDatabase() {
    abstract fun barcodeAccess(): BarcodeDao

    companion object {
        fun createDatabase(appContext: Context) = Room.databaseBuilder(
            appContext,
            BarcodeDatabase::class.java,
            "barcode-store.db3"
        ).build()
    }
}