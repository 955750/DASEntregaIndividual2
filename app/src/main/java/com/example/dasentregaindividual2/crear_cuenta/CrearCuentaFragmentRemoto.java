package com.example.dasentregaindividual2.crear_cuenta;

import android.content.SharedPreferences;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.base_de_datos.usuario.ExisteUsuario;
import com.example.dasentregaindividual2.base_de_datos.usuario.InsertarUsuario;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

public class CrearCuentaFragmentRemoto extends Fragment {

    /* Atributos de la interfaz gráfica */
    private TextInputEditText usuarioTV;
    private TextInputEditText contraseñaTV;
    private TextInputEditText repetirContraseñaTV;
    private Button crearCuenta;


    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_crear_cuenta, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usuarioTV = view.findViewById(R.id.campo_usuario_crear_cuenta);
        contraseñaTV = view.findViewById(R.id.campo_contraseña_crear_cuenta);
        repetirContraseñaTV = view.findViewById(R.id.campo_repetir_contraseña_crear_cuenta);
        crearCuenta = view.findViewById(R.id.boton_crear_cuenta);
        crearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // crearUsuarioYHacerLogin(view);
                comprobarCampos();
            }
        });
    }
    /*
     * En esta función, primero se comprueba si alguno de los 3 campos está vacío. Si ninguno de
     * ellos está vacío, primero si el contenido de los 2 campos de contraseña coincide y en caso
     * de ser así se comprueba si cumple el formato adecuado. En caso de cumplir todos los
     * requisitos, se comprueba si el nombre de usuario está disponible contra la base de datos
     * remota.
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
        } else if (repetirContraseñaTV.getText().toString().equals("")) {
            Toast.makeText(
                            requireContext(),
                            getString(R.string.toast_campo_vacio, "Repetir Contraseña"),
                            Toast.LENGTH_SHORT)
                    .show();
        } else if (!contraseñaTV.getText().toString().equals(
                repetirContraseñaTV.getText().toString())
        ) {
            Toast.makeText(
                            requireContext(),
                            getString(R.string.toast_contraseñas_no_coinciden),
                            Toast.LENGTH_SHORT)
                    .show();
        } else if (contraseñaCumpleFormato()) {
            comprobarUsuarioValido(
                    usuarioTV.getText().toString()
            );
        }
    }

    /* Para la validación de la contraseña se ha utlizado de base el código que podemos encontrar
     * la en la siguiente página:
     * https://www.geeksforgeeks.org/how-to-validate-a-password-using-regular-expressions-in-java/
     */
    private boolean contraseñaCumpleFormato() {
        /*
            ^ represents starting character of the string.
            (?=.*[0-9]) represents a digit must occur at least once.
            (?=.*[a-z]) represents a lower case alphabet must occur at least once.
            (?=.*[A-Z]) represents an upper case alphabet that must occur at least once.
            (?=.*[@#$%^&-+=()] represents a special character that must occur at least once.
            (?=\\S+$) white spaces don’t allowed in the entire string.
            .{8, 20} represents at least 8 characters and at most 20 characters.
            $ represents the end of the string.
         */

        String contraseña = contraseñaTV.getText().toString();
        String regex = "^(?=.*[0-9])" +
                "(?=.*[a-z])(?=.*[A-Z])" +
                "(?=.*[@#$%^&+=])" +
                "(?=\\S+$).{8,20}$";
        Pattern patronContraseña = Pattern.compile(regex);
        boolean cumplePatron = patronContraseña.matcher(contraseña).matches();
        if (contraseña.length() < 8) {
            Toast.makeText(
                            requireContext(),
                            getString(R.string.toast_minimo_8_caracteres),
                            Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if (!cumplePatron) {
            Toast.makeText(
                            requireContext(),
                            getString(R.string.toast_formato_contraseña_invalido),
                            Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else {
            return true;
        }
    }

    /*
     * En esta función, se encola una tarea cuyo cometido es lanzar la siguiente consulta contra
     * la base de datos remota (la tarea requiere de conexión a internet para poder acceder a la
     * base de datos alojada en el servidor):
     *
     * SELECT COUNT(*) FROM Usuario
     * WHERE nombre_usuario = ?
     */
    private void comprobarUsuarioValido(String pUsuario) {
        Data parametros = new Data.Builder()
                .putString("nombreUsuario", pUsuario)
                .build();

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ExisteUsuario.class)
                .setConstraints(restricciones)
                .setInputData(parametros)
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {

                    /*
                     *
                     * Una vez completada la consulta, se comprueba el resultado de la consulta.
                     * En caso de cumplirse la condición 'cantidadUsuarios == 1' se procede a
                     * completar el proceso de creación de cuenta.
                     */
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()) {
                            String cantidadUsuariosStr = workInfo.getOutputData()
                                    .getString("cantidadUsuarios");
                            if (cantidadUsuariosStr != null) {
                                int cantidadUsuarios = Integer.parseInt(cantidadUsuariosStr);
                                if (cantidadUsuarios == 1) { // El nombre de usuario EXISTE
                                    Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.toast_nombre_usuario_existente),
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                } else { // El nombre de usuario NO EXISTE
                                    crearCuenta(
                                            usuarioTV.getText().toString(),
                                            contraseñaTV.getText().toString()
                                    );
                                }
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
     * INSERT INTO Usuario (nombre_usuario, contraseña)
     * VALUES (?, ?)
     */
    private void crearCuenta(String pUsuario, String pContraseña) {
        Data parametros = new Data.Builder()
                .putString("nombreUsuario", pUsuario)
                .putString("contraseña", pContraseña)
                .build();

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(InsertarUsuario.class)
                .setConstraints(restricciones)
                .setInputData(parametros)
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(otwr2.getId())
                .observe(this, new Observer<WorkInfo>() {

                    /*
                     * Una vez completada la consulta, se comprueba el resultado de la consulta.
                     * Si se da la condición 'consultaExitosa == 1' se procede a completar el
                     * proceso de creación de cuenta, se inicia sesión y accedemos a
                     * 'MenuPrincipalFragment'.
                     */
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()) {
                            String consultaExitosaStr = workInfo.getOutputData()
                                    .getString("cantidadUsuarios");
                            if (consultaExitosaStr != null) {
                                int consultaExitosa = Integer.parseInt(consultaExitosaStr);
                                Log.d("CrearCuentaFragment", consultaExitosaStr);
                                if (consultaExitosa == 1) { // La consulta ha sido EXITOSA
                                    Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.toast_cuenta_creada_con_exito),
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    iniciarSesion(usuarioTV.getText().toString());
                                    navegarHaciaMenuPrincipal();
                                } else { // Ha ocurrido un ERROR en la consulta
                                    Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.toast_creacion_cuenta_error),
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    }
                });

        WorkManager.getInstance(requireContext()).enqueue(otwr2);
    }

    /*
     * En esta función, se realiza el proceso de inicio de sesión guardando el nombre de usuario
     * del usuario que acaba de iniciar sesión en un archivo de preferencias. Este archivo se
     * utiliza en el método 'onStart' de la clase 'LoginFragment' para iniciar la sesión de
     * forma automática en caso de no haber cerrado la sesión.
     */
    private void iniciarSesion(String pUsuario) {
        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("usuario", pUsuario);
        editor.apply();
    }

    private void navegarHaciaMenuPrincipal() {
        NavDirections accion = CrearCuentaFragmentDirections
                .actionCrearCuentaFragmentToMenuPrincipalFragment();
        NavHostFragment.findNavController(this).navigate(accion);
    }
}
