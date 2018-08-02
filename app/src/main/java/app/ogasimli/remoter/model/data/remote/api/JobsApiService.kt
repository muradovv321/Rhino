/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.data.remote.api

import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.model.models.JobInfo
import io.reactivex.Observable
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
     * @return              Observable holding the job list retrieved from API
     */
    @GET("jobs")
    fun getJobList(): Observable<Response<List<Job>>>

    /**
     * Request the additional job info
     *
     * @return              Observable holding the additional job info retrieved from API
     */
    @GET("job")
    fun getJobInfo(@Query("url") url: String): Observable<JobInfo>
}