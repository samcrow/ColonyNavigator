package org.samcrow.xbeetest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.rapplogic.xbee.api.XBee;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		testXBee();
	}

	private void testXBee() {

		try {

			Log.d(TAG, "=================== Opening device ==================");
			final D2xxManager manager = D2xxManager.getInstance(MainActivity.this);
			final int deviceCount = manager.createDeviceInfoList(MainActivity.this);
			if (deviceCount > 0) {
				final FT_Device device = manager.openByIndex(MainActivity.this, 0);
				if(device == null) {
					throw new NullPointerException("Device is null");
				}
				device.setBaudRate(9600);

				final XBee xBee = new XBee();
				Log.d(TAG, "=================== Done opening device ==================");

				try {
					Thread.sleep(500);
					Log.d(TAG, "=================== Opening connection ==================");
					xBee.open(device);
					Log.d(TAG, "=================== Done opening connection ==================");
					Thread.sleep(500);
				}
				finally {
					Log.d(TAG, "=================== Closing device ==================");
					xBee.close();
					device.close();
					Log.d(TAG, "=================== Done closing device ==================");
				}
			} else {
				new AlertDialog.Builder(MainActivity.this)
						.setTitle("No devices")
						.setMessage("No FTDI devices are attached")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setNeutralButton("OK", DIALOG_CLICK_NOOP).show();
			}

		} catch (Throwable e) {
			e.printStackTrace();
			new AlertDialog.Builder(MainActivity.this)
					.setTitle(e.getClass().getSimpleName())
					.setMessage(e.getMessage())
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setNeutralButton("OK", DIALOG_CLICK_NOOP).show();
		}

	}

	private static final OnClickListener DIALOG_CLICK_NOOP = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
