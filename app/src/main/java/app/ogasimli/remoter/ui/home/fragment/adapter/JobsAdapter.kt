/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home.fragment.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.ogasimli.remoter.R
import app.ogasimli.remoter.di.module.GlideApp
import app.ogasimli.remoter.helper.utils.getFirstLetters
import app.ogasimli.remoter.helper.utils.inflate
import app.ogasimli.remoter.model.models.Job
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
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
        holder.bindJob(jobs[position])
    }

    override fun getItemCount() = jobs.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener,
            View.OnLongClickListener {

        private val colorGenerator: ColorGenerator = ColorGenerator.MATERIAL
        private val builder: TextDrawable.IBuilder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .endConfig()
                .rect()

        init {
            with(itemView) {
                setOnClickListener(this@ViewHolder)
                setOnLongClickListener(this@ViewHolder)
            }
        }

        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onLongClick(v: View?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        /**
         * Binds jobs to the view
         *
         * @param job   {@link Job} item served to the adapter
         */
        fun bindJob(job: Job) {
            // Load company logo
            loadCompanyLogo(job)
            // Bind position
            itemView.position.text = job.position
            // Bind company name
            itemView.company_name.text = job.company
            // Bind job description
            itemView.job_description.text = job.description
            itemView.posting_date.text = "${job.postingDate.daysTillNow()} d"
        }

        fun String.daysTillNow(): Int {
            val df = ISODateTimeFormat.dateTime()
            val postingDay = DateTime().toDateTime(DateTimeZone.UTC)
            val today = DateTime(DateTimeZone.UTC)
            val passedDays = Days.daysBetween(postingDay, today)
            return passedDays.days
        }

        private fun loadCompanyLogo(itemView: View, job: Job) {
        /**
         * Loads company logo to the View
         *
         * @param job               {@link Job} item served to the adapter
         */
        private fun loadCompanyLogo(job: Job) {
            // Generate placeholder image using TextDrawable
            val placeholder = generatePlaceholderImage(colorGenerator, builder, job.company)
            // Load company logo via Glide
            GlideApp.with(itemView)
                    .load(job.logo)
                    .placeholder(placeholder)
                    .into(itemView.company_logo)
        }

        /**
         * Generates placeholder image for company logo
         *
         * @param colorGenerator    {@link ColorGenerator} color generator for image background
         * @param builder           {@link TextDrawable.IBuilder} text drawable builder
         * @param companyName       name of the company
         */
        private fun generatePlaceholderImage(colorGenerator: ColorGenerator,
                                             builder: TextDrawable.IBuilder,
                                             companyName: String): TextDrawable? {
            val text = companyName.getFirstLetters()
            return builder.build(text, colorGenerator.getColor(companyName))
        }
    }
}