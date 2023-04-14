package com.example.dasentregaindividual2.login;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.base_de_datos.usuario.ExisteParUsuarioContraseña;
import com.google.android.material.textfield.TextInputEditText;


public class LoginFragment extends Fragment {

    /* Atributos de la interfaz gráfica */
    private TextInputEditText usuarioTV;
    private TextInputEditText contraseñaTV;
    private Button botonIniciarSesion;
    private Button botonCrearCuenta;


    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        usuarioTV = view.findViewById(R.id.campo_usuario);
        contraseñaTV = view.findViewById(R.id.campo_contraseña);
        botonIniciarSesion = view.findViewById(R.id.boton_iniciar_sesion);
        botonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarCampos();
            }
        });

        botonCrearCuenta = view.findViewById(R.id.boton_crear_cuenta);
        botonCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navegarHaciaCrearCuenta(view);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences preferencias = PreferenceManager
            .getDefaultSharedPreferences(requireContext());
        String usuario = preferencias.getString("usuario", null);
        if (usuario != null) {
            navegarHaciaMenuPrincipal();
        }
    }

    /*
     * CAMBIAR COMENTARIO
     * En esta función, primero se comprueba si alguno de los 2 campos está vacío. Si ninguno de
     * los campos está vacío, se comprueba si el par usuario - contraseña existe contra la base
     * de datos remota.
     */
    private void comprobarCampos() {
        if (usuarioTV.getText().toString().equals("")) {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_campo_vacio, "Usuario"),
                Toast.LENGTH_SHORT)
            .show();
        } else if (contraseñaTV.getText().toString().equals("")) {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_campo_vacio, "Contraseña"),
                Toast.LENGTH_SHORT)
            .show();
        } else {
            comprobarUsuarioCorrecto(
                usuarioTV.getText().toString(),
                contraseñaTV.getText().toString()
            );
        }
    }

    /*
     * En esta función, se encola una tarea cuyo cometido es lanzar la siguiente consulta contra
     * la base de datos remota (la tarea requiere de conexión a internet para poder acceder a la
     * base de datos alojada en el servidor):
     *
     * SELECT COUNT(*) FROM Usuario
     * WHERE nombre_usuario = ? AND
     * contraseña = ?
     */
    private void comprobarUsuarioCorrecto(String pUsuario, String pContraseña) {
        Data parametros = new Data.Builder()
                .putString("nombreUsuario", pUsuario)
                .putString("contraseña", pContraseña)
                .putInt("opcion", 1)
                .build();

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ExisteParUsuarioContraseña.class)
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
                        if(workInfo != null && workInfo.getState().isFinished()) {
                            String cantidadUsuariosStr = workInfo.getOutputData()
                                    .getString("cantidadUsuarios");
                            if (cantidadUsuariosStr != null) {
                                int cantidadUsuarios = Integer.parseInt(cantidadUsuariosStr);
                                if (cantidadUsuarios == 1) { // El par usuario - contraseña EXISTE
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.toast_usuario_contraseña_correctos),
                                        Toast.LENGTH_SHORT
                                    ).show();
                                    iniciarSesion(usuarioTV.getText().toString());
                                    navegarHaciaMenuPrincipal();
                                } else { // El par usuario - contraseña NO EXISTE
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.toast_usuario_contraseña_incorrectos),
                                        Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                        }
                    }
                });

        WorkManager.getInstance(requireContext()).enqueue(otwr);
    }


    /*
     * En esta función, si hay algún usuario que cumpla las características de la consulta
     * (cantidadUsuarios = 1) significará que los datos de inicio de sesión son correctos.
     */

    private void iniciarSesion(String pUsuario) {
        SharedPreferences preferencias = PreferenceManager
            .getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("usuario", pUsuario);
        editor.apply();
    }

    private void navegarHaciaMenuPrincipal() {
        NavDirections accion = LoginFragmentDirections
            .actionLoginFragmentToMenuPrincipalFragment();
        NavHostFragment.findNavController(this).navigate(accion);
    }

    private void navegarHaciaCrearCuenta(View view) {
        NavDirections accion = LoginFragmentDirections
            .actionLoginFragmentToCrearCuentaFragment();
        Navigation.findNavController(view).navigate(accion);
    }
}