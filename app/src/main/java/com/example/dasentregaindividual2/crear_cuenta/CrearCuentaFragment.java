package com.example.dasentregaindividual2.crear_cuenta;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.example.dasentregaindividual2.R;
import com.example.dasentregaindividual2.base_de_datos.BaseDeDatos;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

public class CrearCuentaFragment extends Fragment {

    /* Atributos de la interfaz gráfica */
    private TextInputEditText usuarioTV;
    private TextInputEditText contraseñaTV;
    private TextInputEditText repetirContraseñaTV;
    private Button crearCuenta;

    /* Otros atributos */
    private SQLiteDatabase baseDeDatos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Recuperar instancia de la base de datos */
        BaseDeDatos gestorBD = new BaseDeDatos(requireContext(), "Euroliga",
            null, 1);
        baseDeDatos = gestorBD.getWritableDatabase();
    }

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
                crearUsuarioYHacerLogin(view);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        baseDeDatos.close();
    }

    private void crearUsuarioYHacerLogin(View view) {
        if (camposValidos()) {
            crearCuenta();
            navegarHaciaMenuPrincipal(view);
        }
    }

    /*
     * En esta función, primero se validan los campos y una vez que cumplen con el formato correcto
     * se comprueba si el usuario existe (no puede haber más de un usuario con el mismo nombre de
     * usuario) y que las contraseñas coinciden. Finalmente, si se cumplen todos los requisitos se
     * procede a la creación de la cuenta
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
        } else if (repetirContraseñaTV.getText().toString().equals("")) {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_campo_vacio, "Repetir Contraseña"),
                Toast.LENGTH_SHORT)
            .show();
            return false;
        } else if (!usuarioValido(usuarioTV.getText().toString())) {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_nombre_usuario_existente),
                Toast.LENGTH_SHORT)
            .show();
            return false;
        } else if (!contraseñaTV.getText().toString().equals(
            repetirContraseñaTV.getText().toString())
        ) {
            Log.d("CrearCuentaFragment", "Contraseñas iguales = true");
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_contraseñas_no_coinciden),
                Toast.LENGTH_SHORT)
            .show();
            return false;
        } else if (!contraseñaCumpleFormato()) {
            return false;
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_cuenta_creada_con_exito),
                Toast.LENGTH_SHORT
            ).show();
            return true;
        }
    }

    /*
     * En esta función, si hay algún usuario que cumpla las características de la consulta
     * (cantidadUsuarios = 1) significará que nombre de usuario no es válido.
     */
    private boolean usuarioValido(String pUsuario) {
        /*
        SELECT COUNT(*) FROM Usuario
        WHERE nombre_usuario = ?
         */
        String[] campos = new String[] {"COUNT(*)"};
        String[] argumentos = new String[] {pUsuario};
        Cursor cUsuario = baseDeDatos.query("Usuario", campos, "nombre_usuario = ?",
            argumentos, null, null, null);

        cUsuario.moveToFirst();
        int cantidadUsuarios = cUsuario.getInt(0);
        cUsuario.close();
        return cantidadUsuarios != 1;
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

    private void crearCuenta() {
        BaseDeDatos gestorBD = new BaseDeDatos(requireContext(), "Euroliga",
            null, 1);
        SQLiteDatabase bd = gestorBD.getWritableDatabase();
        ContentValues nuevoUsuario = new ContentValues();
        nuevoUsuario.put("nombre_usuario", usuarioTV.getText().toString());
        nuevoUsuario.put("contraseña", contraseñaTV.getText().toString());
        bd.insert("Usuario", null, nuevoUsuario);
        bd.close();

        iniciarSesion(usuarioTV.getText().toString());
    }

    private void iniciarSesion(String pUsuario) {
        SharedPreferences preferencias = PreferenceManager
            .getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("usuario", pUsuario);
        editor.apply();
    }

    private void navegarHaciaMenuPrincipal(View view) {
        NavDirections accion = CrearCuentaFragmentDirections
            .actionCrearCuentaFragmentToMenuPrincipalFragment();
        Navigation.findNavController(view).navigate(accion);
    }
}