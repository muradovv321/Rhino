/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.model.data.remote.api

import app.ogasimli.rhino.model.models.Job
import app.ogasimli.rhino.model.models.JobInfo
import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service class for requesting endpoints
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
interface JobsApiService {

    /**
     * Request the list of remote jobs
     *
     * @param from          query parameter indicating the source of the data
     * @return              Observable holding the job list retrieved from API
     */
    @GET("jobs")
    fun getJobList(@Query("from") from: String): Flowable<Response<List<Job>>>

    /**
     * Request the additional job info
     *
     * @param id            query parameter indicating the id of the job announcement
     * @param from          query parameter indicating the source of the data
     * @return              Observable holding the additional job info retrieved from API
     */
    @GET("job")
    fun getJobInfo(@Query("id") id: String, @Query("from") from: String):
            Flowable<Response<JobInfo>>
}