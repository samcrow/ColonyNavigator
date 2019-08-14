package org.samcrow.differentialgps;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class DifferentialGpsService extends Service {

    private static final String TAG = DifferentialGpsService.class.getSimpleName();

    /**
     * The differential GPS object
     */
    private DifferentialGps mDifferentialGps;

    public DifferentialGpsService() {
    }

    private final IDifferentialGps.Stub mBinder = new IDifferentialGps.Stub() {
        @Override
        public StationStatus[] getStationStatus() throws RemoteException {
            if (mDifferentialGps != null) {
                // TODO
                return new StationStatus[0];
            } else {
                Log.w(TAG, "Base stations not initialized, can't provide station status");
                return new StationStatus[0];
            }
        }

        @Override
        public double getLatitudeError() throws RemoteException {
            if (mDifferentialGps != null) {
                return mDifferentialGps.getLatitudeError();
            } else {
                Log.w(TAG, "Base stations not initialized, can't provide latitude error");
                return 0;
            }
        }

        @Override
        public double getLongitudeError() throws RemoteException {
            if (mDifferentialGps != null) {
                return mDifferentialGps.getLongitudeError();
            } else {
                Log.w(TAG, "Base stations not initialized, can't provide longitude error");
                return 0;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
