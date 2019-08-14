package org.samcrow.differentialgps;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Information on a known base station and its connections
 */
public class StationStatus implements Parcelable {

    /**
     * The station
     */
    private final Station mStation;

    /**
     * If this device is connected to the station
     */
    private final boolean mConnected;

    /**
     * Signal strength of communication from the station
     *
     * (if mConnected is false, this will be zero)
     */
    private final int mRssi;


    public StationStatus(Station station, boolean connected, int rssi) {
        mStation = station;
        mConnected = connected;
        mRssi = rssi;
    }

    protected StationStatus(Parcel in) {
        mStation = in.readParcelable(Station.class.getClassLoader());
        mConnected = in.readByte() != 0;
        mRssi = in.readInt();
    }

    public Station getStation() {
        return mStation;
    }

    public boolean isConnected() {
        return mConnected;
    }

    public int getRssi() {
        return mRssi;
    }

    public static final Creator<StationStatus> CREATOR = new Creator<StationStatus>() {
        @Override
        public StationStatus createFromParcel(Parcel in) {
            return new StationStatus(in);
        }

        @Override
        public StationStatus[] newArray(int size) {
            return new StationStatus[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mStation, flags);
        dest.writeByte((byte) (mConnected ? 1 : 0));
        dest.writeInt(mRssi);
    }
}
