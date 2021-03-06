/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.model.models

import app.ogasimli.rhino.model.models.FilterOption.*

/**
 * Data class holding job filtering keywords
 *
 * @author Orkhan Gasimli on 15.08.2018.
 */
data class FilterKeywords(
        val all: Keywords,
        val technical: Keywords,
        val design: Keywords,
        val marketing: Keywords,
        val content: Keywords,
        val executive: Keywords,
        val support: Keywords) {

    /**
     * Returns appropriate keyword set based on FilterOption
     *
     * @param filterOption      {@link FilterOption} for indicating the order of the items
     * @return                  returns appropriate {@link Keywords}
     */
    fun getKeywordsByFilterOption(filterOption: FilterOption): Keywords =
            when (filterOption) {
                ALL_LISTINGS -> all
                TECHNICAL -> technical
                DESIGN -> design
                MARKETING -> marketing
                CONTENT -> content
                EXECUTIVE -> executive
                SUPPORT -> support
            }
}


/**
 * Data class for fetching filtering keywords
 * from local raw JSON file
 *
 * @author Orkhan Gasimli on 15.08.2018.
 */
data class Keywords(val name: String,
                    val include: ArrayList<String>,
                    val exclude: ArrayList<String>)