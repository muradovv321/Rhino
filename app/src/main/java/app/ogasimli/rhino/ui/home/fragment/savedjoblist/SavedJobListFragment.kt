/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.ui.home.fragment.savedjoblist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import app.ogasimli.rhino.R
import app.ogasimli.rhino.helper.exceptions.ConnectionError
import app.ogasimli.rhino.helper.exceptions.GenericApiError
import app.ogasimli.rhino.helper.exceptions.GenericError
import app.ogasimli.rhino.helper.exceptions.TimeOutError
import app.ogasimli.rhino.helper.rx.EventType
import app.ogasimli.rhino.helper.rx.RxBus
import app.ogasimli.rhino.helper.rx.RxEvent
import app.ogasimli.rhino.helper.rx.registerInBus
import app.ogasimli.rhino.helper.utils.inflate
import app.ogasimli.rhino.helper.utils.viewModelProvider
import app.ogasimli.rhino.model.models.Job
import app.ogasimli.rhino.ui.base.BaseFragment
import app.ogasimli.rhino.ui.home.HomeActivity
import app.ogasimli.rhino.ui.home.HomeViewModel
import app.ogasimli.rhino.ui.home.fragment.adapter.JobsAdapter
import kotlinx.android.synthetic.main.fragment_bookmarked_job_list.*
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

/**
 * SavedJobsListFragment class
 *
 * @author Orkhan Gasimli on 26.07.2018.
 */
class SavedJobListFragment : BaseFragment() {

    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var jobsAdapter: JobsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_bookmarked_job_list)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        setupRecyclerView()

        // Bind ViewModel
        viewModel = viewModelProvider(activity!!, viewModelFactory)

        // Fetch bookmarked jobs
        fetchBookmarkedJobs()

        // Observe jobs LiveData
        observeJobs()
    }

    /**
     * Helper function to setup RecyclerView
     */
    private fun setupRecyclerView() {
        with(jobs_recycler_view) {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = jobsAdapter
        }
    }

    /**
     * Helper function to fetch bookmarked jobs
     */
    private fun fetchBookmarkedJobs() {
        // Fetch only if the last operation was not search
        if (viewModel.bookmarkedSearchQuery.value == null) {
            // Fetch jobs
            viewModel.getBookmarkedJobs()
        }
    }

    /**
     * Helper function to observe jobs LiveData
     */
    private fun observeJobs() {
        viewModel.bookmarkedJobs.observe(this, Observer { response ->
            response?.let {
                val jobs = response.data
                if (jobs != null && jobs.isNotEmpty()) {
                    showResultView(jobs)
                } else {
                    if (!response.query.isNullOrBlank()) {
                        showNoSearchResultView()
                    } else {
                        showEmptyView()
                    }
                }

                showErrorView(response.error)
            }
        })
    }

    /**
     * Helper function to setup UI when jobs received
     *
     * @param jobs      list of received job items
     */
    private fun showResultView(jobs: List<Job>) {
        Timber.d("${jobs.size} jobs received")
        // Forward jobs to JobsAdapter
        jobsAdapter.jobs = jobs
        // Show RecyclerView
        jobs_recycler_view.visibility = View.VISIBLE
        // Hide empty view
        empty_view.visibility = View.GONE
        // Hide empty search view
        no_search_result_view.visibility = View.GONE
    }

    /**
     * Helper function to setup UI when no jobs received
     */
    private fun showEmptyView() {
        Timber.d("No jobs received")
        // Forward empty list to JobsAdapter
        jobsAdapter.jobs = emptyList()
        // Show empty view
        empty_view.visibility = View.VISIBLE
        // Hide RecyclerView
        jobs_recycler_view.visibility = View.GONE
        // Hide empty search view
        no_search_result_view.visibility = View.GONE
    }

    /**
     * Helper function to setup UI when no search result received
     */
    private fun showNoSearchResultView() {
        Timber.d("No jobs received matching the search query")
        // Forward empty list to JobsAdapter
        jobsAdapter.jobs = emptyList()
        // Show no search result view
        no_search_result_view.visibility = View.VISIBLE
        // Hide RecyclerView
        jobs_recycler_view.visibility = View.GONE
        // Hide empty view
        empty_view.visibility = View.GONE
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
            val error = when(exception) {
                is HttpException -> GenericApiError()
                is IOException -> ConnectionError()
                is SocketTimeoutException -> TimeOutError()
                else -> GenericError()
            }
            // Show error snack
            (activity as HomeActivity).showErrorSnack(error)
        }
    }

    /**
     * Helper function to listen for RxEvent and register subscriber within the pool
     */
    override fun subscribeToEvents() {
        RxBus.listen<RxEvent>()
                .subscribe { event ->
                    if (userVisibleHint) {
                        when (event.type) {
                            EventType.BOOKMARK_BUTTON_CLICK -> viewModel.bookmarkJob(event.data as Job)
                            else -> return@subscribe
                        }
                    }
                }
                .registerInBus(this)
    }
}