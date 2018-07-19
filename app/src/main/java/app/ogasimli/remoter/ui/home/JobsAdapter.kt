/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.ogasimli.remoter.R
import app.ogasimli.remoter.di.module.GlideApp
import app.ogasimli.remoter.helper.utils.inflate
import app.ogasimli.remoter.model.models.Job
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import kotlinx.android.synthetic.main.job_item_card.view.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Days
import org.joda.time.format.ISODateTimeFormat

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

        val colorGenerator = ColorGenerator.MATERIAL
        val builder = TextDrawable.builder()
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

        fun bindJob(job: Job) {
            loadCompanyLogo(itemView, job)
            itemView.position.text = job.position
            itemView.company_name.text = job.company
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
            val placeholder = generatePlaceholderImage(colorGenerator, builder, job.company)
            GlideApp.with(itemView)
                    .load(job.logo)
                    .placeholder(placeholder)
                    .into(itemView.company_logo)
        }

        private fun generatePlaceholderImage(colorGenerator: ColorGenerator,
                                             builder: TextDrawable.IBuilder,
                                             company: String): TextDrawable? {
            val text = company
                    .split(" ")
                    .map { it[0].toUpperCase() }
                    .joinToString("")
            return builder.build(text, colorGenerator.getColor(company))
        }
    }
}