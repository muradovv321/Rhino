/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.ui.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * BaseViewModel class
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
open class BaseViewModel : ViewModel() {

    @Inject
    lateinit var disposable: CompositeDisposable

    override fun onCleared() {
        super.onCleared()
        // Clear disposable
        this.disposable.clear()
    }
}