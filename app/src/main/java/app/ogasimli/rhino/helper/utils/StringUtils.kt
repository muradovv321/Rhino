/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.helper.utils

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import timber.log.Timber

/**
 * Helper & extension functions for String manipulation
 *
 * @author Orkhan Gasimli on 19.07.2018.
 */

/**
 * Extension function to extract first letters of words
 * capitalize and join them back to one string
 *
 * @param limit     non-negative value specifying the maximum number of substrings to return
 */
fun String.getFirstLetters(limit: Int): String {
    Timber.d("Company name: $this")
    // Return X if company name is blank
    if (this.isBlank()) return "X"
    return this
            .trim()
            .split("(?=\\p{Lu})|\\s".toRegex())
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { it[0].toUpperCase() }
            .joinToString("")
            .take(limit)
}

/**
 * Helper function to get quantity string
 *
 * @param context   context object
 * @param resId     id of the plurals resource
 * @param zeroResId id of the string resource
 * @param count     count of the item
 * @return          formatted text
 */
fun getJobsCountText(context: Context,
                     @PluralsRes resId: Int,
                     @StringRes zeroResId: Int,
                     count: Int,
                     category: String): Spanned {
    val styledCategory = if (category.isEmpty() || category.isBlank()) {
        category
    } else {
        "-> [$category]"
    }
    val text = if (count == 0) {
        context.getString(zeroResId, styledCategory)
    } else {
        context.resources.getQuantityString(resId, count, count, styledCategory)
    }
    return text.decodeFromHtml()
}

/**
 * Extension function to decode HTML formatted text into Spanned
 *
 * @return          decoded text
 */
@Suppress("DEPRECATION")
fun String.decodeFromHtml(): Spanned =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(this)
        }