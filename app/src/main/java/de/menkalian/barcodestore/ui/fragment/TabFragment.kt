package de.menkalian.barcodestore.ui.fragment

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

abstract class TabFragment : Fragment() {
    @StringRes
    abstract fun labelTextRes(): Int

    @DrawableRes
    abstract fun labelIconRes(): Int
}