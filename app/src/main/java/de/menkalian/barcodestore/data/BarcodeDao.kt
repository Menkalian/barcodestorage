package de.menkalian.barcodestore.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BarcodeDao {
    @Query("SELECT * FROM barcode ORDER BY ts DESC")
    fun getAllStoredBarcodes(): List<Barcode>

    @Query("SELECT * FROM barcode WHERE id = :id")
    fun getBarcodeById(id: Long): Barcode

    @Insert
    fun insert(barcode: Barcode): Long

    @Update
    fun update(barcode: Barcode)

    @Delete
    fun delete(barcode: Barcode)
}