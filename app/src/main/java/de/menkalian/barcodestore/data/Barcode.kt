package de.menkalian.barcodestore.data

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.BLOB
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Barcode(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "type") val type: Type,
    @ColumnInfo(name = "data", typeAffinity = BLOB) val data: ByteArray,
    @ColumnInfo(name = "ts") val timestampMillis: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Barcode) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (!data.contentEquals(other.data)) return false
        if (timestampMillis != other.timestampMillis) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + timestampMillis.hashCode()
        return result
    }
}
