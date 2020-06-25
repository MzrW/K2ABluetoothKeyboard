package mzrw.k2aplugin.bluetoothkeyboard;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import mzrw.k2aplugin.bluetoothkeyboard.core.AuthorizedDevicesManager;
import mzrw.k2aplugin.bluetoothkeyboard.core.HidService;
import mzrw.k2aplugin.bluetoothkeyboard.layout.KeyboardLayoutFactory;

public class KeyboardActivity extends AbstractBluetoothActivity implements HidService.StateChangeListener {
    private static final String TAG = KeyboardActivity.class.getName();
    public static final String INTENT_EXTRA_STRING_TO_TYPE = "intent_extra_string_to_type";
    public static final String BUNDLE_KEY_SELECTED_DEVICE = "bundle_key_selected_device";
    public static final String BUNDLE_KEY_SELECTED_LAYOUT = "bundle_key_selected_layout";

    private HidService hidService;

    private SharedPreferences selectedEntriesPreferences;
    private String selectedDevice;
    private String selectedLayout;
    private List<BluetoothDevice> devices;

    private Spinner deviceSpinner;
    private Spinner layoutSpinner;
    private Button btnConnect;
    private Button btnNoDevicesAuthorized;
    private TextView txtState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        hidService = new HidService(getApplicationContext(), bluetoothAdapter);
        selectedEntriesPreferences = getPreferences(MODE_PRIVATE);

        deviceSpinner = findViewById(R.id.deviceSpinner);
        layoutSpinner = findViewById(R.id.layoutSpinner);
        btnConnect = findViewById(R.id.btnConnect);
        btnNoDevicesAuthorized = findViewById(R.id.btnNoDevicesAuthorized);
        txtState = findViewById(R.id.txtState);

        registerListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkBluetoothEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();

        selectedEntriesPreferences.edit()
                .putString(BUNDLE_KEY_SELECTED_DEVICE, selectedDevice)
                .putString(BUNDLE_KEY_SELECTED_LAYOUT, selectedLayout)
                .apply();
    }

    private void registerListeners() {
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = devices.get(position).getAddress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        layoutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLayout = (String) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        btnConnect.setOnClickListener(this::connectToDevice);
        btnNoDevicesAuthorized.setOnClickListener(v -> startActivity(new Intent(KeyboardActivity.this, AuthorizedDevicesActivity.class)));
        hidService.setOnStateChangeListener(this);
    }

    @Override
    protected void onBluetoothEnabled() {
        devices = new AuthorizedDevicesManager(this).filterAuthorizedDevices(bluetoothAdapter.getBondedDevices());
        btnNoDevicesAuthorized.setVisibility(devices.size() > 0 ? View.GONE : View.VISIBLE);
        deviceSpinner.setVisibility(devices.size() > 0 ? View.VISIBLE : View.GONE);

        final List<String> names = devices.stream().map(BluetoothDevice::getName).collect(Collectors.toList());
        final SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        deviceSpinner.setAdapter(adapter);

        updateSelectionFromPreferences();
    }

    private void updateSelectionFromPreferences() {
        selectedDevice = selectedEntriesPreferences.getString(BUNDLE_KEY_SELECTED_DEVICE, null);
        selectedLayout = selectedEntriesPreferences.getString(BUNDLE_KEY_SELECTED_LAYOUT, null);

        IntStream.range(0, devices.size())
                .filter(index -> devices.get(index).getAddress().equals(selectedDevice))
                .findAny()
                .ifPresent(index -> deviceSpinner.setSelection(index));

        final String[] layouts = getResources().getStringArray(R.array.layout_list);
        IntStream.range(0, layouts.length)
                .filter(index -> layouts[index].equals(selectedLayout))
                .findAny()
                .ifPresent(index -> layoutSpinner.setSelection(index));
    }

    public void connectToDevice(View v) {
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(selectedDevice);
        Log.i(TAG, "connecting to " + device.getName() + " using keyboard " + selectedLayout.getClass().getSimpleName());
        try {
            hidService.connect(device, KeyboardLayoutFactory.getLayout(selectedLayout));
        } catch (Exception e) {
            Log.e(TAG, "failed to connect to "+device.getName());
        }
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
