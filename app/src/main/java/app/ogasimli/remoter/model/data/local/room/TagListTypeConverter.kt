/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.data.local.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * TypeConverter class for Room to convert
 * list of strings to string and vice versa
 *
 * @author Orkhan Gasimli on 20.07.2018.
 */
class TagListTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun tagListToString(tagList: List<String>?): String? =
            if (tagList == null) {
                null
            } else {
                gson.toJson(tagList)
            }

    @TypeConverter
    fun stringToTagList(tagList: String?): List<String>? =
            if (tagList == null) {
                null
            } else {
                gson.fromJson(tagList, object : TypeToken<List<String>>() {}.type)
            }
}

