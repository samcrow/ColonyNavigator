package org.samcrow.xbeenet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.FT_Device;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;

/**
 * A service that handles an XBee connection.
 *
 * Once started, this service runs continuously until an application stops it.
 */
public class XBeeService extends Service {

	/**
	 * An action sent to the service to request the status of the connection and XBee
	 *
	 * A {@link #ACTION_STATUS} intent is returned.
	 */
	private static final String ACTION_CHECK_STATUS = XBeeService.class.getName() + ".CHECK_STATUS";

	private static final String ACTION_BROADCAST_MESSAGE = XBeeService.class.getName() + ".BROADCAST_MESSAGE";

	private static final String ACTION_ON_MESSAGE_RECEIVED = XBeeService.class.getName() + ".ON_MESSAGE_RECEIVED";

	private static final String ACTION_MESSAGE_RESULT = XBeeService.class.getName() + ".MESSAGE_RESULT";

	private static final String ACTION_STATUS = XBeeService.class.getName() + ".STATUS";

	private XBeeNet net;

	public XBeeService() {

	}


	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	private void setUpXBee() throws D2xxException, XBeeException {
		final D2xxManager manager = D2xxManager.getInstance(this);
		final int deviceCount = manager.createDeviceInfoList(this);
		if (deviceCount > 0) {
			final FT_Device device = manager.openByIndex(this, 0);
			if (device == null) {
				throw new NullPointerException("Device is null");
			}
			device.setBaudRate(9600);

			final XBee xBee = new XBee();
			xBee.open(device);

			net = new XBeeNet(xBee, null);
		}
		else {
			throw new IllegalStateException("No devices connected");
		}
	}
}
