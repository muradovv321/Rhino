/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.model.models

/**
 * Enum class used to indicate the filtering type of jobs
 *
 * @author Orkhan Gasimli on 02.08.2018.
 */
enum class FilterOption(val type: Int) {
    ALL_LISTINGS(0),
    TECHNICAL(1),
    DESIGN(2),
    MARKETING(3),
    CONTENT(4),
    EXECUTIVE(5),
    SUPPORT(6);

    companion object {

        /**
         * Get FilterOption based on int value
         *
         * @param type      int value of the FilterOption
         * @return          appropriate FilterOption type
         */
        fun getFromType(type: Int): FilterOption {
            FilterOption.values().forEach {
                if (it.type == type) {
                    return it
                }
            }
            return FilterOption.ALL_LISTINGS
        }

        /**
         * Get FilterOption based on name
         *
         * @param name      name of the FilterOption
         * @return          appropriate FilterOption type
         */
        fun getFromName(name: String): FilterOption {
            FilterOption.values().forEach {
                if (it.name == name.replace(" ", "_").toUpperCase()) {
                    return it
                }
            }
            return FilterOption.ALL_LISTINGS
        }
    }
}