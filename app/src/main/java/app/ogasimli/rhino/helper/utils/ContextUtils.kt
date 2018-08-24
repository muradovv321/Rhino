/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.helper.utils

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes

/**
 * Class holding extension methods used by contexts
 *
 * @author Orkhan Gasimli on 20.08.2018.
 */

/**
 * Determines if layout direction is in RTL mode
 *
 * @return      boolean indicating whether the layout direction is in RTL mode
 */
fun Context.isRtl(): Boolean = resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

/**
 * Retrieves theme color by the given id
 *
 * @param id    resource ID of the color
 * @return      integer value of the color
 */
fun Context.getThemeColor(@ColorRes id: Int): Int {
    val theme = theme
    val a = theme.obtainStyledAttributes(intArrayOf(id))
    val result = a.getColor(0, 0)
    a.recycle()
    return result
}

/**
 * Determine if the navigation bar will be on the bottom of the screen, based on logic in
 * PhoneWindowManager.
 */
fun Context.isNavBarOnBottom(): Boolean {
    val cfg = resources.configuration
    val dm = resources.displayMetrics
    val canMove = dm.widthPixels != dm.heightPixels && cfg.smallestScreenWidthDp < 600
    return !canMove || dm.widthPixels < dm.heightPixels
}