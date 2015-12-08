package dominando.android.hotel.gcm;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.gcm.GcmListenerService;

import dominando.android.hotel.HotelActivity;
import dominando.android.hotel.HotelIntentService;
import dominando.android.hotel.R;

public class HotelGcmListenerService extends GcmListenerService {
    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        startService(new Intent(this, HotelIntentService.class));
        if (data != null){
            dispararNotificacao(data.getString("mensagem"));
        }
    }
    private void dispararNotificacao(String msg) {
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        Intent it = new Intent(this, HotelActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(it);
        PendingIntent pit = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pit)
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.texto_notificacao))
                        .setContentText(msg);
        nm.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
