package org.samcrow.antchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.FT_Device;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.zigbee.ZNetTxStatusResponse;
import com.rapplogic.xbee.api.zigbee.ZNetTxStatusResponse.DeliveryStatus;

import org.joda.time.DateTime;
import org.samcrow.antchat.Message.Direction;
import org.samcrow.xbeenet.MessageHandler;
import org.samcrow.xbeenet.XBeeNet;

import java.io.IOException;
import java.util.Random;

public class MessagingActivity extends Activity {

	private static final String TAG = MessagingActivity.class.getSimpleName();

	private static final int FTDI_VENDOR_ID = 1027;
	private static final int FTDI_PRODUCT_ID = 24597;

	private MessageModel model;

	private MessageDB db;

	private XBeeNet net;
	private Button sendButton;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messaging);

		sendButton = (Button) findViewById(R.id.send_button);
		sendButton.getBackground().setColorFilter(Color.parseColor("#2196F3"), Mode.MULTIPLY);

		final EditText entryField = (EditText) findViewById(R.id.message_field);

		list = (ListView) findViewById(R.id.message_list);

		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String messageText = entryField.getText().toString();
				if (!messageText.isEmpty()) {
					final Message message = new Message(messageText, DateTime.now(), Direction.Sent);
					model.add(message);
					list.smoothScrollToPosition(Integer.MAX_VALUE);
					entryField.getText().clear();

					db.insertMessage(message);

					if(net != null) {
						net.broadcastMessage(message.getText());
					}
				}
			}
		});

		// Load messages
		db = new MessageDB(this);
		model = new MessageModel(db.getMessages());
		list.setAdapter(model);
		list.smoothScrollToPosition(Integer.MAX_VALUE);

		// Notify on USB device connection/disconnection
		final IntentFilter usbFilter = new IntentFilter();
		usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i(TAG, intent.toString());
				final UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				if(device.getVendorId() == FTDI_VENDOR_ID && device.getProductId() == FTDI_PRODUCT_ID) {
					if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
						connect();
					}
					else {
						disconnect();
					}
				}
			}
		}, usbFilter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		connect();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(net != null) {
			try {
				net.close();
			} catch (IOException e) {
				// Never thrown
			}
		}
	}

	private void disconnect() {
		if(net != null) {
			try {
				net.close();
			} catch (Exception e) {
				// Nothing
			}
			sendButton.setEnabled(false);
			Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT).show();
		}
	}

	private void connect() {
		if(net != null) {
			try {
				net.close();
			} catch (Exception e) {
				// Nothing
			}
		}
		try {
			setUpXBee();



			net.setHandler(new MessageHandler() {
				@Override
				public void messageReceived(String messageText) {
					Message message = new Message(messageText, DateTime.now(), Direction.Received);
					model.add(message);
					list.smoothScrollToPosition(Integer.MAX_VALUE);

					// Notification
					final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

					final Notification notification = new Notification.Builder(MessagingActivity.this)
							.setContentTitle("AntChat message")
							.setContentText(message.getText())
							.setSmallIcon(R.drawable.ic_chat_white_24dp)
							.setAutoCancel(true)
							.setSound(Uri.parse("android.resource://org.samcrow.antchat/raw/notification_vibraphone"))
							.build();
					manager.notify(0, notification);

					db.insertMessage(message);
				}

				@Override
				public void frameReceived(XBeeResponse frame) {
					if (frame instanceof ZNetTxStatusResponse) {
						final ZNetTxStatusResponse response = (ZNetTxStatusResponse) frame;
						final DeliveryStatus status = response.getDeliveryStatus();
						if (status == DeliveryStatus.SUCCESS) {
							Toast.makeText(MessagingActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
						} else {

							showErrorDialog("Sending failed", "The message could not be sent: " + explainDeliveryStatus(status));
						}
					}
				}
			});

			sendButton.setEnabled(true);
			Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		} catch (D2xxException | XBeeException e) {
			Log.e(TAG, "Failed to connect to XBee", e);
			showErrorDialog(e);
			sendButton.setEnabled(false);
		}
		catch (IllegalStateException e) {
			Toast.makeText(this, "Failed to connect", Toast.LENGTH_SHORT).show();
			sendButton.setEnabled(false);
		}
	}

	private String explainDeliveryStatus(DeliveryStatus status) {
		switch(status) {
			case SUCCESS:
				return "Success";
			case MAC_FAILURE:
				return "Internal error: MAC failure";
			case CCA_FAILURE:
				return "CCA failure";
			case INVALID_DESTINATION_ENDPOINT:
				return "Internal error: Invalid destination endpoint";
			case NETWORK_ACK_FAILURE:
				return "The other devices did not respond";
			case NOT_JOINED_TO_NETWORK:
				return "This device is not connected to any network. Please ensure another device" +
						" is within range.";
			case SELF_ADDRESSED:
				return "Internal error: Self-addressed packet";
			case ADDRESS_NOT_FOUND:
				return "Internal error: Broadcast address not found";
			case ROUTE_NOT_FOUND:
				return "Route not found";
			case BROADCAST_SOURCE_NEIGHBOR_FAILURE:
				return "A device could not relay the message";
			case INVALID_BINDING_TABLE_INDEX:
				return "Internal error: Invalid binding table index";
			case RESOURCE_ERROR_LACK_FREE_BUFFERS:
				return "Internal error: Not enough free buffers";
			case ATTEMPTED_BROADCAST_WITH_APS_TX:
				return "Internal error: Attempted broadcast with APS TX";
			case ATTEMPTED_UNICAST_WITH_APS_TX_EE_ZERO:
				return "Internal error: Attempted unicast with APS TX EE zero";
			case RESOURCE_ERROR_LACK_FREE_BUFFERS_0x32:
				return "Internal error: Not enough free buffers";
			case PAYLOAD_TOO_LARGE:
				return "The message is too long. Please try again with a shorter message.";
			case UNKNOWN:
			default: // Intentional fallthrough
				return "Unknown error";

		}
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

			net = new XBeeNet(xBee, new Handler());
		}
		else {
			throw new IllegalStateException("No devices connected");
		}
	}

	private void showErrorDialog(Throwable t) {
		showErrorDialog(t.getClass().getSimpleName(), t.getMessage());
	}

	private void showErrorDialog(String title, String message) {
		new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(message)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_messaging, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_connect) {
			connect();
			return true;
		}
		else if(id == R.id.action_delete_messages) {
			new Builder(this)
					.setTitle("Delete messages")
					.setMessage("Are you sure you want to delete all messages?")
					.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Nothing
						}
					})
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							model.clear();
							db.deleteAllMessages();
						}
					})
					.show();
		}

		return super.onOptionsItemSelected(item);
	}
}
