/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.ui.details

import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.transition.TransitionInflater
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import app.ogasimli.rhino.R
import app.ogasimli.rhino.helper.constant.Constants.JOB_ITEM_BUNDLE_KEY
import app.ogasimli.rhino.helper.exceptions.*
import app.ogasimli.rhino.helper.utils.decodeFromHtml
import app.ogasimli.rhino.helper.utils.load
import app.ogasimli.rhino.helper.utils.periodTillNow
import app.ogasimli.rhino.helper.utils.viewModelProvider
import app.ogasimli.rhino.model.models.DataResponse
import app.ogasimli.rhino.model.models.Job
import app.ogasimli.rhino.ui.base.BaseActivity
import app.ogasimli.rhino.ui.custom.ElasticDragDismissFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.thefinestartist.finestwebview.FinestWebView
import kotlinx.android.synthetic.main.activity_details.*
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

/**
Job details screen activity
 *
 * @author Orkhan Gasimli on 02.08.2018.
 */
class DetailsActivity : BaseActivity() {

    private val dragListener by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            object : ElasticDragDismissFrameLayout.SystemChromeFader(this) {
                override fun onDragDismissed() {
                    // if we drag dismiss upward
                    if (draggable_frame.translationY < 0) {
                        // the reversal of the enter transition would slide content downwards
                        // which looks weird. So reverse it.
                        window.returnTransition = TransitionInflater.from(this@DetailsActivity)
                                .inflateTransition(android.R.transition.slide_top)
                        // the reversal of the shared element transition would looks weird if the
                        // shared element no more visible. So we need to disable it.
                        window.sharedElementReturnTransition = null
                        ViewCompat.setTransitionName(company_logo, null)
                    }
                    super.onDragDismissed()
                }
            }
        } else {
            object : ElasticDragDismissFrameLayout.ElasticDragDismissCallback() {
                override fun onDragDismissed() {
                    supportFinishAfterTransition()
                }
            }
        }
    }

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
        setupInitialContent(job)

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

    override fun onResume() {
        super.onResume()
        draggable_frame.addListener(dragListener)
    }

    override fun onPause() {
        draggable_frame.removeListener(dragListener)
        super.onPause()
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
        if (job != null) {
            // Show loading
            showLoadingView()
            // Fetch jobs
            viewModel.getJobInfo(job!!.id)
        }
    }

    /**
     * Helper function to observe job LiveData
     */
    private fun observeJob() {
        viewModel.jobResponse.observe(this, Observer { response ->
            response?.let {
                Timber.d("Response item: $it")
                // Set initial content
                setupContent(response)
                // Show error if error is not null
                showErrorView(response.error)
            }
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
    private fun setupInitialContent(job: Job?) {
        job?.let {
            // Load company logo
            with(company_logo) {
                // Set transition name for SharedElement
                ViewCompat.setTransitionName(this, it.id)
                load(it,
                        resources.getDimension(R.dimen.company_logo_width).toInt(),
                        resources.getDimension(R.dimen.company_logo_height).toInt(),
                        true)
            }
            // Setup toolbar
            toolbar_position_title?.text = it.position
            toolbar_posting_date_title?.text = periodTillNow(this, it.postingTime)
            // Set company name
            company_name?.text = it.company
            // Set click listener for apply button
            apply_btn.setOnClickListener { _ ->
                openWebPage(it.url)
            }
        }
    }

    /**
     * Helper function to setup content with
     * initial response info
     *
     * @param response       response from DB & API call
     */
    private fun setupContent(response: DataResponse<Job>) {
        val jobItem = response.data ?: job
        if (jobItem != null) {
            setJobDescription(jobItem)
            setApplyInstructions(jobItem)
        }
        // Hide loading anyway
        if (!response.showLoading) hideLoadingView()
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
        with(job_description) {
            // Decode job description and set description to the TextView
            text = jobDescription.decodeFromHtml()
            // Make the text visible
            visibility = View.VISIBLE
            // Add movement method
            movementMethod = LinkMovementMethod.getInstance()
        }
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
            apply_instruction?.apply {
                // Set to the TextView
                text = decodedText
                // Add movement method
                movementMethod = LinkMovementMethod.getInstance()
            }
            // Make the group visible
            apply_group.visibility = View.VISIBLE
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

    /**
     * Helper function to setup UI when no error occurs
     *
     * @param exception     error occurred during the work of the app
     */
    private fun showErrorView(exception: Throwable?) {
        if (exception != null) {
            Timber.d(exception, "Received exception")
            // Determine the appropriate {@link AppException} from Throwable
            val error = when (exception) {
                is HttpException -> GenericApiError()
                is IOException -> ConnectionError()
                is SocketTimeoutException -> TimeOutError()
                else -> GenericError()
            }
            // Show error snack
            showErrorSnack(error)
        }
    }

    /**
     * Helper method to show snackbar containing error message
     *
     * @param exception     {@link AppException} occurred in the app
     */
    private fun showErrorSnack(exception: AppException) {
        val msg = exception.toMessage(this)
        with(Snackbar.make(container, msg, Snackbar.LENGTH_LONG)) {
            setAction(android.R.string.ok) { dismiss() }
            show()
        }
    }
}
