package mzrw.k2aplugin.bluetoothkeyboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import keepass2android.pluginsdk.AccessManager;
import keepass2android.pluginsdk.Strings;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_REGISTER_PLUGIN = 0x748;
    private static final String TEST_STRING = "test";

    private TextView txtPluginState;
    private Button btnEnablePlugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final NotificationUtility notificationUtility = new NotificationUtility(this);
        notificationUtility.registerNotificationChannel();

        txtPluginState = findViewById(R.id.txtPluginState);
        btnEnablePlugin = findViewById(R.id.btnEnablePlugin);
        final Button btnSendTestString = findViewById(R.id.btnSendTestString);

        updateUIPluginState();
        btnEnablePlugin.setOnClickListener((v) -> onBtnEnableClicked());
        btnSendTestString.setOnClickListener((v) -> {
            final Intent intent =  new Intent(MainActivity.this, KeyboardActivity.class);
            intent.putExtra(KeyboardActivity.INTENT_EXTRA_STRING_TO_TYPE, "test");
            startActivity(intent);
        });
    }

    private void onBtnEnableClicked() {
        final Intent intent = new Intent(Strings.ACTION_EDIT_PLUGIN_SETTINGS);
        intent.putExtra(Strings.EXTRA_PLUGIN_PACKAGE, getPackageName());
        startActivityForResult(intent, REQUEST_CODE_REGISTER_PLUGIN);
    }

    public void onSelectAuthorizedDevices(View v) {
        final Intent intent = new Intent(this, AuthorizedDevicesActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_REGISTER_PLUGIN) {
            if(resultCode == Activity.RESULT_OK)
                Toast.makeText(getApplicationContext(), R.string.toast_plugin_successfully_activated, Toast.LENGTH_LONG).show();
            updateUIPluginState();
        }
    }

    private void updateUIPluginState() {
        if(isPluginEnabled()) {
            btnEnablePlugin.setEnabled(false);
            txtPluginState.setText(R.string.plugin_state_enabled);
        } else {
            btnEnablePlugin.setEnabled(true);
            txtPluginState.setText(R.string.plugin_state_disabled);
        }
    }

    private boolean isPluginEnabled() {
        final ArrayList<String> scopes = new ArrayList<>();
        scopes.add(Strings.SCOPE_CURRENT_ENTRY);
        final String accessToken = AccessManager.tryGetAccessToken(getApplicationContext(), "keepass2android.keepass2android", scopes);

        return accessToken != null;
    }
}
