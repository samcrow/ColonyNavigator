package org.samcrow.colonynavigator.map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.map.android.layer.MyLocationOverlay;
import org.mapsforge.map.model.IMapViewPosition;

/**
 * A type of MyLocationOverlay that can notify a listener when it receives
 * a location update
 *
 * @author samcrow
 */
public class NotifyingMyLocationOverlay extends MyLocationOverlay {

    private LocationListener listener;

    public NotifyingMyLocationOverlay(Context context,
                                      IMapViewPosition mapViewPosition, Bitmap bitmap,
                                      Paint circleFill,
                                      Paint circleStroke) {
        super(context, mapViewPosition, bitmap, circleFill, circleStroke);
    }

    public NotifyingMyLocationOverlay(Context context,
                                      IMapViewPosition mapViewPosition, Bitmap bitmap) {
        super(context, mapViewPosition, bitmap);
    }

    public void setLocationListener(LocationListener newListener) {
        listener = newListener;
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        super.onLocationChanged(newLocation);

        // Notify the additional listener
        if (listener != null) {
            listener.onLocationChanged(newLocation);
        }

    }


}
