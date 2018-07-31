/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home

import android.os.Bundle
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.utils.viewModelProvider
import app.ogasimli.remoter.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

/**
 * Home screen activity
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
class HomeActivity : BaseActivity() {

    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var pagerAdapter: HomePagerAdapter

    // Tab Icons
    private val tabIcons = intArrayOf(R.drawable.ic_tab_jobs, R.drawable.ic_tab_bookmarks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set up the tool bar
        setSupportActionBar(toolbar)

        // Bind ViewModel
        viewModel = viewModelProvider(this, viewModelFactory)

        // Setup ViewPager
        setupViewPager()

    }

    /**
     * Helper method to setup ViewPager
     */
    private fun setupViewPager() {
        with(view_pager) {
            adapter = pagerAdapter
//            setPageTransformer(true, ZoomOutPageTransformer())
            tab_layout.setupWithViewPager(this)
            tabIcons.forEachIndexed { index, icon -> tab_layout.getTabAt(index)?.setIcon(icon) }
        }
    }
}
