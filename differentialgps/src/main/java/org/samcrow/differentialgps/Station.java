package org.samcrow.differentialgps;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Information about a differential GPS base station
 */
public class Station implements Parcelable {
    /**
     * The Bluetooth address of the base station, formatted as a string
     * with colons between the bytes. All letters in the address should
     * be capitalized.
     *
     * This field is never null.
     */
    private final String mAddress;
    /**
     * The actual latitude where the station is placed
     */
    private final double mLatitude;
    /**
     * The actual longitude where the station is placed
     */
    private final double mLongitude;

    public Station(String address, double latitude, double longitude) {
        if (address == null) {
            throw new NullPointerException("Address must not be null");
        }
        mAddress = address;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    protected Station(Parcel in) {
        mAddress = in.readString();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
    }

    public String getAddress() {
        return mAddress;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    @Override
    public String toString() {
        return "Base station " + mAddress + " at " + mLatitude + ", " + mLongitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        if (Double.compare(station.mLatitude, mLatitude) != 0) return false;
        if (Double.compare(station.mLongitude, mLongitude) != 0) return false;
        return mAddress.equals(station.mAddress);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = mAddress.hashCode();
        temp = Double.doubleToLongBits(mLatitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mLongitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAddress);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
    }

    public static final Creator<Station> CREATOR = new Creator<Station>() {
        @Override
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };
}
