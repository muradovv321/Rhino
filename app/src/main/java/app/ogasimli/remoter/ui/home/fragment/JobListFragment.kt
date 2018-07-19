/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.utils.inflate
import app.ogasimli.remoter.helper.utils.viewModelProvider
import app.ogasimli.remoter.ui.base.BaseFragment
import app.ogasimli.remoter.ui.home.fragment.adapter.JobsAdapter
import kotlinx.android.synthetic.main.fragment_job_list.*
import timber.log.Timber
import javax.inject.Inject

/**
 * JobsListFragment class
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
class JobListFragment : BaseFragment() {

    private lateinit var viewModel: JobListViewModel

    @Inject
    lateinit var jobsAdapter: JobsAdapter

    companion object {
        // Teg used for back-stack management
        val TAG: String = JobListFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_job_list)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        setupRecyclerView()

        // Bind ViewModel
        viewModel = viewModelProvider(this, viewModelFactory)

        // Fetch jobs
        viewModel.fetchJobs()

        // Observe jobs LiveData
        observeJobs()
    }

    /**
     * Helper function to setup RecyclerView
     */
    private fun setupRecyclerView() {
        with(recycler_view) {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = jobsAdapter
        }
    }

    private fun observeJobs() {
        viewModel.jobList.observe(this, Observer {
            it?.let {
                Timber.d("${it.size} jobs received")
                jobsAdapter.jobs = it
            }
        })
    }
}