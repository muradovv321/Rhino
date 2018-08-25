/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.ui.custom

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MenuItem
import android.view.ViewAnimationUtils
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import androidx.annotation.Keep
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import app.ogasimli.rhino.R
import app.ogasimli.rhino.helper.rx.RxSearchObservable
import app.ogasimli.rhino.helper.utils.*
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * Custom SearchView
 *
 * @author Orkhan Gasimli on 20.08.2018.
 */
@Keep
class RhinoSearchView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null) : SearchView(context, attrs) {

    private val revealDuration = 300L
    private val translateDuration = 270L
    private val debounceTimeout = 300L

    // Rx Observable that emits search queries into Observer
    val queryObservable: Observable<String> = RxSearchObservable.fromView(this)
            .debounce(debounceTimeout, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()

    init {
        // Set query hint
        queryHint = context.getString(R.string.menu_search_hint)
        // Change font of the TextView
        val searchText = findViewById<SearchAutoComplete>(R.id.search_src_text)
        searchText?.apply {
            typeface = ResourcesCompat.getFont(context, R.font.lato)
            setTextColor(getColor(context, R.color.textColorPrimary))
            setHintTextColor(getColor(context, R.color.textColorSecondary))
        }

        // Set maxWidth of SearchView
        maxWidth = android.R.attr.width
    }

    /**
     * Open SearchView with animation
     *
     * @param activity      activity which holds the SearchView
     * @param toolbar       toolbar which holds the SearchView
     * @param menuItems     list of other visible MenuItems
     */
    fun open(activity: Activity, toolbar: Toolbar, menuItems: List<MenuItem?>) {
        // Run animation only if SearchView is not expanded
        if (isIconified) {
            // Determine if Menu has visible overflow icon
            val containsOverflow = toolbar.isOverflowMenuShowing

            // Circular reveal animation will run only in > API 21
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val width = toolbar.width -
                        (if (containsOverflow) resources.getDimensionPixelSize(R.dimen.action_button_min_width_overflow_material) else 0) -
                        resources.getDimensionPixelSize(R.dimen.action_button_min_width_material) *
                        (menuItems.size + 1) / 2
                val createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar,
                        if (context.isRtl()) toolbar.width - width else width,
                        toolbar.height / 2, 0.0f, width.toFloat())
                createCircularReveal.duration = revealDuration
                createCircularReveal.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {
                        // Set background color of toolbar to white
                        toolbar.background = getDrawable(context, R.drawable.toolbar_search_background)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // Set background color of statusbar to grey
                            activity.setStatusBarColor(getColor(context, R.color.colorQuantumGrey))
                            // Make statusbar icons darker
                            toolbar.setLightStatusBar()
                        }
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        // Hide menu items
                        menuItems.filterNotNull().forEach { it.isVisible = false }
                    }
                })
                createCircularReveal.start()
            } else {
                val translateAnimation = TranslateAnimation(0.0f, 0.0f,
                        (-toolbar.height).toFloat(), 0.0f)
                val animationSet = AnimationSet(true)
                animationSet.addAnimation(translateAnimation)
                animationSet.duration = translateDuration
                animationSet.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {}

                    override fun onAnimationStart(animation: Animation?) {
                        // Set background color of toolbar to white
                        toolbar.background = getDrawable(context, R.drawable.toolbar_search_background)
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        // Hide menu items
                        menuItems.filterNotNull().forEach { it.isVisible = false }
                    }
                })
                toolbar.startAnimation(animationSet)
            }
        }
    }

    /**
     * Close SearchView with animation
     *
     * @param activity      activity which holds the SearchView
     * @param toolbar       toolbar which holds the SearchView
     * @param menuItems     list of other visible MenuItems
     */
    fun close(activity: Activity, toolbar: Toolbar, menuItems: List<MenuItem?>) {
        // Run animation only if SearchView is not collapsed
        if (!isIconified) {
            // Determine if Menu has visible overflow icon
            val containsOverflow = toolbar.isOverflowMenuShowing

            // Circular reveal animation will run only in > API 21
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val width = toolbar.width -
                        (if (containsOverflow) resources.getDimensionPixelSize(R.dimen.action_button_min_width_overflow_material) else 0) -
                        resources.getDimensionPixelSize(R.dimen.action_button_min_width_material) *
                        (menuItems.size + 1) / 2
                val createCircularReveal = ViewAnimationUtils.createCircularReveal(toolbar,
                        if (context.isRtl()) toolbar.width - width else width,
                        toolbar.height / 2, width.toFloat(), 0.0f)
                createCircularReveal.duration = revealDuration
                createCircularReveal.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator) {
                        // Restore background color of toolbar
                        toolbar.setBackgroundColor(context.getThemeColor(R.attr.colorPrimary))
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // Make statusbar icons lighter
                            toolbar.clearLightStatusBar()
                            // Restore background color of statusbar
                            activity.setStatusBarColor(context.getThemeColor(R.attr.colorPrimaryDark))
                        }
                        // Make menu items visible
                        menuItems.filterNotNull().forEach { it.isVisible = true }
                        // Invalidate menu
                        activity.invalidateOptionsMenu()
                    }
                })
                createCircularReveal.start()
            } else {
                val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
                val translateAnimation = TranslateAnimation(0.0f, 0.0f,
                        0.0f, (-toolbar.height).toFloat())
                val animationSet = AnimationSet(true)
                animationSet.addAnimation(alphaAnimation)
                animationSet.addAnimation(translateAnimation)
                animationSet.duration = translateDuration
                animationSet.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}

                    override fun onAnimationRepeat(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        // Restore background color of toolbar
                        toolbar.setBackgroundColor(context.getThemeColor(R.attr.colorPrimary))
                        // Make menu items visible
                        menuItems.filterNotNull().forEach { it.isVisible = true }
                        // Invalidate menu
                        activity.invalidateOptionsMenu()
                    }
                })
                toolbar.startAnimation(animationSet)
            }
        }
    }

    /**
     * Helper function to restore view after config changes
     *
     * @param query         query string
     * @param searchItem    MenuItem of the search
     */
    fun restore(query: String?, searchItem: MenuItem?) {
        if (query != null) {
            searchItem?.expandActionView()
            setQuery(query, true)
            clearFocus()
        }
    }
}