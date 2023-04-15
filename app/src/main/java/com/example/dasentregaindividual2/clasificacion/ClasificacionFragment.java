package com.example.dasentregaindividual2.clasificacion;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.example.dasentregaindividual2.base_de_datos.BaseDeDatos;
import com.example.dasentregaindividual2.base_de_datos.equipo.ListarEquiposOrdenAscDerrotas;
import com.example.dasentregaindividual2.base_de_datos.favorito.EsEquipoFavorito;
import com.example.dasentregaindividual2.base_de_datos.modelos.EquipoClasificacion;
import com.example.dasentregaindividual2.base_de_datos.partido.ListarPartidos;
import com.example.dasentregaindividual2.base_de_datos.usuario.ExisteParUsuarioContraseña;
import com.example.dasentregaindividual2.lista_partidos.ListaPartidosAdapter;

import org.json.JSONArray;
import org.json.JSONException;

public class ClasificacionFragment extends Fragment {

    /* Atributos de la interfaz gráfica */
    private RecyclerView clasificacionRecyclerView;

    /* Otros atributos */
    private EquipoClasificacion[] listaEquipos;
    int listaEquiposInd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listaEquipos = new EquipoClasificacion[18];
        listaEquiposInd = 0;
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
        recuperarListaDeEquipos();
    }

    /*
     * En esta función, se encola una tarea cuyo cometido es lanzar la siguiente consulta contra
     * la base de datos remota (la tarea requiere de conexión a internet para poder acceder a la
     * base de datos alojada en el servidor):
     *
     * SELECT * FROM Equipo
     * ORDER BY part_perdidos_tot ASC
     */
    private void recuperarListaDeEquipos() {
        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ListarEquiposOrdenAscDerrotas.class)
                .setConstraints(restricciones)
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(otwr.getId())
                .observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {

                    /*
                     * Una vez completada la consulta, se recupera la información que nos
                     * devuelve esta y se pasa a la función 'recuperarEquiposFavoritos' para
                     * poder mostrar en pantalla con una estrella los equipos que son favoritos.
                     */
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()) {
                            try {
                                String listaEquiposStr = workInfo.getOutputData()
                                        .getString("listaEquipos");
                                JSONArray listaEquiposJSON = new JSONArray(listaEquiposStr);
                                for (int i = 0; i < listaEquiposJSON.length(); i++) {
                                    int posicion = i + 1;
                                    String nombre = listaEquiposJSON.getJSONObject(i)
                                            .getString("nombre");
                                    int escudoId = Integer.parseInt(listaEquiposJSON.getJSONObject(i)
                                            .getString("escudoId"));
                                    int partGanTot = Integer.parseInt(listaEquiposJSON.getJSONObject(i)
                                            .getString("partGanTot"));
                                    int partPerdTot = Integer.parseInt(listaEquiposJSON.getJSONObject(i)
                                            .getString("partPerdTot"));
                                    int puntFavor = Integer.parseInt(listaEquiposJSON.getJSONObject(i)
                                            .getString("puntFavor"));
                                    int puntContra = Integer.parseInt(listaEquiposJSON.getJSONObject(i)
                                            .getString("puntContra"));
                                    int partGanUlt10 = Integer.parseInt(listaEquiposJSON.getJSONObject(i)
                                            .getString("partGanUlt10"));
                                    int partPerUlt10 = Integer.parseInt(listaEquiposJSON.getJSONObject(i)
                                            .getString("partPerUlt10"));

                                    EquipoClasificacion equipo = new EquipoClasificacion(
                                            posicion, escudoId, nombre, partGanTot, partPerdTot, puntFavor, puntContra,
                                            partGanUlt10, partPerUlt10, false
                                    );
                                    recuperarEquiposFavoritos(equipo);
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });

        WorkManager.getInstance(requireContext()).enqueue(otwr);
    }

    /*
     * En esta función, se encola una tarea cuyo cometido es lanzar la siguiente consulta contra
     * la base de datos remota (la tarea requiere de conexión a internet para poder acceder a la
     * base de datos alojada en el servidor):
     *
     * SELECT COUNT(*) FROM Favorito
     * WHERE nombre_usuario = ?
     * AND nombre_equipo = ?
     */
    private void recuperarEquiposFavoritos(EquipoClasificacion eq) {
        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(requireContext());
        String usuario = preferencias.getString("usuario", null);

        Data parametros = new Data.Builder()
                .putString("nombreUsuario", usuario)
                .putString("nombreEquipo", eq.getNombre())
                .build();

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(EsEquipoFavorito.class)
                .setConstraints(restricciones)
                .setInputData(parametros)
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(otwr2.getId())
                .observe(this, new Observer<WorkInfo>() {

                    /*
                     * Una vez completada la consulta, se comprueba el resultado de la consulta.
                     * Si se da la condición 'esFavorito == 1' el valor 'pEsFavorito' de la clase
                     * modelo 'EquipoClasificacion' pasa a ser 'true'. Una vez definido ese valor
                     * se procede a añadir el equipo a la lista.
                     */
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            String esFavoritoStr = workInfo.getOutputData()
                                    .getString("esFavorito");
                            if (esFavoritoStr != null) {
                                int esFavorito = Integer.parseInt(esFavoritoStr);
                                if (esFavorito == 1) { // El equipo ES FAVORITO
                                    EquipoClasificacion equipoFav = new EquipoClasificacion(
                                            eq.getPosicion(), eq.getEscudoId(), eq.getNombre(),
                                            eq.getPartidosGanadosTotales(),
                                            eq.getPartidosPerdidosTotales(),
                                            eq.getPuntosFavorTotales(),
                                            eq.getPuntosContraTotales(),
                                            eq.getPartidosGanadosUltimos10(),
                                            eq.getPartidosPerdidosUltimos10(),
                                            true
                                    );
                                    listaEquipos[listaEquiposInd] = equipoFav;
                                } else { // El equipo NO ES FAVORITO
                                    listaEquipos[listaEquiposInd] = eq;
                                }

                                listaEquiposInd++;
                                if (listaEquiposInd == 18) {
                                    clasificacionRecyclerView.setAdapter(
                                            new ClasificacionAdapter(listaEquipos)
                                    );
                                }
                            }
                        }
                    }
                });

        WorkManager.getInstance(requireContext()).enqueue(otwr2);
    }
}
