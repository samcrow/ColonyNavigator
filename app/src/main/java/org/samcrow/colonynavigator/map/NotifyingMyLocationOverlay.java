package org.samcrow.colonynavigator.map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.map.android.layer.MyLocationOverlay;
import org.mapsforge.map.model.IMapViewPosition;
import org.mapsforge.map.model.MapViewPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * A type of MyLocationOverlay that can notify listeners when it receives
 * a location update
 *
 * @author samcrow
 */
public class NotifyingMyLocationOverlay extends MyLocationOverlay {

    private List<LocationListener> listeners = new ArrayList<>();

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

    public void addLocationListener(LocationListener newListener) {
        listeners.add(newListener);
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        super.onLocationChanged(newLocation);

        // Notify the listeners
        for (LocationListener listener : listeners) {
            listener.onLocationChanged(newLocation);
        }
    }


}
