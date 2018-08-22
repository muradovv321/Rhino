/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.helper.rx

/**
 * Enum class used to indicate the actions send via {@link RxBus}
 *
 * @author Orkhan Gasimli on 31.07.2018.
 */
enum class EventType {
    OPEN_JOBS_COUNT,
    BOOKMARKED_JOBS_COUNT,
    BOOKMARK_BUTTON_CLICK,
    JOB_ITEM_CLICK
}