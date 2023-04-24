package com.example.dasentregaindividual2.localizar_estadio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.servidor.base_de_datos.equipo.RecuperarEquiposConCoordenadas;
import com.example.dasentregaindividual2.servidor.base_de_datos.equipo.RecuperarEscudoEquipo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;


public class LocalizarEstadioFragment extends Fragment implements OnMapReadyCallback {

    /* Atributos visuales */
    private Button botonAmpliarZoom, botonDisminuirZoom;
    private ChipGroup equiposChipGroup;

    /* Otros atributos */
    private GoogleMap elMapa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_localizar_estadio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        botonAmpliarZoom = view.findViewById(R.id.boton_ampliar_zoom);
        botonAmpliarZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elMapa.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        botonDisminuirZoom = view.findViewById(R.id.boton_disminuir_zoom);
        botonDisminuirZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elMapa.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });

        equiposChipGroup = view.findViewById(R.id.equipos_chip_group);

        SupportMapFragment fragmentoMapa = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.fragmentoMapa);
        fragmentoMapa.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        elMapa = googleMap;
        elMapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        crearChipsYMarcadoresDeEquipos();
        /*elMapa.addMarker(new MarkerOptions()
                .position(new LatLng(52.506359, 13.443642))
                .title("Mercedes-Benz Arena"));*/
    }

    private void crearChipsYMarcadoresDeEquipos() {
        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(RecuperarEquiposConCoordenadas.class)
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
                                    String nombre = listaEquiposJSON.getJSONObject(i)
                                            .getString("nombre");
                                    Float latitud = Float.parseFloat(listaEquiposJSON
                                            .getJSONObject(i).getString("latitud"));
                                    Float longitud = Float.parseFloat(listaEquiposJSON
                                            .getJSONObject(i).getString("longitud"));
                                    String nombreEstadio = listaEquiposJSON.getJSONObject(i)
                                            .getString("nombreEstadio");
                                    // recuperarEscudoEquipo(nombre, latitud, longitud);

                                    recuperarEscudoEquipo(i, nombre, latitud, longitud,
                                            nombreEstadio);
                                    /* CREAR CHIPS */
                                    // crearChip(i, nombre, latitud, longitud);
                                    /*Chip chip = (Chip) getLayoutInflater().inflate(
                                            R.layout.chip_localizar_estadio,
                                            equiposChipGroup,
                                            false
                                    );
                                    chip.setId(i);
                                    chip.setText(nombre);
                                    equiposChipGroup.addView(chip);*/
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });

        WorkManager.getInstance(requireContext()).enqueue(otwr);
    }

    private void recuperarEscudoEquipo(
        int pIdChip,
        String pNombre,
        Float pLatitud,
        Float pLongitud,
        String pNombreEstadio
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
                .observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {

                    /*
                     * Una vez completada la consulta, se recupera la información que nos
                     * devuelve esta y se pasa a la función 'recuperarEquiposFavoritos' para
                     * poder mostrar en pantalla con una estrella los equipos que son favoritos.
                     */

                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()) {
                            String escudoEquipo = workInfo.getOutputData()
                                    .getString("escudoEquipo");
                            Bitmap escudoBitmap = base64StringABitmap(escudoEquipo);
                            BitmapDrawable escudoDrawable = new BitmapDrawable(getResources(),
                                    escudoBitmap);
                            crearChip(pIdChip, pNombre, pLatitud, pLongitud, escudoDrawable);
                            añadirMarcador(pLatitud, pLongitud, pNombreEstadio);
                        }
                    }
                });

        WorkManager.getInstance(requireContext()).enqueue(otwr2);
    }

    private Bitmap base64StringABitmap(String base64String) {
        try{
            byte [] encodeByte = Base64.decode(base64String,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    private void crearChip(
        int pIdChip,
        String pNombre,
        Float pLatitud,
        Float pLongitud,
        BitmapDrawable pEscudo
    ) {
        Chip chip = (Chip) getLayoutInflater().inflate(
                R.layout.chip_localizar_estadio,
                equiposChipGroup,
                false
        );
        chip.setId(pIdChip);
        chip.setText(pNombre);
        chip.setChipIcon(pEscudo);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraUpdate actualizarLocalizacion = CameraUpdateFactory
                        .newLatLngZoom(new LatLng(pLatitud, pLongitud), 10);
                elMapa.animateCamera(actualizarLocalizacion);
                chip.setBackgroundColor(ContextCompat.getColor(requireContext(),
                        R.color.naranja_claro));
            }
        });
        equiposChipGroup.addView(chip);
    }

    private void añadirMarcador(Float pLatitud, Float pLongitud, String pNombreEstadio) {
        elMapa.addMarker(new MarkerOptions()
                .position(new LatLng(pLatitud, pLongitud))
                .title(pNombreEstadio));
    }
}