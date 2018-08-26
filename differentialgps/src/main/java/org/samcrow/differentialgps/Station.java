package org.samcrow.differentialgps;

/**
 * Information about a differential GPS base station
 */
public class Station {
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
}
