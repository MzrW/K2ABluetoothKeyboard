package mzrw.k2aplugin.bluetoothkeyboard.k2a;

import java.util.ArrayList;

import keepass2android.pluginsdk.PluginAccessBroadcastReceiver;
import keepass2android.pluginsdk.Strings;

public class PluginAccessReceiver extends PluginAccessBroadcastReceiver {
    @Override
    public ArrayList<String> getScopes() {
        final ArrayList<String> scopes = new ArrayList<>();
        scopes.add(Strings.SCOPE_CURRENT_ENTRY);
        return scopes;
    }
}
