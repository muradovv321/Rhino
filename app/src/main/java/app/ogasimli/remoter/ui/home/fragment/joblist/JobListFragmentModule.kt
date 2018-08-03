/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home.fragment.joblist

import androidx.core.content.res.ResourcesCompat
import app.ogasimli.remoter.R
import app.ogasimli.remoter.di.scope.FragmentScope
import app.ogasimli.remoter.ui.home.fragment.adapter.JobsAdapter
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import dagger.Module
import dagger.Provides

/**
 * DI Module providing dependencies for JobListFragment
 *
 * @author Orkhan Gasimli on 16.07.2018.
 */
@Module
class JobListFragmentModule {

    @FragmentScope
    @Provides
    fun provideColorGenerator(): ColorGenerator = ColorGenerator.MATERIAL

    @FragmentScope
    @Provides
    fun provideTextBuilder(fragment: JobListFragment): TextDrawable.IBuilder =
            TextDrawable.builder()
                    .beginConfig()
                    .useFont(ResourcesCompat.getFont(fragment.context!!, R.font.montserrat_alternates))
                    .endConfig()
                    .roundRect(16)

    @FragmentScope
    @Provides
    fun provideJobsAdapter(colorGenerator: ColorGenerator, textBuilder: TextDrawable.IBuilder) =
            JobsAdapter(colorGenerator, textBuilder)
}