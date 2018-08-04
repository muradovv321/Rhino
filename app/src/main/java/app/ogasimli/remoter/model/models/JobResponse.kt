/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.models

/**
 * Wrapper class holding the data, source of the data and etc.
 *
 * @author Orkhan Gasimli on 20.07.2018.
 */
data class JobResponse(
        val job: Job? = null,
        val jobs: List<Job> = emptyList(),
        val source: DataSource,
        val message: String = "",
        val error: Throwable? = null)