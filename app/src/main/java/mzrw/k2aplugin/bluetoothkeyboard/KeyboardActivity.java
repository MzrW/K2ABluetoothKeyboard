package mzrw.k2aplugin.bluetoothkeyboard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import mzrw.k2aplugin.bluetoothkeyboard.core.BluetoothHidKeyboard;
import mzrw.k2aplugin.bluetoothkeyboard.layout.KeyboardLayoutFactory;
import mzrw.k2aplugin.bluetoothkeyboard.layout.Layout;

public class KeyboardActivity extends AppCompatActivity {
    private static final String TAG = KeyboardActivity.class.getName();
    private static final int REQUEST_ENABLE_BT = 17;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothHidKeyboard hidKeyboard;

    private BluetoothDevice selectedDevice;
    private Layout selectedLayout;
    private List<BluetoothDevice> devices;

    private Spinner deviceSpinner;
    private Spinner layoutSpinner;
    private Button btnConnect;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        deviceSpinner = findViewById(R.id.deviceSpinner);
        layoutSpinner = findViewById(R.id.layoutSpinner);
        btnConnect = findViewById(R.id.btnConnect);
        btnSend = findViewById(R.id.btnSend);

        checkBluetoothEnabled();
        registerListeners();
    }

    private void checkBluetoothEnabled() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // bluetooth unsupported
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else
            onBluetoothEnabled();
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

        btnSend.setOnClickListener(v -> hidKeyboard.sendString("test"));
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            onBluetoothEnabled();
        }
    }

    private void onBluetoothEnabled() {
        devices = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
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
