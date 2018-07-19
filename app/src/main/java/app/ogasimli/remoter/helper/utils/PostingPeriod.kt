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
import org.joda.time.*
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

/**
 * Helper class used for calculation of
 * time passed between now and posting date
 *
 * @author Orkhan Gasimli on 19.07.2018.
 */
class PostingPeriod(val context: Context, postingDate: String) : ContextWrapper(context) {

    private val df: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()
    private val from: DateTime = df.parseDateTime(postingDate)
    private val till: DateTime = DateTime(from.zone)
    private var years: Int = Years.yearsBetween(from, till).years
    private var months: Int = Months.monthsBetween(from, till).months
    private var weeks: Int = Weeks.weeksBetween(from, till).weeks
    private var days: Int = Days.daysBetween(from, till).days
    private var hours: Int = Hours.hoursBetween(from, till).hours
    private var minutes: Int = Minutes.minutesBetween(from, till).minutes
    private var seconds: Int = Seconds.secondsBetween(from, till).seconds

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