/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home.fragment.savedjoblist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.utils.inflate
import app.ogasimli.remoter.helper.utils.viewModelProvider
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.ui.base.BaseFragment
import app.ogasimli.remoter.ui.home.HomeViewModel
import app.ogasimli.remoter.ui.home.fragment.adapter.JobsAdapter
import app.ogasimli.remoter.ui.home.fragment.adapter.JobsAdapterCallback
import kotlinx.android.synthetic.main.fragment_saved_job_list.*
import timber.log.Timber
import javax.inject.Inject

/**
 * SavedJobsListFragment class
 *
 * @author Orkhan Gasimli on 26.07.2018.
 */
class SavedJobListFragment : BaseFragment(), JobsAdapterCallback {

    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var jobsAdapter: JobsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_saved_job_list)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        setupRecyclerView()

        // Bind ViewModel
        viewModel = viewModelProvider(activity!!, viewModelFactory)

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
     * Helper function to observe jobs LiveData
     */
    private fun observeJobs() {
        viewModel.bookmarkedJobList.observe(this, Observer {
            it?.let {
                Timber.d("${it.size} jobs received")
                jobsAdapter.jobs = it
            }
        })
    }

    /* Callback functions invoked through JobsAdapter */
    override fun onJobSaveClick(job: Job) {
        viewModel.bookmarkJob(job)
    }

    override fun onDetailsClick(job: Job) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onApplyClick(job: Job) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}