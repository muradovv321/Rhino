/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.ui.home

import androidx.lifecycle.MutableLiveData
import app.ogasimli.rhino.helper.rx.EventType
import app.ogasimli.rhino.helper.rx.JobsCount
import app.ogasimli.rhino.helper.rx.JobsCountEvent
import app.ogasimli.rhino.model.data.DataManager
import app.ogasimli.rhino.model.models.DataResponse
import app.ogasimli.rhino.model.models.FilterOption
import app.ogasimli.rhino.model.models.Job
import app.ogasimli.rhino.model.models.SortOption
import app.ogasimli.rhino.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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

    val allJobs = MutableLiveData<DataResponse<List<Job>>>()

    val bookmarkedJobs = MutableLiveData<DataResponse<List<Job>>>()

    val jobsCount = MutableLiveData<JobsCount>()

    var allSearchQuery = MutableLiveData<String>()

    var bookmarkedSearchQuery = MutableLiveData<String>()

    var sortOptionAllJobs = dataManager.getAllSortOption()

    var sortOptionBookmarkedJobs = dataManager.getBookmarkedSortOption()

    var filterOptionAllJobs = dataManager.getAllFilterOption()

    var filterOptionBookmarkedJobs = dataManager.getBookmarkedFilterOption()

    /**
     * Gets all jobs from DB and serves them
     */
    fun getAllJobs(refreshData: Boolean = false) {
        allSearchQuery.value = null
        allJobsDisposable.clear()
        allJobsDisposable.add(dataManager.getAllJobs(refreshData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::setAllJobs,
                        Timber::e
                )
        )
    }

    /**
     * Gets bookmarked jobs from DB and serves them
     */
    fun getBookmarkedJobs() {
        bookmarkedSearchQuery.value = null
        bookmarkJobsDisposable.clear()
        bookmarkJobsDisposable.add(dataManager.getBookmarkedJobs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::setBookmarkedJobs,
                        Timber::e
                )
        )
    }

    /**
     * Gets jobs matching the query text from DB and serves them
     *
     * @param query     query string
     */
    fun searchAllJobs(query: String) {
        allSearchQuery.value =  query
        allJobsDisposable.clear()
        allJobsDisposable.add(dataManager.searchAllJobs(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::setAllJobs,
                        Timber::e
                )
        )
    }

    /**
     * Gets bookmarked jobs matching the query text from DB and serves them
     *
     * @param query     query string
     */
    fun searchBookmarkedJobs(query: String) {
        bookmarkedSearchQuery.value =  query
        bookmarkJobsDisposable.clear()
        bookmarkJobsDisposable.add(dataManager.searchBookmarkedJobs(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::setBookmarkedJobs,
                        Timber::e
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
     * @param sortOption    {@link SortOption} for indicating the order of the items
     */
    fun sortAllJobs(sortOption: SortOption) {
        // Update local variable
        sortOptionAllJobs = sortOption
        // Save option into SharedPreferences
        dataManager.saveAllSortOption(sortOption)
        // Reload data to apply the sorting option
        val query = allSearchQuery.value
        if (query == null || query.isBlank()) {
            getAllJobs()
        } else {
            searchAllJobs(query)
        }
    }

    /**
     * Helper function to sort bookmarked jobs list
     *
     * @param sortOption    {@link SortOption} for indicating the order of the items
     */
    fun sortBookmarkedJobs(sortOption: SortOption) {
        // Update local variable
        sortOptionBookmarkedJobs = sortOption
        // Save option into SharedPreferences
        dataManager.saveBookmarkedSortOption(sortOption)
        // Reload data to apply the sorting option
        val query = bookmarkedSearchQuery.value
        if (query == null || query.isBlank()) {
            getBookmarkedJobs()
        } else {
            searchBookmarkedJobs(query)
        }
    }

    /**
     * Helper function to filter all jobs list
     *
     * @param filterOption    {@link FilterOption} for indicating the order of the items
     */
    fun filterAllJobs(filterOption: FilterOption) {
        // Update local variable
        filterOptionAllJobs = filterOption
        // Save option into SharedPreferences
        dataManager.saveAllFilterOption(filterOption)
        // Reload data to apply the filtering option
        getAllJobs()
    }

    /**
     * Helper function to filter bookmarked jobs list
     *
     * @param filterOption    {@link FilterOption} for indicating the order of the items
     */
    fun filterBookmarkedJobs(filterOption: FilterOption) {
        // Update local variable
        filterOptionBookmarkedJobs = filterOption
        // Save option into SharedPreferences
        dataManager.saveBookmarkedFilterOption(filterOption)
        // Reload data to apply the filtering option
        getBookmarkedJobs()
    }

    /**
     * Set value of allJobs LiveData
     *
     * @param response      Response object containing job list and additional data
     */
    private fun setAllJobs(response: DataResponse<List<Job>>) {
        // Set value of the LiveData
        val newData = if (response.error != null) {
            DataResponse(
                    data = allJobs.value?.data ?: emptyList(),
                    source = response.source,
                    showLoading = response.showLoading,
                    message = response.message,
                    error = response.error)
        } else {
            response
        }
        allJobs.value = newData
        // Set value of jobsCount LiveData
        setJobsCount(JobsCountEvent(EventType.OPEN_JOBS_COUNT,
                newData.data?.size ?: 0,
                response.query)
        )
    }

    /**
     * Set value of bookmarkedJobs LiveData
     *
     * @param response      Response object containing job list and additional data
     */
    private fun setBookmarkedJobs(response: DataResponse<List<Job>>) {
        // Set value of the LiveData
        val newData = if (response.error != null) {
            DataResponse(
                    data = bookmarkedJobs.value?.data ?: emptyList(),
                    source = response.source,
                    showLoading = response.showLoading,
                    message = response.message,
                    error = response.error)
        } else {
            response
        }
        bookmarkedJobs.value = newData
        // Set value of jobsCount LiveData
        setJobsCount(JobsCountEvent(EventType.BOOKMARKED_JOBS_COUNT,
                newData.data?.size ?: 0,
                response.query)
        )
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
        tempJobsCount.query = event.query
        // Set value of the LiveData
        jobsCount.value = tempJobsCount
    }

    override fun onCleared() {
        super.onCleared()
        allJobsDisposable.clear()
        bookmarkJobsDisposable.clear()
    }
}