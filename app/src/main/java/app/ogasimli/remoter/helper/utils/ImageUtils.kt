/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.utils

import android.content.Context
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
fun loadCompanyLogo(job: Job, context: Context, imageView: ImageView) {
    // Generate placeholder image using TextDrawable
    val placeholder = generatePlaceholderImage(job.company, context)
    // Generate new RequestOptions to round image backgrounds
    val glideOptions = RequestOptions()
            .transforms(CenterInside(), RoundedCorners(16))
    // Load company logo via Glide
    GlideApp.with(context)
            .load(job.logo)
            .placeholder(placeholder)
            .apply(glideOptions)
            .into(imageView)
}

/**
 * Generates placeholder image for company logo
 *
 * @param companyName       name of the company
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