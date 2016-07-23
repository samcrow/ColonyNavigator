package org.samcrow.colonynavigator;

import android.content.Context;
import android.util.AttributeSet;

import org.mapsforge.map.android.view.MapView;

public class ColonyMapView extends MapView {

    private static final byte ZOOM_MIN = (byte) 15;

    private static final byte ZOOM_MAX = (byte) 23;

    public ColonyMapView(Context context) {
        super(context);
        colonyMapInit(context);
    }

    public ColonyMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        colonyMapInit(context);
    }

    private void colonyMapInit(Context context) {
        setClickable(true);
        getMapScaleBar().setVisible(true);
        setBuiltInZoomControls(true);
        getMapZoomControls().setZoomLevelMin(ZOOM_MIN);
        getMapZoomControls().setZoomLevelMax(ZOOM_MAX);
    }

}
