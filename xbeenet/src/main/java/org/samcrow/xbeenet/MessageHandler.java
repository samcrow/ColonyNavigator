package org.samcrow.xbeenet;

import com.rapplogic.xbee.api.XBeeResponse;

/**
 * An interface for something that can receive messages from other devices
 */
public interface MessageHandler {
	/**
	 * Called when a message is received
	 * @param message the message
	 */
	void messageReceived(String message);

	void frameReceived(XBeeResponse frame);
}
