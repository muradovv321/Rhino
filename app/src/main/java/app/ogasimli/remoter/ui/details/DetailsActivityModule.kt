/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.details

import androidx.core.content.res.ResourcesCompat
import app.ogasimli.remoter.R
import app.ogasimli.remoter.di.scope.ActivityScope
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import dagger.Module
import dagger.Provides

/**
 * DI Module providing dependencies for DetailsActivity
 *
 * @author Orkhan Gasimli on 02.08.2018.
 */
@Module
class DetailsActivityModule {

    @ActivityScope
    @Provides
    fun provideColorGenerator(): ColorGenerator = ColorGenerator.MATERIAL

    @ActivityScope
    @Provides
    fun provideTextBuilder(activity: DetailsActivity): TextDrawable.IBuilder =
            TextDrawable.builder()
                    .beginConfig()
                    .useFont(ResourcesCompat.getFont(activity, R.font.montserrat_alternates))
                    .endConfig()
                    .roundRect(16)
}