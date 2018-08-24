/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.helper.utils

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat

/**
 * Class holding static methods used for manipulating drawables
 *
 * @author Orkhan Gasimli on 29.07.2018.
 */

/**
 * Helper method to get and change color of drawable
 *
 * @param context       the context
 * @param resId         resource id of color
 * @return              return the drawable resource
 */
fun getDrawable(context: Context?, resId: Int): Drawable? {
    context?.let {
        return ResourcesCompat.getDrawable(context.resources, resId, context.theme)
    }
    return null
}

/**
 * Helper method to get and change color of drawable
 *
 * @param context       the context
 * @param drawableResId resource id of drawable
 * @param color         resource id of color which will be used to paint drawable
 * @param mutate        boolean indicating whether the drawable should be mutated
 * @return              return the drawable
 */
fun getColoredDrawable(context: Context?, @DrawableRes drawableResId: Int, @ColorRes color: Int,
                       mutate: Boolean = true): Drawable? {
    context?.let {
        val drawable = ResourcesCompat.getDrawable(context.resources, drawableResId, context.theme)
        if (mutate) {
            drawable?.mutate()?.setColorFilter(getColor(context, color), PorterDuff.Mode.SRC_IN)
        } else {
            drawable?.setColorFilter(getColor(context, color), PorterDuff.Mode.SRC_IN)
        }
        return drawable
    }
    return null
}

/**
 * Helper method to set color to drawable
 *
 * @param context       the context
 * @param colorResId    resource id of color
 * @param mutate        boolean indicating whether the drawable should be mutated
 */
fun Drawable.setColor(context: Context, @ColorRes colorResId: Int, mutate: Boolean = true) =
        if (mutate) {
            this.mutate().setColorFilter(getColor(context, colorResId), PorterDuff.Mode.SRC_IN)
        } else {
            this.setColorFilter(getColor(context, colorResId), PorterDuff.Mode.SRC_IN)
        }
