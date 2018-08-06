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
import app.ogasimli.remoter.model.models.DataSource
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.model.models.JobResponse
import app.ogasimli.remoter.model.models.SortOption
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
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
     * Request list of jobs from DB & API
     *
     * @param sortOption    {@link SortOptions} for indicating the order of the items
     * @return              Observable holding list of jobs retrieved from API or DB
     */
    fun getAllJobs(sortOption: SortOption): Flowable<JobResponse> =
            Flowable.concatArrayEager(
                    getAllJobsFromDb(sortOption),
                    getAllJobsFromApi(sortOption)
                    //Drop DB data if we can fetch item fast enough from the API
                    //to avoid UI flickers
                    .debounce(400, TimeUnit.MILLISECONDS))

    /**
     * Request list of jobs from API
     *
     * @param sortOption    {@link SortOptions} for indicating the order of the items
     * @return              Observable holding list of jobs retrieved from API
     */
    private fun getAllJobsFromApi(sortOption: SortOption): Flowable<JobResponse> =
            apiService.getJobList()
                    .filter { it.body() != null } // Filter results which has null body
                    .map { response ->
                        Timber.d("Mapping items to JobResponse...")
                        // Distinguish between cache response and network response
                        val source = if (response.raw().networkResponse() != null) {
                            DataSource.API
                        } else {
                            DataSource.CACHE
                        }

                        // Get job list from response body
                        var jobs = response.body()!!

                        // Sort list based on sort options
                        jobs = when (sortOption) {
                            SortOption.BY_POSTING_DATE -> jobs.sortedByDescending { it.postingTime }
                            SortOption.BY_POSITION_NAME -> jobs.sortedBy { it.position }
                            SortOption.BY_COMPANY_NAME -> jobs.sortedBy { it.company }
                        }

                        // Map items to JobResponse
                        JobResponse(jobs = jobs, source = source)
                    }
                    .flatMap { response ->
                        // If response returned from network, then insert results to DB
                        if (response.source == DataSource.API) {
                            jobDao.insertJob(*(response.jobs.toTypedArray()))
                            jobDao.deleteOldJobs(response.jobs.map { it.id })
                        }
                        Flowable.just(response)
                    }
                    .onErrorReturn {
                        Timber.e(it, "Error occurred while fetching from API.")
                        JobResponse(
                                source = DataSource.API,
                                message = "Error occurred while fetching from API.",
                                error = it)
                    }
                    .doOnNext {
                        Timber.d("Saving ${it.jobs.size} jobs from API to DB...")
                    }

    /**
     * Request list of jobs from DB
     *
     * @param sortOption    {@link SortOptions} for indicating the order of the items
     * @return              Observable holding list of jobs retrieved from DB
     */
    private fun getAllJobsFromDb(sortOption: SortOption): Flowable<JobResponse> {
        val fetchMethod: () -> Flowable<List<Job>> = when (sortOption) {
            SortOption.BY_POSTING_DATE -> jobDao::getAllJobsByPostingDate
            SortOption.BY_POSITION_NAME -> jobDao::getAllJobsByPositionName
            SortOption.BY_COMPANY_NAME -> jobDao::getAllJobsByCompanyName
        }
        return fetchMethod()
                .filter { it.isNotEmpty() } // filter empty results
                .map {
                    Timber.d("Mapping items to JobResponse...")
                    // Wrap list of jobs to JobResponse object
                    JobResponse(jobs = it, source = DataSource.DB)
                }
                .onErrorReturn {
                    Timber.e(it, "Error occurred while fetching from DB.")
                    JobResponse(
                            source = DataSource.DB,
                            message = "Error occurred while fetching from DB.",
                            error = it)
                }
                .doOnNext {
                    Timber.d("Dispatching ${it.jobs.size} jobs from DB...")
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
    fun getBookmarkedJobs(sortOption: SortOption): Flowable<JobResponse> {
        val fetchMethod: () -> Flowable<List<Job>> = when (sortOption) {
            SortOption.BY_POSTING_DATE -> jobDao::getBookmarkedJobsByPostingDate
            SortOption.BY_POSITION_NAME -> jobDao::getBookmarkedJobsByPositionName
            SortOption.BY_COMPANY_NAME -> jobDao::getBookmarkedJobsByCompanyName
        }
        return fetchMethod()
                .map {
                    Timber.d("Mapping items to JobResponse...")
                    // Wrap list of jobs to JobResponse object
                    JobResponse(jobs = it, source = DataSource.DB)
                }
                .onErrorReturn {
                    Timber.e(it, "Error occurred while fetching from DB.")
                    JobResponse(
                            source = DataSource.DB,
                            message = "Error occurred while fetching from DB.",
                            error = it)
                }
                .doOnNext {
                    Timber.d("Dispatching ${it.jobs.size} jobs from DB...")
                }
    }

    /**
     * Fetch additional job info from API
     *
     * @param job           job item
     * @return              Observable holding additional job info from API
     */
    fun fetchJobInfo(job: Job): Single<JobResponse> =
            apiService.getJobInfo(job.url)
                    .filter { it.body() != null } // Filter results which has null body
                    .map {
                        val newJob = job.copy(additionalInfo = it.body()!!)
                        Timber.d("Mapping item to JobResponse...")
                        // Distinguish between cache response and network response
                        if (it.raw().networkResponse() != null) {
                            JobResponse(job = newJob, source = DataSource.API)
                        } else {
                            JobResponse(job = newJob, source = DataSource.CACHE)
                        }
                    }
                    .flatMap {
                        Timber.d("Saving new job from API to DB: $it")
                        // If response returned from network, then insert results to DB
                        if (it.source == DataSource.API) {
                            updateJob(it.job!!)
                        }
                        Maybe.just(it)
                    }
                    .toSingle()
                    .onErrorReturn {
                        Timber.e(it, "Error occurred while fetching from API.")
                        JobResponse(
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
    fun getJobById(jobId: String): Flowable<JobResponse> =
            jobDao.getJobById(jobId)
                    .map {
                        Timber.d("Mapping job item to JobResponse: $it")
                        // Wrap list of jobs to JobResponse object
                        JobResponse(job = it, source = DataSource.DB)
                    }
                    .onErrorReturn {
                        Timber.e(it, "Error occurred while fetching job by id: $jobId from DB.")
                        JobResponse(
                                source = DataSource.DB,
                                message = "Error occurred while fetching from DB.",
                                error = it)
                    }
                    .doOnNext {
                        Timber.d("Dispatching job from DB: $it")
                    }
}