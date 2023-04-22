package com.example.dasentregaindividual2.perfil;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.menu_principal.MenuPrincipalFragment;
import com.example.dasentregaindividual2.servidor.base_de_datos.usuario.CambiarFotoDePerfil;
import com.example.dasentregaindividual2.servidor.base_de_datos.usuario.ExisteParUsuarioContraseña;
import com.example.dasentregaindividual2.servidor.base_de_datos.usuario.RecuperarFotoDePerfil;

import java.io.ByteArrayOutputStream;

public class PerfilFragment extends Fragment {

    /* Atributos de la interfaz gráfica */
    private ImageView fotoPerfilIV;
    private Button botonCambiarFotoPerfil;

    /* Otros atributos */
    private ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new
                    ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData()!= null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap fotoBitmap = (Bitmap) bundle.get("data");
                    fotoPerfilIV.setImageBitmap(fotoBitmap);
                    subirFoto(fotoBitmap);
                } else {
                    Log.d("TakenPicture", "No photo taken");
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fotoPerfilIV = view.findViewById(R.id.foto_perfil);

        botonCambiarFotoPerfil = view.findViewById(R.id.boton_cambiar_foto_perfil);
        botonCambiarFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sacarFoto();
            }
        });

        recuperarFotoDePerfil();
    }

    private void sacarFoto() {
        Intent elIntentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(elIntentFoto);
    }

    private void subirFoto(Bitmap fotoBitmap) {
        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(requireContext());
        String usuario = preferencias.getString("usuario", null);
        Log.d("PerfilFragment", "ORIGINAL --> " + fotoBitmap.getByteCount());
        Bitmap bitmapReescalado = reescalarBitmap(fotoBitmap);
        Log.d("PerfilFragment", "REESCALADO --> " + bitmapReescalado.getByteCount());
        String fotoPerfilBase64 = bitmapABase64(bitmapReescalado);
        Data parametros = new Data.Builder()
                .putString("nombreUsuario", usuario)
                .putString("fotoPerfilBase64", fotoPerfilBase64)
                .build();

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(CambiarFotoDePerfil.class)
                .setConstraints(restricciones)
                .setInputData(parametros)
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {

                    /*
                     * Una vez completada la consulta, se comprueba el resultado de la consulta.
                     * Si se da la condición 'cantidadUsuarios == 1' se procede a completar el
                     * proceso de inicio de sesión
                     */
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            String consultaExitosaStr = workInfo.getOutputData()
                                    .getString("consultaExitosa");
                            if (consultaExitosaStr != null) {
                                int consultaExitosa = Integer.parseInt(consultaExitosaStr);
                                if (consultaExitosa == 1) { // La consulta ha sido EXITOSA
                                    Toast.makeText(
                                            requireContext(),
                                            "La foto de perfil se ha actualizado con éxito",
                                            Toast.LENGTH_SHORT)
                                    .show();
                                } else { // Ha ocurrido un ERROR en la consulta
                                    Toast.makeText(
                                            requireContext(),
                                            "Ha ocurrido un error al actualizar la foto de perfil",
                                            Toast.LENGTH_SHORT)
                                    .show();
                                }

                            }
                        }
                    }
                });

        WorkManager.getInstance(requireContext()).enqueue(otwr);
    }

    private String bitmapABase64(Bitmap fotoBitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        fotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] fotoTransformada = stream.toByteArray();
        return Base64.encodeToString(fotoTransformada,Base64.DEFAULT);
    }

    private Bitmap reescalarBitmap(Bitmap bitmapFoto) {
        int anchoDestino = fotoPerfilIV.getWidth();
        int altoDestino = fotoPerfilIV.getHeight();
        Log.d("PerfilFragment", "ImageView --> ANCHO: " + anchoDestino + " ALTO: " + altoDestino);
        int anchoImagen = bitmapFoto.getWidth();
        int altoImagen = bitmapFoto.getHeight();
        Log.d("PerfilFragment", "Bitmap --> ANCHO: " + anchoImagen + " ALTO: " + altoImagen);
        float ratioImagen = (float) anchoImagen / (float) altoImagen;
        float ratioDestino = (float) anchoDestino / (float) altoDestino;
        int anchoFinal = anchoDestino;
        int altoFinal = altoDestino;
        if (ratioDestino > ratioImagen) {
            anchoFinal = (int) ((float)altoDestino * ratioImagen);
        } else {
            altoFinal = (int) ((float)anchoDestino / ratioImagen);
        }
        return Bitmap.createScaledBitmap(bitmapFoto,anchoFinal,altoFinal,true);
    }

    private void recuperarFotoDePerfil() {
        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(requireContext());
        String usuario = preferencias.getString("usuario", null);
        Data parametros = new Data.Builder()
                .putString("nombreUsuario", usuario)
                .build();

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(RecuperarFotoDePerfil.class)
                .setConstraints(restricciones)
                .setInputData(parametros)
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(otwr2.getId())
                .observe((LifecycleOwner) requireContext(), new Observer<WorkInfo>() {

                    /*
                     * CAMBIAR COMENTARIO
                     *
                     * Una vez completada la consulta, se comprueba el resultado de la consulta.
                     * Si se da la condición 'cantidadUsuarios == 1' se procede a completar el
                     * proceso de inicio de sesión
                     */
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            String fotoPerfilBase64 = workInfo.getOutputData()
                                    .getString("fotoPerfilBase64");
                            Log.d("PerfilFragment", "foto --> '" + fotoPerfilBase64 + "'");
                            if (fotoPerfilBase64 != null) {
                                Bitmap fotoPerfilBitmap = base64StringABitmap(fotoPerfilBase64);
                                fotoPerfilIV.setImageBitmap(fotoPerfilBitmap);
                            } else {
                                fotoPerfilIV.setImageResource(R.drawable.ic_usuario_24dp);
                            }
                        }
                    }
                });

        WorkManager.getInstance(requireContext()).enqueue(otwr2);
    }

    private Bitmap base64StringABitmap(String base64String) {
        try{
            byte [] encodeByte = Base64.decode(base64String,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}