package mzrw.k2aplugin.bluetoothkeyboard;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mzrw.k2aplugin.bluetoothkeyboard.core.AuthorizedDevicesManager;

public class AuthorizedDevicesActivity extends AbstractBluetoothActivity {
    private AuthorizedDevicesManager authorizedDevicesManager;
    private RecyclerView recyclerViewAuthorizedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorized_devices);

        recyclerViewAuthorizedDevices = findViewById(R.id.recyclerViewAuthroizedDevices);

        recyclerViewAuthorizedDevices.setHasFixedSize(true);
        recyclerViewAuthorizedDevices.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAuthorizedDevices.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        authorizedDevicesManager = new AuthorizedDevicesManager(this);

        checkBluetoothEnabled();
    }

    @Override
    protected void onBluetoothEnabled() {
        super.onBluetoothEnabled();

        setAuthorizedDevicesAdapter();
    }

    @Override
    protected void onPause() {
        super.onPause();

        authorizedDevicesManager.saveAuthorizedDevices();
    }

    private void setAuthorizedDevicesAdapter() {
        final List<BluetoothDevice> bluetoothDevices = new ArrayList<>(bluetoothAdapter.getBondedDevices());
        bluetoothDevices.sort(this::compareBluetoothDevices);
        recyclerViewAuthorizedDevices.setAdapter(new AuthorizedDevicesAdapter(bluetoothDevices));
    }

    private int compareBluetoothDevices(BluetoothDevice a, BluetoothDevice b) {
        int comparison = Boolean.compare(authorizedDevicesManager.isDeviceAuthorized(b), authorizedDevicesManager.isDeviceAuthorized(a));
        if(comparison != 0)
            return comparison;

        return a.getName().compareTo(b.getName());
    }

    class AuthorizedDevicesAdapter extends RecyclerView.Adapter<AuthorizedDeviceViewHolder> {
        private final List<BluetoothDevice> devices;

        private AuthorizedDevicesAdapter(List<BluetoothDevice> devices) {
            this.devices = devices;
        }

        @NonNull
        @Override
        public AuthorizedDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(AuthorizedDevicesActivity.this).inflate(R.layout.layout_authorized_device_entry, parent, false);

            return new AuthorizedDeviceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AuthorizedDeviceViewHolder holder, int position) {
            holder.setBluetoothDevice(devices.get(position));
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }
    }

    class AuthorizedDeviceViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private BluetoothDevice bluetoothDevice;

        private final TextView txtAuthorizedDeviceName, txtAuthorizedDeviceAddress;
        private final Switch switchAuthorizedDeviceAuthorized;


        private AuthorizedDeviceViewHolder(View itemView) {
            super(itemView);

            txtAuthorizedDeviceName = itemView.findViewById(R.id.txtAuthorizedDeviceName);
            txtAuthorizedDeviceAddress = itemView.findViewById(R.id.txtAuthorizedDeviceAddress);
            switchAuthorizedDeviceAuthorized = itemView.findViewById(R.id.switchAuthorizedDeviceAuthorized);

            switchAuthorizedDeviceAuthorized.setOnCheckedChangeListener(this);
        }

        private void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
            this.bluetoothDevice = bluetoothDevice;

            txtAuthorizedDeviceName.setText(bluetoothDevice.getName());
            txtAuthorizedDeviceAddress.setText(bluetoothDevice.getAddress());
            switchAuthorizedDeviceAuthorized.setChecked(authorizedDevicesManager.isDeviceAuthorized(bluetoothDevice));
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            authorizedDevicesManager.setAuthorizationState(bluetoothDevice, isChecked);
        }
    }
}
