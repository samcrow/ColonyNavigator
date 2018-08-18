package org.samcrow.colonynavigator;

import android.graphics.Matrix;
import android.graphics.PointF;

import org.mapsforge.core.model.LatLong;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;

/**
 * Transforms coordinates from GPS latitude/longitude into local colony
 * coordinates
 *
 * @author Sam Crow
 */
public class CoordinateTransformer {

    private static CoordinateTransformer instance;

    /*
     * How this works:
     * Colony coordinates <-> UTM coordinates (zone 12N) <-> latitude/longitude
     */

    /**
     * Matrix mapping from homogenous colony coordinates to homogenous UTM coordinates
     */
    private final Matrix mColonyToUtm;
    /**
     * Matrix mapping from homogenous UTM coordinates to homogenous UTM coordinates
     */
    private final Matrix mUtmToColony;

    /**
     * Constructor
     */
    private CoordinateTransformer() {
        mColonyToUtm = new Matrix();
        mColonyToUtm.setValues(new float[] {
                1.02062179523250e+04f,  -3.86246861418180e+03f,   2.58912843430477e+06f,
                5.25180433326394e+04f,  -1.98904950624002e+04f,   1.33356990408822e+07f,
                1.48869418695798e-02f,  -5.63826502026197e-03f,   3.78016445511664e+00f,
        });
        mUtmToColony = new Matrix();
        mUtmToColony.setValues(new float[] {
                1.10574743324865e-01f,   3.29840736957084e-01f,  -1.23935058042237e+06f,
                1.19897446505948e-01f,   4.73794368925693e+00f,  -1.67966821958193e+07f,
                -2.56630686041893e-04f,   5.76786077766980e-03f,  -2.01718752027190e+04f,
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
        // Part 1: Latitude/longitude to UTM
        final LatLng ll = new LatLng(latitude, longitude);
        final UTMRef utm = ll.toUTMRef();
        // Part 2: UTM to colony coordinates
        final float[] coordinates = new float[] {(float) utm.getEasting(), (float) utm.getNorthing()};
        mUtmToColony.mapPoints(coordinates);
        return new PointF(coordinates[0], coordinates[1]);
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
        // Part 1: Colony to UTM
        final float[] utm = new float[] {x, y};
        mColonyToUtm.mapPoints(utm);
        final float easting = utm[0];
        final float northing = utm[1];

        // Part 2: UTM to latitude/longitude
        final UTMRef utmRef = new UTMRef(easting, northing, 'N', 12);
        final LatLng ll = utmRef.toLatLng();
        return new LatLong(ll.getLat(), ll.getLng());
    }
}
