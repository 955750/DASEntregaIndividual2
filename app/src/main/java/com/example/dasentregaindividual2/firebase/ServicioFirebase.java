package com.example.dasentregaindividual2.firebase;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServicioFirebase extends FirebaseMessagingService {

    public ServicioFirebase() {

    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) { // Si el mensaje viene con datos
            Log.d("ServicioFirebase", remoteMessage.getData().toString());
        }
        if (remoteMessage.getNotification() != null) { // Si el mensaje es una notificación
            Log.d("ServicioFirebase", "Title --> " + remoteMessage.getNotification().getTitle());
            Log.d("ServicioFirebase", "Body -->" + remoteMessage.getNotification().getBody());
        }
    }

    /*
     * En esta función, al instalar la aplicación o borrar los datos y volver a abrirla se recibe
     * un token. Este es añadido a la base de datos para poder enviar notificaciones push via FCM
     */
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("ServicioFirebase", "Token --> '" + s + "'");
        añadirToken(s);
    }

    /*
     * En esta función se ejecuta la siguiente consulta de forma asíncrona:
     *
     * INSERT INTO TokenFCM (token)
     * VALUES ?
     */
    private void añadirToken(String token) {
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/" +
                "jfuentes019/WEB/Entrega%20Individual%202/consultas_token_fcm.php";

        HttpURLConnection urlConnection;

        try {
            // Hacer la llamada HTTP
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Añadir parámetros a la llamada HTTP
            String opcion = "1";
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("fcmToken", token)
                    .appendQueryParameter("opcion", opcion);
            String parametros = builder.build().getEncodedQuery();

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            int statusCode = urlConnection.getResponseCode();
            Log.d("ServicioFirebase", "STATUS CODE: " + statusCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}