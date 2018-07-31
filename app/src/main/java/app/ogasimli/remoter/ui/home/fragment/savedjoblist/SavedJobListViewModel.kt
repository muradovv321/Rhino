/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home.fragment.savedjoblist

import androidx.lifecycle.MutableLiveData
import app.ogasimli.remoter.helper.rx.EventType
import app.ogasimli.remoter.helper.rx.JobsCountEvent
import app.ogasimli.remoter.helper.rx.RxBus
import app.ogasimli.remoter.model.data.DataManager
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel class SavedJobListFragment
 *
 * @author Orkhan Gasimli on 26.07.2018.
 */
class SavedJobListViewModel @Inject constructor(private val dataManager: DataManager) : BaseViewModel() {

    var jobList = MutableLiveData<List<Job>>()

    /**
     * Fetches saved jobs from DB
     */
    fun fetchJobs() {
        disposable.add(dataManager.getAllSavedJobs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            jobList.postValue(it.jobs)
                            RxBus.publish(JobsCountEvent(EventType.BOOKMARKED_JOBS_COUNT, it.jobs.size))
                        },
                        {
                            Timber.e(it)
                        }
                )
        )
    }

    /**
     * Marks job as bookmarked or un-bookmarked
     */
    fun bookmarkJob(job: Job) {
        job.isBookmarked = !job.isBookmarked
        disposable.add(dataManager.updateJob(job)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            Timber.d("${it.size} jobs updated in the DB...")
                        },
                        {
                            Timber.e(it)
                        }
                )
        )
    }
}