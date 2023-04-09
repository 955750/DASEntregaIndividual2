package com.example.dasentregaindividual2.clasificacion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.base_de_datos.modelos.EquipoClasificacion;

public class ClasificacionAdapter extends RecyclerView.Adapter<ClasificacionViewHolder> {

    private EquipoClasificacion[] clasificacion;
    private boolean[] seleccionados;

    /*
     * A la hora de inicializar la variable 'seleccionados' se realiza un bucle para que los
     * equipos que son favoritos aparezcan con una estrella con relleno
     */
    public ClasificacionAdapter(EquipoClasificacion[] pClasificacion) {
        clasificacion = pClasificacion;
        seleccionados = new boolean[pClasificacion.length];
        for (int i = 0; i < pClasificacion.length; i++) {
            seleccionados[i] = pClasificacion[i].getEsFavorito();
        }
    }

    @NonNull
    @Override
    public ClasificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View clasificacionItem = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.clasificacion_item, parent, false);
        ClasificacionViewHolder clasificacionViewHolder = new ClasificacionViewHolder(
            clasificacionItem);
        clasificacionViewHolder.seleccion = seleccionados;
        return clasificacionViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClasificacionViewHolder holder, int position) {
        Context context = holder.itemView.getContext();

        /* Cargar datos del equipo correspondiente */
        EquipoClasificacion equipoActual = clasificacion[position];
        holder.posicionClasifacionTV.setText(String.valueOf(equipoActual.getPosicion()));
        holder.escudoIV.setImageResource(equipoActual.getEscudoId());
        holder.nombreEquipoTV.setText(equipoActual.getNombre());
        holder.partidosGanadosTotalesTV.setText(String.valueOf(equipoActual
            .getPartidosGanadosTotales()));
        holder.partidosPerdidosTotalesTV.setText(String.valueOf(equipoActual
            .getPartidosPerdidosTotales()));
        holder.puntosFavorTotalesTV.setText(String.valueOf(equipoActual.getPuntosFavorTotales()));
        holder.puntosContraTotalesTV.setText(String.valueOf(equipoActual.getPuntosContraTotales()));
        String rachaUltimosPartidos = holder.itemView.getContext()
            .getString(R.string.racha_ultimos_partidos,
                equipoActual.getPartidosGanadosUltimos10(),
                equipoActual.getPartidosPerdidosUltimos10()
            );
        holder.rachaUltimos10TV.setText(rachaUltimosPartidos);

        /* Cambiar colores de los MaterialCard en función de la posición */
        if (equipoActual.getPosicion() > 8) { // Quedan eliminados
            holder.cardClasificacion.setStrokeColor(context.getColor(R.color.naranja_menos_vivo));
            holder.cardClasificacion.setCardBackgroundColor(context.getColor(R.color.naranja_claro));
        } else { // Pasan a los playoffs
            holder.cardClasificacion.setStrokeColor(context.getColor(R.color.naranja_vivo));
            holder.cardClasificacion.setCardBackgroundColor(context.getColor(R.color.naranja_oscuro));
        }

        /* Cambiar apariencia de la estrella en función de es un equipo favorito o no */
        if (seleccionados[position]) {
            holder.añadirEliminarFavoritosIV.setImageResource(R.drawable.ic_favorito_true_32dp);
        } else {
            holder.añadirEliminarFavoritosIV.setImageResource(R.drawable.ic_favorito_false_32dp);
        }
    }

    @Override
    public int getItemCount() {
        return clasificacion.length;
    }
}
