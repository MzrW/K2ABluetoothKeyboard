package mzrw.k2aplugin.bluetoothkeyboard.k2a;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import keepass2android.pluginsdk.KeepassDefs;
import keepass2android.pluginsdk.PluginAccessException;
import keepass2android.pluginsdk.PluginActionBroadcastReceiver;
import keepass2android.pluginsdk.Strings;
import mzrw.k2aplugin.bluetoothkeyboard.R;

public class PluginActionReceiver extends PluginActionBroadcastReceiver {
    private static final String TAG = PluginActionReceiver.class.getName();
    public static final String BUNDLE_KEY_USERNAME = "username";
    public static final String BUNDLE_KEY_PASSWORD = "password";
    public static final String BUNDLE_KEY_ENTER = "enter";

    @Override
    protected void openEntry(OpenEntryAction oe) {
        final Context ctx = oe.getContext();

        try {
            for (String field : oe.getEntryFields().keySet()) {
                oe.addEntryFieldAction("mzrw.k2aplugin.bluetoothkeyboard.type",
                        Strings.PREFIX_STRING + field,
                        "Type to Bluetooth",
                        R.drawable.ic_launcher_foreground,
                        new Bundle());
            }
            final Bundle type_user_pass = new Bundle();
            type_user_pass.putBoolean(BUNDLE_KEY_USERNAME, true);
            type_user_pass.putBoolean(BUNDLE_KEY_PASSWORD, true);

            final Bundle type_user_pass_enter = new Bundle();
            type_user_pass_enter.putBoolean(BUNDLE_KEY_USERNAME, true);
            type_user_pass_enter.putBoolean(BUNDLE_KEY_PASSWORD, true);
            type_user_pass_enter.putBoolean(BUNDLE_KEY_ENTER, true);

            oe.addEntryAction(ctx.getString(R.string.type_user_pass), R.drawable.ic_launcher_foreground, type_user_pass);
            oe.addEntryAction(ctx.getString(R.string.type_user_pass_enter), R.drawable.ic_launcher_foreground, type_user_pass_enter);
        } catch(PluginAccessException e) {
            Log.e(TAG, "Failed to register actions for entries: "+e.getMessage(), e);
        }
    }

    @Override
    protected void actionSelected(ActionSelectedAction actionSelected) {
        final Context ctx = actionSelected.getContext();
        final String text = extractStringToTypeFromBundle(actionSelected);

        Toast.makeText(ctx, "Typing "+text, Toast.LENGTH_LONG).show();
    }

    private String extractStringToTypeFromBundle(ActionSelectedAction actionSelected) {
        final StringBuilder stringBuilder = new StringBuilder();

        if(actionSelected.isEntryAction()) {
            final Bundle data = actionSelected.getActionData();
            if(data.containsKey(BUNDLE_KEY_USERNAME))
                stringBuilder.append(actionSelected.getEntryFields().get(KeepassDefs.UserNameField));
            if(data.containsKey(BUNDLE_KEY_PASSWORD)) {
                if(data.containsKey(BUNDLE_KEY_USERNAME))
                    stringBuilder.append('\t');
                stringBuilder.append(actionSelected.getEntryFields().get(KeepassDefs.PasswordField));
            }
            if(data.containsKey(BUNDLE_KEY_ENTER))
                stringBuilder.append('\n');

            if(stringBuilder.length() > 0) {

            }
        } else {
            final String fieldKey = actionSelected.getFieldId().substring(Strings.PREFIX_STRING.length());
            stringBuilder.append(actionSelected.getEntryFields().get(fieldKey));
        }

        return stringBuilder.toString();
    }
}
