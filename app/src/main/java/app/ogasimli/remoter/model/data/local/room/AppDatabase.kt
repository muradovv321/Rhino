/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.ogasimli.remoter.model.models.Job

/**
 * The Room database builder class
 *
 * @author Orkhan Gasimli on 20.07.2018.
 */
const val APP_DB_VERSION = 1

@Database(entities = [Job::class], version = APP_DB_VERSION)
@TypeConverters(TagListTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getJobDao(): JobDao
}