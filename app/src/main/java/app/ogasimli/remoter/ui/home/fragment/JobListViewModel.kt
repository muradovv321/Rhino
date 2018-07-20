/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home.fragment

import androidx.lifecycle.MutableLiveData
import app.ogasimli.remoter.model.data.DataManager
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel class JobListFragment
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
class JobListViewModel @Inject constructor(private val dataManager: DataManager) : BaseViewModel() {

    var jobList: MutableLiveData<List<Job>> = MutableLiveData()

    /**
     * Fetches jobs, updates local DB and serves them
     */
    fun fetchJobs() {
        compositeDisposable.add(dataManager.getAllJobs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { jobList.postValue(it.jobs) },
                        { Timber.e(it) }
                )
        )
    }
}