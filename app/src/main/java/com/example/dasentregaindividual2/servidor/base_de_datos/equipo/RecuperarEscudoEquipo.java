package com.example.dasentregaindividual2.servidor.base_de_datos.equipo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecuperarEscudoEquipo extends Worker {

    public RecuperarEscudoEquipo(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/" +
                "jfuentes019/WEB/Entrega%20Individual%202/consultas_equipo.php";

        HttpURLConnection urlConnection;
        Data resultado = null;

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
            String opcion = "2";
            String nombreEquipo = getInputData().getString("nombreEquipo");
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("nombreEquipo", nombreEquipo)
                    .appendQueryParameter("opcion", opcion);
            String parametros = builder.build().getEncodedQuery();

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            // Procesar la respuesta de la llamada HTTP
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(
                        urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8")
                );

                String respuesta = bufferedReader.readLine();
                Log.d("RecuperarEscudoEquipo", respuesta);

                // Preparar los datos a devolver
                resultado = new Data.Builder()
                        .putString("escudoEquipo", respuesta)
                        .build();
                inputStream.close();

            // Procesar la respuesta de la llamada HTTP
            /*int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                Bitmap elBitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                elBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] fototransformada = stream.toByteArray();
                String fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);

                // Preparar los datos a devolver
                resultado = new Data.Builder()
                        .putString("escudoEquipo", fotoen64)
                        .build();*/
            }
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (resultado != null) {
            return Result.success(resultado);
        } else {
            return Result.failure();
        }
    }
}
