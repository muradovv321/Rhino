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
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Single access point to saving/retrieve job data from API & DB
 *
 * @author Orkhan Gasimli on 20.07.2018.
 */
@ApplicationScope
class JobRepository @Inject constructor(private val disposable: CompositeDisposable,
                                        private val apiService: JobsApiService,
                                        private val jobDao: JobDao) {

    /**
     * Request list of jobs from API
     *
     * @return              Observable holding list of jobs retrieved from API
     */
    fun fetchAllJobs(): Observable<JobResponse> {
        return apiService.getJobList()
                .filter { it.body() != null } // Filter results which has null body
                .map {
                    Timber.d("Mapping items to JobResponse...")
                    // Distinguish between cache response and network response
                    val source = if (it.raw().networkResponse() != null) {
                        DataSource.API
                    } else {
                        DataSource.CACHE
                    }

                    // Get jobs list from response
                    val jobs = it.body()!!

                    // If response returned from network, then insert results to DB
                    if (source == DataSource.API) {
                        insertNewJobsToDb(jobs)
                    }

                    JobResponse(jobs = jobs, source = source)
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
    }

    /**
     * Request list of jobs from DB
     *
     * @param sortOption    {@link SortOptions} for indicating the order of the items
     * @return              Observable holding list of jobs retrieved from DB
     */
    fun getAllJobs(sortOption: SortOption): Flowable<JobResponse> {
        val fetchMethod: () -> Flowable<List<Job>> = when (sortOption) {
            SortOption.BY_POSTING_DATE -> jobDao::getAllJobsByPostingDate
            SortOption.BY_POSITION_NAME -> jobDao::getAllJobsByPositionName
            SortOption.BY_COMPANY_NAME -> jobDao::getAllJobsByCompanyName
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
     * Insert new jobs that are not present in DB
     *
     * @param jobs          list of all jobs fetched from API
     */
    private fun insertNewJobsToDb(jobs: List<Job>) {
        disposable.add(Single.fromCallable { jobDao.insertJob(*(jobs.toTypedArray())) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map {
                    deleteOldJobsFromDb(jobs)
                    it
                }
                .doOnSuccess {
                    Timber.d("Inserted ${it.size} new jobs to DB...")
                    disposable.clear()
                }
                .doOnError { Timber.e(it) }
                .subscribe()
        )
    }

    /**
     * Delete all jobs that are no more present in data fetched from API
     *
     * @param jobs          list of all jobs fetched from API
     */
    private fun deleteOldJobsFromDb(jobs: List<Job>) {
        disposable.add(Single.fromCallable { jobDao.deleteOldJobs(jobs.map { it.id }) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess {
                    Timber.d("Deleted $it old jobs from DB...")
                    disposable.clear()
                }
                .doOnError { Timber.e(it) }
                .subscribe()
        )
    }

    /**
     * Update job in the DB
     *
     * @param job           job that should be updated
     * @return              Observable holding list of indexes of updated items
     */
    fun updateJob(vararg job: Job) {
        disposable.add(Single.fromCallable { jobDao.upsertJob(*job) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess {
                    Timber.d("Updated ${it.size} jobs in DB...")
                    disposable.clear()
                }
                .doOnError { Timber.e(it) }
                .subscribe()
        )
    }

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
    fun fetchJobInfo(job: Job): Observable<JobResponse> {
        return apiService.getJobInfo(job.url)
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
                .onErrorReturn {
                    Timber.e(it, "Error occurred while fetching from API.")
                    JobResponse(
                            source = DataSource.API,
                            message = "Error occurred while fetching from API.",
                            error = it)
                }
                .doOnNext {
                    Timber.d("Saving new job from API to DB: $it")
                    // If response returned from network, then insert results to DB
                    if (it.source == DataSource.API) {
                        updateJob(it.job!!)
                    }
                }
    }

    /**
     * Request job by id from DB
     *
     * @param jobId         id of the job to be deleted
     * @return              Observable holding job item retrieved from DB
     */
    fun getJobById(jobId: String): Flowable<JobResponse> {
        return jobDao.getJobById(jobId)
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
}