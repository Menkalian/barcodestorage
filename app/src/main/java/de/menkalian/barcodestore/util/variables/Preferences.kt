package de.menkalian.barcodestore.util.variables

object Preferences {
    const val BASE = "Preferences"

    object Hint {
        const val BASE = "${Preferences.BASE}.Hint"

        object Menu {
            const val BASE = "${Hint.BASE}.Menu"
            const val Show = "$BASE.Show"
        }
    }
}