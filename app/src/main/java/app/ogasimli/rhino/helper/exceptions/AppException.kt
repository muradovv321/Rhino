/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.helper.exceptions

import android.content.Context
import app.ogasimli.rhino.R

/**
 * Exception classes to be used for app specific purposes
 *
 * @author Orkhan Gasimli on 09.08.2018.
 */

sealed class AppException(message: String = "") : RuntimeException(message)

class DbException : AppException()

class GenericApiError : AppException()

class GenericError : AppException()

class ConnectionError : AppException()

class TimeOutError : AppException()

/**
 * Extension function to convert error type into string
 *
 * @param context   context object to get string resource
 */
fun AppException.toMessage(context: Context?): String {
    context?.let {
        return when (this) {
            is DbException -> it.getString(R.string.db_error_msg)
            is GenericApiError -> it.getString(R.string.generic_api_error_msg)
            is ConnectionError -> it.getString(R.string.connection_error_msg)
            is GenericError -> it.getString(R.string.generic_error_msg)
            is TimeOutError -> it.getString(R.string.timeout_error_msg)
        }
    }
    return ""
}