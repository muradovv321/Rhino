/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.app

import androidx.multidex.MultiDexApplication
import app.ogasimli.remoter.BuildConfig
import app.ogasimli.remoter.di.component.DaggerAppComponent
import app.ogasimli.remoter.helper.timber.DebugLogTree
import app.ogasimli.remoter.helper.timber.ReleaseLogTree
import timber.log.Timber

/**
 * Application class
 *
 * @author Orkhan Gasimli on 16.07.2018.
 */
class RemoterApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        // Inject Dagger
        injectDagger()

        // Plant Timber
        plantTimber()
    }

    /**
     * Helper method to inject DaggerAppComponent
     */
    private fun injectDagger() {
        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this)
    }

    /**
     * Helper method to plan Timber logging trees.
     *
     * If app is in debug mode, then plant {@link DebugLogTree},
     * else plant {@link ReleaseLogTree}
     */
    private fun plantTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugLogTree())
        } else {
            Timber.plant(ReleaseLogTree())
        }
    }
}