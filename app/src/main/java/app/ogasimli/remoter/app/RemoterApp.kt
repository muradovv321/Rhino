/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.app

import android.content.Context
import androidx.multidex.MultiDex
import app.ogasimli.remoter.BuildConfig
import app.ogasimli.remoter.di.component.DaggerAppComponent
import app.ogasimli.remoter.helper.timber.DebugLogTree
import app.ogasimli.remoter.helper.timber.ReleaseLogTree
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

/**
 * Application class
 *
 * @author Orkhan Gasimli on 16.07.2018.
 */
class RemoterApp : DaggerApplication() {

    private lateinit var refWatcher: RefWatcher

    override fun onCreate() {
        super.onCreate()

        // Plant Timber
        plantTimber()

        //Initialize LeakCanary
        initLeakCanary()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // Enable MultiDex
        MultiDex.install(this)
    }

    /**
     * Injects DaggerAppComponent
     */
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
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
    /**
     * Helper method to initialize LeakCanary.
     */
    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        refWatcher = LeakCanary.install(this)
    }

    companion object {
        fun getRefWatcher(context: Context): RefWatcher =
                (context.applicationContext as RemoterApp).refWatcher
    }
}