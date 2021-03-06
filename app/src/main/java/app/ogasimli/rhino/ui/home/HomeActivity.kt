/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import app.ogasimli.rhino.R
import app.ogasimli.rhino.helper.constant.Constants.JOB_ITEM_BUNDLE_KEY
import app.ogasimli.rhino.helper.exceptions.AppException
import app.ogasimli.rhino.helper.exceptions.toMessage
import app.ogasimli.rhino.helper.rx.*
import app.ogasimli.rhino.helper.utils.*
import app.ogasimli.rhino.model.models.FilterOption
import app.ogasimli.rhino.model.models.Job
import app.ogasimli.rhino.model.models.SortOption
import app.ogasimli.rhino.ui.base.BaseActivity
import app.ogasimli.rhino.ui.custom.*
import app.ogasimli.rhino.ui.details.DetailsActivity
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.backdrop_front.*
import kotlinx.android.synthetic.main.backdrop_layout.*
import kotlinx.android.synthetic.main.sort_popup_window.view.*
import org.jetbrains.anko.childrenSequence
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

    @Inject
    lateinit var disposable: CompositeDisposable

    private lateinit var jobsCount: JobsCount

    // Tab Icons
    private val tabIcons = intArrayOf(R.drawable.ic_tie, R.drawable.ic_bookmark)

    // Listener for revealing backdrop
    private lateinit var mBackdropRevealListener: BackdropRevealListener

    // SearchView element
    private var searchView: RhinoSearchView? = null

    // Search MenuItem
    private var searchItem: MenuItem? = null

    // Flag used to indicated whether the dynamic status check is started
    private var chipStatusChecking: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set up the tool bar
        initToolbar()

        sort_btn.setOnClickListener {
            // Rotate button 180 degree
            it.rotateBy(180F)
            // Show Popup Menu
            showSortPopup(it)
        }

        // Bind ViewModel
        viewModel = viewModelProvider(this, viewModelFactory)

        // Setup ViewPager
        setupViewPager()

        // Set up the filter group located in the backdrop
        setupFilterGroup()

        // Observe count of jobs
        observeJobsCount()
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Setup menu
        menuInflater.inflate(R.menu.home_menu, menu)
        // Find filter MenuItem
        val filterItem = menu?.findItem(R.id.menu_filter)
        // Initialize BackdropRevealListener
        mBackdropRevealListener =
                BackdropRevealListener(
                        this,
                        main_content,
                        backdrop,
                        filterItem,
                        ElasticInOutInterpolator(),
                        R.drawable.ic_menu_filter,
                        R.drawable.ic_menu_filter_close)

        // Create list of MenuItems that has visible icon, except search item
        val menuItems = listOf(filterItem)

        // Find search MenuItem
        searchItem = menu?.findItem(R.id.menu_search)
        // Setup SearchView
        searchView = initSearchView(searchItem, menuItems)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            when (item?.itemId) {
                R.id.menu_search -> true
                R.id.menu_filter -> {
                    mBackdropRevealListener.toggle()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    /**
     * Helper function to setup and configure SearchView
     *
     * @param searchItem    MenuItem that should trigger search functionality
     * @param menuItems     list of MenuItems that need to be hidden/shown during the animation
     */
    private fun initSearchView(searchItem: MenuItem?, menuItems: List<MenuItem?>): RhinoSearchView? {
        // Get access to SearchView
        val searchView = searchItem?.actionView as? RhinoSearchView

        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                // Open SearchView
                searchView?.open(this@HomeActivity, toolbar, menuItems)
                // Close backdrop
                mBackdropRevealListener.close()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                // Cancel search results and reload full data
                if (viewModel.allSearchQuery.value != null) {
                    viewModel.getAllJobs()
                } else if (viewModel.bookmarkedSearchQuery.value != null) {
                    viewModel.getBookmarkedJobs()
                }
                // Close SearchView
                searchView?.close(this@HomeActivity, toolbar, menuItems)
                return false
            }
        })

        // Hook into RxSearchObservable to observe new search queries
        val queryObservable = searchView?.queryObservable
        queryObservable?.apply {
            disposable.add(queryObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { query ->
                        Timber.d("Query: $query")

                        // Search for query
                        if (searchItem.isActionViewExpanded) {
                            when (view_pager.currentItem) {
                                0 -> viewModel.searchAllJobs(query)
                                1 -> viewModel.searchBookmarkedJobs(query)
                            }
                        }
                    }
            )
        }

        // Restore SearchView on config changes
        searchView?.restore(when (view_pager.currentItem) {
            0 -> viewModel.allSearchQuery.value
            1 -> viewModel.bookmarkedSearchQuery.value
            else -> null
        }, searchItem)

        return searchView
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
            v.rotateBy(180F)
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
        run breaker@{
            radioButtons.forEachIndexed { index, radioButton ->
                if (checkRadioButton(radioButton, index)) return@breaker
            }
        }

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
     * @param sortOption    {@link SortOption} for indicating the order of the items
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
     * @param position      position of the Chip
     * @return              true if button is checked, else false
     */
    private fun checkRadioButton(radioButton: RadioButton, position: Int): Boolean {
        return when (view_pager.currentItem) {
            0 -> {
                radioButton.isChecked = position == viewModel.sortOptionAllJobs.type
                radioButton.isChecked
            }
            1 -> {
                radioButton.isChecked = position == viewModel.sortOptionBookmarkedJobs.type
                radioButton.isChecked
            }
            else -> false
        }
    }

    /**
     * Helper method to initialize toolbar
     */
    private fun initToolbar() {
        setSupportActionBar(toolbar)
    }

    /**
     * Helper method to initialize filter
     * chip group
     */
    private fun setupFilterGroup() {
        // Get filter categories from string array
        val categories = resources.getStringArray(R.array.filter_categories)
        // Iterate over categories
        categories.forEach { category ->
            // Inflate chip
            val chip = layoutInflater.inflate(R.layout.chip_group_item_default,
                    filter_chip_group, false) as Chip
            // Set text of the chip
            chip.text = category
        }

        // Set check isChecked
        setFilterChipsStatus()

        // Add OnCheckedChangeListener to ChipGroup
        filter_chip_group.setOnCheckedChangeListener { _, checkedId ->
            if (!chipStatusChecking) {
                if (checkedId == -1) {
                    filterJobs(FilterOption.ALL_LISTINGS)
                } else {
                    // Get FilterOption based on checkedChipId,
                    filterJobs(FilterOption.getFromType(filter_chip_group.findCheckedChipId()))
                }
            }
        }
    }

    /**
     * Helper method to determine the check state of chips
     *
     * @param chip          Chip element
     * @param position      position of the Chip
     * @return              true if chip is checked, else false
     */
    private fun checkChip(chip: Chip, position: Int): Boolean {
        return when (view_pager.currentItem) {
            0 -> {
                chip.isChecked = position == viewModel.filterOptionAllJobs.type
                chip.isChecked
            }
            1 -> {
                chip.isChecked = position == viewModel.filterOptionBookmarkedJobs.type
                chip.isChecked
            }
            else -> false
        }
    }

    /**
     * Helper method to filter jobs
     *
     * @param filterOption    {@link FilterOption} for indicating the order of the items
     */
    private fun filterJobs(filterOption: FilterOption) {
        when (view_pager.currentItem) {
            0 -> viewModel.filterAllJobs(filterOption)
            1 -> viewModel.filterBookmarkedJobs(filterOption)
        }
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
            if (searchItem?.isActionViewExpanded == true) {
                searchItem?.collapseActionView()
            }
            setFilterChipsStatus()
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
                setSortButtonsStatus()
                setBackdropHeaderText()
            }
        })
    }

    /**
     * Helper method to set the text of the header text
     * of Backdrop's front layer
     */
    private fun setBackdropHeaderText() {
        val category = filter_chip_group.getCheckedChipsText()
        val query = jobsCount.query
        header_info_text_view.text = when (view_pager.currentItem) {
            0 -> if (query != null) {
                getJobsCountText(this,
                        R.plurals.backdrop_front_header_search_result_jobs,
                        R.string.backdrop_front_header_search_result_jobs_zero,
                        jobsCount.openJobs, query)
            } else {
                getJobsCountText(this,
                        R.plurals.backdrop_front_header_all_jobs,
                        R.string.backdrop_front_header_all_jobs_zero,
                        jobsCount.openJobs, category)
            }
            1 -> if (query != null) {
                getJobsCountText(this,
                        R.plurals.backdrop_front_header_search_result_jobs,
                        R.string.backdrop_front_header_search_result_jobs_zero,
                        jobsCount.bookmarkedJobs, query)
            } else {
                getJobsCountText(this,
                        R.plurals.backdrop_front_header_bookmarked_jobs,
                        R.string.backdrop_front_header_bookmarked_jobs_zero,
                        jobsCount.bookmarkedJobs, category)
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
     * Helper method to set checked state of filtering chips
     */
    private fun setFilterChipsStatus() {
        chipStatusChecking = true
        run breaker@{
            filter_chip_group.childrenSequence().forEachIndexed { index, chip ->
                if (checkChip(chip as Chip, index + 1)) return@breaker
            }
        }
        chipStatusChecking = false
    }

    /**
     * Helper method to show snackbar containing error message
     *
     * @param exception     {@link AppException} occurred in the app
     */
    fun showErrorSnack(exception: AppException) {
        val msg = exception.toMessage(this)
        with(Snackbar.make(container, msg, Snackbar.LENGTH_LONG)) {
            setAction(android.R.string.ok) { dismiss() }
            show()
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
                            val job = (event.data as Pair<*, *>).first as Job
                            val logo = event.data.second as View
                            val intent = Intent(this, DetailsActivity::class.java)
                            intent.putExtra(JOB_ITEM_BUNDLE_KEY, job)
                            launchActivity(intent, logo, ViewCompat.getTransitionName(logo))
                        }
                        else -> return@subscribe
                    }
                }
                .registerInBus(this)
    }
}
