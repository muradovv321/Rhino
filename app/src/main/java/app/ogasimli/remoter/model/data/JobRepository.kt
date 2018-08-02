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
import app.ogasimli.remoter.model.models.JobList
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
    fun fetchAllJobs(): Observable<JobList> {
        return apiService.getJobList()
                .filter { it.body() != null } // Filter results which has null body
                .map {
                    Timber.d("Mapping items to JobList...")
                    // Distinguish between cache response and network response
                    if (it.raw().networkResponse() != null) {
                        JobList(jobs = it.body()!!, source = DataSource.API)
                    } else {
                        JobList(jobs = it.body()!!, source = DataSource.CACHE)
                    }
                }
                .onErrorReturn {
                    Timber.e(it, "Error occurred while fetching from API.")
                    JobList(
                            source = DataSource.API,
                            message = "Error occurred while fetching from API.",
                            error = it)
                }
                .doOnNext {
                    Timber.d("Saving ${it.jobs.size} jobs from API to DB...")
                    // If response returned from network, then insert results to DB
                    if (it.source == DataSource.API) {
                        insertNewJobsToDb(it.jobs)
                        deleteOldJobsFromDb(it.jobs)
                    }
                }
    }

    /**
     * Request list of jobs from DB
     *
     * @param sortOption    {@link SortOptions} for indicating the order of the items
     * @return              Observable holding list of jobs retrieved from DB
     */
    fun getAllJobs(sortOption: SortOption): Flowable<JobList> {
        val fetchMethod: () -> Flowable<List<Job>> = when(sortOption) {
            SortOption.BY_POSTING_DATE -> jobDao::getAllJobsByPostingDate
            SortOption.BY_POSITION_NAME -> jobDao::getAllJobsByPositionName
            SortOption.BY_COMPANY_NAME -> jobDao::getAllJobsByCompanyName
        }
        return fetchMethod()
                .map {
                    Timber.d("Mapping items to JobList...")
                    // Wrap list of jobs to JobList object
                    JobList(it, DataSource.DB)
                }
                .onErrorReturn {
                    Timber.e(it, "Error occurred while fetching from DB.")
                    JobList(
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
        disposable.add(Observable.fromCallable { jobDao.insertJob(*(jobs.toTypedArray())) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnComplete {
                    disposable.clear()
                }
                .subscribe(
                        {
                            Timber.d("Inserted ${it.size} new jobs to DB...")
                        },
                        {
                            Timber.e(it)
                        }
                )
        )
    }

    /**
     * Delete all jobs that are no more present in data fetched from API
     *
     * @param jobs          list of all jobs fetched from API
     */
    private fun deleteOldJobsFromDb(jobs: List<Job>) {
        disposable.add(Observable.fromCallable { jobDao.deleteOldJobs(jobs.map { it.id }) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnComplete {
                    disposable.clear()
                }
                .subscribe(
                        {
                            Timber.d("Deleted $it old jobs from DB...")
                        },
                        {
                            Timber.e(it)
                        }
                )
        )
    }

    /**
     * Update job in the DB
     *
     * @param job           job that should be updated
     * @return              Observable holding list of indexes of updated items
     */
    fun updateJob(vararg job: Job): Single<LongArray> = Single.fromCallable { jobDao.upsertJob(*job) }

    /**
     * Request list of bookmarked jobs from DB
     *
     * @param sortOption    {@link SortOptions} for indicating the order of the items
     * @return              Observable holding list of bookmarked jobs retrieved from DB
     */
    fun getBookmarkedJobs(sortOption: SortOption): Flowable<JobList> {
        val fetchMethod: () -> Flowable<List<Job>> = when(sortOption) {
            SortOption.BY_POSTING_DATE -> jobDao::getBookmarkedJobsByPostingDate
            SortOption.BY_POSITION_NAME -> jobDao::getBookmarkedJobsByPositionName
            SortOption.BY_COMPANY_NAME -> jobDao::getBookmarkedJobsByCompanyName
        }
        return fetchMethod()
                .map {
                    Timber.d("Mapping items to JobList...")
                    // Wrap list of jobs to JobList object
                    JobList(it, DataSource.DB)
                }
                .onErrorReturn {
                    Timber.e(it, "Error occurred while fetching from DB.")
                    JobList(
                            source = DataSource.DB,
                            message = "Error occurred while fetching from DB.",
                            error = it)
                }
                .doOnNext {
                    Timber.d("Dispatching ${it.jobs.size} jobs from DB...")
                }
    }
}