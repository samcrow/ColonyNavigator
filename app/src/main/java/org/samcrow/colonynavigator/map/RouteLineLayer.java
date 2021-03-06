package org.samcrow.colonynavigator.map;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.overlay.Polyline;

public class RouteLineLayer extends Polyline {

    private final NotifyingMyLocationOverlay locationLayer;

    private LatLong destination = null;

    public RouteLineLayer(NotifyingMyLocationOverlay location) {
        super(getPaint(), AndroidGraphicFactory.INSTANCE);

        this.locationLayer = location;
        // Set up location callback
        locationLayer.addLocationListener(new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                updatePositions();
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }
        });

        // Make room for a start point and an end point
        getLatLongs().add(new LatLong(0, 0));
        getLatLongs().add(new LatLong(0, 0));

        // Invisible until a destination marker is set
        setVisible(false);
    }

    private static Paint getPaint() {
        Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        paint.setStyle(Style.STROKE);

        return paint;
    }

    public void setDestination(LatLong destination) {
        this.destination = destination;
        updatePositions();
    }

    private void updatePositions() {
        // Get latitude/longitude from location
        if (locationLayer != null) {
            Location location = locationLayer.getLastLocation();
            if (location != null) {
                setStartPoint(new LatLong(location.getLatitude(), location.getLongitude()));
            } else {
                setVisible(false);
                return;
            }
        } else {
            setVisible(false);
            return;
        }

        if (destination != null) {
            setEndPoint(destination);
        } else {
            setVisible(false);
            return;
        }
        // None of those failed, so the line should be visible
        setVisible(true);
        requestRedraw();
    }

    private void setStartPoint(LatLong point) {
        getLatLongs().set(0, point);
    }

    private void setEndPoint(LatLong point) {
        getLatLongs().set(1, point);
    }
}
