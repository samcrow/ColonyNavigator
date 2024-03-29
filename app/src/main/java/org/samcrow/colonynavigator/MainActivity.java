package org.samcrow.colonynavigator;

import android.Manifest.permission;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import com.applantation.android.svg.SVG;
import com.applantation.android.svg.SVGParseException;
import com.applantation.android.svg.SVGParser;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidPreferences;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.IMapViewPosition;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.model.common.PreferencesFacade;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.rendertheme.XmlRenderTheme;
import org.samcrow.colonynavigator.data4.Colony;
import org.samcrow.colonynavigator.data4.ColonySelection;
import org.samcrow.colonynavigator.data4.ColonySet;
import org.samcrow.colonynavigator.data4.NewColony;
import org.samcrow.colonynavigator.data4.NewColonyDatabase;
import org.samcrow.colonynavigator.data4.NewColonyProvider;
import org.samcrow.colonynavigator.map.ColonyMarker;
import org.samcrow.colonynavigator.map.NotifyingMyLocationOverlay;
import org.samcrow.colonynavigator.map.RouteLineLayer;
import org.samcrow.data.provider.ColonyProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The main activity
 */
public class MainActivity extends AppCompatActivity implements
        OnSharedPreferenceChangeListener, ColonyEditDialogFragment.ColonyChangeListener,
        NewColonyDialogFragment.NewColonyListener {

    /**
     * The initial position of the map
     */
    private static final MapPosition START_POSITION = new MapPosition(
            new LatLong(31.872176, -109.040983), (byte) 17);

    /**
     * The request code used for permissions
     */
    private static final int PERMISSION_REQUEST = 30223;
    private static final int CARD_TREE_REQUEST = 30225;
    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * The current selected colony
     */
    private final ColonySelection selection = new ColonySelection();
    private PreferencesFacade preferencesFacade;
    private MapView mapView;
    private LayerManager layerManager;
    private NotifyingMyLocationOverlay locationOverlay;
    private ColonyProvider provider;
    private ColonySet colonies;
    private NewColonyDatabase newColonyDB;
    /**
     * If the application has been granted all permissions and has completed initialization
     */
    private boolean mInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Set up GraphicFactory
            AndroidGraphicFactory.createInstance(getApplication());

            createSharedPreferences();

            setContentView(R.layout.activity_main);

            setTitle("Map");

            // Check permissions
            final String[] allPermissions = {
                    permission.ACCESS_FINE_LOCATION,
                    permission.INTERNET,
                    permission.READ_EXTERNAL_STORAGE,
                    permission.WRITE_EXTERNAL_STORAGE,
            };
            final List<String> missingPermissions = new ArrayList<>(allPermissions.length);
            for (String permission : allPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.add(permission);
                }
            }
            if (missingPermissions.isEmpty()) {
                // Go ahead
                postPermissionSetup();
            } else {
                // Request permission
                ActivityCompat.requestPermissions(this, missingPermissions.toArray(new String[0]), PERMISSION_REQUEST);
            }


        } catch (Exception ex) {
            // Show a dialog, then quit
            Log.e(TAG, "Exception", ex);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(ex.getClass().getSimpleName())
                    .setMessage(ex.getMessage())
                    .setNeutralButton("Quit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Close this activity
                            MainActivity.this.finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            boolean allGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                try {
                    postPermissionSetup();
                } catch (Exception ex) {
                    Log.e(TAG, "Exception", ex);
                    // Show a dialog, then quit
                    new AlertDialog.Builder(this)
                            .setTitle(ex.getClass().getSimpleName())
                            .setMessage(ex.getMessage())
                            .setNeutralButton("Quit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Close this activity
                                    MainActivity.this.finish();
                                }
                            })
                            .show();
                }
            } else {
                // Error dialog
                new Builder(this)
                        .setTitle(R.string.permissions_not_granted)
                        .setMessage(R.string.request_grant_permissions)
                        .setNeutralButton(R.string.quit, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        }
    }

    /**
     * Does setup tasks. Assumes that all necessary permissions have been granted.
     *
     * @throws IOException if an error occurs
     */
    private void postPermissionSetup() throws IOException {
        setUpMap();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Need to get permission to read and write memory card
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            final String cardUriString = preferences.getString("card_uri", null);
            final Uri cardUri = cardUriString != null ? Uri.parse(cardUriString) : null;

            if (cardUri == null) {
                // Ask for permission and for the user to select the card
                openCardFolder(null);
            } else {
                // Check if we have permission to access the URI
                final DocumentFile cardFile = DocumentFile.fromTreeUri(this, cardUri);
                assert cardFile != null;
                Log.i(TAG, "Card URI " + cardUri + ": exists " + cardFile.exists() + " canRead " + cardFile.canRead() + " canWrite " + cardFile.canWrite() + " files " + Arrays.toString(cardFile.listFiles()));
                if (cardFile.exists() && cardFile.canRead() && cardFile.canWrite()) {
                    // Already have permission, don't need to ask
                    postCardPermissionSetup(cardUri);
                } else {
                    // Ask for permission
                    openCardFolder(cardUri);
                }
            }
        } else {
            // Earlier version of Android, no permission needed
            postCardPermissionSetup(null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openCardFolder(@Nullable final Uri initialUri) {
        new Builder(this).setTitle(R.string.memory_card_access)
                .setMessage(R.string.choose_memory_card)
                .setPositiveButton(R.string.button_continue, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Intent cardIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        if (initialUri != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            cardIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri);
                        }
                        startActivityForResult(cardIntent, CARD_TREE_REQUEST);
                    }
                })
                .setCancelable(false)
                .show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CARD_TREE_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                final Uri cardUri = data.getData();
                if (cardUri != null) {
                    Log.i(TAG, "Opened memory card " + cardUri);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        // Got permission to access this directory
                        // Make it persistent if possible
                        if ((data.getFlags() & Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION) != 0) {
                            Log.i(TAG, "Requesting persistent permission to access card");
                            getContentResolver().takePersistableUriPermission(cardUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        }
                    }

                    try {
                        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                        preferences.edit().putString("card_uri", cardUri.toString()).apply();

                        postCardPermissionSetup(cardUri);
                    } catch (Exception ex) {
                        // Show a dialog, then quit
                        Log.e(TAG, "Exception", ex);
                        new Builder(MainActivity.this)
                                .setTitle(ex.getClass().getSimpleName())
                                .setMessage(ex.getMessage())
                                .setNeutralButton("Quit", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Close this activity
                                        MainActivity.this.finish();
                                    }
                                })
                                .show();
                    }
                }
            } else {
                // Folder selection not complete or no folder chosen
                finish();
            }
        }
    }

    private void postCardPermissionSetup(@Nullable Uri chosenUri) throws IOException {
        // Add colonies
        final Storage.FileUris uris = Storage.getMemoryCardUris(this, chosenUri);
        if (uris == null) {
            throw new IllegalStateException("Can't find memory card");
        }
        Log.i(TAG, "Found storage URIS " + uris);
        provider = new NewColonyProvider(this, uris);

        final CoordinateTransformer transformer = CoordinateTransformer.getInstance();
        colonies = provider.getColonies();
        for (Colony colony : colonies) {
            layerManager.getLayers().add(new ColonyMarker(colony, transformer));
        }

        // Add layers above colonies

        // Location layer
        setUpLocationOverlay();
        // Route line layer
        setUpRouteLine();

        // New colonies
        newColonyDB = new NewColonyDatabase(this);
        mInitialized = true;
        // Start location updates
        locationOverlay.enableMyLocation(false);
    }

    private void setUpMap() {

        mapView = new ColonyMapView(this);

        Model model = mapView.getModel();
        model.init(this.preferencesFacade);


        // Put the map view in the layout
        FrameLayout layout = findViewById(R.id.map_view_frame);
        layout.addView(mapView);

        // Create a tile cache
        TileCache tileCache = AndroidUtil.createTileCache(this, getPersistableId(),
                mapView.getModel().displayModel.getTileSize(),
                getScreenRatio(),
                mapView.getModel().frameBufferModel.getOverdrawFactor());

        layerManager = mapView.getLayerManager();

        try {
            // Create a tile layer for OpenStreetMap data
            TileRendererLayer tileRendererLayer = createTileRendererLayer(
                    tileCache,
                    initializePosition(mapView.getModel().mapViewPosition),
                    InternalRenderTheme.OSMARENDER);

            layerManager.getLayers().add(tileRendererLayer);
        } catch (IOException e) {
            new Builder(MainActivity.this)
                    .setTitle(R.string.failed_to_open_map)
                    .setMessage(e.getMessage())
                    .show();
        }
        mapView.getModel().mapViewPosition.setMapPosition(START_POSITION);
    }

    private void setUpRouteLine() {
        final RouteLineLayer route = new RouteLineLayer(locationOverlay);
        selection.addChangeListener(new ColonySelection.Listener() {
            @Override
            public void selectedColonyChanged(Colony oldColony, Colony newColony) {
                route.setDestination(CoordinateTransformer.getInstance()
                        .toGps((float) newColony.getX(), (float) newColony.getY()));
            }
        });
        layerManager.getLayers().add(route);
    }

    private void setUpLocationOverlay() {
        locationOverlay = new NotifyingMyLocationOverlay(this,
                mapView.getModel().mapViewPosition,
                AndroidGraphicFactory.convertToBitmap(getMyLocationDrawable()));
        layerManager.getLayers().add(locationOverlay);
        // locationOverlay.enableMyLocation() gets called in onResume().

        final TextView coordinatesView = findViewById(R.id.current_coordinates);
        locationOverlay.addLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                final PointF coordinates = CoordinateTransformer.getInstance().toLocal(location.getLongitude(), location.getLatitude());
                coordinatesView.setText(String.format(Locale.getDefault(), "(%.0f, %.0f)", coordinates.x, coordinates.y));
            }
        });
    }

    private IMapViewPosition initializePosition(IMapViewPosition mvp) {
        LatLong center = mvp.getCenter();

        if (center.equals(new LatLong(0, 0))) {
            mvp.setMapPosition(START_POSITION);
        }
        return mvp;
    }

    private void createSharedPreferences() {
        SharedPreferences sp = this.getSharedPreferences(getPersistableId(),
                MODE_PRIVATE);
        this.preferencesFacade = new AndroidPreferences(sp);
    }

    /**
     * @return the id that is used to save this mapview
     */
    private String getPersistableId() {
        return this.getClass().getSimpleName();
    }

    /**
     * @return the screen ratio that the mapview takes up (for cache
     * calculation)
     */
    private float getScreenRatio() {
        return 1.0f;
    }

    private TileRendererLayer createTileRendererLayer(
            TileCache tileCache, IMapViewPosition mapViewPosition,
            XmlRenderTheme renderTheme) throws IOException {
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache,
                new MapFile(Storage.getResourceAsFile(this, R.raw.site)), mapViewPosition,
                AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(renderTheme);
        tileRendererLayer.setTextScale(1.5f);
        return tileRendererLayer;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences,
                                          String key) {

    }

    private Drawable getMyLocationDrawable() {
        try {
            final SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.my_location);
            return svg.createPictureDrawable();
        } catch (SVGParseException e) {
            throw new RuntimeException("Could not load my location image", e);
        }
    }

    @Override
    public void onColonyChanged(Bundle colonyData) {
        // Find the colony that was changed and update its data
        final String colonyId = colonyData.getString("colony_id");
        Colony colony = colonies.get(colonyId);
        if (colonyData.containsKey("colony_visited")) {
            colony.setAttribute("census.visited", colonyData.getBoolean("colony_visited"));
        }
        if (colonyData.containsKey("colony_active")) {
            colony.setAttribute("census.active", colonyData.getBoolean("colony_active"));
        }
        // Save the colony
        provider.updateColony(colony);
        // Redraw the colonies, and all other layers
        layerManager.redrawLayers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // Search box/item
        final MenuItem searchItem = menu.findItem(R.id.search_box);
        searchItem.expandActionView();
        final SearchView searchView = (SearchView) searchItem.getActionView();
        // Configure: Only expect numbers
        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Search for the colony
                String colonyId = query.trim();

                Colony newSelectedColony = colonies.get(colonyId);
                if (newSelectedColony != null) {
                    // Deselect the current selected colony and select the new one
                    selection.setSelectedColony(newSelectedColony);

                    // Remove the focus from the search field
                    searchView.clearFocus();

                    // Center the map view on the colony
                    mapView.getModel().mapViewPosition.animateTo(
                            CoordinateTransformer.getInstance()
                                    .toGps((float) newSelectedColony.getX(),
                                            (float) newSelectedColony.getY()));
                    return true;
                } else {
                    // No colony
                    new Builder(MainActivity.this)
                            .setTitle(R.string.not_found)
                            .setMessage(R.string.no_colony_exists)
                            .show();
                    return false;
                }
            }

        });

        // Edit item
        final MenuItem editItem = menu.findItem(R.id.edit_item);
        editItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final Colony selectedColony = selection.getSelectedColony();
                if (selectedColony != null) {
                    ColonyEditDialogFragment editor = ColonyEditDialogFragment.newInstance(
                            selectedColony);
                    editor.show(getSupportFragmentManager(), "editor");
                }
                return true;
            }

        });

        // My location toggle item
        final MenuItem myLocationItem = menu.findItem(R.id.my_location_item);
        // Change the check state and toggle snap-to-location when pressed
        myLocationItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                myLocationItem.setChecked(!myLocationItem.isChecked());

                if (myLocationItem.isChecked()) {
                    myLocationItem.setIcon(R.drawable.ic_menu_my_location_blue);
                    locationOverlay.setSnapToLocationEnabled(true);
                } else {
                    myLocationItem.setIcon(R.drawable.ic_menu_my_location_gray);
                    locationOverlay.setSnapToLocationEnabled(false);
                }

                return true;
            }
        });

        // Add colony item
        final MenuItem newColonyItem = menu.findItem(R.id.new_colony_item);
        newColonyItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NewColonyDialogFragment dialog = new NewColonyDialogFragment();
                dialog.show(getSupportFragmentManager(), "new colony");
                return true;
            }
        });

        // Show colonies item
        final MenuItem showColoniesItem = menu.findItem(R.id.show_new_colonies_item);
        showColoniesItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final List<NewColony> colonies = newColonyDB.getNewColonies();

                final NewColonyListDialogFragment dialog = new NewColonyListDialogFragment();
                final Bundle args = new Bundle();
                args.putParcelableArray("colonies",
                        colonies.toArray(new NewColony[0]));
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "new colony list");

                return true;
            }
        });

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationOverlay != null) {
            // Pause location updates
            locationOverlay.disableMyLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mInitialized) {
            if (locationOverlay == null) {
                setUpLocationOverlay();
            }
            // Start location updates
            locationOverlay.enableMyLocation(false);
        }
    }

    @Override
    public void createColony(String name, String notes) {
        final Location currentLocation = locationOverlay.getLastLocation();
        if (currentLocation == null) {
            new Builder(this)
                    .setTitle(R.string.no_location_available)
                    .setMessage(R.string.wait_for_location)
                    .show();
            return;
        }
        final PointF localCoords = CoordinateTransformer.getInstance()
                .toLocal(currentLocation.getLongitude(), currentLocation.getLatitude());
        final NewColony colony = new NewColony(localCoords.x, localCoords.y, name, notes);
        try {
            newColonyDB.insertNewColony(colony);
        } catch (SQLException e) {
            new Builder(this)
                    .setTitle(R.string.colony_save_failed)
                    .setMessage(e.getMessage())
                    .show();
        }
    }
}
