/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.data

import app.ogasimli.remoter.di.scope.ApplicationScope
import app.ogasimli.remoter.model.data.api.JobsApiService
import javax.inject.Inject

/**
 * Single access point to saving/retrieve data from SharedPreferences, API & DB
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
@ApplicationScope
class DataManager @Inject constructor(private val apiService: JobsApiService) {

    /* ___________________ API ___________________*/
    /**
     * Request the list of remote jobs
     *
     * @return              Observable holding the job list retrieved from API
     */
    fun getJobList() = apiService.getJobList()

    /**
     * Request the additional job info
     *
     * @return              Observable holding the additional job info retrieved from API
     */
    fun getJobInfo(url: String) = apiService.getJobInfo(url)
}