package de.menkalian.barcodestore.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import de.menkalian.barcodestore.data.BarcodeDatabase
import de.menkalian.barcodestore.databinding.ActivityCodeviewBinding
import de.menkalian.barcodestore.ui.fragment.DecodeTabFragment
import de.menkalian.barcodestore.ui.fragment.TabFragment
import de.menkalian.barcodestore.ui.fragment.ViewTabFragment
import de.menkalian.barcodestore.util.variables.Extras
import de.menkalian.vela.loglib.Logger
import de.menkalian.vela.loglib.LoggerFactory


class ViewBarcodeActivity : AppCompatActivity() {
    private val log: Logger = LoggerFactory.getFactory().getLogger("UI")
    private lateinit var viewBinding: ActivityCodeviewBinding
    private lateinit var database: BarcodeDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCodeviewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        database = BarcodeDatabase.createDatabase(applicationContext)

        val id = intent.getLongExtra(Extras.Barcode.ID, -1)

        val fragments = listOf(
            ViewTabFragment(),
            DecodeTabFragment()
        )

        fragments.forEach {
            val fragmentArgs = Bundle()
            fragmentArgs.putLong(Extras.Barcode.ID, id)
            it.arguments = fragmentArgs
        }

        viewBinding.viewPager.adapter = FragmentAdapter(fragments)
        TabLayoutMediator(viewBinding.tabLayout, viewBinding.viewPager) { tab, pos ->
            tab.setText(fragments[pos].labelTextRes())
            tab.setIcon(fragments[pos].labelIconRes())
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
    }

    inner class FragmentAdapter(private val fragments: List<TabFragment>) : FragmentStateAdapter(supportFragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

    }
}