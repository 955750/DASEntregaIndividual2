package com.example.dasentregaindividual2.lista_partidos;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dasentregaindividual2.R;
import com.google.android.material.card.MaterialCardView;

public class ListaPartidosViewHolder extends RecyclerView.ViewHolder {

    /* Atributos de la interfaz gráfica */
    public ImageView escudoEquipoLocalIV;
    public TextView nombreEquipoLocalTV;
    public TextView ultimosPartidosEquipoLocalTV;
    public TextView puntosEquipoLocalTV;
    public ImageView escudoEquipoVisitanteIV;
    public TextView nombreEquipoVisitanteTV;
    public TextView ultimosPartidosEquipoVisitanteTV;
    public TextView puntosEquipoVisitanteTV;
    public TextView fechaPartidoTV;
    public TextView horaPartidoTV;
    public MaterialCardView cardPartido;

    /* Otros atributos */
    public boolean[] seleccion;


    public ListaPartidosViewHolder(@NonNull View itemView) {
        super(itemView);

        /* Instanciar elementos visuales del equipo local */
        escudoEquipoLocalIV = itemView.findViewById(R.id.escudo_equipo_local);
        nombreEquipoLocalTV = itemView.findViewById(R.id.nombre_equipo_local);
        ultimosPartidosEquipoLocalTV = itemView.findViewById(R.id.ultimos_partidos_equipo_local);
        puntosEquipoLocalTV = itemView.findViewById(R.id.puntos_equipo_local);

        /* Instanciar elementos visuales del equipo visitante */
        escudoEquipoVisitanteIV = itemView.findViewById(R.id.escudo_equipo_visitante);
        nombreEquipoVisitanteTV = itemView.findViewById(R.id.nombre_equipo_visitante);
        ultimosPartidosEquipoVisitanteTV = itemView
            .findViewById(R.id.ultimos_partidos_equipo_visitante);
        puntosEquipoVisitanteTV = itemView.findViewById(R.id.puntos_equipo_visitante);

        /* Instanciar elementos visuales del resto de detalles del partido */
        fechaPartidoTV = itemView.findViewById(R.id.fecha_partido);
        horaPartidoTV = itemView.findViewById(R.id.hora_partido);
        cardPartido = itemView.findViewById(R.id.card_partido);
    }
}
