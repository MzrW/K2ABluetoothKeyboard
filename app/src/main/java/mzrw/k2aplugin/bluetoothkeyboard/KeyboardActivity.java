package mzrw.k2aplugin.bluetoothkeyboard;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import mzrw.k2aplugin.bluetoothkeyboard.core.AuthorizedDevicesManager;
import mzrw.k2aplugin.bluetoothkeyboard.core.BluetoothHidKeyboard;
import mzrw.k2aplugin.bluetoothkeyboard.layout.KeyboardLayoutFactory;
import mzrw.k2aplugin.bluetoothkeyboard.layout.Layout;

public class KeyboardActivity extends AbstractBluetoothActivity {
    private static final String TAG = KeyboardActivity.class.getName();
    public static final String INTENT_EXTRA_STRING_TO_TYPE = "intent_extra_string_to_type";

    private AuthorizedDevicesManager authorizedDevicesManager;
    private BluetoothHidKeyboard hidKeyboard;

    private BluetoothDevice selectedDevice;
    private Layout selectedLayout;
    private List<BluetoothDevice> devices;

    private Spinner deviceSpinner;
    private Spinner layoutSpinner;
    private Button btnConnect;

    public static void startActivityToSendText(Context context, String text) {
        final boolean isActivityContext = context instanceof Activity;
        if(!isActivityContext)
            context = context.getApplicationContext();

        final Intent intent = new Intent(context, KeyboardActivity.class);
        intent.putExtra(INTENT_EXTRA_STRING_TO_TYPE, text);
        if(!isActivityContext)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        authorizedDevicesManager = new AuthorizedDevicesManager(this);

        deviceSpinner = findViewById(R.id.deviceSpinner);
        layoutSpinner = findViewById(R.id.layoutSpinner);
        btnConnect = findViewById(R.id.btnConnect);

        checkBluetoothEnabled();
        registerListeners();
    }

    private void registerListeners() {
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = devices.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        layoutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String layoutName = (String) parent.getSelectedItem();
                try {
                    selectedLayout = KeyboardLayoutFactory.getLayout(layoutName);
                } catch (Exception e) {
                    Log.e(TAG, "error selecting layout", e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnConnect.setOnClickListener(this::connectToDevice);
    }

    @Override
    protected void onBluetoothEnabled() {
        devices = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        for (BluetoothDevice device : authorizedDevicesManager.filterAuthorizedDevices(bluetoothAdapter.getBondedDevices())) {
            devices.add(device);
            names.add(device.getName());
        }

        final SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        deviceSpinner.setAdapter(adapter);
    }

    public void connectToDevice(View v) {
        Log.i(TAG, "connecting to "+selectedDevice.getName()+" using keyboard "+selectedLayout.getClass().getSimpleName());
        hidKeyboard = new BluetoothHidKeyboard(getApplicationContext(), selectedLayout, bluetoothAdapter, selectedDevice);
        hidKeyboard.connect();
    }
}
