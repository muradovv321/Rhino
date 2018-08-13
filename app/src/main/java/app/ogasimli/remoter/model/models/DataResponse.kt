/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.models

/**
 * Wrapper classes holding the data, source of the data and etc.
 *
 * @author Orkhan Gasimli on 20.07.2018.
 */
data class DataResponse<T>(
        val data: T? = null,
        val showLoading: Boolean = false,
        val source: DataSource,
        val message: String = "",
        val error: Throwable? = null)