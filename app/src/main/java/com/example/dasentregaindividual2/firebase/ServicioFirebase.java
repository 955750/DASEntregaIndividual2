/*
package com.example.dasentregaindividual2.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {

    public ServicioFirebase() {

    }

    */
/*
     * APK en primer plano --> Se ejecuta el método; NO se muestra notificación (a no ser que la
     * programemos nosotros)
     *
     * APK en 'background' -->
     *      + Tipo NOTIFICACIÓN -->
     *          - Se muestra notificación en la barra
     *          - NO se ejecuta el método
     *          - En la propia notificación se puede configurar el evento a ejecutar
     *
     *      + Tipo DATOS -->
     *          - Se ejecuta el método
     *//*

    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) { // Si el mensaje viene con datos
            Log.d("ServicioFirebase", remoteMessage.getData().toString());
        }
        if (remoteMessage.getNotification() != null) { // Si el mensaje es una notificación
            Log.d("ServicioFirebase", "Title --> " + remoteMessage.getNotification().getTitle());
            Log.d("ServicioFirebase", "Body -->" + remoteMessage.getNotification().getBody());
        }
    }

    */
/*
     * Qué hacer cada vez que se genere un token para el dispositivo
     *//*

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.d("ServicioFirebase", "Token --> '" + s + "'");
    }


}
*/
