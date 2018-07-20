/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Enum class used to indicate the data source
 *
 * @author Orkhan Gasimli on 20.07.2018.
 */
enum class DataSource : Parcelable {
    API,
    DB,
    CACHE;

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataSource> {
        override fun createFromParcel(parcel: Parcel): DataSource {
            return DataSource.values()[parcel.readInt()]
        }

        override fun newArray(size: Int): Array<DataSource?> {
            return arrayOfNulls(size)
        }
    }
}