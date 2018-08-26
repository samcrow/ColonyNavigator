package org.samcrow.differentialgps;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import org.joda.time.LocalTime;
import java.util.UUID;

/**
 * A connected BLE device with services
 */
class BleConnection {

    private static final UUID FIX_STATUS_UUID = expandUuid((short) 0x0010);
    private static final UUID POSITION_UUID = expandUuid((short) 0x0011);
    private static final UUID TIME_UUID = expandUuid((short) 0x0100);

    /**
     * State values
     */
    public enum State {
        Idle,
        ReadingFixStatus,
        ReadingPosition,
        ReadingTime,
    }

    /**
     * The device
     */
    final BluetoothGatt mGatt;

    /**
     * The fix status characteristic
     */
    final BluetoothGattCharacteristic mFixStatus;
    /**
     * The position characteristic
     */
    final BluetoothGattCharacteristic mPosition;
    /**
     * The time characteristic
     */
    final BluetoothGattCharacteristic mTime;

    /**
     * The current state
     */
    State mState;

    /**
     * Creates a connection from a BluetoothGatt object
     * @param gatt A non-null GATT object with all required services discovered
     *
     * @throws IllegalStateException if an expected characteristic has not been discovered
     */
    public BleConnection(BluetoothGatt gatt) {
        if (gatt == null) {
            throw new NullPointerException("gatt must not be null");
        }
        mGatt = gatt;

        // Look for characteristics

        BluetoothGattCharacteristic fixStatus = null;
        BluetoothGattCharacteristic position = null;
        BluetoothGattCharacteristic time = null;

        for (BluetoothGattService service : gatt.getServices()) {
            if (service.getUuid().equals(BleConnector.SERVICE_UUID)) {
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    if (characteristic.getUuid().equals(FIX_STATUS_UUID)) {
                        fixStatus = characteristic;
                    } else if (characteristic.getUuid().equals(POSITION_UUID)) {
                        position = characteristic;
                    } else if (characteristic.getUuid().equals(TIME_UUID)) {
                        time = characteristic;
                    }
                }
                break;
            }
        }

        if (fixStatus == null) {
            throw new IllegalStateException("No fix status characteristic found");
        }
        if (position == null) {
            throw new IllegalStateException("No position characteristic found");
        }
        if (time == null) {
            throw new IllegalStateException("No time characteristic found");
        }
        mFixStatus = fixStatus;
        mPosition = position;
        mTime = time;

        mState = State.Idle;
    }

    public boolean readFixStatus() {
        if (mState != State.Idle) {
            throw new IllegalStateException("Can't read characteristics when not idle");
        }
        mState = State.ReadingFixStatus;
        return mGatt.readCharacteristic(mFixStatus);
    }

    public boolean readPosition() {
        if (mState != State.Idle) {
            throw new IllegalStateException("Can't read characteristics when not idle");
        }
        mState = State.ReadingPosition;
        return mGatt.readCharacteristic(mPosition);
    }

    public boolean readTime() {
        if (mState != State.Idle) {
            throw new IllegalStateException("Can't read characteristics when not idle");
        }
        mState = State.ReadingTime;
        return mGatt.readCharacteristic(mTime);
    }


    public FixStatus getFixStatus() {
        final byte[] value = mFixStatus.getValue();
        if (value == null) {
            throw new IllegalStateException("No value available");
        }
        // Convert to string
        final String valueString = new String(value);
        if (valueString.equals("no_fix")) {
            return FixStatus.NO_FIX;
        } else {
            // Try to parse
            final float hdp = Float.parseFloat(valueString);
            return FixStatus.withHdp(hdp);
        }
    }

    public double getLatitude() {
        return Double.parseDouble(getPositionParts()[0]);
    }
    public double getLongitude() {
        return Double.parseDouble(getPositionParts()[1]);
    }

    public LocalTime getUtcTime() {
        final String value = mTime.getStringValue(0);
        if (value == null) {
            throw new IllegalStateException("No value available");
        }
        final String[] parts = value.split(":");
        if (parts.length != 3) {
            throw new IllegalStateException("Invalid time format");
        }
        final int hours = Integer.parseInt(parts[0]);
        final int minutes = Integer.parseInt(parts[1]);
        final float seconds = Float.parseFloat(parts[2]);
        final int wholeSeconds = (int) seconds;
        final int milliseconds = (int)((seconds % 1.0f) * 1000);

        return new LocalTime(hours, minutes, wholeSeconds, milliseconds);
    }

    private String[] getPositionParts() {
        final String value = mPosition.getStringValue(0);
        if (value == null) {
            throw new IllegalStateException("No value available");
        }
        final String[] parts = value.split(",");
        if (parts.length != 2) {
            throw new IllegalStateException("Invalid position format");
        }
        return parts;
    }

    private static UUID expandUuid(short shortUuid) {
        final long highBits = 0x0000000000001000L;
        final long lowBits = 0x800000805f9b34fbL;

        final long expandedShortUuid = ((long) shortUuid) & 0xffffL;
        return new UUID(highBits | (expandedShortUuid << 32), lowBits);
    }
}
