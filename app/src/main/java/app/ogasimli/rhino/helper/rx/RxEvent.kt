/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.helper.rx

/**
 * Data class for sending event via {@link RxBus} class
 *
 * @author Orkhan Gasimli on 31.07.2018.
 */
open class RxEvent(val type: EventType, val message: String = "", val data: Any? = null)

class JobsCountEvent(type: EventType, val count: Int, var query: String? = null) : RxEvent(type)

class JobsCount {
    var openJobs = 0
    var bookmarkedJobs = 0
    var query: String? = null
}