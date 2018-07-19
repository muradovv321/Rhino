/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.app

import android.app.Activity
import android.content.Context
import androidx.multidex.MultiDexApplication
import app.ogasimli.remoter.BuildConfig
import app.ogasimli.remoter.di.component.AppComponent
import app.ogasimli.remoter.di.component.DaggerAppComponent
import app.ogasimli.remoter.helper.timber.DebugLogTree
import app.ogasimli.remoter.helper.timber.ReleaseLogTree
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * Application class
 *
 * @author Orkhan Gasimli on 16.07.2018.
 */
class RemoterApp : MultiDexApplication(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    private lateinit var refWatcher: RefWatcher

    companion object {

        lateinit var appComponent: AppComponent

        fun getRefWatcher(context: Context): RefWatcher =
                (context.applicationContext as RemoterApp).refWatcher
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector
    }

    override fun onCreate() {
        super.onCreate()

        // Inject Dagger
        injectDagger()

        // Plant Timber
        plantTimber()

        //Initialize LeakCanary
        initLeakCanary()
    }

    /**
     * Helper method to inject DaggerAppComponent
     */
    private fun injectDagger() {
        appComponent = DaggerAppComponent
                .builder()
                .application(this)
                .build()
        appComponent.inject(this)
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
}