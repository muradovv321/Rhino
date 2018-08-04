/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.lifecycle.Observer
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.constant.Constants
import app.ogasimli.remoter.helper.constant.Constants.JOB_ITEM_BUNDLE_KEY
import app.ogasimli.remoter.helper.utils.decodeFromHtml
import app.ogasimli.remoter.helper.utils.loadCompanyLogo
import app.ogasimli.remoter.helper.utils.periodTillNow
import app.ogasimli.remoter.helper.utils.viewModelProvider
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_details.*
import timber.log.Timber


/**
Job details screen activity
 *
 * @author Orkhan Gasimli on 02.08.2018.
 */
class DetailsActivity : BaseActivity() {

    private lateinit var viewModel: DetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Set up the tool bar
        initToolbar()

        // Bind ViewModel
        viewModel = viewModelProvider(this, viewModelFactory)

        // Observe jobs LiveData
        observeJob()

        // Fetch jobs
        fetchJobs()
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
    private fun fetchJobs() {
        // Show loading
        showLoadingView()
        // Get data from Intent
        val job: Job = intent.getParcelableExtra(JOB_ITEM_BUNDLE_KEY)
        // Pass Intent extra to ViewModel and set LiveData
        viewModel.setJob(job)
        // Fetch jobs
        viewModel.fetchJobInfo(job)
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
     * Method to start SwipeRefreshLayout
     */
    private fun showLoadingView() {
//        if (!swipe_refresh_layout.isRefreshing) {
//            swipe_refresh_layout.isRefreshing = true
//        }
    }

    /**
     * Method to cancel SwipeRefreshLayout
     */
    private fun hideLoadingView() {
//        if (swipe_refresh_layout.isRefreshing) {
//            swipe_refresh_layout.isRefreshing = false
//        }
    }

    /**
     * Helper function to setup of the page
     *
     * @param job       job item
     */
    private fun setupContent(job: Job) {
        loadCompanyLogo(job, this, company_logo)
        toolbar_position_title.text = job.position
        toolbar_posting_date_title.text = periodTillNow(this, job.postingDate)
        company_name.text = job.company
        setJobDescription(job)
        setApplyInstructions(job)
        apply_btn.setOnClickListener {
            var url = job.additionalInfo?.applyUrl ?: "${Constants.BASE_APPLY_URL}/l/$job.id"
            if (url.startsWith("/l/")) {
                url = url.prependIndent(Constants.BASE_APPLY_URL)
            }
            openWebPage(url)
        }
    }

    private fun setApplyInstructions(job: Job) {
        // Assign instructions to new variable
        val instruction = job.additionalInfo?.applyInstruction
        // If instructions are available
        if (instruction != null && instruction.isNotBlank()) {
            // Decode it and set to the TextView
            apply_instruction.text = instruction.decodeFromHtml()
            // Make the group visible
            apply_group.visibility = View.VISIBLE
        } else {
            // Make the group invisible
            apply_group.visibility = View.GONE
        }
        // Add movement method
        apply_instruction.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setJobDescription(job: Job) {
        // If detailed job description available use it, otherwise use compact description
        var jobDescription = job.additionalInfo?.jobDesc ?: job.description
        // Decode job description (convert HTML tags to styling)
        jobDescription = jobDescription.decodeFromHtml()
        // If none of the descriptions available, then use predefined no description message
        if (jobDescription.isBlank()) jobDescription = getString(R.string.no_job_description)
        // Set description to the TextView
        job_description.text = jobDescription
        // Add movement method
        job_description.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Helper function to open browser with given URL
     *
     * @param url       URL of the web page
     */
    private fun openWebPage(url: String) {
        val webPage = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webPage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}
