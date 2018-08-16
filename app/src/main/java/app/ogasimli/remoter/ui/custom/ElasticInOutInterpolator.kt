/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.custom

import android.animation.TimeInterpolator

/**
 * Custom [TimeInterpolator]
 *
 * @author Orkhan Gasimli on 16.08.2018.
 */
class ElasticInOutInterpolator : TimeInterpolator {

    /**
     * @param time      elapsed time / total time
     * @return          eased value
     */
    override fun getInterpolation(time: Float): Float {
        val amplitude = 1.0
        val period = 0.45
        val pi2 = Math.PI * 2

        val time1 = time * 2
        val time2 = time1 - 1

        val s = period / pi2 * Math.asin(1 / amplitude)
        return if (time1 < 1) {
            (-0.5f * (amplitude
                    * Math.pow(2.0, (10 * time2).toDouble())
                    * Math.sin((time2 - s) * pi2 / period))).toFloat()
        } else ((amplitude
                * Math.pow(2.0, (-10 * time2).toDouble())
                * Math.sin((time2 - s) * pi2 / period) * 0.5) + 1).toFloat()

    }
}
