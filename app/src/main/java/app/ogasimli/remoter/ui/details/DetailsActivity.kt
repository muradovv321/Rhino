/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.details

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.lifecycle.Observer
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.constant.Constants.JOB_ITEM_BUNDLE_KEY
import app.ogasimli.remoter.helper.utils.decodeFromHtml
import app.ogasimli.remoter.helper.utils.load
import app.ogasimli.remoter.helper.utils.periodTillNow
import app.ogasimli.remoter.helper.utils.viewModelProvider
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.ui.base.BaseActivity
import com.thefinestartist.finestwebview.FinestWebView
import com.thefinestartist.utils.content.ResourcesUtil.getDimension
import kotlinx.android.synthetic.main.activity_details.*
import timber.log.Timber


/**
Job details screen activity
 *
 * @author Orkhan Gasimli on 02.08.2018.
 */
class DetailsActivity : BaseActivity() {

    private lateinit var viewModel: DetailsViewModel

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Set up the tool bar
        initToolbar()

        // Get data from Intent or Bundle
        job = if (savedInstanceState != null) {
            savedInstanceState.getParcelable(JOB_ITEM_BUNDLE_KEY)
        } else {
            intent.getParcelableExtra(JOB_ITEM_BUNDLE_KEY)
        }

        // Finish activity if job is null
        if (job == null) supportFinishAfterTransition()

        // Initial setup of content
        setupInitialContent(job!!)

        // Bind ViewModel
        viewModel = viewModelProvider(this, viewModelFactory)

        // Fetch jobs
        fetchJobInfo()

        // Observe jobs LiveData
        observeJob()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(JOB_ITEM_BUNDLE_KEY, job)
    }

    /**
     * Helper method to initialize toolbar
     */
    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            supportFinishAfterTransition()
        }
    }

    /**
     * Helper function to fetch jobs
     */
    private fun fetchJobInfo() {
        // Show loading
        showLoadingView()
        // Fetch jobs
        viewModel.fetchJobInfo(job!!)
    }

    /**
     * Helper function to observe job LiveData
     */
    private fun observeJob() {
        viewModel.job.observe(this, Observer { job ->
            job?.let {
                Timber.d("Job item: $it")
                // Set initial content
                setupContent(job)
            }
            // Hide loading
            hideLoadingView()
        })
    }

    /**
     * Method to start loading animation
     */
    private fun showLoadingView() {
        job_description?.visibility = View.GONE
        apply_group?.visibility = View.GONE
        shimmer_layout?.visibility = View.VISIBLE
        shimmer_layout?.startShimmerAnimation()
        // Disallow touch events pass through the container to its children
        constraint_layout?.requestDisallowInterceptTouchEvent(true)
    }

    /**
     * Method to cancel loading animation
     */
    private fun hideLoadingView() {
        shimmer_layout?.visibility = View.GONE
        shimmer_layout?.stopShimmerAnimation()
        // Allow touch events pass through the container to its children
        constraint_layout?.requestDisallowInterceptTouchEvent(false)
    }

    /**
     * Helper function for initial page setup
     *
     * @param job       job item
     */
    private fun setupInitialContent(job: Job) {
        company_logo.load(job,
                getDimension(R.dimen.company_logo_width).toInt(),
                getDimension(R.dimen.company_logo_height).toInt(),
                true)
        toolbar_position_title.text = job.position
        toolbar_posting_date_title.text = periodTillNow(this, job.postingTime)
        company_name.text = job.company
    }

    /**
     * Helper function to setup content
     * with initial job info
     *
     * @param job       job item
     */
    private fun setupContent(job: Job) {
        setJobDescription(job)
        setApplyInstructions(job)
        apply_btn.setOnClickListener {
            openWebPage(job.url)
        }
    }

    /**
     * Helper function to set job description
     *
     * @param job       job item
     */
    private fun setJobDescription(job: Job) {
        // If detailed job description available use it, otherwise use compact description
        var jobDescription = job.additionalInfo?.jobDesc ?: job.description
        // If none of the descriptions available, then use predefined no description message
        if (jobDescription.isBlank()) jobDescription = getString(R.string.no_job_description)
        // Decode job description and set description to the TextView
        job_description.text = jobDescription.decodeFromHtml()
        // Make the text visible
        job_description.visibility = View.VISIBLE
        // Add movement method
        job_description.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Helper function to setup how to apply section
     *
     * @param job       job item
     */
    private fun setApplyInstructions(job: Job) {
        // Assign instructions to new variable
        val instruction = job.additionalInfo?.applyInstruction
        // Decode instructions from HTML
        val decodedText = instruction?.decodeFromHtml()
        // If decoded instructions are available
        if (decodedText != null && decodedText.isNotBlank()) {
            // Set to the TextView
            apply_instruction.text = decodedText
            // Make the group visible
            apply_group.visibility = View.VISIBLE
            // Add movement method
            apply_instruction.movementMethod = LinkMovementMethod.getInstance()
        } else {
            // Make the group invisible
            apply_group.visibility = View.GONE
        }
    }

    /**
     * Helper function to open browser with given URL
     *
     * @param url       URL of the web page
     */
    private fun openWebPage(url: String) {
        FinestWebView.Builder(this)
                .progressBarColorRes(R.color.colorAccent)
                .swipeRefreshColorsRes(R.array.swipeRefreshColorArray)
                .show(url)
    }
}
