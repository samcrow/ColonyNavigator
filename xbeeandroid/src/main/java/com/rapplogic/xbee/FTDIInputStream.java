package com.rapplogic.xbee;

import android.util.Log;

import com.ftdi.j2xx.FT_Device;

import java.io.IOException;
import java.io.InputStream;

/**
 * An InputStream that reads bytes from an FTDI device
 */
public class FTDIInputStream extends InputStream {

    private static final String TAG = FTDIInputStream.class.getSimpleName();

    /**
     * The device
     */
    private final FT_Device device;

    public FTDIInputStream(FT_Device device) {
        this.device = device;
    }

    @Override
    public int available() throws IOException {
        if (device.isOpen()) {
            final int count = device.getQueueStatus();
            Log.i(TAG, "Available bytes: " + count);
            Log.v(TAG, "About to unlock device");
            return count;
        } else {
            throw new IOException("Device closed");
        }
    }

    @Override
    public void close() throws IOException {
        if (device.isOpen()) {
            device.close();
        }
    }

    @Override
    public int read() throws IOException {
        final byte[] bytes = new byte[1];
        final int readCount = device.read(bytes);
        Log.i(TAG, "Read a byte");
        if (readCount != 1) {
            throw new IOException("Could not read a byte");
        }
        return bytes[0];
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        Log.i(TAG, "Reading bytes");
        return device.read(buffer);
    }
}
