package mzrw.k2aplugin.bluetoothkeyboard.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothHidDeviceAppQosSettings;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import mzrw.k2aplugin.bluetoothkeyboard.layout.Layout;

public class BluetoothHidKeyboard implements BluetoothProfile.ServiceListener {
    private static final String TAG = BluetoothHidKeyboard.class.getName();

    private final Context context;
    private final Layout layout;
    private final BluetoothAdapter bluetoothAdapter;
    private final BluetoothDevice device;

    private final CallbackImpl callback = new CallbackImpl();
    private final Executor executor;
    private BluetoothHidDevice hidDevice;

    public BluetoothHidKeyboard(Context context, Layout layout, BluetoothAdapter bluetoothAdapter, BluetoothDevice device) {
        this.executor = Executors.newSingleThreadExecutor();

        this.context = context;
        this.layout = layout;
        this.bluetoothAdapter = bluetoothAdapter;
        this.device = device;
    }

    public void connect() {
        if (!bluetoothAdapter.getProfileProxy(context, this, BluetoothProfile.HID_DEVICE)) {
            Log.e(TAG, "failed to retrieve HID_DEVICE Bluetooth Proxy");
            // some error
        }
    }

    public void sendString(String string) {
        Log.i(TAG, "sending string "+string);
        for (byte[] report : UsbReports.stringToKeystrokeReports(layout, string))
            if(!hidDevice.sendReport(device, 0x1, report)) {
                Log.w(TAG, "Report is not sent");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        if (profile != BluetoothProfile.HID_DEVICE) {
            Log.w(TAG, "profile is not a HID_DEVICE");
            return;
        }

        Log.i(TAG, "service connected");

        hidDevice = (BluetoothHidDevice) proxy;

        BluetoothHidDeviceAppSdpSettings sdp = new BluetoothHidDeviceAppSdpSettings(
                "K2A Keyboard",
                "K2A Plugin Keyboard over Bluetooth",
                "K2A",
                BluetoothHidDevice.SUBCLASS1_KEYBOARD,
                UsbReports.USB_KEYBOARD_REPORT);
        BluetoothHidDeviceAppQosSettings qos = new BluetoothHidDeviceAppQosSettings(
                BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
                800,
                9,
                0,
                11250,
                BluetoothHidDeviceAppQosSettings.MAX
        );

        hidDevice.registerApp(sdp, null, qos, executor, callback);
    }

    @Override
    public void onServiceDisconnected(int profile) {
        hidDevice = null;
    }

    private class CallbackImpl extends BluetoothHidDevice.Callback {
        @Override
        public void onAppStatusChanged(BluetoothDevice pluggedDevice, boolean registered) {
            Log.i(TAG, "app state changed");
            if(registered) {
                hidDevice.connect(device);
            }
        }

        @Override
        public void onConnectionStateChanged(BluetoothDevice device, int state) {
            Log.i(TAG, "Connected state changed to " + state + "for device " + device.getName());

            if (state == BluetoothHidDevice.STATE_CONNECTED) {
                sendString("asdf");
            }
        }
    }
}
