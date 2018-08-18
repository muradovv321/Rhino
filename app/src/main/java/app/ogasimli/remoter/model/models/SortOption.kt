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

        /**
         * Get SortOption based on int value
         *
         * @param type      int value of the SortOption
         * @return          appropriate SortOption type
         */
        fun getFromType(type: Int): SortOption {
            SortOption.values().forEach {
                if (it.type == type) {
                    return it
                }
            }
            return SortOption.BY_POSTING_DATE
        }

        /**
         * Get name of the DB column based on int value
         *
         * @param option    SortOption value
         * @return          appropriate column name
         */
        fun toColumnName(option: SortOption): String {
            return when (option) {
                BY_POSTING_DATE -> "postingTime"
                BY_POSITION_NAME -> "position"
                BY_COMPANY_NAME -> "company"
            }
        }
    }
}