package org.samcrow.colonynavigator;

import android.graphics.Matrix;
import android.graphics.PointF;

import org.mapsforge.core.model.LatLong;

/**
 * Transforms coordinates from GPS latitude/longitude into local colony
 * coordinates
 *
 * @author Sam Crow
 */
public class CoordinateTransformer {

    private static final String TAG = CoordinateTransformer.class.getName();
    private static CoordinateTransformer instance;
    private Matrix matrix = new Matrix();
    private Matrix inverse = new Matrix();

    /**
     * Constructor
     */
    private CoordinateTransformer() {

        // Hard-coded values provided by Erik Steiner
        inverse.setValues(new float[]{
                0.0000031740000f, 0.0000006358000f, -109.043098200f,
                -0.0000005184000f, 0.0000027130000f, 31.870789500f,
                0.0f, 0.0f, 1.0f
        });
        matrix.setValues(new float[]{
                303445, -71113.3f, 3.5355E7f,
                57982.3f, 355007, -4.99179E6f,
                0.0f, 0.0f, 1.0f
        });
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
     * @param longitude The longitude (X-axis location)
     * @param latitude  The latitude (Y-axis location);
     * @return A point with the transformed coordinates
     */
    public PointF toLocal(double longitude, double latitude) {
        double x = longitude;
        double y = latitude;
        float[] points = new float[]{(float) x, (float) y};

        matrix.mapPoints(points);

        return new PointF(points[0], points[1]);
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

        float[] points = new float[]{x, y};

        inverse.mapPoints(points);

        return new LatLong(points[1], points[0]);

    }
}
