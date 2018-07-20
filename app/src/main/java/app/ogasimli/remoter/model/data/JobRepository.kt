/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.data

import app.ogasimli.remoter.di.scope.ApplicationScope
import app.ogasimli.remoter.model.data.api.JobsApiService
import app.ogasimli.remoter.model.data.room.JobDao
import app.ogasimli.remoter.model.models.DataSource
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.model.models.JobList
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
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
     * @return          Observable holding list of jobs retrieved from API or DB
     */
    fun getAllJobs(): Flowable<JobList> {
        return Flowable.concatArray(
                getAllJobsFromDb(),
                getAllJobsFromApi())
/*              // wrap the observed object types into an observable Notification object
                // on which we can check whether the onNext, onError and/or onComplete
                // methods are called
                .materialize()
                .map {
                    // if the observables onError is called, invoke
                    // callback so that presenters can handle error
                    it.error?.let {
                        handleErrorCallback(it, errorCallback)
                    }
                    // put item back into stream
                    it
                }
                // filter out any Observable on which the .onError method is called before
                // passing it to subscribers
                .filter { !it.isOnError }
                // Reverse the effect of materialization
                .dematerialize<JobList>()*/
                //Drop DB data if we can fetch item fast enough from the API
                //to avoid UI flickers
                .debounce(400, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Request list of jobs from DB
     *
     * @return          Observable holding list of jobs retrieved from DB
     */
    private fun getAllJobsFromDb(): Flowable<JobList> {
        return jobDao.getAllJobs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { it.isNotEmpty() } // Filter empty results
                .toObservable() // convert back to observable
                .toFlowable(BackpressureStrategy.MISSING)
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
     * Request list of jobs from API
     *
     * @return          Observable holding list of jobs retrieved from API
     */
    private fun getAllJobsFromApi(): Flowable<JobList> {
        return apiService.getJobList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { it.body() != null } // Filter results which has null body
                .toFlowable(BackpressureStrategy.MISSING)
                .map {
                    Timber.d("Mapping items to JobList...")
                    // Distinguish between cache response and network response
                    if (it.raw().networkResponse() != null) {
                        JobList(it.body()!!, DataSource.API)
                    } else {
                        JobList(it.body()!!, DataSource.CACHE)
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
                    Timber.d("Dispatching ${it.jobs.size} jobs from API...")
                    // If response returned from network, then insert results to DB
                    if (it.source == DataSource.API) {
                        insertNewJobsToDb(it.jobs)
                        deleteOldJobsFromDb(it.jobs)
                    }
                }
    }

    /**
     * Insert new jobs that are not present in DB
     *
     * @param jobs      list of all jobs fetched from API
     */
    private fun insertNewJobsToDb(jobs: List<Job>) {
        Observable.fromCallable { jobDao.insertJob(*(jobs.toTypedArray())) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { Timber.d("Inserted ${it.size} new jobs to DB...") }
                .dispose()
    }

    /**
     * Delete all jobs that are no more present in data fetched from API
     *
     * @param jobs      list of all jobs fetched from API
     */
    private fun deleteOldJobsFromDb(jobs: List<Job>) {
        Observable.fromCallable { jobDao.deleteOldJobs(jobs.map { it.id }) }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { Timber.d("Deleted $it old jobs from DB...") }
                .dispose()
    }
}