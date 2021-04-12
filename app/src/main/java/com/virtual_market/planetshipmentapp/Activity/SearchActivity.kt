package com.virtual_market.planetshipmentapp.Activity

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.virtual_market.planetshipmentapp.Adapter.ViewPagerAdapter
import com.virtual_market.planetshipmentapp.Fragment.PartFragment
import com.virtual_market.planetshipmentapp.Fragment.ProductFragment
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.databinding.ActivitySearchBinding


class SearchActivity : AppCompatActivity() {

    private lateinit var showModel:ArrayList<String>
    private lateinit var activity:ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity=DataBindingUtil.setContentView(this, R.layout.activity_search)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, 0)
        viewPagerAdapter.addFragment(ProductFragment(), "Product")
        viewPagerAdapter.addFragment(PartFragment(), "Part")
        activity.viewPager.adapter = viewPagerAdapter
        activity.tabLayout.setupWithViewPager(activity.viewPager)

    }

    override fun onResume() {
        super.onResume()

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

    }

    override fun onPause() {
        super.onPause()

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.searchBar.windowToken, 0)

    }

}