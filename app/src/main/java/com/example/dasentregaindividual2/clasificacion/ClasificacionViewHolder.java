package com.example.dasentregaindividual2.clasificacion;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.base_de_datos.favorito.AñadirFavorito;
import com.example.dasentregaindividual2.base_de_datos.favorito.EliminarFavorito;
import com.google.android.material.card.MaterialCardView;

public class ClasificacionViewHolder extends RecyclerView.ViewHolder {

    /* Atributos de la interfaz gráfica */
    public TextView posicionClasifacionTV;
    public ImageView escudoIV;
    public TextView nombreEquipoTV;
    public TextView partidosGanadosTotalesTV;
    public TextView partidosPerdidosTotalesTV;
    public TextView puntosFavorTotalesTV;
    public TextView puntosContraTotalesTV;
    public TextView rachaUltimos10TV;
    public MaterialCardView cardClasificacion;
    public ImageView añadirEliminarFavoritosIV;

    /* Otros atributos */
    public boolean[] seleccion;
    

    public ClasificacionViewHolder(@NonNull View itemView) {
        super(itemView);

        /* Instanciar elementos visuales del equipo */
        posicionClasifacionTV = itemView.findViewById(R.id.posicion_clasificacion);
        escudoIV = itemView.findViewById(R.id.escudo_clasificacion);
        nombreEquipoTV = itemView.findViewById(R.id.nombre_equipo_clasificacion);
        partidosGanadosTotalesTV = itemView.findViewById(R.id.partidos_ganados_totales);
        partidosPerdidosTotalesTV = itemView.findViewById(R.id.partidos_perdidos_totales);
        puntosFavorTotalesTV = itemView.findViewById(R.id.puntos_favor_totales);
        puntosContraTotalesTV = itemView.findViewById(R.id.puntos_contra_totales);
        rachaUltimos10TV = itemView.findViewById(R.id.racha_ultimos_10);
        cardClasificacion = itemView.findViewById(R.id.card_clasificacion);
        añadirEliminarFavoritosIV = itemView.findViewById(R.id.añadir_eliminar_favoritos);
        añadirEliminarFavoritosIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                añadirEliminarEquipoFavorito();
            }
        });
    }
    
    private void añadirEliminarEquipoFavorito() {
        Context context = itemView.getContext();
        if (!seleccion[getAdapterPosition()]) {
            añadirEquipoFavoritoABaseDeDatos(context);
            añadirEliminarFavoritosIV.setImageResource(R.drawable.ic_favorito_true_32dp);
        } else {
            eliminarEquipoFavoritoDeBaseDeDatos(context);
            añadirEliminarFavoritosIV.setImageResource(R.drawable.ic_favorito_false_32dp);
        }
        seleccion[getAdapterPosition()] = !seleccion[getAdapterPosition()];
    }

    /*
     * En esta función, se encola una tarea cuyo cometido es lanzar la siguiente consulta contra
     * la base de datos remota (la tarea requiere de conexión a internet para poder acceder a la
     * base de datos alojada en el servidor):
     *
     * INSERT INTO Favorito (nombre_usuario, nombre_equipo)
     * VALUES (?, ?)
     */
    private void añadirEquipoFavoritoABaseDeDatos(Context context) {
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);
        String usuario = preferencias.getString("usuario", null);
        String nombreEquipo = nombreEquipoTV.getText().toString();
        Data parametros = new Data.Builder()
                .putString("nombreUsuario", usuario)
                .putString("nombreEquipo", nombreEquipo)
                .build();

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(AñadirFavorito.class)
                .setConstraints(restricciones)
                .setInputData(parametros)
                .build();

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(otwr.getId())
                .observe((LifecycleOwner) context, new Observer<WorkInfo>() {

                    /*
                     * Una vez completada la consulta, se comprueba el resultado de la consulta.
                     * Si se da la condición 'consultaExitosa == 1' se procede a añadir el equipo
                     * a la lista de favoritos del usuario logeado.
                     */
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()) {
                            String consultaExitosaStr = workInfo.getOutputData()
                                    .getString("consultaExitosa");
                            if (consultaExitosaStr != null) {
                                int consultaExitosa = Integer.parseInt(consultaExitosaStr);
                                if (consultaExitosa == 1) { // La consulta ha sido EXITOSA
                                    Toast.makeText(
                                            context,
                                            context.getString(R.string.toast_añadir_favorito,
                                                    nombreEquipo),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else { // Ha ocurrido un ERROR en la consulta
                                    Toast.makeText(
                                            context,
                                            context.getString(R.string.toast_añadir_favoritos_error,
                                                    nombreEquipo),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                        }
                    }
                });

        WorkManager.getInstance(context).enqueue(otwr);
    }

    /*
     * En esta función, se encola una tarea cuyo cometido es lanzar la siguiente consulta contra
     * la base de datos remota (la tarea requiere de conexión a internet para poder acceder a la
     * base de datos alojada en el servidor):
     *
     * DELETE FROM Favorito
     * WHERE nombre_usuario = ?
     * AND nombre_equipo = ?
     */
    private void eliminarEquipoFavoritoDeBaseDeDatos(Context context) {
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);
        String usuario = preferencias.getString("usuario", null);
        String nombreEquipo = nombreEquipoTV.getText().toString();
        Data parametros = new Data.Builder()
                .putString("nombreUsuario", usuario)
                .putString("nombreEquipo", nombreEquipo)
                .build();

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(EliminarFavorito.class)
                .setConstraints(restricciones)
                .setInputData(parametros)
                .build();

        WorkManager.getInstance(context).getWorkInfoByIdLiveData(otwr2.getId())
                .observe((LifecycleOwner) context, new Observer<WorkInfo>() {

                    /*
                     * Una vez completada la consulta, se comprueba el resultado de la consulta.
                     * Si se da la condición 'consultaExitosa == 1' se procede a eliminar el equipo
                     * a la lista de favoritos del usuario logeado.
                     */
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()) {
                            String consultaExitosaStr = workInfo.getOutputData()
                                    .getString("consultaExitosa");
                            if (consultaExitosaStr != null) {
                                int consultaExitosa = Integer.parseInt(consultaExitosaStr);
                                if (consultaExitosa == 1) { // La consulta ha sido EXITOSA
                                    Toast.makeText(
                                            context,
                                            context.getString(R.string.toast_eliminar_favorito, nombreEquipo),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else { // Ha ocurrido un ERROR en la consulta
                                    Toast.makeText(
                                            context,
                                            context.getString(
                                                    R.string.toast_eliminar_favoritos_error,
                                                    nombreEquipo
                                            ),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                        }
                    }
                });

        WorkManager.getInstance(context).enqueue(otwr2);
    }
}
