package de.menkalian.barcodestore.data

enum class Type {
    // Product codes (1D)
    UPC_A,
    UPC_E,
    EAN_8,
    EAN_13,
    UPC_EAN_EXTENSION_2_5, // Not writable

    // Industrial codes (1D)
    CODE_39,
    CODE_93,
    CODE_128,
    CODABAR,
    ITF,

    // 2D codes
    QR_CODE,
    DATA_MATRIX,
    AZTEC,
    PDF_417,
    MAXI_CODE,    // Not writable
    RSS_14,       // Not writable
    RSS_EXPANDED; // Not writable
}