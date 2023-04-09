package com.example.dasentregaindividual2.login;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.base_de_datos.BaseDeDatos;
import com.google.android.material.textfield.TextInputEditText;


public class LoginFragment extends Fragment {

    /* Atributos de la interfaz gráfica */
    private TextInputEditText usuarioTV;
    private TextInputEditText contraseñaTV;
    private Button botonIniciarSesion;
    private Button botonCrearCuenta;

    /* Otros atributos */
    private SQLiteDatabase baseDeDatos;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /* Recuperar instancia de la base de datos */
        BaseDeDatos gestorBD = new BaseDeDatos (requireContext(), "Euroliga",
            null, 1);
        baseDeDatos = gestorBD.getWritableDatabase();
    }

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
                hacerLogin();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        baseDeDatos.close();
    }
    
    private void hacerLogin() {
        if (camposValidos()) {
            iniciarSesion(usuarioTV.getText().toString());
            navegarHaciaMenuPrincipal();
        }
    }

    /*
     * En esta función, primero se validan los campos y una vez que cumplen con el formato correcto
     * se comprueba si el usuario y la contraseña son correctos
     */
    private boolean camposValidos() {
        if (usuarioTV.getText().toString().equals("")) {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_campo_vacio, "Usuario"),
                Toast.LENGTH_SHORT)
            .show();
            return false;
        } else if (contraseñaTV.getText().toString().equals("")) {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_campo_vacio, "Contraseña"),
                Toast.LENGTH_SHORT)
            .show();
            return false;
        } else if (!usuarioCorrecto(
            usuarioTV.getText().toString(),
            contraseñaTV.getText().toString()
        )) {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_usuario_contraseña_incorrectos),
                Toast.LENGTH_SHORT
            ).show();
            return false;
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_usuario_contraseña_correctos),
                Toast.LENGTH_SHORT
            ).show();
            return true;
        }
    }

    /*
     * En esta función, si hay algún usuario que cumpla las características de la consulta
     * (cantidadUsuarios = 1) significará que los datos de inicio de sesión son correctos.
     */
    private boolean usuarioCorrecto(String pUsuario, String pContraseña) {
        /*
        SELECT COUNT(*) FROM Usuario
        WHERE nombre_usuario = ? AND
        contraseña = ?
        */
        String[] campos = new String[]{"COUNT(*)"};
        String[] argumentos = new String[]{pUsuario, pContraseña};
        Cursor cUsuario = baseDeDatos.query("Usuario", campos,
                "nombre_usuario = ? AND contraseña = ?",
                argumentos, null, null, null);

        cUsuario.moveToFirst();
        int cantidadUsuarios = cUsuario.getInt(0);
        cUsuario.close();
        return cantidadUsuarios == 1;
    }

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