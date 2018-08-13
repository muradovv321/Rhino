/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.utils

import android.view.View
import android.view.animation.DecelerateInterpolator

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