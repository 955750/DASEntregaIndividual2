package com.example.dasentregaindividual2.servidor.base_de_datos;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ListarEquiposDeUnPartido extends Worker {

    public ListarEquiposDeUnPartido(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /*
     * En esta función se ejecuta la siguiente consulta de forma asíncrona:
     *
     * SELECT j.puntos, j.partido_id, j.local, e.nombre, e.fotoBase64, e.part_ganados_ult_10,
     * e.part_perdidos_ult_10
     * FROM Juega AS j INNER JOIN Equipo AS e ON j.nombre_equipo = e.nombre
     * WHERE j.partido_id = ?
     */
    @NonNull
    @Override
    public Result doWork() {
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/" +
                "jfuentes019/WEB/Entrega%20Individual%202/consultas_varias_tablas.php";

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
            String partidoId = getInputData().getString("partidoId");
            String opcion = "1";
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("partidoId", partidoId)
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

                // Preparar los datos a devolver
                resultado = new Data.Builder()
                        .putString("listaEquiposPartido", respuesta)
                        .build();
                inputStream.close();
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
