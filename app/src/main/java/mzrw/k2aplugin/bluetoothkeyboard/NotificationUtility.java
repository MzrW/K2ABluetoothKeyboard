package mzrw.k2aplugin.bluetoothkeyboard;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

public class NotificationUtility {
    public static final String CHANNEL_ID = "k2a_bluetooth_channel";
    public static final int NOTIFICATION_ID_KEYBOARD = 1;
    public static final int NOTIFICATION_TIMEOUT_MS = 30_000;
    private final Context context;

    public NotificationUtility(Context context) {
        this.context = context;
    }

    public void registerNotificationChannel() {
        final NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, context.getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(context.getString(R.string.notification_channel_description));

        context.getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);
    }

    public void notifyTextAvailable(String text) {
        final Intent intent = new Intent(context, KeyboardActivity.class);
        intent.putExtra(KeyboardActivity.INTENT_EXTRA_STRING_TO_TYPE, text);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(context.getString(R.string.notification_entry_available))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setTimeoutAfter(NOTIFICATION_TIMEOUT_MS)
                .build();

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_KEYBOARD, notification);
    }
}
