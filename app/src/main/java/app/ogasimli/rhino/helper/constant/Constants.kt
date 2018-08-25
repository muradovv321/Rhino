/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

@file:JvmName("Constants")

package app.ogasimli.rhino.helper.constant

import app.ogasimli.rhino.BuildConfig

/**
 * Helper class to hold constants
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
object Constants {

    /**
     * Package firstName of the app
     */
    private const val PACKAGE_NAME = BuildConfig.APPLICATION_ID

    /**
     * Default key of SharedPreferences
     */
    const val DEFAULT_SHARED_PREF_KEY = "$PACKAGE_NAME.SHARED_PREF_KEY"

    /**
     * SharedPreferences key for {@link SortOption} of all jobs
     */
    const val SORT_OPTION_ALL_KEY = "$PACKAGE_NAME.SORT_OPTION_ALL_KEY"

    /**
     * SharedPreferences key for {@link SortOption} of bookmarked jobs
     */
    const val SORT_OPTION_BOOKMARKED_KEY = "$PACKAGE_NAME.SORT_OPTION_BOOKMARKED_KEY"

    /**
     * SharedPreferences key for {@link FilterOption} of all jobs
     */
    const val FILTER_OPTION_ALL_KEY = "$PACKAGE_NAME.FILTER_OPTION_ALL_KEY"

    /**
     * SharedPreferences key for {@link FilterOption} of bookmarked jobs
     */
    const val FILTER_OPTION_BOOKMARKED_KEY = "$PACKAGE_NAME.FILTER_OPTION_BOOKMARKED_KEY"

    /**
     * Key of the job item to pass between activities via the Bundle
     */
    const val JOB_ITEM_BUNDLE_KEY = "$PACKAGE_NAME.JOB_ITEM_BUNDLE_KEY"

    /**
     * Timeout limit of http requests in seconds
     */
    const val OK_HTTP_TIMEOUT = 15L // 15 seconds

    /**
     * Folder firstName of OkHttp cache
     */
    const val OK_HTTP_CACHE_FOLDER_NAME = "http-cache"

    /**
     * Max cache size of OkHttp
     */
    const val OK_HTTP_MAX_CACHE_SIZE = 10L * 1024 * 1024 // 10 MB

    /**
     * Max age of http requests in minutes
     */
    const val OK_HTTP_MAX_AGE = 10

    /**
     * Cache expiration time for
     * Firebase Remote Config in seconds
     */
    @JvmStatic
    val FIREBASE_REMOTE_CACHE_EXPIRATION = if(BuildConfig.DEBUG) 0 else 6 * 3600L

    /**
     * Remote config key for fetching 'from' query parameter
     */
    const val DATA_SOURCE_KEY = "data_source"

    /**
     * Base URL for fetching coin data
     */
    const val API_BASE_URL = "https://rhino-app.herokuapp.com/v1/"
}