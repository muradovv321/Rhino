/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.data

import app.ogasimli.remoter.di.scope.ApplicationScope
import app.ogasimli.remoter.model.data.local.room.JobDao
import app.ogasimli.remoter.model.data.remote.api.JobsApiService
import app.ogasimli.remoter.model.models.DataResponse
import app.ogasimli.remoter.model.models.DataSource
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.model.models.SortOption
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

/**
 * Single access point to saving/retrieve job data from API & DB
 *
 * @author Orkhan Gasimli on 20.07.2018.
 */
@ApplicationScope
class JobRepository @Inject constructor(private val apiService: JobsApiService,
                                        private val jobDao: JobDao) {

    /**
     * Request list of jobs from API
     *
     * @param sortOption    {@link SortOptions} for indicating the order of the items
     * @return              Observable holding list of jobs retrieved from API
     */
    private fun fetchAllJobs(sortOption: SortOption): Flowable<DataResponse<List<Job>>> =
            apiService.getJobList()
                    .map { response ->
                        // If network error occurred then throw HttpException
                        // This will forward the emission directly to onErrorReturn method
                        if (response.errorBody() != null) {
                            throw HttpException(response)
                        }

                        Timber.d("Mapping items to DataResponse...")

                        // Distinguish between cache response and network response
                        val source = if (response.raw().networkResponse() != null) {
                            DataSource.API
                        } else {
                            DataSource.CACHE
                        }

                        // Get job list from response body
                        val jobs = response.body()!!

                        // Map items to DataResponse
                        DataResponse(data = jobs, source = source)
                    }
                    .flatMap { response ->
                        val jobs = response.data
                        // If response result is not null or empty, then insert results to DB
                        if (response.source == DataSource.API && jobs != null && jobs.isNotEmpty()) {
                            jobDao.insertJob(*(jobs.toTypedArray()))
                            jobDao.deleteOldJobs(jobs.map { it.id })
                        }

                        Flowable.just(response)
                    }
                    .map { response ->
                        var jobs = response.data
                        // If response result is not null or empty
                        if (jobs != null && jobs.isNotEmpty()) {
                            // Sort list based on sort options
                            jobs = when (sortOption) {
                                SortOption.BY_POSTING_DATE -> jobs.sortedByDescending { it.postingTime }
                                SortOption.BY_POSITION_NAME -> jobs.sortedBy { it.position }
                                SortOption.BY_COMPANY_NAME -> jobs.sortedBy { it.company }
                            }
                        }
                        response.copy(data = jobs)
                    }
                    .onErrorReturn {
                        Timber.e(it, "Error occurred while fetching from API.")
                        DataResponse(
                                source = DataSource.API,
                                message = "Error occurred while fetching from API.",
                                error = it)
                    }

    /**
     * Request list of jobs from DB
     *
     * @param sortOption    {@link SortOptions} for indicating the order of the items
     * @return              Observable holding list of jobs retrieved from DB
     */
    fun getAllJobs(refreshData: Boolean, sortOption: SortOption): Flowable<DataResponse<List<Job>>> {
        val fetchMethod: () -> Flowable<List<Job>> = when (sortOption) {
            SortOption.BY_POSTING_DATE -> jobDao::getAllJobsByPostingDate
            SortOption.BY_POSITION_NAME -> jobDao::getAllJobsByPositionName
            SortOption.BY_COMPANY_NAME -> jobDao::getAllJobsByCompanyName
        }

        var localRefreshData = refreshData
        return fetchMethod()
                .map {
                    Timber.d("Mapping items to DataResponse...")
                    // Wrap list of jobs to DataResponse object
                    DataResponse(
                            data = it,
                            showLoading = refreshData || it.isEmpty(),
                            source = DataSource.DB
                    )
                }
                .flatMap {
                    if (it.data == null || it.data.isEmpty()) {
                        localRefreshData = false
                        fetchAllJobs(sortOption)
                    } else if (localRefreshData) {
                        localRefreshData = false
                        fetchAllJobs(sortOption)
                    } else {
                        Flowable.just(it)
                    }
                }
                .onErrorReturn {
                    Timber.e(it, "Error occurred while fetching from DB.")
                    DataResponse(
                            source = DataSource.DB,
                            message = "Error occurred while fetching from DB.",
                            error = it)
                }
    }

    /**
     * Update job in the DB
     *
     * @param job           job that should be updated
     * @return              Observable holding list of indexes of updated items
     */
    fun updateJob(vararg job: Job): Single<LongArray> =
            Single.fromCallable { jobDao.upsertJob(*job) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnSuccess {
                        Timber.d("Updated ${it.size} jobs in DB...")
                    }
                    .doOnError { Timber.e(it) }

    /**
     * Request list of bookmarked jobs from DB
     *
     * @param sortOption    {@link SortOptions} for indicating the order of the items
     * @return              Observable holding list of bookmarked jobs retrieved from DB
     */
    fun getBookmarkedJobs(sortOption: SortOption): Flowable<DataResponse<List<Job>>> {
        val fetchMethod: () -> Flowable<List<Job>> = when (sortOption) {
            SortOption.BY_POSTING_DATE -> jobDao::getBookmarkedJobsByPostingDate
            SortOption.BY_POSITION_NAME -> jobDao::getBookmarkedJobsByPositionName
            SortOption.BY_COMPANY_NAME -> jobDao::getBookmarkedJobsByCompanyName
        }
        return fetchMethod()
                .map {
                    Timber.d("Mapping items to DataResponse...")
                    // Wrap list of jobs to DataResponse object
                    DataResponse(data = it, source = DataSource.DB)
                }
                .onErrorReturn {
                    Timber.e(it, "Error occurred while fetching from DB.")
                    DataResponse(
                            source = DataSource.DB,
                            message = "Error occurred while fetching from DB.",
                            error = it)
                }
    }

    /**
     * Fetch additional job info from API
     *
     * @param job           job item
     * @return              Observable holding additional job info from API
     */
    fun fetchJobInfo(job: Job): Single<DataResponse<Job>> =
            apiService.getJobInfo(job.id)
                    .filter { it.body() != null } // Filter results which has null body
                    .map {
                        val newJob = job.copy(additionalInfo = it.body()!!)
                        Timber.d("Mapping item to JobResponse...")
                        // Distinguish between cache response and network response
                        if (it.raw().networkResponse() != null) {
                            DataResponse(data = newJob, source = DataSource.API)
                        } else {
                            DataResponse(data = newJob, source = DataSource.CACHE)
                        }
                    }
                    .flatMap { response ->
                        Timber.d("Saving new job from API to DB: $response")
                        // If response returned from network, then insert results to DB
                        if (response.source == DataSource.API && response.data != null) {
                            updateJob(response.data).subscribe(
                                    {
                                        "Updated ${it.size} jobs in DB..."
                                    },
                                    {
                                        Timber.e(it)
                                    }
                            )
                        }
                        Maybe.just(response)
                    }
                    .toSingle()
                    .onErrorReturn {
                        Timber.e(it, "Error occurred while fetching from API.")
                        DataResponse(
                                source = DataSource.API,
                                message = "Error occurred while fetching from API.",
                                error = it)
                    }

    /**
     * Request job by id from DB
     *
     * @param jobId         id of the job to be deleted
     * @return              Observable holding job item retrieved from DB
     */
    fun getJobById(jobId: String): Flowable<DataResponse<Job>> =
            jobDao.getJobById(jobId)
                    .map {
                        Timber.d("Mapping job item to JobResponse: $it")
                        // Wrap list of jobs to JobResponse object
                        DataResponse(data = it, source = DataSource.DB)
                    }
                    .onErrorReturn {
                        Timber.e(it, "Error occurred while fetching job by id: $jobId from DB.")
                        DataResponse(
                                source = DataSource.DB,
                                message = "Error occurred while fetching from DB.",
                                error = it)
                    }
                    .doOnNext {
                        Timber.d("Dispatching job from DB: $it")
                    }
}