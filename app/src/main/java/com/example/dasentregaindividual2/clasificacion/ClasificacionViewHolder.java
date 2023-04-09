package com.example.dasentregaindividual2.clasificacion;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.base_de_datos.BaseDeDatos;
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

    private void añadirEquipoFavoritoABaseDeDatos(Context context) {
        /* Recuperar instancia de la base de datos */
        BaseDeDatos gestorBD = new BaseDeDatos(context, "Euroliga",
            null, 1);
        SQLiteDatabase baseDeDatos = gestorBD.getWritableDatabase();

        /*
        INSERT INTO Favorito (nombre_usuario, nombre_equipo)
        VALUES (?, ?)
        */
        ContentValues nuevoEqFavorito = new ContentValues();
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);
        String usuario = preferencias.getString("usuario", null);
        String nombreEquipo = nombreEquipoTV.getText().toString();
        nuevoEqFavorito.put("nombre_usuario", usuario);
        nuevoEqFavorito.put("nombre_equipo", nombreEquipo);
        baseDeDatos.insert("Favorito", null, nuevoEqFavorito);

        Toast.makeText(
            context,
            context.getString(R.string.toast_añadir_favorito, nombreEquipo),
            Toast.LENGTH_SHORT
        ).show();

        gestorBD.close();
    }

    private void eliminarEquipoFavoritoDeBaseDeDatos(Context context) {
        /* Recuperar instancia de la base de datos */
        BaseDeDatos gestorBD = new BaseDeDatos (context, "Euroliga",
                null, 1);
        SQLiteDatabase baseDeDatos = gestorBD.getWritableDatabase();

        /*
        DELETE FROM Favorito
        WHERE nombre_usuario = ?
        AND nombre_equipo = ?
        */
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);
        String usuario = preferencias.getString("usuario", null);
        String nombreEquipo = nombreEquipoTV.getText().toString();
        String[] argumentos = {usuario, nombreEquipo};
        baseDeDatos.delete("Favorito", "nombre_usuario = ? AND nombre_equipo = ?",
            argumentos);

        Toast.makeText(
            context,
            context.getString(R.string.toast_eliminar_favorito, nombreEquipo),
            Toast.LENGTH_SHORT
        ).show();

        gestorBD.close();
    }
}
