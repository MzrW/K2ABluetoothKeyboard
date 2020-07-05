package mzrw.k2aplugin.bluetoothkeyboard.k2a;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import keepass2android.pluginsdk.KeepassDefs;
import keepass2android.pluginsdk.PluginAccessException;
import keepass2android.pluginsdk.PluginActionBroadcastReceiver;
import keepass2android.pluginsdk.Strings;
import mzrw.k2aplugin.bluetoothkeyboard.NotificationUtility;
import mzrw.k2aplugin.bluetoothkeyboard.R;

/**
 * BroadcastReceiver to "talk" to the keepass2android-Application.
 */
public class PluginActionReceiver extends PluginActionBroadcastReceiver {
    private static final String TAG = PluginActionReceiver.class.getName();
    public static final String BUNDLE_KEY_USERNAME = "username";
    public static final String BUNDLE_KEY_PASSWORD = "password";
    public static final String BUNDLE_KEY_ENTER = "enter";

    /**
     * Add some actions to the opened entry in keepass2android.
     * @param oe the {@link keepass2android.pluginsdk.PluginActionBroadcastReceiver.OpenEntryAction}
     */
    @Override
    protected void openEntry(OpenEntryAction oe) {
        final Context ctx = oe.getContext();

        try {
            for (String field : oe.getEntryFields().keySet()) {
                oe.addEntryFieldAction("mzrw.k2aplugin.bluetoothkeyboard.type",
                        Strings.PREFIX_STRING + field,
                        "Type to Bluetooth",
                        R.drawable.notification_icon_grey,
                        new Bundle());
            }
            final Bundle type_user_pass = new Bundle();
            type_user_pass.putBoolean(BUNDLE_KEY_USERNAME, true);
            type_user_pass.putBoolean(BUNDLE_KEY_PASSWORD, true);

            final Bundle type_user_pass_enter = new Bundle();
            type_user_pass_enter.putBoolean(BUNDLE_KEY_USERNAME, true);
            type_user_pass_enter.putBoolean(BUNDLE_KEY_PASSWORD, true);
            type_user_pass_enter.putBoolean(BUNDLE_KEY_ENTER, true);

            oe.addEntryAction(ctx.getString(R.string.type_user_pass), R.drawable.notification_icon_grey, type_user_pass);
            oe.addEntryAction(ctx.getString(R.string.type_user_pass_enter), R.drawable.notification_icon_grey, type_user_pass_enter);
        } catch(PluginAccessException e) {
            Log.e(TAG, "Failed to register actions for entries: "+e.getMessage(), e);
        }
    }

    /**
     * An action registered in {@link #openEntry(OpenEntryAction)} was selected.
     * Create a notification to send the text from the action to a bluetooth device.
     * @param actionSelected the selected action.
     */
    @Override
    protected void actionSelected(ActionSelectedAction actionSelected) {
        final Context ctx = actionSelected.getContext();
        final String text = extractTextToTypeFromBundle(actionSelected);

        new NotificationUtility(ctx).notifyTextAvailable(text);
    }

    /**
     * Extract the text to type/send from the selected action
     * @param actionSelected the selected action
     * @return the text to type
     */
    private String extractTextToTypeFromBundle(ActionSelectedAction actionSelected) {
        final StringBuilder text = new StringBuilder();

        if(actionSelected.isEntryAction()) {
            final Bundle data = actionSelected.getActionData();
            if(data.containsKey(BUNDLE_KEY_USERNAME))
                text.append(actionSelected.getEntryFields().get(KeepassDefs.UserNameField));
            if(data.containsKey(BUNDLE_KEY_PASSWORD)) {
                if(data.containsKey(BUNDLE_KEY_USERNAME))
                    text.append('\t');
                text.append(actionSelected.getEntryFields().get(KeepassDefs.PasswordField));
            }
            if(data.containsKey(BUNDLE_KEY_ENTER))
                text.append('\n');
        } else {
            final String fieldKey = actionSelected.getFieldId().substring(Strings.PREFIX_STRING.length());
            text.append(actionSelected.getEntryFields().get(fieldKey));
        }

        return text.toString();
    }
}
