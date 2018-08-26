package org.samcrow.differentialgps;

import android.bluetooth.*;
import android.content.Context;
import android.util.Log;

import java.util.*;

/**
 * Scans for base stations, connects to them, and gets location information
 */
class BleConnector {

    interface BaseStationCallback {
        /**
         * Called when a position report is received from a base station
         * @param station the base station with its known fixed position
         * @param latitude the measured base station latitude
         * @param longitude the measured base station longitude
         * @param hdp the horizontal dilution of precision of the position (lower is better)
         */
        void baseStationPositionUpdated(Station station, double latitude, double longitude, float hdp);
    }

    private static final String TAG = BleConnector.class.getSimpleName();

    /**
     * The UUID of the differential GPS service
     */
    static final UUID SERVICE_UUID = UUID.fromString("6f3c12bf-1ec0-40a6-9d1a-000b8848dae6");

    /**
     * Interval between update requests
     */
    private static final int UPDATE_INTERVAL = 1000;

    /**
     * The context
     */
    private final Context mContext;

    /**
     * Timer used to schedule update requests
     */
    private Timer mTimer;

    /**
     * The Bluetooth adapter
     */
    private final BluetoothAdapter mAdapter;

    /**
     * The known stations (not null, contains no null elements)
     */
    private final Station[] mStations;

    /**
     * Base stations that the device is connecting to
     */
    private final Map<Station, BluetoothDevice> mConnectingDevices;

    /**
     * Connected base stations that the device is discovering services for
     */
    private final Map<Station, BluetoothGatt> mDiscoveringServicesDevices;

    /**
     * Connected base stations
     */
    private final Map<Station, BleConnection> mConnections;

    /**
     * GATT callback object used for all connections
     */
    private final GattCallback mGattCallback = new GattCallback();

    /**
     * Scan callback used for scanning
     */
    private final ScanCallback mScanCallback = new ScanCallback();

    /**
     * If currently scanning
     */
    private boolean mScanning;

    /**
     * The callback to be notified when a base station position is received
     */
    private BaseStationCallback mBaseStationCallback;

    BleConnector(Context context, Station[] stations) {
        if (context == null) {
            throw new NullPointerException("context must not be null");
        }
        if (stations == null) {
            throw new NullPointerException("stations must not be null");
        }
        mContext = context;
        mStations = stations;

        mConnectingDevices = new HashMap<>(stations.length);
        mDiscoveringServicesDevices = new HashMap<>(stations.length);
        mConnections = new HashMap<>(stations.length);

        mScanning = false;

        // Get Bluetooth things
        final BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager == null) {
            throw new IllegalStateException("Bluetooth manager not available");
        }
        mAdapter = manager.getAdapter();
        if (mAdapter == null) {
            throw new IllegalStateException("No Bluetooth adapter");
        }
    }

    public void setBaseStationCallback(BaseStationCallback callback) {
        mBaseStationCallback = callback;
    }

    public void start() {
        if (!mScanning) {
            Log.i(TAG, "Starting scan");
            mScanning = true;
            mAdapter.startLeScan(new UUID[] { SERVICE_UUID }, mScanCallback);
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new ReadTask(), UPDATE_INTERVAL, UPDATE_INTERVAL);
        }
    }

    public void pause() {
        if (mScanning) {
            Log.i(TAG, "Stopping scan");
            mScanning = false;
            mAdapter.stopLeScan(mScanCallback);
            mTimer.cancel();
            mTimer = null;
        }
    }

    private class ScanCallback implements BluetoothAdapter.LeScanCallback {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            synchronized (BleConnector.this) {
                // Look for the base station that matches this device
                final Station station = findStationWithAddress(device.getAddress());
                if (station != null) {
                    handleBaseStationScan(station, device);
                }
            }
        }

        private void handleBaseStationScan(Station station, BluetoothDevice device) {
            // If this station is not connecting, discovering services,
            // or connected, start connecting to it
            if (!mConnectingDevices.containsKey(station)
                    && !mDiscoveringServicesDevices.containsKey(station)
                    && !mConnections.containsKey(station)) {
                device.connectGatt(mContext, true, mGattCallback);
                Log.i(TAG, "Connecting to base station " + station);
                mConnectingDevices.put(station, device);
            }
        }
    }

    private class GattCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            synchronized (BleConnector.this) {
                final Station station = findStationWithAddress(gatt.getDevice().getAddress());
                if (station != null) {
                    if (mConnectingDevices.containsKey(station)) {
                        onConnectingDeviceStateChange(station, gatt, status, newState);
                    } else if (mDiscoveringServicesDevices.containsKey(station)) {
                        onDiscoveringDeviceStateChange(station, gatt, status, newState);
                    } else if (mConnections.containsKey(station)) {
                        onConnectedDeviceStateChange(station, gatt, status, newState);
                    }
                }
            }
        }

        private void onConnectingDeviceStateChange(Station station, BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTING:
                    Log.i(TAG, station + " connecting -> connecting");
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i(TAG, station + " connecting -> connected");
                    mConnectingDevices.remove(station);
                    // Start discovering services
                    final boolean discoverStatus = gatt.discoverServices();
                    if (discoverStatus) {
                        mDiscoveringServicesDevices.put(station, gatt);
                    } else {
                        Log.w(TAG, station + ": Failed to start service discovery");
                    }

                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    Log.i(TAG, station + " connecting -> disconnecting");
                    mConnectingDevices.remove(station);
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.i(TAG, station + " connecting -> disconnected");
                    mConnectingDevices.remove(station);
                    break;
            }
        }

        private void onDiscoveringDeviceStateChange(Station station, BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTING:
                    Log.w(TAG, station + " discovering -> connecting");
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    Log.w(TAG, station + " discovering -> connected");
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    Log.i(TAG, station + " discovering -> disconnecting");
                    mDiscoveringServicesDevices.remove(station);
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.i(TAG, station + " discovering -> disconnected");
                    mDiscoveringServicesDevices.remove(station);
                    break;
            }

        }

        private void onConnectedDeviceStateChange(Station station, BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTING:
                    Log.w(TAG, station + " connected -> connecting");
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    Log.w(TAG, station + " connected -> connected");
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    Log.i(TAG, station + " connected -> disconnecting");
                    mConnections.remove(station);
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.i(TAG, station + " connected -> disconnected");
                    mConnections.remove(station);
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            synchronized (BleConnector.this) {
                final Station station = findStationWithAddress(gatt.getDevice().getAddress());
                if (station != null) {
                    mDiscoveringServicesDevices.remove(station);
                    try {
                        final BleConnection connection = new BleConnection(gatt);
                        Log.i(TAG, station + " discovering -> fully connected");
                        mConnections.put(station, connection);
                    } catch (IllegalStateException e) {
                        Log.w(TAG, station + ": Not all required characteristics were found: " + e.getLocalizedMessage());
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            synchronized (BleConnector.this) {
                final Station station = findStationWithAddress(gatt.getDevice().getAddress());
                final BleConnection connection = mConnections.get(station);
                connection.mState = BleConnection.State.Idle;
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (characteristic.equals(connection.mFixStatus)) {
                        final FixStatus fixStatus = connection.getFixStatus();
                        Log.i(TAG, "Read fix status " + fixStatus);

                        // If a fix exists, request it
                        if (!fixStatus.equals(FixStatus.NO_FIX)) {
                            connection.readPosition();
                        }
                    } else if (characteristic.equals(connection.mPosition)) {
                        // Read position
                        final FixStatus fixStatus = connection.getFixStatus();
                        final double latitude = connection.getLatitude();
                        final double longitude = connection.getLongitude();

                        Log.i(TAG, "Got position " + latitude + ", " + longitude);

                        if (mBaseStationCallback != null) {
                            mBaseStationCallback.baseStationPositionUpdated(station, latitude, longitude, fixStatus.getHdp());
                        }
                    }
                } else {
                    Log.w(TAG, "Characteristic read failed");
                }
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            synchronized (BleConnector.this) {

            }
        }
    }

    /**
     * A task that periodically requests information from each connected base station
     */
    private class ReadTask extends TimerTask {

        @Override
        public void run() {
            synchronized (BleConnector.this) {
                for (BleConnection connection : mConnections.values()) {
                    if (connection.mState.equals(BleConnection.State.Idle)) {
                        Log.i(TAG, "Reading fix status");
                        connection.readFixStatus();
                    }
                }
            }
        }
    }

    /**
     * Returns the first station with the provided address, or null if no matching station exists
     */
    private Station findStationWithAddress(String address) {
        for (Station station : mStations) {
            if (station.getAddress().equals(address)) {
                return station;
            }
        }
        return null;
    }
}
