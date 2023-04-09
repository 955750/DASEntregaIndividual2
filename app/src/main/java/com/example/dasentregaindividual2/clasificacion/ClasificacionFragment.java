package com.example.dasentregaindividual2.clasificacion;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.base_de_datos.BaseDeDatos;
import com.example.dasentregaindividual2.base_de_datos.modelos.EquipoClasificacion;

public class ClasificacionFragment extends Fragment {

    /* Atributos de la interfaz gráfica */
    private RecyclerView clasificacionRecyclerView;

    /* Otros atributos */
    private SQLiteDatabase baseDeDatos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Recuperar instancia de la base de datos */
        BaseDeDatos gestorBD = new BaseDeDatos(requireContext(), "Euroliga",
            null, 1);
        baseDeDatos = gestorBD.getWritableDatabase();
    }

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_clasificacion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clasificacionRecyclerView = view.findViewById(R.id.clasifiacion_recycler_view);
        clasificacionRecyclerView.setAdapter(new ClasificacionAdapter(recuperarListaDeEquipos()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        baseDeDatos.close();
    }

    private EquipoClasificacion[] recuperarListaDeEquipos() {
        Cursor cEquipo = baseDeDatos.rawQuery(
        "SELECT * FROM Equipo " +
            "ORDER BY part_perdidos_tot ASC", null
        );
        EquipoClasificacion[] listaEquipos = new EquipoClasificacion[18];
        int ind = 0;
        while (cEquipo.moveToNext()) {
            /*
             * Formatear los resultados de la consulta de forma que podamos hacer uso de ellos en la
             * aplicación.
             */
            int posicion = ind + 1;
            String nombre = cEquipo.getString(0);
            int escudoId = cEquipo.getInt(1);
            int partGanTot = cEquipo.getInt(2);
            int partPerdTot = cEquipo.getInt(3);
            int puntFavor = cEquipo.getInt(4);
            int puntContra = cEquipo.getInt(5);
            int partGanUlt10 = cEquipo.getInt(6);
            int partPerUlt10 = cEquipo.getInt(7);
            listaEquipos[ind] = new EquipoClasificacion(
                posicion, escudoId, nombre, partGanTot, partPerdTot, puntFavor, puntContra,
                partGanUlt10, partPerUlt10, esEquipoFavorito(nombre)
            );
            ind++;
        }
        cEquipo.close();
        return listaEquipos;
    }

    /*
     * En esta función, si hay algún equipo con el usuario que ha iniciado sesión dentro de la
     * tabla de favoritos (cantidadEquipos = 1), significará que ese equipo esta añadido a los
     * favoritos del usuario
     */
    private boolean esEquipoFavorito(String nombreEquipo) {
        SharedPreferences preferencias = PreferenceManager
            .getDefaultSharedPreferences(requireContext());
        String usuario = preferencias.getString("usuario", null);
        /*
        SELECT COUNT(*) FROM Favorito
        WHERE nombre_usuario = ?
        AND nombre_equipo = ?
        */
        String[] campos = new String[] {"COUNT(*)"};
        String[] argumentos = new String[] {usuario, nombreEquipo};
        Cursor cFavorito = baseDeDatos.query("Favorito", campos,
            "nombre_usuario = ? AND nombre_equipo = ?", argumentos, null,
            null, null);

        cFavorito.moveToFirst();
        int cantidadEquipos = cFavorito.getInt(0);
        cFavorito.close();
        return cantidadEquipos == 1;
    }
}
