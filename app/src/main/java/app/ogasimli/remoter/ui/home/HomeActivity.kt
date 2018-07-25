/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home

import android.os.Bundle
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.utils.replaceFragment
import app.ogasimli.remoter.helper.utils.viewModelProvider
import app.ogasimli.remoter.ui.base.BaseActivity
import app.ogasimli.remoter.ui.home.fragment.joblist.JobListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*

/**
 * Home screen activity
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
class HomeActivity : BaseActivity() {

    private lateinit var viewModel: HomeViewModel

    private val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.bottom_nav_jobs -> {
//                        message.setText(R.string.title_home)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.bottom_nav_saved -> {
//                        message.setText(R.string.title_dashboard)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Bind ViewModel
        viewModel = viewModelProvider(this, viewModelFactory)

        if (null == savedInstanceState) {
            replaceFragment<JobListFragment>(R.id.home_fragment_container, false)
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
