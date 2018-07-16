/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.app

import android.app.Activity
import android.app.Application
import app.ogasimli.remoter.di.component.AppComponent
import app.ogasimli.remoter.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Application class
 *
 * @author Orkhan Gasimli on 16.07.2018.
 */
class RemoterApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector
    }

    override fun onCreate() {
        super.onCreate()

        // Inject Dagger
        injectDagger()
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

}