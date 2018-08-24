/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.ogasimli.rhino.helper.utils

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.core.content.res.ResourcesCompat

/**
 * Utility methods for working with colors.
 */

/**
 * Helper method to get color resource
 *
 * @param context       the context
 * @param colorResId    resource id of color
 * @return              return the color
 */
fun getColor(context: Context, @ColorRes colorResId: Int) =
        ResourcesCompat.getColor(context.resources, colorResId, context.theme)

/**
 * Set the alpha component of `color` to be `alpha`.
 */
fun modifyAlpha(@ColorInt color: Int, @IntRange(from = 0, to = 255) alpha: Int): Int {
    return color and 0x00ffffff or (alpha shl 24)
}

/**
 * Set the alpha component of `color` to be `alpha`.
 */
fun modifyAlpha(@ColorInt color: Int,
                @FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
    return modifyAlpha(color, (255f * alpha).toInt())
}
