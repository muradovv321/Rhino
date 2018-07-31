/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.rx

/**
 * Data class for sending event via {@link RxBus} class
 *
 * @author Orkhan Gasimli on 31.07.2018.
 */
open class RxEvent(val type: EventType, val message: String = "")

class JobsCountEvent(type: EventType, val count: Int, var isSearching: Boolean = false) : RxEvent(type)