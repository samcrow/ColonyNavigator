package com.rapplogic.xbee;

import com.ftdi.j2xx.FT_Device;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An OutputStream that writes bytes to an FTDI device
 */
public class FTDIOutputStream extends OutputStream {

    /**
     * The device
     */
    private final FT_Device device;

    /**
     * Creates an output stream
     * @param device the device to wrap
     */
    public FTDIOutputStream(FT_Device device) {
        this.device = device;
    }
    @Override
    public void write(int oneByte) throws IOException {
        if (!device.isOpen()) throw new AssertionError("Device not open");
        synchronized (device) {
            device.write(new byte[]{(byte) oneByte});
        }
    }

    /**
     * Closes the connection to the FTDI device
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        synchronized (device) {
            if (device.isOpen()) {
                device.close();
            }
        }
    }

    @Override
    public void write(byte[] buffer) throws IOException {
        if (!device.isOpen()) throw new AssertionError("Device not open");
        synchronized (device) {
            device.write(buffer);
        }
    }
}
