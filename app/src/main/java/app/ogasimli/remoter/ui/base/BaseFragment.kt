/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.base

import android.content.Context
import androidx.fragment.app.Fragment
import com.squareup.leakcanary.RefWatcher
import dagger.android.support.AndroidSupportInjection

/**
 * Base class for all fragments.
 * Watches memory leaks and performs dependency injections
 *
 * @see AndroidSupportInjection
 * @see RefWatcher
 *
 * @author Orkhan Gasimli on 23.02.2018.
 */
abstract class BaseFragment: Fragment() {

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}