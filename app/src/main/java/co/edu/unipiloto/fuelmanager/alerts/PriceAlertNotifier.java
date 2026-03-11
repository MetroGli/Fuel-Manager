package co.edu.unipiloto.fuelmanager.alerts;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.PriceAlert;
import co.edu.unipiloto.fuelmanager.stations.StationListActivity;

public class PriceAlertNotifier {

    private static final String CHANNEL_ID   = "price_alerts";
    private static final String CHANNEL_NAME = "Alertas de precio";

    private final Context        context;
    private final DatabaseHelper db;
    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    public PriceAlertNotifier(Context context) {
        this.context = context.getApplicationContext();
        this.db      = DatabaseHelper.getInstance(context);
        createChannel();
    }

    public void checkAndNotify(int userId) {
        List<PriceAlert> changed = db.checkPriceChanges(userId);
        for (int i = 0; i < changed.size(); i++) {
            sendNotification(changed.get(i), i);
        }
    }

    private void sendNotification(PriceAlert alert, int notifId) {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Intent para abrir StationListActivity al tocar la notificación
        Intent intent = new Intent(context, StationListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, notifId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String fuelEmoji = fuelEmoji(alert.getFuelType());
        String title = fuelEmoji + " Precio actualizado · " + alert.getStationName();
        String body  = alert.getFuelType() + " ahora cuesta $"
                + COP.format(alert.getLastKnownPrice()) + "/gal";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_fuel_drop)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi)
                .setAutoCancel(true);

        if (nm != null) nm.notify(notifId + 100, builder.build());
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Avisa cuando cambia el precio de combustible");
            NotificationManager nm = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    private String fuelEmoji(String fuel) {
        switch (fuel) {
            case "Extra": return "🟡";
            case "ACPM":  return "🔵";
            default:      return "🟠";
        }
    }
}