package com.example.dasentregaindividual2.lista_partidos;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
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
import com.example.dasentregaindividual2.servidor.base_de_datos.equipo.RecuperarEscudoEquipo;
import com.example.dasentregaindividual2.servidor.base_de_datos.modelos.EquipoPartido;
import com.example.dasentregaindividual2.servidor.base_de_datos.modelos.Partido;
import com.example.dasentregaindividual2.servidor.base_de_datos.partido.ListarPartidos;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;

public class ListaPartidosFragment extends Fragment {

    /* Atributos de la interfaz gráfica */
    private RecyclerView jornadasRecyclerView;

    /* Otros atributos */
    private ListenerListaPartidosFragment listenerListaPartidosFragment;
    private Partido[] listaPartidos;
    private int listaPartidosInd;
    private EquipoPartido[][] equiposPartidos;
    private int equiposPartidosInd;


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
        equiposPartidos = new EquipoPartido[9][2];
        equiposPartidosInd = 0;
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
        verCalendarios();
    }

    /*
     * Mediante esta función obtenemos acceso a la lista de los calendarios del dispositivo
     * haciendo uso del 'content provider' de los calendarios
     */
    private void verCalendarios() {
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] columnas = new String[] {
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };
        String condicion = null;
        String[] argumentos = null;
        String orden = null;
        Cursor cursor = requireActivity().getContentResolver()
                .query(uri, columnas, condicion, argumentos, orden);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            Log.d("ListaPartidosFragment", "id = " + id);
            String accountName = cursor.getString(1);
            Log.d("ListaPartidosFragment", "accountName = " + accountName);
            String calendarDisplayName = cursor.getString(2);
            Log.d("ListaPartidosFragment", "calendarDisplayName = " + calendarDisplayName);
            String ownerAccount = cursor.getString(3);
            Log.d("ListaPartidosFragment", "ownerAccount = " + ownerAccount);
        }
    }

    /*
     * Con esta función, por cada partido de la jornada se añade un evento a un calendario
     * especificando la fecha, hora de inicio y final, y una breve descripción del evento.
     */
    private void crearEventosNuevos() {
        for (Partido partido : listaPartidos) {
            String[] datosFecha = partido.getFecha().split("-");
            String[] datosHora = partido.getHora().split(":");
            int horasInicio = Integer.parseInt(datosHora[0]);
            int minutosInicio = Integer.parseInt(datosHora[1]);
            int minutosFinal = minutosInicio + 90;
            int horasFinal = horasInicio + minutosFinal / 60;
            minutosFinal = minutosFinal % 60;
            crearEventoNuevo(
                Integer.parseInt(datosFecha[0]),
                Integer.parseInt(datosFecha[1]),
                Integer.parseInt(datosFecha[2]),
                horasInicio,
                minutosInicio,
                horasFinal,
                minutosFinal,
                partido.getEquipos()[0].getNombre(),
                partido.getEquipos()[1].getNombre()
            );
        }
    }

    /*
     * Esta función es la encargada de crear un evento individual. Par ello hace uso de los datos
     * del partido que recoge como parámetros y añade el evento correpondiente a un calendario.
     * Hay que destacar que el calendario con ID 1 en mi teléfono es un calendario local (el
     * único modificable), por lo que es posible que en otros dispositivos no funcione, al no
     * tener estos calendarios locales
     */
    private void crearEventoNuevo(int pAño, int pMes, int pDia, int pHorasInicio,
                                  int pMinutosInicio, int pHorasFinal, int pMinutosFinal,
                                  String pLocal, String pVisitante) {
        long calID = 1;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(pAño, pMes - 1, pDia, pHorasInicio, pMinutosInicio);
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(pAño, pMes - 1, pDia, pHorasFinal, pMinutosFinal);
        long endMillis = endTime.getTimeInMillis();

        ContentResolver cr = requireActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, pLocal + " - " + pVisitante);
        values.put(CalendarContract.Events.DESCRIPTION,
            "Partido de la jornada 29 entre " + pLocal + " y " + pVisitante + ".");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
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
                     * se recupera su correspondiente información y se envía a la función
                     * 'recuperarEscudoEquipo' para ser añadir el escudo de cada equipo al resto
                     * de los datos
                     */
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()) {
                            try {
                                String listaEquiposPartidoStr = workInfo.getOutputData()
                                        .getString("listaEquiposPartido");
                                JSONArray listaEquiposPartidoJSON = new JSONArray(
                                        listaEquiposPartidoStr);
                                for(int i = 0; i < listaEquiposPartidoJSON.length(); i++) {
                                    int puntos = Integer.parseInt(listaEquiposPartidoJSON
                                            .getJSONObject(i).getString("puntos"));
                                    int local = Integer.parseInt(listaEquiposPartidoJSON
                                            .getJSONObject(i).getString("local"));
                                    String nombre = listaEquiposPartidoJSON.getJSONObject(i)
                                            .getString("nombre");
                                    int partGanUlt10 = Integer.parseInt(listaEquiposPartidoJSON
                                            .getJSONObject(i).getString("partGanUlt10"));
                                    int partPerUlt10 = Integer.parseInt(listaEquiposPartidoJSON
                                            .getJSONObject(i).getString("partPerUlt10"));

                                    recuperarEscudoEquipo(puntos, local, nombre,
                                            partGanUlt10, partPerUlt10, fecha, hora,
                                            Integer.parseInt(partidoId));
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });

        WorkManager.getInstance(requireContext()).enqueue(otwr2);
    }

    private void recuperarEscudoEquipo(
        int pPuntos,
        int pLocal,
        String pNombre,
        int pPartGanUlt10,
        int pPartPerUlt10,
        String pFecha,
        String pHora,
        int pPartidoId
    ) {
        Data parametros = new Data.Builder()
                .putString("nombreEquipo", pNombre)
                .build();

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(RecuperarEscudoEquipo.class)
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
                            String escudoEquipo = workInfo.getOutputData()
                                    .getString("escudoEquipo");
                            Log.d("ClasificacionFragment", escudoEquipo);
                            if (escudoEquipo != null) {
                                EquipoPartido equipoConEscudo = new EquipoPartido(
                                        escudoEquipo,
                                        pNombre,
                                        getString(R.string.racha_ultimos_partidos, pPartGanUlt10, pPartPerUlt10),
                                        pPuntos
                                );

                                if (pLocal == 1) {
                                    equiposPartidos[pPartidoId - 1][0] = equipoConEscudo;
                                } else {
                                    equiposPartidos[pPartidoId - 1][1] = equipoConEscudo;
                                }
                            }
                            equiposPartidosInd++;
                            if (equiposPartidosInd == 18) {
                                for(int i = 0; i < equiposPartidos.length; i++) {
                                    EquipoPartido[] equiposPartidoActual = new EquipoPartido[2];
                                    equiposPartidoActual[0] = equiposPartidos[i][0];
                                    equiposPartidoActual[1] = equiposPartidos[i][1];
                                    Partido partidoJornada = new Partido(equiposPartidoActual,
                                            pFecha, pHora);
                                    listaPartidos[listaPartidosInd] = partidoJornada;
                                    listaPartidosInd++;
                                }
                                jornadasRecyclerView.setAdapter(
                                        new ListaPartidosAdapter(listaPartidos)
                                );
                                crearEventosNuevos();
                            }
                        }
                    }
                });

        WorkManager.getInstance(requireContext()).enqueue(otwr2);
    }
}