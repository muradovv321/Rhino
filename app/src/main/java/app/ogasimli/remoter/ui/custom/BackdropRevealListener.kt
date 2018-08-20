/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.custom

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.utils.getDrawable

/**
 * [android.view.View.OnClickListener] used to translate the jobs list
 * downward on the Y-axis when the filter icon in the toolbar is pressed.
 *
 * @author Orkhan Gasimli on 16.08.2018.
 */
class BackdropRevealListener(
        private val context: Context,
        private val frontView: View,
        private val backdropView: View,
        private val imageView: Any?,
        private val interpolator: TimeInterpolator? = null,
        @DrawableRes private val openIcon: Int? = null,
        @DrawableRes private val closeIcon: Int? = null) {

    private val animatorSet = AnimatorSet()
    private var backdropShown = false
    private var layoutChanged = false

    // OnLayoutChangeListener for tracking height changes of backdrop view
    private val onLayoutChangeListener =
            View.OnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (backdropShown && bottom != oldBottom) {
                    layoutChanged = true
                    backdropShown = false
                    toggle()
                }
            }

    /**
     * Slides front view up/down
     */
    fun toggle() {
        backdropShown = !backdropShown

        // Cancel the existing animations
        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()

        updateIcon()

        val animator = ObjectAnimator
                .ofFloat(frontView, "translationY", getTranslateY())
        animator.duration = 500
        if (interpolator != null) animator.interpolator = interpolator
        animatorSet.play(animator)
        animator.start()

        toggleListener()
    }

    /**
     * Slides front view up only if it is open
     */
    fun close() {
        if(backdropShown) toggle()
    }

    /**
     * Slides front view down only if it is closed
     */
    fun open() {
        if(!backdropShown) toggle()
    }

    /**
     * Adds/Removes OnLayoutChangeListener to/from backdrop view
     */
    private fun toggleListener() {
        if (backdropShown) {
            backdropView.addOnLayoutChangeListener(onLayoutChangeListener)
        } else {
            backdropView.removeOnLayoutChangeListener(onLayoutChangeListener)
        }
    }

    /**
     * Helper function to set open/close
     * drawables/icons of the ImageView/MenuItem
     */
    private fun updateIcon() {
        if (imageView != null && openIcon != null && closeIcon != null) {
            when (imageView) {
                is ImageView -> imageView.setDrawable()
                is MenuItem -> imageView.setIcon()
                else -> throw IllegalArgumentException("updateIcon() must be called on an " +
                        "ImageView or MenuItem")
            }
        }
    }

    /**
     * Extension function to set open/close
     * drawables of the ImageView
     */
    private fun ImageView.setDrawable() {
        if (backdropShown) {
            setImageDrawable(getDrawable(context, closeIcon!!))
        } else {
            setImageDrawable(getDrawable(context, openIcon!!))
        }
    }

    /**
     * Extension function to set open/close
     * icons of the MenuItem
     */
    private fun MenuItem.setIcon() {
        icon = if (backdropShown) {
            getDrawable(context, closeIcon!!)
        } else {
            getDrawable(context, openIcon!!)
        }
    }

    /**
     * Helper function to determine the new Y coordinate
     * of the view that will slide
     */
    private fun getTranslateY() = if (backdropShown) {
        layoutChanged = false
        (backdropView.height + context.resources
                .getDimensionPixelSize(R.dimen.backdrop_front_reveal_height)).toFloat()
    } else {
        0F
    }
}
