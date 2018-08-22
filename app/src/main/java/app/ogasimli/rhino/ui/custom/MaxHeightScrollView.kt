/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import app.ogasimli.rhino.R

/**
 * Custom ScrollView that respects maxHeight
 *
 * @author Orkhan Gasimli on 16.08.2018.
 */
class MaxHeightScrollView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : ScrollView(context, attrs, defStyleAttr) {

    private var maxHeight: Int = 0
    private val defaultHeight = 200

    init {
        if (attrs != null) {
            val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView)
            //200 is a default value
            maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.MaxHeightScrollView_maxHeight,
                    defaultHeight)
            styledAttrs.recycle()
        } else {
            maxHeight = defaultHeight
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxHeightMeasureSpec = if (MeasureSpec.getSize(heightMeasureSpec) > maxHeight) {
            MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
        } else {
            heightMeasureSpec
        }
        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec)
    }
}