package org.samcrow.colonynavigator;

import androidx.annotation.NonNull;

/**
 * A transform between two coordinate systems that uses six parameters with the following
 * equations:
 *
 * <pre>X = a0 + a1 * x + a2 * y</pre>
 *
 * <pre>Y = b0 + b1 * x + b2 * y</pre>
 */
class SixParameterTransform {
    private final double a0;
    private final double a1;
    private final double a2;
    private final double b0;
    private final double b1;
    private final double b2;

    public SixParameterTransform(double a0, double a1, double a2, double b0, double b1, double b2) {
        this.a0 = a0;
        this.a1 = a1;
        this.a2 = a2;
        this.b0 = b0;
        this.b1 = b1;
        this.b2 = b2;
    }

    public void transform(@NonNull double[] pointIn, @NonNull double[] pointOut) {
        assert  pointIn.length == 2;
        assert pointOut.length == 2;

        final double inX = pointIn[0];
        final double inY = pointIn[1];

        final double convertedX = a0 + a1 * inX + a2 * inY;
        final double convertedY = b0 + b1 * inX + b2 * inY;

        pointOut[0] = convertedX;
        pointOut[1] = convertedY;
    }
}
