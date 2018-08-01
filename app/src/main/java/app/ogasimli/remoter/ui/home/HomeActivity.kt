/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home

import android.os.Bundle
import androidx.lifecycle.Observer
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.rx.JobsCount
import app.ogasimli.remoter.helper.utils.viewModelProvider
import app.ogasimli.remoter.ui.base.BaseActivity
import app.ogasimli.remoter.ui.custom.CustomPageChangeListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.backdrop_front.*
import timber.log.Timber
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

    private lateinit var jobsCount: JobsCount

    // Tab Icons
    private val tabIcons = intArrayOf(R.drawable.ic_tab_jobs, R.drawable.ic_tab_bookmarks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set up the tool bar
        initToolbar()

        // Bind ViewModel
        viewModel = viewModelProvider(this, viewModelFactory)

        // Setup ViewPager
        setupViewPager()

        // Observe count of jobs
        observeJobsCount()
    }

    /**
     * Helper method to initialize toolbar
     */
    private fun initToolbar() {
        setSupportActionBar(toolbar)
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
            addOnPageChangeListener(pageChangeListener)
        }
    }

    private val pageChangeListener = object: CustomPageChangeListener() {
        override fun onPageSelected(position: Int) {
            setBackdropHeaderText()
        }
    }

    /**
     * Helper method to observe count of jobs
     */
    private fun observeJobsCount() {
        viewModel.jobsCount.observe(this, Observer {
            it?.let {
                Timber.d("$it jobs received")
                jobsCount = it
                setBackdropHeaderText()
            }
        })
    }

    private fun setBackdropHeaderText() {
        header_info_text_view.text = when (view_pager.currentItem) {
            0 -> if (jobsCount.isSearching) {
                resources.getQuantityString(R.plurals.backdrop_front_header_search_result_jobs,
                        jobsCount.openJobs, jobsCount.openJobs)
            } else {
                resources.getQuantityString(R.plurals.backdrop_front_header_all_jobs,
                        jobsCount.openJobs, jobsCount.openJobs)
            }
            1 -> if (jobsCount.isSearching) {
                resources.getQuantityString(R.plurals.backdrop_front_header_search_result_jobs,
                        jobsCount.openJobs, jobsCount.openJobs)
            } else {
                resources.getQuantityString(R.plurals.backdrop_front_header_bookmarked_jobs,
                        jobsCount.bookmarkedJobs, jobsCount.bookmarkedJobs)
            }
            else -> ""
        }
    }
}
