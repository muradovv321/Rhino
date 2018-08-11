/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.utils

import android.content.Context
import android.content.ContextWrapper
import app.ogasimli.remoter.R
import java.util.concurrent.TimeUnit


/**
 * Helper class used for calculation of
 * time passed between now and posting date
 *
 * @author Orkhan Gasimli on 19.07.2018.
 */
class PostingPeriod(val context: Context, postingTime: Long) : ContextWrapper(context) {

    var postingTime: Long = 0
        set(value) {
            field = value
            now = System.currentTimeMillis()
        }

    private var now: Long = System.currentTimeMillis()
    private var years: Int = (postingTime.periodTillNow(TimeUnit.DAYS) / 360).toInt()
    private var months: Int = (postingTime.periodTillNow(TimeUnit.DAYS) / 30).toInt()
    private var weeks: Int = (postingTime.periodTillNow(TimeUnit.DAYS) / 7).toInt()
    private var days: Int = postingTime.periodTillNow(TimeUnit.DAYS).toInt()
    private var hours: Int = postingTime.periodTillNow(TimeUnit.HOURS).toInt()
    private var minutes: Int = postingTime.periodTillNow(TimeUnit.MINUTES).toInt()
    private var seconds: Int = postingTime.periodTillNow(TimeUnit.SECONDS).toInt()

    private fun Long.periodTillNow(timeUnit: TimeUnit): Long {
        return timeUnit.convert(now - this, TimeUnit.MILLISECONDS)
    }

    /**
     * Returns String representation of passed period
     */
    fun print(): String {
        return when {
            years > 0 -> getString(R.string.posting_year, years)
            months > 0 -> getString(R.string.posting_month, months)
            weeks > 0 -> getString(R.string.posting_week, weeks)
            days > 0 -> getString(R.string.posting_day, days)
            hours > 0 -> getString(R.string.posting_hour, hours)
            minutes > 0 -> getString(R.string.posting_min, minutes)
            else -> getString(R.string.posting_sec, seconds)
        }
    }
}