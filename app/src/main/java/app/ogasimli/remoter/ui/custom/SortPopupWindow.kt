/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.custom

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.utils.getDrawable
import app.ogasimli.remoter.helper.utils.toPx

/**
 * Custom PopupWindow for showing sorting options
 *
 * @author Orkhan Gasimli on 08.08.2018.
 */
class SortPopupWindow @JvmOverloads constructor(
        val context: Context,
        root: View?,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : PopupWindow(context, attrs, defStyleAttr) {

    init {
        contentView = root
        isOutsideTouchable = true
        isFocusable = true
        // The below parameters were also set via the style
        setBackgroundDrawable(getDrawable(context, R.drawable.sort_popup_background))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            overlapAnchor = false
            elevation = 30F.toPx(context)
        }
    }

    override fun showAsDropDown(anchor: View?) {
        val yOff = 10F.toPx(context).toInt()
        super.showAsDropDown(anchor, 0, yOff, Gravity.START)
    }
}