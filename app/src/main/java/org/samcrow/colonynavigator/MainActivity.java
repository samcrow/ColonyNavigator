package org.samcrow.colonynavigator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.SearchView;

import com.applantation.android.svg.SVG;
import com.applantation.android.svg.SVGParseException;
import com.applantation.android.svg.SVGParser;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.rapplogic.xbee.xbee.AndroidFTDIConnection;
import com.rapplogic.xbee.xbee.api.InputStreamThread;
import com.rapplogic.xbee.xbee.api.XBee;
import com.rapplogic.xbee.xbee.api.XBeeConfiguration;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.android.AndroidPreferences;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.model.common.PreferencesFacade;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.rendertheme.XmlRenderTheme;
import org.samcrow.colonynavigator.data4.ColonySelection;
import org.samcrow.colonynavigator.map.ColonyMarker;
import org.samcrow.colonynavigator.map.NotifyingMyLocationOverlay;
import org.samcrow.colonynavigator.map.RouteLineLayer;
import org.samcrow.data.provider.ColonyProvider;
import org.samcrow.data.provider.MemoryCardDataProvider;
import org.samcrow.colonynavigator.data4.Colony;
import org.samcrow.colonynavigator.data4.ColonySet;

import java.io.File;
import java.io.IOException;

/**
 * The main activity
 */
public class MainActivity extends Activity implements
		OnSharedPreferenceChangeListener, ColonyEditDialogFragment.ColonyChangeListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	/**
	 * The initial position of the map
	 */
	private static final MapPosition START_POSITION = new MapPosition(
			new LatLong(31.872176, -109.040983), (byte) 17);

	private static final File MAP_FILE = new File(getSDCardPath().getAbsolutePath() + "/new-mexico.map");

	private PreferencesFacade preferencesFacade;

	private MapView mapView;

	private TileCache tileCache;

	private LayerManager layerManager;
	
	private NotifyingMyLocationOverlay locationOverlay;

	private ColonyProvider provider;

	private ColonySet colonies;
	/**
	 * The current selected colony
	 */
	private ColonySelection selection = new ColonySelection();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			// Set up GraphicFactory
			AndroidGraphicFactory.createInstance(getApplication());

			createSharedPreferences();

			setContentView(R.layout.activity_main);

			setTitle("Map");

			setUpMap();

			// Add colonies
			provider = new MemoryCardDataProvider(getSDCardPath());

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

		} catch (Exception ex) {
			// Show a dialog, then quit
			new AlertDialog.Builder(MainActivity.this)
					.setTitle(ex.getClass().getSimpleName())
					.setMessage(ex.getMessage())
					.setIcon(android.R.drawable.ic_dialog_alert)
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

	private void setUpMap() {

		mapView = new ColonyMapView(this);

		Model model = mapView.getModel();
		model.init(this.preferencesFacade);


		// Put the map view in the layout
		FrameLayout layout = (FrameLayout) findViewById(R.id.map_view_frame);
		layout.addView(mapView);

		// Create a tile cache
		tileCache = AndroidUtil.createTileCache(this, getPersistableId(),
				mapView.getModel().displayModel.getTileSize(),
				getScreenRatio(),
				mapView.getModel().frameBufferModel.getOverdrawFactor());

		layerManager = mapView.getLayerManager();

		try {
			// Create a tile layer for OpenStreetMap data
			TileRendererLayer tileRendererLayer = createTileRendererLayer(
					tileCache,
					initializePosition(mapView.getModel().mapViewPosition),
					MAP_FILE, InternalRenderTheme.OSMARENDER, false);

			layerManager.getLayers().add(tileRendererLayer);
		}
		catch (IOException e) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("Failed to open map file")
					.setMessage(e.getMessage())
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setNeutralButton("OK", DIALOG_CLICK_NOOP).show();
		}
		mapView.getModel().mapViewPosition.setMapPosition(START_POSITION);
	}
	
	private void setUpRouteLine() {
		final RouteLineLayer route = new RouteLineLayer(locationOverlay);
		selection.addChangeListener(new ColonySelection.Listener() {
			@Override
			public void selectedColonyChanged(Colony oldColony, Colony newColony) {
				route.setDestination(CoordinateTransformer.getInstance().toGps((float) newColony.getX(), (float) newColony.getY()));
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
	}

	private MapViewPosition initializePosition(MapViewPosition mvp) {
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
	 *         calculation)
	 */
	private float getScreenRatio() {
		return 1.0f;
	}

	private TileRendererLayer createTileRendererLayer(
			TileCache tileCache, MapViewPosition mapViewPosition, File mapFile,
			XmlRenderTheme renderTheme, boolean hasAlpha) throws IOException {
		TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, new MapFile(MapFileResource.getMapFile(this, R.raw.site)), mapViewPosition, hasAlpha, true, AndroidGraphicFactory.INSTANCE);
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
			e.printStackTrace();
			return null;
		}
	}
	

	@Override
	public void onColonyChanged(Bundle colonyData) {
		// Find the colony that was changed and update its data
		final int colonyId = colonyData.getInt("colony_id");
		Colony colony = colonies.get(colonyId);
		if(colonyData.containsKey("colony_visited")) {
			colony.setAttribute("census.visited", colonyData.getBoolean("colony_visited"));
		}
		if(colonyData.containsKey("colony_active")) {
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
		searchView.setInputType(InputType.TYPE_CLASS_NUMBER);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				// Search for the colony
				try {
					int colonyId = Integer.valueOf(query);

					Colony newSelectedColony = colonies.get(colonyId);
					if(newSelectedColony != null) {
						// Deselect the current selected colony and select the new one
						selection.setSelectedColony(newSelectedColony);

						// Remove the focus from the search field
						searchView.clearFocus();

						// Center the map view on the colony
						mapView.getModel().mapViewPosition.animateTo(
								CoordinateTransformer.getInstance().toGps((float) newSelectedColony.getX(),
										(float) newSelectedColony.getY()));
						return true;
					}
					else {
						// No colony
						new AlertDialog.Builder(MainActivity.this)
								.setTitle("Not found")
								.setMessage("No colony with that number exists")
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setNeutralButton("OK", DIALOG_CLICK_NOOP).show();
						return false;
					}
				} catch (NumberFormatException e) {
					new AlertDialog.Builder(MainActivity.this)
							.setTitle("Invalid query")
							.setMessage("The search query is not a number")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setNeutralButton("OK", DIALOG_CLICK_NOOP).show();
				}
				return false;
			}

		});

		// Edit item
		final MenuItem editItem = menu.findItem(R.id.edit_item);
		editItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				final Colony selectedColony = selection.getSelectedColony();
				if(selectedColony != null) {
					ColonyEditDialogFragment editor = ColonyEditDialogFragment.newInstance(selectedColony);
					editor.show(getFragmentManager(), "editor");
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
				
				if(myLocationItem.isChecked()) {
					myLocationItem.setIcon(R.drawable.ic_menu_my_location_blue);
					locationOverlay.setSnapToLocationEnabled(true);
				}
				else {
					myLocationItem.setIcon(R.drawable.ic_menu_my_location_gray);
					locationOverlay.setSnapToLocationEnabled(false);
				}
				
				return true;
			}
		});
		
		// Check for updates item
		final MenuItem checkForUpdatesItem = menu.findItem(R.id.check_for_updates_item);
		checkForUpdatesItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// Start the update check activity
				final Intent intent = new Intent(MainActivity.this, org.samcrow.updater.UpdateCheckActivity.class);
				intent.setData(Uri.parse("https://dl.dropboxusercontent.com/u/1278290/ColonyNavigator3Update"));
				startActivity(intent);
				
				return true;
			}
		});

		final MenuItem xBeeTestItem = menu.findItem(R.id.test_item);
		xBeeTestItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {

				try {

					Log.d(TAG, "=================== Opening device ==================");
					final D2xxManager manager = D2xxManager.getInstance(MainActivity.this);
					final int deviceCount = manager.createDeviceInfoList(MainActivity.this);
					if (deviceCount > 0) {
						final FT_Device device = manager.openByIndex(MainActivity.this, 0);
						if(device == null) {
							throw new NullPointerException("Device is null");
						}
						device.setBaudRate(9600);

						final XBee xBee = new XBee();
						final AndroidFTDIConnection connection = new AndroidFTDIConnection(device);
						final InputStreamThread inputThread = new InputStreamThread(connection, new XBeeConfiguration());
						Log.d(TAG, "=================== Done opening device ==================");

						try {
							Thread.sleep(500);
							Log.d(TAG, "=================== Opening connection ==================");
							xBee.open(connection);
							Log.d(TAG, "=================== Done opening connection ==================");
							Thread.sleep(500);
						}
						finally {
							Log.d(TAG, "=================== Closing device ==================");
							inputThread.interrupt();
							xBee.close();
							connection.close();
							device.close();
							Log.d(TAG, "=================== Done closing device ==================");
						}
					} else {
						new AlertDialog.Builder(MainActivity.this)
								.setTitle("No devices")
								.setMessage("No FTDI devices are attached")
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setNeutralButton("OK", DIALOG_CLICK_NOOP).show();
					}

				} catch (Throwable e) {
					e.printStackTrace();
					new AlertDialog.Builder(MainActivity.this)
							.setTitle(e.getClass().getSimpleName())
							.setMessage(e.getMessage())
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setNeutralButton("OK", DIALOG_CLICK_NOOP).show();
				}

				return true;
			}
		});

		return true;
	}

	private static final DialogInterface.OnClickListener DIALOG_CLICK_NOOP = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {

		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		// Pause location updates
		locationOverlay.disableMyLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(locationOverlay == null) {
			setUpLocationOverlay();
		}
		// Start location updates
		locationOverlay.enableMyLocation(false);
	}

	private static File getSDCardPath() {
		File dir = new File("/mnt/extSdCard");
		if(dir.exists()) {
			return dir;
		}
		dir = new File("/Removable/MicroSD");
		if(dir.exists()) {
			return dir;
		}
		return Environment.getExternalStorageDirectory();
	}
}
