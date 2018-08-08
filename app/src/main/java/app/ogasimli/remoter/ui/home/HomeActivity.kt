/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.RadioButton
import androidx.lifecycle.Observer
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.constant.Constants.JOB_ITEM_BUNDLE_KEY
import app.ogasimli.remoter.helper.rx.*
import app.ogasimli.remoter.helper.utils.getJobsCountText
import app.ogasimli.remoter.helper.utils.launchActivity
import app.ogasimli.remoter.helper.utils.viewModelProvider
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.model.models.SortOption
import app.ogasimli.remoter.ui.base.BaseActivity
import app.ogasimli.remoter.ui.custom.CustomPageChangeListener
import app.ogasimli.remoter.ui.custom.SortPopupWindow
import app.ogasimli.remoter.ui.details.DetailsActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.backdrop_front.*
import kotlinx.android.synthetic.main.sort_popup_window.view.*
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
    private val tabIcons = intArrayOf(R.drawable.ic_tie, R.drawable.ic_bookmark)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set up the tool bar
        initToolbar()

        sort_btn.setOnClickListener {
            // Rotate button 180 degree
            rotateBy(180F, it)
            // Show Popup Menu
            showSortPopup(it)
        }

        // Bind ViewModel
        viewModel = viewModelProvider(this, viewModelFactory)

        // Setup ViewPager
        setupViewPager()

        // Observe count of jobs
        observeJobsCount()
    }

    private fun rotateBy(degree: Float, view: View?) {
        view?.apply {
            animate()
                    .rotationBy(degree)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
        }
    }

    /**
     * Helper function to display {@link SortPopupWindow}
     */
    @SuppressLint("InflateParams")
    private fun showSortPopup(v: View) {
        // Get root layout of the popup
        val rootView = layoutInflater.inflate(R.layout.sort_popup_window, null)
        // Create instance of SortPopupWindow
        val popupWindow = SortPopupWindow(this, rootView)
        // Rotate sort button, when popup is dismissed
        popupWindow.setOnDismissListener {
            rotateBy(180F, v)
        }

        // Create array of RadioButtons
        val radioButtons = arrayListOf<RadioButton>(
                rootView.by_date_radio_btn,
                rootView.by_position_radio_btn,
                rootView.by_company_radio_btn)

        // Create array of ViewGroups holding RadioButtons
        val radioButtonContainers = arrayListOf(
                rootView.by_date_container,
                rootView.by_position_container,
                rootView.by_company_container)

        // Set isChecked status of each RadioButton
        radioButtons.forEachIndexed { index, radioButton -> checkRadioButton(radioButton, index) }

        // Iterate over ViewGroups holding RadioButtons
        radioButtonContainers.forEachIndexed { index, container ->
            // Set OnClickListener to each ViewGroups holding RadioButtons
            container.setOnClickListener { _ ->
                // Toggle isChecked state of each RadioButton
                setChecked(radioButtons[index], radioButtons)
                // Close popup
                popupWindow.dismiss()
                // Sort jobs
                sortJobs(SortOption.getFromType(index))
            }
        }

        // If popup is not visible
        if (!popupWindow.isShowing) {
            // then show it
            popupWindow.showAsDropDown(v)
        } else {
            // Else close it
            popupWindow.dismiss()
        }
    }

    /**
     * Helper method to sort jobs
     *
     * @param sortOption    {@link SortOptions} for indicating the order of the items
     */
    private fun sortJobs(sortOption: SortOption) {
        when (view_pager.currentItem) {
            0 -> viewModel.sortAllJobs(sortOption)
            1 -> viewModel.sortBookmarkedJobs(sortOption)
        }
    }

    /**
     * Helper method to set the selected RadioButton checked and unset others
     *
     * @param radioButton   RadioButton that should be checked
     * @param radioButtons  array of all available RadioButtons
     */
    private fun setChecked(radioButton: RadioButton, radioButtons: ArrayList<RadioButton>) {
        radioButtons.forEach { it.isChecked = it.id == radioButton.id }
    }

    /**
     * Helper method to determine the check state of RadioButton
     *
     * @param radioButton   RadioButton element
     */
    private fun checkRadioButton(radioButton: RadioButton, position: Int) {
        when (view_pager.currentItem) {
            0 -> radioButton.isChecked = position == viewModel.sortOptionAllJobs.type
            1 -> radioButton.isChecked = position == viewModel.sortOptionBookmarkedJobs.type
        }
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
            tab_layout.setupWithViewPager(this)
            tabIcons.forEachIndexed { index, icon -> tab_layout.getTabAt(index)?.setIcon(icon) }
            addOnPageChangeListener(pageChangeListener)
        }
    }

    private val pageChangeListener = object : CustomPageChangeListener() {
        override fun onPageSelected(position: Int) {
            setBackdropHeaderText()
            setSortButtonsStatus()
        }
    }

    /**
     * Helper method to observe count of jobs
     */
    private fun observeJobsCount() {
        viewModel.jobsCount.observe(this, Observer { count ->
            count?.let {
                Timber.d("${it.openJobs} open and ${it.bookmarkedJobs} bookmarked jobs received")
                jobsCount = it
                setBackdropHeaderText()
                setSortButtonsStatus()
            }
        })
    }

    /**
     * Helper method to set the text of the header text
     * of Backdrop's front layer
     */
    private fun setBackdropHeaderText() {
        header_info_text_view.text = when (view_pager.currentItem) {
            0 -> if (jobsCount.isSearching) {
                getJobsCountText(this, R.plurals.backdrop_front_header_search_result_jobs,
                        R.string.backdrop_front_header_search_result_jobs_zero, jobsCount.openJobs)
            } else {
                getJobsCountText(this, R.plurals.backdrop_front_header_all_jobs,
                        R.string.backdrop_front_header_all_jobs_zero, jobsCount.openJobs)
            }
            1 -> if (jobsCount.isSearching) {
                getJobsCountText(this, R.plurals.backdrop_front_header_search_result_jobs,
                        R.string.backdrop_front_header_search_result_jobs_zero, jobsCount.bookmarkedJobs)
            } else {
                getJobsCountText(this, R.plurals.backdrop_front_header_bookmarked_jobs,
                        R.string.backdrop_front_header_bookmarked_jobs_zero, jobsCount.bookmarkedJobs)
            }
            else -> ""
        }
    }

    /**
     * Helper method to enable/disable sort button
     * based on the jobsCount
     */
    private fun setSortButtonsStatus() {
        when (view_pager.currentItem) {
            0 -> sort_btn.isEnabled = jobsCount.openJobs > 0
            1 -> sort_btn.isEnabled = jobsCount.bookmarkedJobs > 0
        }
    }

    /**
     * Helper function to listen for RxEvent and register subscriber within the pool
     */
    override fun subscribeToEvents() {
        RxBus.listen<RxEvent>()
                .subscribe { event ->
                    when (event.type) {
                        EventType.JOB_ITEM_CLICK -> {
                            val intent = Intent(this, DetailsActivity::class.java)
                            intent.putExtra(JOB_ITEM_BUNDLE_KEY, event.data as Job)
                            launchActivity(intent)
                        }
                        else -> return@subscribe
                    }
                }
                .registerInBus(this)
    }
}
