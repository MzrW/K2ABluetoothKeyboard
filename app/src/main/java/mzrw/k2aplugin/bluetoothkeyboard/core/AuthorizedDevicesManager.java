package mzrw.k2aplugin.bluetoothkeyboard.core;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthorizedDevicesManager {
    private static final String AUTHORIZED_DEVICES_PREFERENCES_NAME = "authorized_devices";
    private static final String AUTHORIZED_DEVICES = "authorized_devices";
    private final Context context;

    public AuthorizedDevicesManager(Context context) {
        this.context = context;
    }

    public boolean isDeviceAuthorized(BluetoothDevice device) {
        if(device == null)
            return false;
        if(device.getBondState() != BluetoothDevice.BOND_BONDED)
            return false;
        final String address = device.getAddress();
        return getAuthorizedDeviceAddresses().contains(address);
    }

    public void setAuthorizationState(BluetoothDevice device, boolean authorized) {
        final SharedPreferences sharedPreferences = getSharedPreferences();

        final Set<String> authorizedDevices = getAuthorizedDeviceAddresses();

        if(authorized)
            authorizedDevices.add(device.getAddress());
        else
            authorizedDevices.remove(device.getAddress());

        sharedPreferences.edit().putStringSet(AUTHORIZED_DEVICES, authorizedDevices).apply();
    }

    private Set<String> getAuthorizedDeviceAddresses() {
        final SharedPreferences authorizedDevicesPreferences = getSharedPreferences();

        return authorizedDevicesPreferences.getStringSet(AUTHORIZED_DEVICES, new HashSet<>());
    }

    public List<BluetoothDevice> filterAuthorizedDevices(Set<BluetoothDevice> authorizedDevices) {
        return authorizedDevices.stream()
                .filter(this::isDeviceAuthorized)
                .collect(Collectors.toList());
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(AUTHORIZED_DEVICES_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
