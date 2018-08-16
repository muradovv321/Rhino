/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.data

import app.ogasimli.remoter.di.scope.ApplicationScope
import app.ogasimli.remoter.helper.constant.Constants.FILTER_OPTION_ALL_KEY
import app.ogasimli.remoter.helper.constant.Constants.FILTER_OPTION_BOOKMARKED_KEY
import app.ogasimli.remoter.helper.constant.Constants.SORT_OPTION_ALL_KEY
import app.ogasimli.remoter.helper.constant.Constants.SORT_OPTION_BOOKMARKED_KEY
import app.ogasimli.remoter.model.data.local.PreferencesHelper
import app.ogasimli.remoter.model.models.FilterOption
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.model.models.SortOption
import javax.inject.Inject

/**
 * Single access point to saving/retrieve data from SharedPreferences, API & DB
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
@ApplicationScope
class DataManager @Inject constructor(
        private val jobRepository: JobRepository,
        private val prefsHelper: PreferencesHelper) {

    /* ___________________ Jobs Table ___________________*/

    /**
     * Request list of jobs from DB & API
     *
     * @return          Observable holding list of jobs retrieved from API or DB
     */
    fun getAllJobs(refreshData: Boolean) = jobRepository.getAllJobs(refreshData, getAllSortOption())

    /**
     * Request list of bookmarked jobs from DB
     *
     * @return              Observable holding list of bookmarked jobs retrieved from DB
     */
    fun getBookmarkedJobs() = jobRepository.getBookmarkedJobs(getBookmarkedSortOption())

    /**
     * Request job's additional info by id from DB
     *
     * @param jobId         id of the job
     * @return              Observable holding job item retrieved from DB
     */
    fun getJobInfo(jobId: String) = jobRepository.getJobInfo(jobId)

    /**
     * Update job
     *
     * @return              Observable holding list of indexes of updated items
     */
    fun updateJob(vararg job: Job) = jobRepository.updateJob(*job)

    /* ___________________ SharedPreferences ___________________*/

    /**
     * Save sorting option of all jobs to SharedPreferences
     *
     * @param sortOption    {@link SortOption} for indicating the order of the items
     */
    fun saveAllSortOption(sortOption: SortOption) = saveSortOption(sortOption, SORT_OPTION_ALL_KEY)

    /**
     * Retrieve sorting option of all jobs from SharedPreferences
     *
     * @return              {@link SortOption} selected by the user
     */
    fun getAllSortOption() = getSortOption(SORT_OPTION_ALL_KEY)

    /**
     * Save sorting option of bookmarked jobs to SharedPreferences
     *
     * @param sortOption    {@link SortOption} for indicating the order of the items
     */
    fun saveBookmarkedSortOption(sortOption: SortOption) =
            saveSortOption(sortOption, SORT_OPTION_BOOKMARKED_KEY)

    /**
     * Retrieve sorting option of bookmarked jobs from SharedPreferences
     *
     * @return              {@link SortOption} selected by the user
     */
    fun getBookmarkedSortOption() = getSortOption(SORT_OPTION_BOOKMARKED_KEY)

    /**
     * Save sorting option to SharedPreferences
     *
     * @param prefsKey      key of the item in SharedPreferences
     * @param sortOption    {@link SortOption} for indicating the order of the items
     */
    private fun saveSortOption(sortOption: SortOption, prefsKey: String) =
            prefsHelper.putInt(prefsKey, sortOption.type)

    /**
     * Retrieve sorting option from SharedPreferences
     *
     * @param prefsKey      key of the item in SharedPreferences
     * @return              {@link SortOption} selected by the user
     */
    private fun getSortOption(prefsKey: String) =
            SortOption.getFromType(prefsHelper.getInt(prefsKey, 0))

    /**
     * Save filtering option of all jobs to SharedPreferences
     *
     * @param filterOption    {@link FilterOption} for indicating the order of the items
     */
    fun saveAllFilterOption(filterOption: FilterOption) =
            saveFilterOption(filterOption, FILTER_OPTION_ALL_KEY)

    /**
     * Retrieve filtering option of all jobs from SharedPreferences
     *
     * @return              {@link FilterOption} selected by the user
     */
    fun getAllFilterOption() = getFilterOption(FILTER_OPTION_ALL_KEY)

    /**
     * Save filtering option of bookmarked jobs to SharedPreferences
     *
     * @param filterOption    {@link FilterOption} for indicating the order of the items
     */
    fun saveBookmarkedFilterOption(filterOption: FilterOption) =
            saveFilterOption(filterOption, FILTER_OPTION_BOOKMARKED_KEY)

    /**
     * Retrieve filtering option of bookmarked jobs from SharedPreferences
     *
     * @return              {@link FilterOption} selected by the user
     */
    fun getBookmarkedFilterOption() = getFilterOption(FILTER_OPTION_BOOKMARKED_KEY)

    /**
     * Save filtering option to SharedPreferences
     *
     * @param prefsKey      key of the item in SharedPreferences
     * @param filterOption    {@link FilterOption} for indicating the order of the items
     */
    private fun saveFilterOption(filterOption: FilterOption, prefsKey: String) =
            prefsHelper.putInt(prefsKey, filterOption.type)

    /**
     * Retrieve filtering option from SharedPreferences
     *
     * @param prefsKey      key of the item in SharedPreferences
     * @return              {@link FilterOption} selected by the user
     */
    private fun getFilterOption(prefsKey: String) =
            FilterOption.getFromType(prefsHelper.getInt(prefsKey, 0))
}