/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.ui.custom;

import android.animation.TimeInterpolator;

/**
 * Custom {@link TimeInterpolator}
 *
 * @author Orkhan Gasimli on 16.08.2018.
 */
public class ElasticInOutInterpolator implements TimeInterpolator {

    /**
     * @param time elapsed time / total time
     * @return easedValue
     */
    @Override
    public float getInterpolation(float time) {
        double amplitude = 1;
        double period = 0.45;
        double pi2 = Math.PI * 2;

        float time1 = time * 2;
        float time2 = time1 - 1;

        double s = period / pi2 * Math.asin(1 / amplitude);
        if (time1 < 1) {
            return (float) (-0.5f * (amplitude
                    * Math.pow(2, 10 * time2)
                    * Math.sin((time2 - s) * pi2 / period)));
        }
        return (float) (amplitude
                * Math.pow(2, -10 * time2)
                * Math.sin((time2 - s) * pi2 / period) * 0.5 + 1);

    }
}
