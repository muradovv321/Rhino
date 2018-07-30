/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.utils.instanceOf
import app.ogasimli.remoter.ui.custom.SmartFragmentStatePagerAdapter
import app.ogasimli.remoter.ui.home.fragment.joblist.JobListFragment
import app.ogasimli.remoter.ui.home.fragment.savedjoblist.SavedJobListFragment


/**
 * Custom ViewPagerAdapter class for HomeActivity
 *
 * @author Orkhan Gasimli on 26.07.2018.
 */
class HomePagerAdapter(val context: Context, fragmentManager: FragmentManager) :
        SmartFragmentStatePagerAdapter(fragmentManager) {

    companion object {
        private const val NUM_ITEMS = 2
    }

    // Tab Titles
    private val tabTitles = intArrayOf(R.string.tab_jobs_title, R.string.tab_bookmarks_title)

    // Returns total number of pages
    override fun getCount(): Int {
        return NUM_ITEMS
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment? =
            when (position) {
            // Fragment # 0 - This will show JobListFragment
                0 -> instanceOf<JobListFragment>()
            // Fragment # 1 - This will show SavedJobListFragment
                1 -> instanceOf<SavedJobListFragment>()
                else -> null
            }

    // Returns the title of the page
    override fun getPageTitle(position: Int): CharSequence? = context.getString(tabTitles[position])
}