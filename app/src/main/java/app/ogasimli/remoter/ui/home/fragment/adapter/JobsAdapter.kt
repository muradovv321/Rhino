/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home.fragment.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import app.ogasimli.remoter.R
import app.ogasimli.remoter.di.module.GlideApp
import app.ogasimli.remoter.helper.utils.getFirstLetters
import app.ogasimli.remoter.helper.utils.inflate
import app.ogasimli.remoter.helper.utils.periodTillNow
import app.ogasimli.remoter.model.models.Job
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import kotlinx.android.synthetic.main.job_item_card.view.*

/**
 * Adapter class for RecyclerView displaying job list
 *
 * @author Orkhan Gasimli on 17.07.2018.
 */
class JobsAdapter(private val callback: JobsAdapterCallback) :
        RecyclerView.Adapter<JobsAdapter.ViewHolder>() {

    private lateinit var viewGroup: ViewGroup
    private var expandedPosition = -1
    private var isExpanded = false

    // {@link ColorGenerator} color generator for image background
    private val colorGenerator: ColorGenerator = ColorGenerator.MATERIAL

    // {@link TextDrawable.IBuilder} text drawable textBuilder
    private val textBuilder: TextDrawable.IBuilder = TextDrawable.builder()
            .beginConfig()
            .withBorder(4)
            .endConfig()
            .rect()

    var jobs: List<Job> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = parent.inflate(R.layout.job_item_card)
        viewGroup = parent
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val job = jobs[position]
        with(holder) {
            // Load image resource of the save button
            loadSaveButtonsImage(job, saveBtn)
            // Attach ClickListener to save button
            saveBtn?.setOnClickListener {
                callback.onJobSaveClick(job)
            }
            // Load company logo
            loadCompanyLogo(job)
            // Bind position
            positionName?.text = job.position
            // Bind company name
            companyName?.text = job.company
            // Bind job description
            jobDescription?.text = job.description
            // Calculate and bind passed time since job posting
            postingDate?.text = periodTillNow(itemView.context, job.postingDate)

            isExpanded = position == expandedPosition
            group?.visibility = if (isExpanded) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                expandedPosition = if (isExpanded) -1 else position
                TransitionManager.beginDelayedTransition(viewGroup)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount() = jobs.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val positionName: TextView? = itemView.position
        val companyName: TextView? = itemView.company_name
        val postingDate: TextView? = itemView.posting_date
        val jobDescription: TextView? = itemView.job_description
        val logo: ImageView? = itemView.company_logo
        val saveBtn: ImageButton? = itemView.save_btn
        val detailsBtn: Button? = itemView.details_button
        val applyBtn: Button? = itemView.apply_button
        val group: Group? = itemView.group

        /**
         * Determine and set the image resource of the save button
         *
         * @param job               {@link Job} item served to the adapter
         * @param button            button whose image resource will be set
         */
        internal fun loadSaveButtonsImage(job: Job, button: ImageButton?) {
            val saveBtnImg = if (job.isBookmarked) {
                R.drawable.ic_tab_bookmarks
            } else {
                R.drawable.ic_unbookmark
            }
            button?.setImageResource(saveBtnImg)
        }

        /**
         * Loads company logo to the View
         *
         * @param job               {@link Job} item served to the adapter
         */
        internal fun loadCompanyLogo(job: Job) {
            // Generate placeholder image using TextDrawable
            val placeholder = generatePlaceholderImage(job.company)
            // Load company logo via Glide
            GlideApp.with(itemView)
                    .load(job.logo)
                    .placeholder(placeholder)
                    .into(itemView.company_logo)
        }

        /**
         * Generates placeholder image for company logo
         *
         * @param companyName       name of the company
         */
        private fun generatePlaceholderImage(companyName: String): TextDrawable? {
            val text = companyName.getFirstLetters()
            return textBuilder.build(text, colorGenerator.getColor(companyName))
        }
    }
}