/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.Observer
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.rx.*
import app.ogasimli.remoter.helper.utils.getJobsCountText
import app.ogasimli.remoter.helper.utils.viewModelProvider
import app.ogasimli.remoter.model.models.SortOption
import app.ogasimli.remoter.ui.base.BaseActivity
import app.ogasimli.remoter.ui.custom.CustomPageChangeListener
import app.ogasimli.remoter.ui.details.DetailsActivity
import com.github.zawadz88.materialpopupmenu.popupMenu
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.backdrop_front.*
import kotlinx.android.synthetic.main.sort_by_company_name_layout.view.*
import kotlinx.android.synthetic.main.sort_by_position_name_layout.view.*
import kotlinx.android.synthetic.main.sort_by_posting_date_layout.view.*
import org.jetbrains.anko.startActivity
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

        sort_btn.setOnClickListener {
            showSortPopup(it)
        }

        // Bind ViewModel
        viewModel = viewModelProvider(this, viewModelFactory)

        // Setup ViewPager
        setupViewPager()

        // Observe count of jobs
        observeJobsCount()
    }

    /**
     * Helper function to display Popup Menu
     */
    private fun showSortPopup(v: View) {
        popupMenu.show(this@HomeActivity, v)
    }

    // Popup menu
    private val popupMenu = popupMenu {
        style = R.style.Widget_Remoter_MPM_Menu
        dropdownGravity = Gravity.START
        val radioButtons = arrayListOf<RadioButton>()
        section {
            title = "Sort by"
            customItem {
                lateinit var radioBtn: RadioButton
                layoutResId = R.layout.sort_by_posting_date_layout
                viewBoundCallback = { view ->
                    radioBtn = view.by_posting_date_radio_btn
                    radioButtons.add(radioBtn)
                    checkRadioButton(radioBtn, 0)
                }
                callback = {
                    setChecked(radioBtn, radioButtons)
                    sortJobs(SortOption.BY_POSTING_DATE)
                }
            }
            customItem {
                lateinit var radioBtn: RadioButton
                layoutResId = R.layout.sort_by_position_name_layout
                viewBoundCallback = { view ->
                    radioBtn = view.by_position_name_radio_btn
                    radioButtons.add(radioBtn)
                    checkRadioButton(radioBtn, 1)
                }
                callback = {
                    setChecked(radioBtn, radioButtons)
                    sortJobs(SortOption.BY_POSITION_NAME)
                }
            }
            customItem {
                lateinit var radioBtn: RadioButton
                layoutResId = R.layout.sort_by_company_name_layout
                viewBoundCallback = { view ->
                    radioBtn = view.by_company_name_radio_btn
                    radioButtons.add(radioBtn)
                    checkRadioButton(radioBtn, 2)
                }
                callback = {
                    setChecked(radioBtn, radioButtons)
                    sortJobs(SortOption.BY_COMPANY_NAME)
                }
            }
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
        }
    }

    /**
     * Helper method to observe count of jobs
     */
    private fun observeJobsCount() {
        viewModel.jobsCount.observe(this, Observer { count ->
            count?.let {
                Timber.d("$it jobs received")
                jobsCount = it
                setBackdropHeaderText()
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
     * Helper function to listen for RxEvent and register subscriber within the pool
     */
    override fun subscribeToEvents() {
        RxBus.listen<RxEvent>()
                .subscribe { event ->
                    when (event.type) {
                        EventType.JOB_ITEM_CLICK -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                val intent = Intent(this, DetailsActivity::class.java)
                                val options = ActivityOptions.makeSceneTransitionAnimation(this)
                                startActivity(intent, options.toBundle())
                            } else {
                                startActivity<DetailsActivity>()
                            }
                        }
                        else -> return@subscribe
                    }
                }
                .registerInBus(this)
    }
}
