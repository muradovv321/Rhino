/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

/**
 * Helper & extension functions for ViewModel manipulations
 *
 * @author Orkhan Gasimli on 19.07.2018.
 */

/**
 * Extension function to produce and return {@link ViewModel}
 *
 * @param fragment  fragment to which the ViewModel will be bound
 * @param factory   {@link ViewModelProvider.Factory}
 * @return          returns instance of corresponding class extending {@link ViewModel}
 */
inline fun <reified T : ViewModel> viewModelProvider(fragment: Fragment,
                                                     factory: ViewModelProvider.Factory): T =
        ViewModelProviders
                .of(fragment, factory)
                .get(T::class.java)



/**
 * Extension function to produce and return {@link ViewModel}
 *
 * @param activity  activity to which the ViewModel will be bound
 * @param factory   {@link ViewModelProvider.Factory}
 * @return          returns instance of corresponding class extending {@link ViewModel}
 */
inline fun <reified T : ViewModel> viewModelProvider(activity: FragmentActivity,
                                                     factory: ViewModelProvider.Factory): T =
        ViewModelProviders
                .of(activity, factory)
                .get(T::class.java)