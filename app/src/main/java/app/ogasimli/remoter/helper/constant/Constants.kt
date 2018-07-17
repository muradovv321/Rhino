/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.constant

/**
 * Helper class to hold constants
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
object Constants {

    /**
     * Timeout limit of http requests in seconds
     */
    @JvmStatic
    val OK_HTTP_TIMEOUT = 15L // 15 seconds

    /**
     * Folder firstName of OkHttp cache
     */
    @JvmStatic
    val OK_HTTP_CACHE_FOLDER_NAME = "http-cache"

    /**
     * Max cache size of OkHttp
     */
    @JvmStatic
    val OK_HTTP_MAX_CACHE_SIZE = 10L * 1024 * 1024 // 10 MB

    /**
     * Max age of http requests in minutes
     */
    @JvmStatic
    val OK_HTTP_MAX_AGE = 10

    /**
     * Base URL for fetching coin data
     */
    @JvmStatic
    val API_BASE_URL = "https://remote-job.herokuapp.com"
}