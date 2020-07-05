package mzrw.k2aplugin.bluetoothkeyboard.core;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The {@link AuthorizedDevicesManager} is used to filter the Set of bonded devices to contain
 * authorized devices only.
 * The address of each authorized device is saved in a stringSet inside {@link SharedPreferences}.
 *
 */
public class AuthorizedDevicesManager {
    private static final String AUTHORIZED_DEVICES_PREFERENCES_NAME = "authorized_devices";
    private static final String AUTHORIZED_DEVICES = "authorized_devices";

    private final SharedPreferences preferences;
    private final Set<String> authorizedDevices = new HashSet<>();

    public AuthorizedDevicesManager(Context context) {
        preferences = context.getSharedPreferences(AUTHORIZED_DEVICES_PREFERENCES_NAME, Context.MODE_PRIVATE);
        authorizedDevices.addAll(preferences.getStringSet(AUTHORIZED_DEVICES, new HashSet<>()));
    }

    /**
     * Determine if a given device is authorized.
     * @param device the given device
     * @return true, if the device is authorized, false otherwise
     */
    public boolean isDeviceAuthorized(BluetoothDevice device) {
        if(device == null)
            return false;
        if(device.getBondState() != BluetoothDevice.BOND_BONDED)
            return false;
        final String address = device.getAddress();
        return authorizedDevices.contains(address);
    }

    /**
     * Update the authorization state for a given device.
     * Save must be called separately.
     * @param device the given device
     * @param authorized the authorization state
     */
    public void setAuthorizationState(BluetoothDevice device, boolean authorized) {
        if(authorized)
            authorizedDevices.add(device.getAddress());
        else
            authorizedDevices.remove(device.getAddress());
    }

    /**
     * Filter the given set of {@link BluetoothDevice}s to contain authorized devices only.
     * @param bluetoothDevices the given set of {@link BluetoothDevice}s
     * @return a subset of authorized devices from the given set of authorized {@link BluetoothDevice}s
     */
    public List<BluetoothDevice> filterAuthorizedDevices(Set<BluetoothDevice> bluetoothDevices) {
        return bluetoothDevices.stream()
                .filter(this::isDeviceAuthorized)
                .collect(Collectors.toList());
    }

    /**
     * Save/ Persist the actual set of authorized devices to the {@link SharedPreferences}.
     */
    public void saveAuthorizedDevices() {
        preferences.edit().putStringSet(AUTHORIZED_DEVICES_PREFERENCES_NAME, authorizedDevices).apply();
    }
}
