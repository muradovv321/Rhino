/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.base

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import app.ogasimli.remoter.app.RemoterApp
import app.ogasimli.remoter.helper.rx.RxBus
import com.squareup.leakcanary.RefWatcher
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Base class for all fragments.
 * Watches memory leaks and performs dependency injections
 *
 * @see AndroidSupportInjection
 * @see RefWatcher
 *
 * @author Orkhan Gasimli on 23.02.2018.
 */
abstract class BaseFragment: DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Subscribe to events
        subscribeToEvents()
    }

    override fun onDestroy() {
        // Un-subscribe from bus events
        RxBus.unregister(this)
        super.onDestroy()
        // Watch memory leaks
        RemoterApp.getRefWatcher(context!!).watch(this)
    }

    /**
     * Base method to subscribe to RxEvents
     */
    protected open fun subscribeToEvents() {

    }
}