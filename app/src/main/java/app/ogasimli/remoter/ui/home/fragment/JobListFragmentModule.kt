/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home.fragment

import app.ogasimli.remoter.di.scope.FragmentScope
import app.ogasimli.remoter.model.models.Job
import app.ogasimli.remoter.ui.home.JobsAdapter
import dagger.Module
import dagger.Provides

/**
 * DI Module providing dependencies for JobListFragment
 *
 * @author Orkhan Gasimli on 16.07.2018.
 */
@Module
class JobListFragmentModule {

    @FragmentScope
    @Provides
    fun provideJobsAdapter() = JobsAdapter()

    @FragmentScope
    @Provides
    fun provideDummyJobList(): List<Job> = listOf(
            Job("68101",
                    1531787783000,
                    "2018-07-16T17:36:23-07:00",
                    "Year old startup",
                    "Senior Full Stack Rails Developer",
                    "Job descriptionWe&rsquo;re a year-old startup that&rsquo;s" +
                            " initially grown with a licensed tech solution that includes " +
                            "customer and admin mobile/web apps. We now need to incrementally " +
                            "build out our own stack and therefore need an experienced, hands " +
                            "on dev to kick start and grow o...",
                    listOf("full stack",
                            "dev",
                            "ruby",
                            "senior",
                            "digital nomad",
                            "ruby"),
                    "https://remoteok.io/remote-jobs/68101"
            ),
            Job("68098",
                    1531782538000,
                    "2018-07-16T16:08:58-07:00",
                    "O'Reilly Media",
                    "XD Designer",
                    "About the RoleReporting to the Lead Experience Designer, the" +
                            " Experience Designer (XD) collaborates with members of the " +
                            "Product, Engineering, and Creative teams to imagine and build" +
                            " user-centric learning experiences delivered via our online" +
                            " learning platform. Using a solve, learn,...",
                    listOf(),
                    "https://remoteok.io/remote-jobs/68098"
            ),
            Job("68099",
                    1531782320000,
                    "2018-07-16T16:05:20-07:00",
                    "Clevertech",
                    "Director Of DevOps",
                    "PST TimeZone Preferred!### DESCRIPTIONClevertech is looking " +
                            "for a DevOp Leader to join our global team. We are looking " +
                            "for team members to help us develop world class software " +
                            "products for the most exclusive organizations in the world. " +
                            "We have been at this for sixteen years, and ...",
                    listOf("exec",
                            "devops",
                            "devops"),
                    "https://remoteok.io/remote-jobs/68099"
            )
    )
}