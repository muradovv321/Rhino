/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.rx

/**
 * Enum class used to indicate the actions send via {@link RxBus}
 *
 * @author Orkhan Gasimli on 31.07.2018.
 */
enum class EventType {
    INTRO_LOGIN_CLICKED,
    INTRO_SIGNUP_CLICKED,
    START_LOCATION_SERVICE,
    STOP_LOCATION_SERVICE,
    LOCATION_SERVICE_STARTED,
    START_TRIP_RECORDING,
    STOP_TRIP_RECORDING,
    SET_DRIVER_ONLINE,
    UPDATE_START_BUTTON,
    OPEN_HOME_ACTIVITY,
    OPEN_CAR_SELECT_FRAGMENT,
    OPEN_SIGNUP_FRAGMENT,
    OPEN_HOME_FRAGMENT,
    OPEN_PWD_RESET_CODE_FRAGMENT,
    OPEN_PWD_RESET_FRAGMENT,
    OPEN_LOGIN_FRAGMENT,
    OPEN_CHANGE_CAR_FRAGMENT
}