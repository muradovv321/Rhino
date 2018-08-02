/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.di.module

import android.content.Context
import androidx.room.Room
import app.ogasimli.remoter.di.qualifier.ApplicationContext
import app.ogasimli.remoter.di.scope.ApplicationScope
import app.ogasimli.remoter.model.data.local.room.AppDatabase
import app.ogasimli.remoter.model.data.local.room.JobDao
import dagger.Module
import dagger.Provides

/**
 * DI Module providing app-level dependencies for Room DB
 *
 * @author Orkhan Gasimli on 20.07.2018.
 */
@Module
class RoomModule {

    @ApplicationScope
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "Remoter.db").build()
    }

    @ApplicationScope
    @Provides
    fun provideJobDao(appDatabase: AppDatabase): JobDao {
        return appDatabase.getJobDao()
    }
}