package mzrw.k2aplugin.bluetoothkeyboard;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mzrw.k2aplugin.bluetoothkeyboard.core.AuthorizedDevicesManager;
import mzrw.k2aplugin.bluetoothkeyboard.core.HidService;
import mzrw.k2aplugin.bluetoothkeyboard.layout.KeyboardLayoutFactory;
import mzrw.k2aplugin.bluetoothkeyboard.layout.Layout;

public class KeyboardActivity extends AbstractBluetoothActivity implements HidService.StateChangeListener {
    private static final String TAG = KeyboardActivity.class.getName();
    public static final String INTENT_EXTRA_STRING_TO_TYPE = "intent_extra_string_to_type";

    private AuthorizedDevicesManager authorizedDevicesManager;
    private HidService hidService;

    private BluetoothDevice selectedDevice;
    private Layout selectedLayout;
    private List<BluetoothDevice> devices;

    private Spinner deviceSpinner;
    private Spinner layoutSpinner;
    private Button btnConnect;
    private TextView txtState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        authorizedDevicesManager = new AuthorizedDevicesManager(this);
        hidService = new HidService(getApplicationContext(), bluetoothAdapter);

        deviceSpinner = findViewById(R.id.deviceSpinner);
        layoutSpinner = findViewById(R.id.layoutSpinner);
        btnConnect = findViewById(R.id.btnConnect);
        txtState = findViewById(R.id.txtState);

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
        hidService.setOnStateChangeListener(this);
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
        Log.i(TAG, "connecting to " + selectedDevice.getName() + " using keyboard " + selectedLayout.getClass().getSimpleName());
        hidService.connect(selectedDevice, selectedLayout);
    }

    @Override
    public void onStateChanged(int state) {
        switch (state) {
            case HidService.STATE_DISCONNECTED:
                finishAndRemoveTask();
                break;
            case HidService.STATE_CONNECTING:
                txtState.setText("Connecting");
                break;
            case HidService.STATE_CONNECTED:
                txtState.setText("Connected");
                hidService.sendText(getIntent().getStringExtra(INTENT_EXTRA_STRING_TO_TYPE));
                break;
            case HidService.STATE_SENDING:
                txtState.setText("Sending");
                break;
            case HidService.STATE_SENT:
                txtState.setText("Sent");
                hidService.disconnect();
                break;
            case HidService.STATE_DISCONNECTING:
                txtState.setText("Disconnecting");
                break;
        }
    }
}
