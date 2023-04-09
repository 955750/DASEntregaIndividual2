package com.example.dasentregaindividual2.lista_partidos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.base_de_datos.BaseDeDatos;
import com.example.dasentregaindividual2.base_de_datos.modelos.EquipoPartido;
import com.example.dasentregaindividual2.base_de_datos.modelos.Partido;

public class ListaPartidosFragment extends Fragment {

    /* Atributos de la interfaz gráfica */
    private RecyclerView jornadasRecyclerView;

    /* Otros atributos */
    private SQLiteDatabase baseDeDatos;
    private ListenerListaPartidosFragment listenerListaPartidosFragment;


    /*
     * Interfaz para que 'MainActivity' implemente la notificación del partido y que aparezca al
     * acceder a este fragmento.
     */
    public interface ListenerListaPartidosFragment {
        void borrarNotificaciones(int cantidadFavoritos);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Recuperar instancia de la base de datos */
        BaseDeDatos gestorBD = new BaseDeDatos(requireContext(), "Euroliga",
                null, 1);
        baseDeDatos = gestorBD.getWritableDatabase();

        /* RECUPERAR DATOS DEL PARTIDO SELECCIONADO */
        if (getArguments() != null) {
            int cantidadEquiposFavoritos = getArguments().getInt("cantidadFavoritos");
            Log.d("ListaPartidosFragment", String.valueOf(cantidadEquiposFavoritos));
            listenerListaPartidosFragment.borrarNotificaciones(cantidadEquiposFavoritos);
        }
    }

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_lista_partidos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        jornadasRecyclerView = view.findViewById(R.id.jornadas_recycler_view);
        jornadasRecyclerView.setAdapter(new ListaPartidosAdapter(recuperarListaDePartidos()));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listenerListaPartidosFragment = (ListenerListaPartidosFragment) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("La clase " + context
                + "debe implementar ListenerListaPartidosFragment");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        baseDeDatos.close();
    }

    private Partido[] recuperarListaDePartidos() {
        Cursor cPartido = baseDeDatos.rawQuery(
            "SELECT * FROM Partido", null
        );
        Partido[] listaPartidos = new Partido[9];
        int j = 0;
        while (cPartido.moveToNext()) {
            /*
             * Formatear los resultados de la consulta de forma que podamos hacer uso de ellos en la
             * aplicación.
             */
            Integer partidoId = cPartido.getInt(0);
            Integer numJornada = cPartido.getInt(1); // Este dato sería útil de cara a agregar más de una jornada
            String fecha = cPartido.getString(2);
            String hora = cPartido.getString(3);
            EquipoPartido[] equiposPartido = recuperarEquiposDeUnPartido(partidoId);
            listaPartidos[j] = new Partido(equiposPartido, fecha, hora);
            j++;
        }
        cPartido.close();
        return listaPartidos;
    }

    private EquipoPartido[] recuperarEquiposDeUnPartido(Integer partidoId){
        /*
        SELECT j.puntos, j.partido_id, j.local, e.nombre, e.escudo_id, e.part_ganados_ult_10,
        e.part_perdidos_ult_10
        FROM Juega AS j INNER JOIN Equipo AS e ON j.nombre_equipo = e.nombre
        WHERE j.partido_id = ?
        */
        String[] campos = new String[] {"j.puntos", "j.partido_id", "j.local", "e.nombre",
                "e.escudo_id", "e.part_ganados_ult_10", "e.part_perdidos_ult_10"};
        String[] argumentos = new String[] {partidoId.toString()};
        String tabla = "Juega AS j INNER JOIN Equipo AS e ON j.nombre_equipo = e.nombre";
        Cursor cEquipoPartido = baseDeDatos.query(tabla, campos, "j.partido_id = ?",
            argumentos, null, null, null);

        /*
         * Formatear los resultados de la consulta de forma que podamos hacer uso de ellos en la
         * aplicación.
         */
        EquipoPartido[] equiposPartido = new EquipoPartido[2];
        while (cEquipoPartido.moveToNext()) {
            int puntos = cEquipoPartido.getInt(0);
            int local = cEquipoPartido.getInt(2);
            String nombre = cEquipoPartido.getString(3);
            int escudoId = cEquipoPartido.getInt(4);
            int partGanUlt10 = cEquipoPartido.getInt(5);
            int partPerUlt10 = cEquipoPartido.getInt(6);
            EquipoPartido eq = new EquipoPartido(
                escudoId,
                nombre,
                getString(R.string.racha_ultimos_partidos, partGanUlt10, partPerUlt10),
                puntos
            );
            if (local == 1) {
                equiposPartido[0] = eq;
            } else {
                equiposPartido[1] = eq;
            }
        }
        cEquipoPartido.close();
        return equiposPartido;
    }
}