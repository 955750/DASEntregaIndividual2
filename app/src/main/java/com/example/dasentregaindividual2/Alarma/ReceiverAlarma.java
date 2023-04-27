package com.example.dasentregaindividual2.Alarma;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.dasentregaindividual2.R;

public class ReceiverAlarma extends BroadcastReceiver {

    /*
     * Esta función es la función que se ejecuta cuando se activa una alarma
     * (La alarma se ha configurado para que se active al 5 segundos aproximadamente para
     * poder verificar su funcionamiento)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager managerNotificaciones = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builderNotificaciones = new NotificationCompat
                .Builder(context, "APK Euroliga");

        builderNotificaciones.setSmallIcon(R.drawable.logo_notificaciones_euroliga)
                .setContentTitle(context.getString(R.string.notificacion_alarma_titulo))
                .setContentText(context.getString(R.string.notificacion_alarma_texto))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canalNotificaciones = new NotificationChannel(
                    "APK Euroliga",
                    context.getString(R.string.notificacion_grande_titulo),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            managerNotificaciones.createNotificationChannel(canalNotificaciones);

            canalNotificaciones.setDescription("APK Euroliga");
            canalNotificaciones.enableLights(true);
            canalNotificaciones.setLightColor(Color.RED);

            int idNotificacion = 20;
            managerNotificaciones.notify(idNotificacion, builderNotificaciones.build());
        }
    }
}
