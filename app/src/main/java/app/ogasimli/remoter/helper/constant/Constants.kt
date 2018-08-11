/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.constant

import app.ogasimli.remoter.BuildConfig

/**
 * Helper class to hold constants
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
object Constants {

    /**
     * Package firstName of the app
     */
    @JvmStatic
    private val PACKAGE_NAME = BuildConfig.APPLICATION_ID

    /**
     * Default key of SharedPreferences
     */
    @JvmStatic
    val DEFAULT_SHARED_PREF_KEY = "$PACKAGE_NAME.SHARED_PREF_KEY"

    /**
     * SharedPreferences key for {@link SortOption} of all jobs
     */
    @JvmStatic
    val SORT_OPTION_ALL_KEY = "$PACKAGE_NAME.SORT_OPTION_ALL_KEY"

    /**
     * SharedPreferences key for {@link SortOption} of bookmarked jobs
     */
    @JvmStatic
    val SORT_OPTION_BOOKMARKED_KEY = "$PACKAGE_NAME.SORT_OPTION_BOOKMARKED_KEY"

    /**
     * Key of the job item to pass between activities via the Bundle
     */
    @JvmStatic
    val JOB_ITEM_BUNDLE_KEY = "$PACKAGE_NAME.JOB_ITEM_BUNDLE_KEY"

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
    val API_BASE_URL = "https://remote-job.herokuapp.com/v1/"
}