package org.samcrow.xbeenet;

/**
 * Stores the status of the device's connection to the network
 */
public class Status {

    /**
     * True if the XBee is connected to the device, otherwise false
     */
    private final boolean xBeeConnected;
    /**
     * True if the XBee is connected to a network with other devices
     */
    private final boolean connectedToNetwork;

    public Status(boolean xBeeConnected, boolean connectedToNetwork) {
        this.xBeeConnected = xBeeConnected;
        this.connectedToNetwork = connectedToNetwork;
    }

    public boolean isxBeeConnected() {
        return xBeeConnected;
    }

    public boolean isConnectedToNetwork() {
        return connectedToNetwork;
    }
}
