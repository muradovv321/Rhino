/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.home.fragment.adapter

import app.ogasimli.remoter.model.models.Job

/**
 * Interface for forwarding events to parent of the {@link JobsAdapter}
 *
 * @author Orkhan Gasimli on 24.07.2018.
 */
interface JobsAdapterCallback {

    fun onJobSaveClick(job: Job)

    fun onDetailsClick(job: Job)

    fun onApplyClick(job: Job)
}