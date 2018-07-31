/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.base

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import app.ogasimli.remoter.helper.rx.RxBus
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * Base class for all activities.
 * Performs dependency injections
 *
 * @see AndroidInjection
 *
 * @author Orkhan Gasimli on 23.02.2018.
 */
abstract class BaseActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Subscribe to events
        subscribeToEvents()
    }

    /**
     * Base method to subscribe to RxEvents
     */
    protected open fun subscribeToEvents() {

    }

    override fun onDestroy() {
        // Un-subscribe from bus events
        RxBus.unregister(this)
        super.onDestroy()
    }
}