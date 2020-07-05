package mzrw.k2aplugin.bluetoothkeyboard;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Abstract Activity to ensure bluetooth is activated.
 */
public abstract class AbstractBluetoothActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 17;

    protected BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Call this method to check that bluetooth is enabled in {@link #onCreate(Bundle)}.
     */
    protected void checkBluetoothEnabled() {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            onBluetoothEnabled();
        }
    }

    /**
     * This method is called when bluetooth is activated.
     * Overwrite this method to take some action when bluetooth is enabled.
     */
    protected void onBluetoothEnabled() {

    }
}
