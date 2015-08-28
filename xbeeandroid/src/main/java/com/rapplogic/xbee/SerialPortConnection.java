/**
 * Copyright (c) 2008 Andrew Rapp. All rights reserved.
 * <p/>
 * This file is part of XBee-API.
 * <p/>
 * XBee-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * XBee-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with XBee-API.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.rapplogic.xbee;

import com.ftdi.j2xx.FT_Device;

import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class encapsulates a serial port, providing access to input/output streams,
 * and notifying the subclass of new data events via the handleSerialData method.
 *
 * @author andrew
 */
public class SerialPortConnection implements XBeeConnection {

	private final static Logger log = Logger.getLogger(SerialPortConnection.class);

	private InputStream inputStream;
	private OutputStream outputStream;

	private FT_Device serialPort;

	public SerialPortConnection() {

	}

	public void openSerialPort(FT_Device device) {
		serialPort = device;

		inputStream = new FTDIInputStream(serialPort);
		outputStream = new BufferedOutputStream(new FTDIOutputStream(serialPort));
	}

	/**
	 * Shuts down RXTX
	 */
	@Override
	public void close() throws IOException {
		serialPort.close();
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void serialEvent() {

		try {
			if (this.getInputStream().available() > 0) {
				try {
					log.debug("serialEvent: " + serialPort.getQueueStatus() + " bytes available");

					synchronized (this) {
						this.notify();
					}
				} catch (Exception e) {
					log.error("Error in handleSerialData method", e);
				}
			} else {
				log.warn("We were notified of new data but available() is returning 0");
			}
		} catch (IOException ex) {
			// it's best not to throw the exception because the RXTX thread may not be prepared to handle
			log.error("RXTX error in serialEvent method", ex);
		}
	}
}