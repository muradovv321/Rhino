/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.utils

import android.content.Context
import android.util.DisplayMetrics

/**
 * Class holding static methods used for
 * dimension calculations
 *
 * @author Orkhan Gasimli on 25.12.2017.
 */

/**
 * Extension function to convert dp to pixels
 *
 * @param context       context object
 * @return              return the dp value
 */
fun Float.toPx(context: Context): Float =
        this * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)

/**
 * Extension function to convert pixels to dp
 *
 * @param context       context object
 * @return              return the dp value
 */
fun Float.toDp(context: Context): Float =
        this / context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT
