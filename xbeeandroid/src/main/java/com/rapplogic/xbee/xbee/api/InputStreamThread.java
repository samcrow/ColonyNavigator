/**
 * Copyright (c) 2008 Andrew Rapp. All rights reserved.
 *  
 * This file is part of XBee-API.
 *  
 * XBee-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * XBee-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with XBee-API.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.rapplogic.xbee.xbee.api;

import android.util.Log;

import com.rapplogic.xbee.xbee.XBeeConnection;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Reads data from the input stream and hands off to PacketParser for packet parsing.
 * Notifies XBee class when a new packet is parsed
 * <p/>
 * @author andrew
 *
 */
public class InputStreamThread implements Runnable {

	private static final String TAG = InputStreamThread.class.getSimpleName();
	
	private Thread thread;
	private ExecutorService listenerPool;
	private volatile boolean done = false;
	private final XBeeConnection connection;
	private XBeeConfiguration conf;
	
	public XBeeConnection getXBeeConnection() {
		return connection;
	}

	private final BlockingQueue<XBeeResponse> responseQueue = new LinkedBlockingQueue<XBeeResponse>();
	
	// TODO use weak references
	private final List<PacketListener> packetListenerList = new LinkedList<PacketListener>();
	
	public List<PacketListener> getPacketListenerList() {
		return packetListenerList;
	}

	public BlockingQueue<XBeeResponse> getResponseQueue() {
		return responseQueue;
	}

	public InputStreamThread(final XBeeConnection connection, XBeeConfiguration conf) {
		this.connection = connection;
		this.conf = conf;
		
        // Create an executor to deliver incoming packets to listeners. We'll use a single
        // thread with an unbounded queue.
		listenerPool = Executors.newSingleThreadExecutor();
		
		thread = new Thread(this);
		thread.setName("InputStreamThread");
		thread.start();
	}
	
	private void addResponse(final XBeeResponse response) throws InterruptedException {
		
		if (conf.getResponseQueueFilter() != null) {
			if (conf.getResponseQueueFilter().accept(response)) {
				this.addToResponseQueue(response);
			}
		} else {
			this.addToResponseQueue(response);
		}
		
		listenerPool.submit(new Runnable() {
			public void run() {
				// must synchronize to avoid  java.util.ConcurrentModificationException at java.util.AbstractList$Itr.checkForComodification(Unknown Source)
				// this occurs if packet listener add/remove is called while we are iterating
				
				synchronized (packetListenerList) {
					for (PacketListener pl : packetListenerList) {
						try {
							if (pl != null) {
								pl.processResponse(response);
							}
						} catch (Throwable th) {
						}
					}			
				}				
			}
		});
	}
	
	private void addToResponseQueue(final XBeeResponse response) throws InterruptedException{
		
		if (conf.getMaxQueueSize() == 0) {
			// warn
			return;
		}
		// trim the queue
		while (responseQueue.size() >= conf.getMaxQueueSize()) {
			responseQueue.poll();
		}
		
		responseQueue.put(response);
	}
	
	public void run() {

		int val = -1;
		
		XBeeResponse response = null;
		PacketParser packetStream = null;

		try {
			while (!done) {
				try {
					if (connection.getInputStream().available() > 0) {
						val = connection.getInputStream().read();
						Log.d(TAG, "Bytes available. Read "+val);
						
						if (val == XBeePacket.SpecialByte.START_BYTE.getValue()) {
							packetStream = new PacketParser(connection.getInputStream());
							response = packetStream.parsePacket();
							
							// success
							this.addResponse(response);
						}
					} else {
						Log.d(TAG, "No bytes available");
						// we will wait here for RXTX to notify us of new data
						synchronized (this.connection) {
							// There's a chance that we got notified after the first in.available check
							if (connection.getInputStream().available() > 0) {
								continue;
							}

							Log.d(TAG, "Waiting on connection " + System.identityHashCode(this.connection));
							// wait until new data arrives
							try {
								this.connection.wait();
							} catch (InterruptedException e) {
								Log.e(TAG, "Interrupted!");
							}
							Log.d(TAG, "Done waiting on connection");
						}
					}				
				} catch (Exception e) {
					Log.w(TAG, "General exception", e);
					if (e instanceof InterruptedException) throw ((InterruptedException)e);
					
					if (e instanceof IOException) {
						// this is thrown by RXTX if the serial device unplugged while we are reading data; if we are waiting then it will waiting forever
						break;
					}
				}
			}
		} catch(InterruptedException ie) {
			// We've been told to stop -- the user called the close() method
		} catch (Throwable t) {
		} finally {
			Log.w(TAG, "Thread exiting");
			try {
				if (connection != null) {
					connection.close();
				}
				
				if (listenerPool != null) {
					try {
						listenerPool.shutdownNow();
					} catch (Throwable t) {
					}
				}				
			} catch (Throwable t) {
			}
		}
	}

	public void setDone(boolean done) {
		this.done = done;
	}
	
	public void interrupt() {
		if (thread != null) {
			try {
				thread.interrupt();	
			} catch (Exception e) {
			}
		}
	}
}