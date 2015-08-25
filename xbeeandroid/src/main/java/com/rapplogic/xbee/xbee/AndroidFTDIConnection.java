package com.rapplogic.xbee.xbee;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.D2xxManager.FtDeviceInfoListNode;
import com.ftdi.j2xx.FT_Device;
import com.rapplogic.xbee.xbee.ftdi.FTDIInputStream;
import com.rapplogic.xbee.xbee.ftdi.FTDIOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A connection to an FTDI USB device for Android
 */
public class AndroidFTDIConnection implements XBeeConnection {

    private final FT_Device device;

    private final OutputStream out;
    private final InputStream in;

    private final NotificationThread notifier;

    public AndroidFTDIConnection(FT_Device device) {
        this.device = device;

        in = new FTDIInputStream(device);
        out = new FTDIOutputStream(device);

        // Notification thread
        notifier = new NotificationThread();
        notifier.start();
    }

    @Override
    public OutputStream getOutputStream() {
        return out;
    }

    @Override
    public InputStream getInputStream() {
        return in;
    }

    @Override
    public void close() throws IOException {
        device.close();
        notifier.interrupt();
        try {
            notifier.join();
        } catch (InterruptedException e) {
            throw new IOException("Interrupted while waiting for notifier thread to stop", e);
        }
    }

    private class NotificationThread extends Thread {
        /**
         * Number of milliseconds to wait in between checks
         */
        private final int DELAY_MS = 100;

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    sleep(DELAY_MS);
                    boolean available;
                    synchronized (AndroidFTDIConnection.this.device) {
                        available = AndroidFTDIConnection.this.device.getQueueStatus() > 0;
                    }
                    if(available) {
                        try {
                            AndroidFTDIConnection.this.notify();
                        }
                        catch (IllegalMonitorStateException e) {

                        }
                    }
                }
            }
            catch (InterruptedException e) {
                // return
            }
        }
    }
}
