package com.example.dasentregaindividual2.lista_partidos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.base_de_datos.modelos.EquipoPartido;
import com.example.dasentregaindividual2.base_de_datos.modelos.Partido;

public class ListaPartidosAdapter extends RecyclerView.Adapter<ListaPartidosViewHolder> {

    private Partido[] partidosJornada;
    private boolean[] seleccionados;

    public ListaPartidosAdapter(Partido[] pPartidosJornada) {
        partidosJornada = pPartidosJornada;
        seleccionados = new boolean[pPartidosJornada.length];
    }

    @NonNull
    @Override
    public ListaPartidosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listaPartidosItem = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.lista_partidos_item, parent, false);
        ListaPartidosViewHolder listaPartidosViewHolder = new ListaPartidosViewHolder(
            listaPartidosItem);
        listaPartidosViewHolder.seleccion = seleccionados;
        return listaPartidosViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListaPartidosViewHolder holder, int position) {
        /* Cargar datos del equipo local */
        EquipoPartido equipoLocal = partidosJornada[position].getEquipos()[0];
        holder.escudoEquipoLocalIV.setImageResource(equipoLocal.getEscudoId());
        holder.nombreEquipoLocalTV.setText(equipoLocal.getNombre());
        holder.ultimosPartidosEquipoLocalTV.setText(equipoLocal.getRachaUltimosPartidos());
        holder.puntosEquipoLocalTV.setText(String.valueOf(equipoLocal.getPuntos()));

        /* Cargar datos del equipo visitante */
        EquipoPartido equipoVisitante = partidosJornada[position].getEquipos()[1];
        holder.escudoEquipoVisitanteIV.setImageResource(equipoVisitante.getEscudoId());
        holder.nombreEquipoVisitanteTV.setText(equipoVisitante.getNombre());
        holder.ultimosPartidosEquipoVisitanteTV.setText(equipoVisitante.getRachaUltimosPartidos());
        holder.puntosEquipoVisitanteTV.setText(String.valueOf(equipoVisitante.getPuntos()));

        /* Cargar el resto de datos del partido (fecha y hora) */
        holder.fechaPartidoTV.setText(partidosJornada[position].getFecha());
        holder.horaPartidoTV.setText(partidosJornada[position].getHora());

        /* Cambiar colores de los MaterialCard (alternando 2 diseños) */
        Context context = holder.itemView.getContext();
        if (position % 2 == 1) {
            holder.cardPartido.setStrokeColor(context.getColor(R.color.naranja_vivo));
            holder.cardPartido.setCardBackgroundColor(context.getColor(R.color.naranja_oscuro));
        } else {
            holder.cardPartido.setStrokeColor(context.getColor(R.color.naranja_menos_vivo));
            holder.cardPartido.setCardBackgroundColor(context.getColor(R.color.naranja_claro));
        }
    }

    @Override
    public int getItemCount() {
        return partidosJornada.length;
    }
}
