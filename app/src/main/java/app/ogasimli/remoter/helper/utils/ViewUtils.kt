/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.utils

import android.annotation.TargetApi
import android.os.Build
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup



/**
 * Helper & extension functions for managing View related tasks
 *
 * @author Orkhan Gasimli on 13.08.2018.
 */

/**
 * Extension function to rotate View by certain degrees
 *
 * @param degree    degree of the rotation
 */
fun View?.rotateBy(degree: Float) {
    this?.apply {
        animate()
                .rotationBy(degree)
                .setInterpolator(DecelerateInterpolator())
                .start()
    }
}

/**
 * Extension function to find text of checked Chip
 * within a ChipGroup
 *
 * @return          text of checked Chip
 */
fun ChipGroup.getCheckedChipsText(): String {
    val checkedChip = getChildAt(checkedChipId - 1) as? Chip
    return checkedChip?.text?.toString() ?: ""
}

/**
 * Extension function to make statusbar icons darker
 */
@TargetApi(Build.VERSION_CODES.M)
fun View.setLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        systemUiVisibility = flags
    }
}

/**
 * Extension function to make statusbar icons lighter
 */
@TargetApi(Build.VERSION_CODES.M)
fun View.clearLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var flags = systemUiVisibility
        flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        systemUiVisibility = flags
    }
}