package com.example.dasentregaindividual2.firebase;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.servidor.base_de_datos.usuario.InsertarUsuario;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServicioFirebase extends FirebaseMessagingService {

    public ServicioFirebase() {

    }

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
     */

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
     * Qué hacer cada vez que se genere un token para el dispositivo
     */

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("ServicioFirebase", "Token --> '" + s + "'");
        añadirToken(s);
    }

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