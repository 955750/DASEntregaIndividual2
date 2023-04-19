package com.example.dasentregaindividual2.lista_partidos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.servidor.base_de_datos.ListarEquiposDeUnPartido;
import com.example.dasentregaindividual2.servidor.base_de_datos.modelos.EquipoPartido;
import com.example.dasentregaindividual2.servidor.base_de_datos.modelos.Partido;
import com.example.dasentregaindividual2.servidor.base_de_datos.partido.ListarPartidos;

import org.json.JSONArray;
import org.json.JSONException;

public class ListaPartidosFragment extends Fragment {

    /* Atributos de la interfaz gráfica */
    private RecyclerView jornadasRecyclerView;

    /* Otros atributos */
    private ListenerListaPartidosFragment listenerListaPartidosFragment;
    private Partido[] listaPartidos;
    private int listaPartidosInd;


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

        /* RECUPERAR DATOS DEL PARTIDO SELECCIONADO */
        if (getArguments() != null) {
            int cantidadEquiposFavoritos = getArguments().getInt("cantidadFavoritos");
            listenerListaPartidosFragment.borrarNotificaciones(cantidadEquiposFavoritos);
        }

        listaPartidos = new Partido[9];
        listaPartidosInd = 0;
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
        recuperarListaDePartidos();
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

    /*
     * En esta función, se encola una tarea cuyo cometido es lanzar la siguiente consulta contra
     * la base de datos remota (la tarea requiere de conexión a internet para poder acceder a la
     * base de datos alojada en el servidor):
     *
     * SELECT * FROM Partido
     */
    private void recuperarListaDePartidos() {
        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ListarPartidos.class)
                .setConstraints(restricciones)
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(otwr.getId())
                .observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {

                    /*
                     * Una vez completada la consulta, se recupera la información que nos
                     * devuelve esta y se pasa a la función 'recuperarEquiposDeUnPartido' para
                     * recuperar la información de los equipos que juegan cada partido.
                     */
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()) {
                            try {
                                String listaPartidosStr = workInfo.getOutputData()
                                        .getString("listaPartidos");
                                JSONArray listaPartidosJSON = new JSONArray(listaPartidosStr);
                                for (int i = 0; i < listaPartidosJSON.length(); i++) {
                                    String partidoId = listaPartidosJSON.getJSONObject(i)
                                            .getString("partidoId");
                                    // Este dato sería útil de cara a agregar más de una jornada
                                    Integer numJornada = Integer.parseInt(listaPartidosJSON
                                            .getJSONObject(i).getString("numJornada"));
                                    String fecha = listaPartidosJSON.getJSONObject(i)
                                            .getString("fecha");
                                    String hora = listaPartidosJSON.getJSONObject(i)
                                            .getString("hora");
                                    recuperarEquiposDeUnPartido(partidoId, numJornada, fecha, hora);
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
     * SELECT j.puntos, j.partido_id, j.local, e.nombre, e.escudo_id, e.part_ganados_ult_10,
     * e.part_perdidos_ult_10
     * FROM Juega AS j INNER JOIN Equipo AS e ON j.nombre_equipo = e.nombre
     * WHERE j.partido_id = ?
     */
    private void recuperarEquiposDeUnPartido(
            String partidoId,
            Integer numJornada,
            String fecha,
            String hora
    ) {
        Data parametros = new Data.Builder()
                .putString("partidoId", partidoId)
                .build();

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(ListarEquiposDeUnPartido.class)
                .setConstraints(restricciones)
                .setInputData(parametros)
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(otwr2.getId())
                .observe(this, new Observer<WorkInfo>() {

                    /*
                     * Una vez completada la consulta, por cada equipo que disputa el partido
                     * se recupera su correspondiente información y se transforma en clases
                     * modelo de forma que los datos recuperados se puedan usar en la aplicación
                     * para mostrarlos en el 'RecyclerView' del fragmento
                     */
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()) {
                            try {
                                String listaEquiposPartidoStr = workInfo.getOutputData()
                                        .getString("listaEquiposPartido");
                                JSONArray listaEquiposPartidoJSON = new JSONArray(
                                        listaEquiposPartidoStr);
                                EquipoPartido[] equiposPartido = new EquipoPartido[2];
                                int j = 0;
                                for(int i = 0; i < listaEquiposPartidoJSON.length(); i++) {
                                    int puntos = Integer.parseInt(listaEquiposPartidoJSON
                                            .getJSONObject(i).getString("puntos"));
                                    int local = Integer.parseInt(listaEquiposPartidoJSON
                                            .getJSONObject(i).getString("local"));
                                    String nombre = listaEquiposPartidoJSON.getJSONObject(i)
                                            .getString("nombre");
                                    int escudoId = Integer.parseInt(listaEquiposPartidoJSON
                                            .getJSONObject(i).getString("escudoId"));
                                    int partGanUlt10 = Integer.parseInt(listaEquiposPartidoJSON
                                            .getJSONObject(i).getString("partGanUlt10"));
                                    int partPerUlt10 = Integer.parseInt(listaEquiposPartidoJSON
                                            .getJSONObject(i).getString("partPerUlt10"));

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
                                Partido partidoJornada = new Partido(equiposPartido, fecha, hora);
                                listaPartidos[listaPartidosInd] = partidoJornada;
                                listaPartidosInd++;
                                if (listaPartidosInd == 9) {
                                    jornadasRecyclerView.setAdapter(
                                            new ListaPartidosAdapter(listaPartidos)
                                    );
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });

        WorkManager.getInstance(requireContext()).enqueue(otwr2);
    }
}