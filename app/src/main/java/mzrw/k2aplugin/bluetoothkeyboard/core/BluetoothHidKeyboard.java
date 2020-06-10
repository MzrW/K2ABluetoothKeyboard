package mzrw.k2aplugin.bluetoothkeyboard.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
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
    public static final byte USB_KEYBOARD_SUBCLASS = (byte) 1;

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

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        if (profile != BluetoothProfile.HID_DEVICE) {
            Log.w(TAG, "profile is not a HID_DEVICE");
            return;
        }

        hidDevice = (BluetoothHidDevice) proxy;

        BluetoothHidDeviceAppSdpSettings sdp = new BluetoothHidDeviceAppSdpSettings("K2A Keyboard", "K2A Plugin Keyboard over Bluetooth", "K2A", USB_KEYBOARD_SUBCLASS, UsbReports.USB_KEYBOARD_REPORT);
        hidDevice.registerApp(sdp, null, null, executor, callback);
    }

    @Override
    public void onServiceDisconnected(int profile) {
        hidDevice = null;
    }

    private class CallbackImpl extends BluetoothHidDevice.Callback {
        @Override
        public void onConnectionStateChanged(BluetoothDevice device, int state) {
            Log.i(TAG, "Connected state changed to " + state + "for device " + device.getName());

            if (state == BluetoothHidDevice.STATE_CONNECTED) {
                for (byte[] report : UsbReports.stringToKeystrokeReports(layout, "asdf"))
                    hidDevice.sendReport(device, 0, report);
            }
        }
    }
}
