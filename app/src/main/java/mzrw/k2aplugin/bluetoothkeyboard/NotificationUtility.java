package mzrw.k2aplugin.bluetoothkeyboard;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

/**
 * This Utility is used to open the {@link KeyboardActivity} from a BroadcastReceiver.
 * As in new versions of android, background tasks can't open activities anymore, creating a notification is required.
 */
public class NotificationUtility {
    public static final String CHANNEL_ID = "k2a_bluetooth_channel";
    public static final int NOTIFICATION_ID_KEYBOARD = 1;
    public static final int NOTIFICATION_TIMEOUT_MS = 30_000;

    private final Context context;

    public NotificationUtility(Context context) {
        this.context = context;
    }

    private void registerNotificationChannel() {
        final NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(context.getString(R.string.notification_channel_description));
        notificationChannel.enableVibration(false);

        context.getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);
    }

    /**
     * Create a fullscreen intent notification to open the {@link KeyboardActivity} to send the given text.
     * @param text the given text to be sent
     */
    public void notifyTextAvailable(String text) {
        final Intent intent = new Intent(context, KeyboardActivity.class);
        intent.putExtra(KeyboardActivity.INTENT_EXTRA_STRING_TO_TYPE, text);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon_grey)
                .setContentTitle(context.getString(R.string.notification_entry_available))
                .setFullScreenIntent(pendingIntent, true)
                .setContentIntent(pendingIntent)
                .setTimeoutAfter(NOTIFICATION_TIMEOUT_MS)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_KEYBOARD, notification);
    }
}
