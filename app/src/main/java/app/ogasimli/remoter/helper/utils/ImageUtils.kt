/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import app.ogasimli.remoter.R
import app.ogasimli.remoter.di.module.GlideApp
import app.ogasimli.remoter.model.models.Job
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 * Extension
 *
 * @author Orkhan Gasimli on 03.08.2018.
 */

/**
 * Loads company logo to the View
 *
 * @param job               {@link Job} item served to the adapter
 */
fun ImageView.load(job: Job, loadOnlyFromCache: Boolean = false) {

    // Generate placeholder image using TextDrawable
    val textDrawable = generatePlaceholderImage(job.company, context)
    val placeholder = BitmapDrawable(context.resources, textDrawable?.toBitmap(context))
    // Generate new RequestOptions to round image backgrounds
    val glideOptions = RequestOptions
            .placeholderOf(placeholder)
            .onlyRetrieveFromCache(loadOnlyFromCache)
            .transforms(CenterInside(), RoundedCorners(16))
    // Load company logo via Glide
    GlideApp.with(context)
            .load(job.logo)
            .apply(glideOptions)
            .into(this)
}

/**
 * Generates placeholder image for company logo
 *
 * @param companyName       name of the company
 * @param context           Context object used to retrieve image dimens
 * @return                  TextDrawable created from company name
 */
private fun generatePlaceholderImage(companyName: String, context: Context): TextDrawable? {
    val text = companyName.getFirstLetters(2)
    val textBuilder = TextDrawable.builder()
            .beginConfig()
            .useFont(ResourcesCompat.getFont(context, R.font.montserrat_alternates))
            .fontSize(52)
            .endConfig()
            .roundRect(16)
    val colorGenerator = ColorGenerator.MATERIAL
    return textBuilder.build(text, colorGenerator.getColor(companyName))
}


/**
 * Converts {@link TextDrawable} into Bitmap
 *
 * @param context           Context object used to retrieve image dimens
 * @return                  Bitmap generated from TextDrawable
 */
private fun TextDrawable.toBitmap(context: Context): Bitmap {
    // Set width of the bitmap
    val imageWidth = context.resources.getDimension(R.dimen.company_logo_width).toInt()
    var width = intrinsicWidth
    width = if (width > 0) width else imageWidth

    // Set height of the bitmap
    val imageHeight = context.resources.getDimension(R.dimen.company_logo_height).toInt()
    var height = this.intrinsicHeight
    height = if (height > 0) height else imageHeight

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)

    return bitmap
}