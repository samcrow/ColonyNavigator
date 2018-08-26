package org.samcrow.differentialgps;

import android.content.Context;
import android.util.Log;

/**
 * A differential GPS location provider
 */
public class DifferentialGps {

    private static final String TAG = DifferentialGps.class.getSimpleName();

    private final BleConnector mConnector;

    /**
     * Difference between measured and placed latitude
     */
    private double mLatitudeError;
    /**
     * Difference between measured and placed longitude
     */
    private double mLongitudeError;

    public DifferentialGps(Context context, Station[] stations) {
        mConnector = new BleConnector(context, stations);

        mLatitudeError = 0;
        mLongitudeError = 0;

        mConnector.setBaseStationCallback(new BleConnector.BaseStationCallback() {
            @Override
            public void baseStationPositionUpdated(Station station, double latitude, double longitude, float hdp) {
                double latitudeError = latitude - station.getLatitude();
                double longitudeError = longitude - station.getLongitude();
                Log.i(TAG, "Position error " + latitudeError + ", " + longitudeError);

                // Currently only handles one base station
                mLatitudeError = latitudeError;
                mLongitudeError = longitudeError;
            }
        });
    }

    public void start() {
        mConnector.start();
    }

    public void pause() {
        mConnector.pause();
    }

    public double getLatitudeError() {
        return mLatitudeError;
    }

    public double getLongitudeError() {
        return mLongitudeError;
    }
}
