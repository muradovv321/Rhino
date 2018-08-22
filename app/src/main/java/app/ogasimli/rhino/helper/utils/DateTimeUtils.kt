/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.helper.utils

import android.content.Context

/**
 * Helper & extension functions for date/time manipulation
 *
 * @author Orkhan Gasimli on 19.07.2018.
 */

/**
 * Helper function to calculate time passed between now and posting date
 *
 * @param context       context object
 * @param postingTime   posting date represented in milliseconds
 * @return              passed time in String format
 */
fun periodTillNow(context: Context, postingTime: Long): String =
        PostingPeriod(context, postingTime).print()