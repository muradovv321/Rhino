/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home

import androidx.lifecycle.MutableLiveData
import app.ogasimli.remoter.helper.rx.*
import app.ogasimli.remoter.ui.base.BaseViewModel
import javax.inject.Inject

class JobsCount {
    var openJobs = 0
    var bookmarkedJobs = 0
    var isSearching = false
}

/**
 * ViewModel class HomeActivity
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
class HomeViewModel @Inject constructor() : BaseViewModel() {

    var jobsCount = MutableLiveData<JobsCount>()

    /**
     * Helper function to listen for RxEvent and register subscriber within the pool
     */
    override fun subscribeToEvents() {
        RxBus.listen<RxEvent>()
                .subscribe { event ->
                    when(event) {
                        is JobsCountEvent -> {
                            val tempJobsCount = jobsCount.value ?: JobsCount()
                            when (event.type) {
                                EventType.OPEN_JOBS_COUNT -> tempJobsCount.openJobs = event.count
                                EventType.BOOKMARKED_JOBS_COUNT -> tempJobsCount.bookmarkedJobs = event.count
                            }
                            tempJobsCount.isSearching = event.isSearching
                            jobsCount.postValue(tempJobsCount)
                        }
                        else -> return@subscribe
                    }
                }
                .registerInBus(this)
    }
}