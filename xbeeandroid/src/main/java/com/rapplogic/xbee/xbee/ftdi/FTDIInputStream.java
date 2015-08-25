package com.rapplogic.xbee.xbee.ftdi;

import com.ftdi.j2xx.FT_Device;

import java.io.IOException;
import java.io.InputStream;

/**
 * An InputStream that reads bytes from an FTDI device
 */
public class FTDIInputStream extends InputStream {

    /**
     * The device
     */
    private final FT_Device device;

    public FTDIInputStream(FT_Device device) {
        this.device = device;
    }

    @Override
    public int available() throws IOException {
        synchronized (device) {
            if (device.isOpen()) {
                return device.getQueueStatus();
            } else {
                throw new IOException("Device closed");
            }
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (device) {
            if (device.isOpen()) {
                device.close();
            }
        }
    }

    @Override
    public int read() throws IOException {
        final byte[] bytes = new byte[1];
        synchronized (device) {
            final int readCount = device.read(bytes);
            if (readCount != 1) {
                throw new IOException("Could not read a byte");
            }
        }
        return bytes[0];
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        synchronized (device) {
            return device.read(buffer);
        }
    }
}
