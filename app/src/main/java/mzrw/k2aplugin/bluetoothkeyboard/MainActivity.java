package mzrw.k2aplugin.bluetoothkeyboard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

import keepass2android.pluginsdk.Strings;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_REGISTER_PLUGIN = 0x748;
    private Button btnEnablePlugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEnablePlugin = findViewById(R.id.btnEnablePlugin);

        btnEnablePlugin.setOnClickListener((v) -> onBtnEnableClicked());
    }

    private void onBtnEnableClicked() {
        final Intent intent = new Intent(Strings.ACTION_EDIT_PLUGIN_SETTINGS);
        intent.putExtra(Strings.EXTRA_PLUGIN_PACKAGE, getPackageName());
        startActivityForResult(intent, REQUEST_CODE_REGISTER_PLUGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_REGISTER_PLUGIN) {
            Toast.makeText(getApplicationContext(), "Plugin registration returned "+resultCode, Toast.LENGTH_SHORT).show();
        }
    }
}
