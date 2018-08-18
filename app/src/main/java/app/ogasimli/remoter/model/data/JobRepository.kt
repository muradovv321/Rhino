/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.data

import androidx.sqlite.db.SimpleSQLiteQuery
import app.ogasimli.remoter.di.scope.ApplicationScope
import app.ogasimli.remoter.model.data.local.room.JobDao
import app.ogasimli.remoter.model.data.remote.api.JobsApiService
import app.ogasimli.remoter.model.models.*
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
                                        private val jobDao: JobDao,
                                        private val filterKeywords: FilterKeywords) {

    /**
     * Request list of jobs from API
     *
     * @param dbResponse    result of fetching DB
     * @return              Observable holding list of jobs retrieved from API
     */
    private fun fetchAllJobs(dbResponse: DataResponse<List<Job>>):
            Flowable<DataResponse<List<Job>>> =
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
                    .map { it.copy(data = dbResponse.data) }
                    .onErrorReturn {
                        Timber.e(it, "Error occurred while fetching from API.")
                        DataResponse(
                                data = dbResponse.data,
                                source = DataSource.API,
                                message = "Error occurred while fetching from API.",
                                error = it)
                    }

    /**
     * Request list of jobs from DB
     *
     * @param sortOption    {@link SortOption} for indicating the order of the items
     * @param filterOption  {@link FilterOption} for indicating the order of the items
     * @return              Observable holding list of jobs retrieved from DB
     */
    fun getAllJobs(refreshData: Boolean, sortOption: SortOption,
                   filterOption: FilterOption): Flowable<DataResponse<List<Job>>> {
        var localRefreshData = refreshData
        val query = getAllJobsQuery(sortOption, filterOption)
        return jobDao.getAllJobs(query)
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
                    if (it.data == null || it.data.isEmpty() || localRefreshData) {
                        localRefreshData = false
                        Flowable.just(it).mergeWith(fetchAllJobs(it))
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
     * Generates and returns {@Link SimpleSQLiteQuery}
     * for querying jobs from DB
     *
     * @param sortOption    {@link SortOption} for indicating the order of the items
     * @param filterOption  {@link FilterOption} for indicating the order of the items
     * @return              SQLite query
     */
    private fun getAllJobsQuery(sortOption: SortOption, filterOption: FilterOption): SimpleSQLiteQuery {
        val keywords = filterKeywords.getKeywordsByFilterOption(filterOption)
        val include = keywords.include
        val exclude = keywords.exclude
        val columnName = SortOption.toColumnName(sortOption)
        return SimpleSQLiteQuery("""
            SELECT * FROM jobs
            ${if (exclude.isNotEmpty() || include.isNotEmpty()) "WHERE" else ""}
                ${exclude.mapIndexed { index, item ->
            "tags NOT LIKE '%$item%' ${if (index < exclude.size - 1 || include.isNotEmpty()) "AND" else ""}"
        }.joinToString(" ")}
                ${include.mapIndexed { index, item ->
            "${if (index == 0) "(" else ""} tags LIKE '%$item%' OR position LIKE '%$item%' " +
                    if (index < include.size - 1) "OR" else ")"
        }.joinToString(" ")}
            ORDER BY $columnName ${if (sortOption.name == "BY_POSTING_DATE") "DESC" else "ASC"}
            """.trimIndent())
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
     * @param sortOption    {@link SortOption} for indicating the order of the items
     * @return              Observable holding list of bookmarked jobs retrieved from DB
     */
    fun getBookmarkedJobs(sortOption: SortOption,
                          filterOption: FilterOption): Flowable<DataResponse<List<Job>>> {
        val query = getBookmarkedJobsQuery(sortOption, filterOption)
        return jobDao.getBookmarkedJobs(query)
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
     * Generates and returns {@Link SimpleSQLiteQuery}
     * for querying bookmarked jobs from DB
     *
     * @param sortOption    {@link SortOption} for indicating the order of the items
     * @param filterOption  {@link FilterOption} for indicating the order of the items
     * @return              SQLite query
     */
    private fun getBookmarkedJobsQuery(sortOption: SortOption, filterOption: FilterOption): SimpleSQLiteQuery {
        val keywords = filterKeywords.getKeywordsByFilterOption(filterOption)
        val include = keywords.include
        val exclude = keywords.exclude
        val columnName = SortOption.toColumnName(sortOption)
        return SimpleSQLiteQuery("""
            SELECT * FROM jobs
            WHERE isBookmarked = 1
            ${if (exclude.isNotEmpty() || include.isNotEmpty()) "AND" else ""}
                ${exclude.mapIndexed { index, item ->
            "tags NOT LIKE '%$item%' ${if (index < exclude.size - 1 || include.isNotEmpty()) "AND"
            else ""}"
        }.joinToString(" ")}
                ${include.mapIndexed { index, item ->
            "${if (index == 0) "(" else ""} tags LIKE '%$item%' OR position LIKE '%$item%' " +
                    if (index < include.size - 1) "OR" else ")"
        }.joinToString(" ")}
            ORDER BY $columnName ${if (sortOption.name == "BY_POSTING_DATE") "DESC" else "ASC"}
            """.trimIndent())
    }

    /**
     * Fetch additional job info from API
     *
     * @param job           job item
     * @return              Observable holding additional job info from API
     */
    private fun fetchJobInfo(job: Job): Flowable<DataResponse<Job>> =
            apiService.getJobInfo(job.id)
                    .map { response ->
                        // If network error occurred then throw HttpException
                        // This will forward the emission directly to onErrorReturn method
                        if (response.errorBody() != null) {
                            throw HttpException(response)
                        }

                        Timber.d("Mapping item to DataResponse...")

                        // Distinguish between cache response and network response
                        val source = if (response.raw().networkResponse() != null) {
                            DataSource.API
                        } else {
                            DataSource.CACHE
                        }

                        // Get job info from response body
                        val newJob = job.copy(additionalInfo = response.body()!!)

                        DataResponse(data = newJob, source = source)
                    }
                    .flatMap { response ->
                        Timber.d("Saving new job from API to DB: $response")
                        // If response is not null, then insert results to DB
                        if (response.data != null) {
                            updateJob(response.data).subscribe(
                                    {
                                        Timber.d("Updated ${it.size} jobs in DB...")
                                    },
                                    {
                                        Timber.e(it)
                                    }
                            )
                        }
                        val newResponse = response.copy(data = null)
                        Flowable.just(newResponse)
                    }
                    .onErrorReturn {
                        Timber.e(it, "Error occurred while fetching from API.")
                        DataResponse(
                                source = DataSource.API,
                                message = "Error occurred while fetching from API.",
                                error = it)
                    }

    /**
     * Request job's additional info by id from DB
     *
     * @param jobId         id of the job
     * @return              Observable holding job item retrieved from DB
     */
    fun getJobInfo(jobId: String): Flowable<DataResponse<Job>> =
            jobDao.getJobById(jobId)
                    .map {
                        Timber.d("Mapping job item to DataResponse: $it")
                        // Wrap list of jobs to DataResponse object
                        DataResponse(data = it, source = DataSource.DB)
                        DataResponse(
                                data = it,
                                showLoading = it.additionalInfo == null,
                                source = DataSource.DB
                        )
                    }
                    .flatMap {
                        if (it.data != null && it.data.additionalInfo == null) {
                            fetchJobInfo(it.data)
                        } else {
                            Flowable.just(it)
                        }
                    }
                    .onErrorReturn {
                        Timber.e(it, "Error occurred while fetching job by id: $jobId from DB.")
                        DataResponse(
                                source = DataSource.DB,
                                message = "Error occurred while fetching from DB.",
                                error = it)
                    }
}