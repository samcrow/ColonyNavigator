package org.samcrow.colonynavigator;

import android.graphics.PointF;

import org.mapsforge.core.model.LatLong;

/**
 * Transforms coordinates from GPS latitude/longitude into local colony
 * coordinates
 * <p>
 * This uses a 6-parameter model with values calculated separately.
 *
 * @author Sam Crow
 */
public class CoordinateTransformer {
    private static CoordinateTransformer instance;

    // Hard-coded values calculated separately
    // These use X for latitude and Y for longitude
    private final SixParameterTransform gpsToColony = new SixParameterTransform(35380813.397795536, -66056.10712056136, 305159.705957632, -5185834.815475205, 358739.976344031, 57293.81311218767);
    private final SixParameterTransform colonyToGps = new SixParameterTransform(31.870795376913772, -5.05871722366544e-7, 2.6943863161521207e-6, -109.04307506765184, 3.1674695723172968e-6, 5.832377854913447e-7);

    /**
     * Constructor
     */
    private CoordinateTransformer() {
    }

    public static CoordinateTransformer getInstance() {

        if (instance == null) {
            instance = new CoordinateTransformer();
        }

        return instance;
    }

    /**
     * Transform given GPS coordinates into local coordinates.
     *
     * @param longitude The longitude
     * @param latitude  The latitude
     * @return A point with the transformed coordinates
     */
    public PointF toLocal(double longitude, double latitude) {
        double[] gpsPoint = new double[]{latitude, longitude};
        double[] localPoint = new double[2];

        gpsToColony.transform(gpsPoint, localPoint);

        return new PointF((float) localPoint[0], (float) localPoint[1]);
    }

    /**
     * Transform the given local coordinates into GPS coordinates
     *
     * @param x The X location
     * @param y The y location
     * @return A point with the longitude mapped to x and the latitude mapped to
     * y
     */
    public LatLong toGps(float x, float y) {
        double[] localPoint = new double[]{x, y};
        double[] gpsPoint = new double[2];

        colonyToGps.transform(localPoint, gpsPoint);

        return new LatLong(gpsPoint[0], gpsPoint[1]);

    }
}
