/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.rhino.di.scope

import javax.inject.Scope

/**
 * Defines Activity level scope, any field or method
 * annotated with this will have only a single instance
 *
 * @author Orkhan Gasimli on 21.12.2017.
 */
@Scope
@MustBeDocumented
@Retention
annotation class ActivityScope