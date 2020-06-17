package mzrw.k2aplugin.bluetoothkeyboard;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mzrw.k2aplugin.bluetoothkeyboard.core.AuthorizedDevicesManager;

public class AuthorizedDevicesActivity extends AbstractBluetoothActivity {
    private AuthorizedDevicesManager authorizedDevicesManager;
    private RecyclerView recyclerViewAuthroizedDevices;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorized_devices);

        recyclerViewAuthroizedDevices = findViewById(R.id.recyclerViewAuthroizedDevices);

        recyclerViewAuthroizedDevices.setHasFixedSize(true);
        recyclerViewAuthroizedDevices.setLayoutManager(new LinearLayoutManager(this));

        authorizedDevicesManager = new AuthorizedDevicesManager(this);
        if(bluetoothAdapter != null) {
            final List<BluetoothDevice> bluetoothDevices = new ArrayList<>(bluetoothAdapter.getBondedDevices());
            adapter = new AuthorizedDevicesAdapter(bluetoothDevices);
            recyclerViewAuthroizedDevices.setAdapter(adapter);
        }

    }

    class AuthorizedDevicesAdapter extends RecyclerView.Adapter<AuthorizedDeviceViewHolder> {
        private final List<BluetoothDevice> devices;

        public AuthorizedDevicesAdapter(List<BluetoothDevice> devices) {
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


        public AuthorizedDeviceViewHolder(View itemView) {
            super(itemView);

            txtAuthorizedDeviceName = itemView.findViewById(R.id.txtAuthorizedDeviceName);
            txtAuthorizedDeviceAddress = itemView.findViewById(R.id.txtAuthorizedDeviceAddress);
            switchAuthorizedDeviceAuthorized = itemView.findViewById(R.id.switchAuthorizedDeviceAuthorized);

            switchAuthorizedDeviceAuthorized.setOnCheckedChangeListener(this);
        }

        public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
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
