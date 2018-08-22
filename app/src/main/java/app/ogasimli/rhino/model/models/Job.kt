/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.model.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Data class holding info about job
 *
 * @author Orkhan Gasimli on 20.03.2018.
 */
@Parcelize
@Entity(tableName = "jobs",
        // Index id fields and force them to be unique
        indices = [(Index(value = ["id"], unique = true))])
data class Job(
        @PrimaryKey
        val id: String,
        val postingTime: Long,
        val company: String,
        val position: String,
        val tags: List<String>,
        val logo: String?,
        val description: String,
        val url: String,
        @Embedded var additionalInfo: JobInfo?,
        var isBookmarked: Boolean = false) : Parcelable