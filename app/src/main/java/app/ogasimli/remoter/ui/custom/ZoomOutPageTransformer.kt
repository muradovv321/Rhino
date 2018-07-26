/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.custom

import android.view.View
import androidx.viewpager.widget.ViewPager


/**
 * {@link ViewPager.PageTransformer} that shrinks and
 * fades pages when scrolling between adjacent pages.
 * As a page gets closer to the center, it grows back
 * to its normal size and fades in.
 *
 * @author Orkhan Gasimli on 26.07.2018.
 */
class ZoomOutPageTransformer : ViewPager.PageTransformer {

    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f
    }

    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        val pageHeight = view.height

        when {
            position < -1 -> // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.alpha = 0F
            position <= 1 -> { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                val verticalMargin = pageHeight * (1 - scaleFactor) / 2
                val horizontalMargin = pageWidth * (1 - scaleFactor) / 2
                if (position < 0) {
                    view.translationX = horizontalMargin - verticalMargin / 2
                } else {
                    view.translationX = -horizontalMargin + verticalMargin / 2
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor

                // Fade the page relative to its size.
                view.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
            }
            else -> // (1,+Infinity]
                // This page is way off-screen to the right.
                view.alpha = 0F
        }
    }
}