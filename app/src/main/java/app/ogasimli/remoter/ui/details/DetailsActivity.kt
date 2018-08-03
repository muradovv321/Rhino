/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.details

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV
import android.text.method.LinkMovementMethod
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.utils.viewModelProvider
import app.ogasimli.remoter.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_details.*



/**
 Job details screen activity
 *
 * @author Orkhan Gasimli on 02.08.2018.
 */
class DetailsActivity : BaseActivity() {

    private lateinit var viewModel: DetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Set up the tool bar
        initToolbar()

        // Bind ViewModel
        viewModel = viewModelProvider(this, viewModelFactory)

        var sp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(getString(R.string.sample_job_description), FROM_HTML_SEPARATOR_LINE_BREAK_DIV)
        } else {
            Html.fromHtml(getString(R.string.sample_job_description))
        }
        job_description.text = sp
        job_description.movementMethod = LinkMovementMethod.getInstance()

        sp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(getString(R.string.sample_how_to_apply), FROM_HTML_SEPARATOR_LINE_BREAK_DIV)
        } else {
            Html.fromHtml(getString(R.string.sample_how_to_apply))
        }
        apply_instruction.text = sp
        apply_instruction.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Helper method to initialize toolbar
     */
    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            supportFinishAfterTransition()
        }
    }
}
