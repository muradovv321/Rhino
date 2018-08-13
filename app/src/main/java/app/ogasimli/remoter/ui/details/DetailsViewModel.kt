/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.details

import androidx.lifecycle.MutableLiveData
import app.ogasimli.remoter.model.data.DataManager
import app.ogasimli.remoter.model.models.DataResponse
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.ui.base.BaseViewModel
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

    val jobResponse = MutableLiveData<DataResponse<Job>>()

    /**
     * Request job's additional info by id from DB
     *
     * @param jobId     id of the job
     * @return          Observable holding job item retrieved from DB
     */
    fun getJobInfo(jobId: String) {
        disposable.clear()
        disposable.add(dataManager.getJobInfo(jobId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            jobResponse.value = it
                        },
                        {
                            Timber.e(it)
                        }
                )
        )
    }
}