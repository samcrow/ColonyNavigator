// IDifferentialGps.aidl
package org.samcrow.differentialgps;

// Declare any non-default types here with import statements
import org.samcrow.differentialgps.StationStatus;

/**
 * The interface that the differential GPS service provides
 */
interface IDifferentialGps {

    /**
     * Returns information on each known station and its connection status
     */
    StationStatus[] getStationStatus();

    /**
     * Returns the difference between measured latitude and actual latitude
     *
     * This value can be subtracted from a latitude value to apply the correction
     */
    double getLatitudeError();

    /**
     * Returns the difference between measured longitude and actual longitude
     *
     * This value can be subtracted from a longitude value to apply the correction
     */
    double getLongitudeError();

}
