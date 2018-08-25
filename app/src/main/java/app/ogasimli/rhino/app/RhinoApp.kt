/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.app

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import app.ogasimli.rhino.BuildConfig
import app.ogasimli.rhino.di.component.AppComponent
import app.ogasimli.rhino.di.component.DaggerAppComponent
import app.ogasimli.rhino.helper.timber.DebugLogTree
import app.ogasimli.rhino.helper.timber.ReleaseLogTree
import app.ogasimli.rhino.model.data.DataManager
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
class RhinoApp : MultiDexApplication(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dataManager: DataManager

    private lateinit var refWatcher: RefWatcher

    companion object {

        lateinit var appComponent: AppComponent

        fun getRefWatcher(context: Context): RefWatcher =
                (context.applicationContext as RhinoApp).refWatcher
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

        // Enable vector drawables
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // TODO: Move this into intro screen
        // Fetch and update remote configs
        dataManager.fetchRemoteConfigs()
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