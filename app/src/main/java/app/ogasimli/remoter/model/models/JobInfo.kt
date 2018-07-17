/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Data class holding additional job information
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
@Parcelize
data class JobInfo(
        val jobDesc: String,
        val applyInstruction: String,
        val applyUrl: String) : Parcelable