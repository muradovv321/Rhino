/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home.fragment.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.ogasimli.remoter.R
import app.ogasimli.remoter.helper.rx.EventType
import app.ogasimli.remoter.helper.rx.RxBus
import app.ogasimli.remoter.helper.rx.RxEvent
import app.ogasimli.remoter.helper.utils.inflate
import app.ogasimli.remoter.helper.utils.loadCompanyLogo
import app.ogasimli.remoter.helper.utils.periodTillNow
import app.ogasimli.remoter.model.models.Job
import kotlinx.android.synthetic.main.job_item_card.view.*

/**
 * Adapter class for RecyclerView displaying job list
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
class JobsAdapter : RecyclerView.Adapter<JobsAdapter.ViewHolder>() {

    var jobs: List<Job> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = parent.inflate(R.layout.job_item_card)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val job = jobs[position]
        with(holder) {
            // Get context
            val context = itemView.context
            // Load company logo
            loadCompanyLogo(job, context, itemView.company_logo)
            // Load image resource of the bookmark button
            loadBookmarkButtonsImage(job, bookmarkBtn)
            // Attach ClickListener to bookmark button
            bookmarkBtn?.setOnClickListener {
                RxBus.publish(RxEvent(EventType.BOOKMARK_BUTTON_CLICK, data = job))
            }
            // Set position
            positionName?.text = job.position
            // Set company name and period
            companyName?.text = context.getString(R.string.company_name_and_period,
                    job.company, periodTillNow(context, job.postingDate))

            // Attach ClickListener to CardView
            itemView.setOnClickListener {
                RxBus.publish(RxEvent(EventType.JOB_ITEM_CLICK, data = job))
            }
        }
    }

    override fun getItemCount() = jobs.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val positionName: TextView? = itemView.position
        val companyName: TextView? = itemView.company_name
        val logo: ImageView? = itemView.company_logo
        val bookmarkBtn: ImageButton? = itemView.bookmark_btn

        /**
         * Determine and set the image resource of the save button
         *
         * @param job               {@link Job} item served to the adapter
         * @param button            button whose image resource will be set
         */
        internal fun loadBookmarkButtonsImage(job: Job, button: ImageButton?) {
            val saveBtnImg = if (job.isBookmarked) {
                R.drawable.ic_tab_bookmarks
            } else {
                R.drawable.ic_unbookmark
            }
            button?.setImageResource(saveBtnImg)
        }
    }
}