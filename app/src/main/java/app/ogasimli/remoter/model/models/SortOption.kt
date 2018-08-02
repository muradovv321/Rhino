/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.models

/**
 * Enum class used to indicate the sorting type of jobs
 *
 * @author Orkhan Gasimli on 02.08.2018.
 */
enum class SortOption(val type: Int) {
    BY_POSTING_DATE(0),
    BY_POSITION_NAME(1),
    BY_COMPANY_NAME(2);

    companion object {
        fun getFromType(type: Int): SortOption {
            SortOption.values().forEach {
                if (it.type == type) {
                    return it
                }
            }
            throw IllegalArgumentException("Unknown enum type: $type")
        }
    }
}