package org.samcrow.xbeenet;

import android.os.Handler;
import android.util.Log;

import com.rapplogic.xbee.api.PacketListener;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress16;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxResponse;
import com.rapplogic.xbee.api.zigbee.ZNetTxRequest;
import com.rapplogic.xbee.api.zigbee.ZNetTxRequest.Option;

import java.io.Closeable;
import java.io.IOException;

/**
 * Provides a high-level interface to a personal area network of XBee devices
 */
public class XBeeNet implements Closeable {
    private static final String TAG = XBeeNet.class.getSimpleName();

    private final XBee xBee;
    /**
     * The handler used to run callbacks on the UI thread
     */
    private final Handler uiThreadHandler;
    private MessageHandler handler;

    public XBeeNet(XBee xBee, final Handler uiThreadHandler) {
        this.xBee = xBee;
        this.uiThreadHandler = uiThreadHandler;

        this.xBee.addPacketListener(new PacketListener() {
            @Override
            public void processResponse(final XBeeResponse frame) {
                if (frame instanceof ZNetRxResponse) {
                    final ZNetRxResponse response = (ZNetRxResponse) frame;
                    // Copy into a byte array
                    // :-(
                    final int[] messageInts = response.getData();
                    final byte[] messageBytes = new byte[messageInts.length];
                    for (int i = 0; i < messageInts.length; i++) {
                        messageBytes[i] = (byte) messageInts[i];
                    }
                    final String message = new String(messageBytes);

                    // Run the callback
                    if (handler != null) {
                        uiThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                handler.messageReceived(message);
                            }
                        });
                    }
                } else {
                    if (handler != null) {
                        uiThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                handler.frameReceived(frame);
                            }
                        });
                    }
                }
            }
        });
    }

    public void setHandler(MessageHandler handler) {
        this.handler = handler;
    }

    /**
     * @return true if this device is connected to the network and able to send and receive
     * messages, otherwise false
     */
    public boolean isConnected() {
        return false;
    }

    /**
     * Asynchronously broadcasts the provided message to all connected devices
     *
     * @param message the message to send
     * @throws NullPointerException if message is null
     */
    public void broadcastMessage(String message) {
        final byte[] messageBytes = message.getBytes();
        final int[] messageInts = new int[messageBytes.length];
        for (int i = 0; i < messageBytes.length; i++) {
            messageInts[i] = messageBytes[i];
        }

        final ZNetTxRequest frame = new ZNetTxRequest(0x17, XBeeAddress64.BROADCAST,
                XBeeAddress16.ZNET_BROADCAST, 0, Option.BROADCAST, messageInts);

        try {
            xBee.sendAsynchronous(frame);
        } catch (XBeeException e) {
            Log.e(TAG, "Failed to send message", e);
        }
    }

    /**
     * Releases the resources used by this network. Closes the XBee.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        xBee.close();
    }
}
