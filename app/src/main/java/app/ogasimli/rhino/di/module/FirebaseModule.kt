/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.di.module

import app.ogasimli.rhino.BuildConfig
import app.ogasimli.rhino.R
import app.ogasimli.rhino.di.scope.ApplicationScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.Module
import dagger.Provides

/**
 * DI Module providing app-level dependencies for Firebase instances
 *
 * @author Orkhan Gasimli on 25.08.2018.
 */
@Module
class FirebaseModule {

    @ApplicationScope
    @Provides
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        // Get Remote Config instance.
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development.
        val settings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        firebaseRemoteConfig.setConfigSettings(settings)
        // Set default Remote Config parameter values.
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults)
        return firebaseRemoteConfig
    }
}