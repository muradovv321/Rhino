/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.utils

/**
 * Helper & extension functions for String manipulation
 *
 * @author Orkhan Gasimli on 19.07.2018.
 */

/**
 * Extension function to extract first letters of words
 * capitalize and join them back to one string
 */
fun String.getFirstLetters(): String {
    // Return X if company name is blank
    if (this.isBlank()) return "X"
    return this.trim().split(" ").map { it[0].toUpperCase() }.joinToString("")
}