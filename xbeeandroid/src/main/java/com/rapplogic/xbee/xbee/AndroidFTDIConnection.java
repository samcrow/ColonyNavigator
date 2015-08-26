package com.rapplogic.xbee.xbee;

import android.util.Log;

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

    private static final String TAG = AndroidFTDIConnection.class.getSimpleName();

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
            Log.d(TAG, "Joining notifier thread");
            notifier.join();
            Log.d(TAG, "Done joining");
        } catch (InterruptedException e) {
            throw new IOException("Interrupted while waiting for notifier thread to stop", e);
        }
    }

    private class NotificationThread extends Thread {
        private final String TAG = NotificationThread.class.getSimpleName();
        /**
         * Number of milliseconds to wait in between checks
         */
        private final int DELAY_MS = 100;

        public NotificationThread() {
            super("NotificationThread");
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
//              Log.d(TAG, "Sleeping");
//              sleep(DELAY_MS);
//              Log.d(TAG, "Done sleeping, locking device...");
                boolean available;
                available = AndroidFTDIConnection.this.device.getQueueStatus() > 0;

//                Log.d(TAG, "Done locking device");
                if (available) {
                    try {
//                        Log.d(TAG, "Notifying " + System.identityHashCode(AndroidFTDIConnection.this));
                        AndroidFTDIConnection.this.notify();
                    } catch (IllegalMonitorStateException e) {
//                        Log.w(TAG, "Nothing waiting");
                    }
                }
            }
        }
    }
}
