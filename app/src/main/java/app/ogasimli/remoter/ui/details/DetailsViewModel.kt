/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.details

import androidx.lifecycle.MutableLiveData
import app.ogasimli.remoter.model.data.DataManager
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel class DetailsActivity
 *
 * @author Orkhan Gasimli on 02.08.2018.
 */
class DetailsViewModel @Inject constructor(private val dataManager: DataManager) : BaseViewModel() {

    val job = MutableLiveData<Job>()

    /**
     * Helper method to set the value of the LiveData
     *
     * @param job       job item to be set as the value of the LiveData
     */
    private fun setJob(job: Job?) {
        if (job != null) this.job.value = job
    }

    /**
     * Fetch additional job info from API
     *
     * @param job           job item
     * @return              Observable holding additional job info from API
     */
    fun fetchJobInfo(job: Job) {
        if (job.additionalInfo != null) {
            getJobById(job.id)
        } else {
            disposable.add(dataManager.fetchJobInfo(job)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap {
                        getJobById(job.id)
                        Observable.just(it)
                    }
                    .subscribe(
                            {
                                // TODO: Handle network errors
                            },
                            {
                                Timber.e(it)
                            }
                    )
            )
        }
    }

    /**
     * Request job by id from DB
     *
     * @param jobId     id of the job to be deleted
     * @return          Observable holding job item retrieved from DB
     */
    private fun getJobById(jobId: String) {
        disposable.add(dataManager.getJobById(jobId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            setJob(it.job)
                        },
                        {
                            Timber.e(it)
                        }
                )
        )
    }
}