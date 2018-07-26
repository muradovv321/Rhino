/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home

import android.os.Bundle
import android.view.MenuItem
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.utils.viewModelProvider
import app.ogasimli.remoter.ui.base.BaseActivity
import app.ogasimli.remoter.ui.custom.CustomPageChangeListener
import app.ogasimli.remoter.ui.custom.ZoomOutPageTransformer
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

/**
 * Home screen activity
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
class HomeActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var pagerAdapter: HomePagerAdapter

    private val pageChangeListener = object : CustomPageChangeListener() {
        override fun onPageSelected(position: Int) {
            when (position) {
            // Fragment # 0 - This will show JobListFragment
                0 -> navigation.selectedItemId = R.id.bottom_nav_jobs
            // Fragment # 1 - This will show SavedJobListFragment
                1 -> navigation.selectedItemId = R.id.bottom_nav_saved
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Bind ViewModel
        viewModel = viewModelProvider(this, viewModelFactory)

        // Setup ViewPager
        setupViewPager()

        navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (view_pager.currentItem > 0) {
            super.onBackPressed()
        } else {
            view_pager.currentItem -= 1
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.bottom_nav_jobs -> {
                view_pager.currentItem = 0
                true
            }
            R.id.bottom_nav_saved -> {
                view_pager.currentItem = 1
                true
            }
            else -> false
        }
    }

    /**
     * Helper method to setup ViewPager
     */
    private fun setupViewPager() {
        with(view_pager) {
            adapter = pagerAdapter
            setPageTransformer(true, ZoomOutPageTransformer())
            addOnPageChangeListener(pageChangeListener)
        }
    }
}
