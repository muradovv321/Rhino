/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.data

import app.ogasimli.remoter.di.scope.ApplicationScope
import app.ogasimli.remoter.model.models.Job
import javax.inject.Inject

/**
 * Single access point to saving/retrieve data from SharedPreferences, API & DB
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
@ApplicationScope
class DataManager @Inject constructor(private val jobRepository: JobRepository) {

    /* ___________________ Job ___________________*/

    /**
     * Request list of jobs from DB & API
     *
     * @return          Observable holding list of jobs retrieved from API or DB
     */
    fun getAllJobs() = jobRepository.getAllJobs()

    /**
     * Update job
     *
     * @return          Observable holding list of indexes of updated items
     */
    fun updateJob(vararg job: Job) = jobRepository.updateJob(*job)
}