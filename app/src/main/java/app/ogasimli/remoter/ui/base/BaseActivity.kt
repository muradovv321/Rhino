/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection

/**
 * Base class for all activities.
 * Performs dependency injections
 *
 * @see AndroidInjection
 *
 * @author Orkhan Gasimli on 23.02.2018.
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inject external dependencies
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}