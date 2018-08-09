/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home

import androidx.lifecycle.MutableLiveData
import app.ogasimli.remoter.helper.exceptions.AppException
import app.ogasimli.remoter.helper.exceptions.ConnectionError
import app.ogasimli.remoter.helper.exceptions.GenericApiError
import app.ogasimli.remoter.helper.rx.EventType
import app.ogasimli.remoter.helper.rx.JobsCount
import app.ogasimli.remoter.helper.rx.JobsCountEvent
import app.ogasimli.remoter.model.data.DataManager
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.model.models.SortOption
import app.ogasimli.remoter.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel class HomeActivity
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
class HomeViewModel @Inject constructor(
        private val dataManager: DataManager,
        private val allJobsDisposable: CompositeDisposable,
        private val bookmarkJobsDisposable: CompositeDisposable) : BaseViewModel() {

    val allJobList = MutableLiveData<List<Job>>()

    val bookmarkedJobList = MutableLiveData<List<Job>>()

    val jobsCount = MutableLiveData<JobsCount>()

    val appException = MutableLiveData<AppException>()

    var sortOptionAllJobs = dataManager.getAllSortOption()

    var sortOptionBookmarkedJobs = dataManager.getBookmarkedSortOption()

    /**
     * Gets all jobs from DB and serves them
     */
    fun getAllJobs() {
        allJobsDisposable.clear()
        allJobsDisposable.add(dataManager.getAllJobs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            setAllJobs(it.jobs)
                        },
                        {
                            Timber.e(it, "Unable to fetch jobs")
                            // Assign exception type to this variable
                            appException.value = when(it) {
                                // non 200 codes
                                is HttpException -> GenericApiError()
                                else -> ConnectionError()
                            }
                        }
                )
        )
    }

    /**
     * Gets bookmarked jobs from DB and serves them
     */
    fun getBookmarkedJobs() {
        bookmarkJobsDisposable.clear()
        bookmarkJobsDisposable.add(dataManager.getBookmarkedJobs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            setBookmarkedJobs(it.jobs)
                        },
                        {
                            Timber.e(it)
                        }
                )
        )
    }

    /**
     * Marks job as bookmarked or un-bookmarked
     *
     * @param job       the job item that should be marked as bookmarked or not bookmarked
     */
    fun bookmarkJob(job: Job) {
        job.isBookmarked = !job.isBookmarked
        disposable.add(dataManager.updateJob(job).subscribe({}, { Timber.e(it) }))
    }

    /**
     * Helper function to sort all jobs list
     *
     * @param sortOption    {@link SortOptions} for indicating the order of the items
     */
    fun sortAllJobs(sortOption: SortOption) {
        // Update local variable
        sortOptionAllJobs = sortOption
        // Save option into SharedPreferences
        dataManager.saveAllSortOption(sortOption)
        // Reload data to apply the sorting option
        getAllJobs()
    }

    /**
     * Helper function to sort bookmarked jobs list
     *
     * @param sortOption    {@link SortOptions} for indicating the order of the items
     */
    fun sortBookmarkedJobs(sortOption: SortOption) {
        // Update local variable
        sortOptionBookmarkedJobs = sortOption
        // Save option into SharedPreferences
        dataManager.saveBookmarkedSortOption(sortOption)
        // Reload data to apply the sorting option
        getBookmarkedJobs()
    }

    /**
     * Set value of allJobList LiveData
     *
     * @param jobs      list of jobs
     */
    private fun setAllJobs(jobs: List<Job>) {
        // Set value of the LiveData
        allJobList.value = jobs
        // Set value of jobsCount LiveData
        setJobsCount(JobsCountEvent(EventType.OPEN_JOBS_COUNT, jobs.size))
    }

    /**
     * Set value of bookmarkedJobList LiveData
     *
     * @param jobs      list of jobs
     */
    private fun setBookmarkedJobs(jobs: List<Job>) {
        // Set value of the LiveData
        bookmarkedJobList.value = jobs
        // Set value of jobsCount LiveData
        setJobsCount(JobsCountEvent(EventType.BOOKMARKED_JOBS_COUNT, jobs.size))
    }

    /**
     * Set value of jobsCount LiveData
     *
     * @param event     wrapper object containing all information about jobs count
     */
    private fun setJobsCount(event: JobsCountEvent) {
        // Create new temporary JobsCount object
        val tempJobsCount = jobsCount.value ?: JobsCount()
        // Assign values to relevant fields
        when (event.type) {
            EventType.OPEN_JOBS_COUNT -> tempJobsCount.openJobs = event.count
            EventType.BOOKMARKED_JOBS_COUNT -> tempJobsCount.bookmarkedJobs = event.count
            else -> return
        }
        tempJobsCount.isSearching = event.isSearching
        // Set value of the LiveData
        jobsCount.value = tempJobsCount
    }

    override fun onCleared() {
        super.onCleared()
        allJobsDisposable.clear()
        bookmarkJobsDisposable.clear()
    }
}