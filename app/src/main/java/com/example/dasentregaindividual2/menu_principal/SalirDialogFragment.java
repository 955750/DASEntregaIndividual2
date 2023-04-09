package com.example.dasentregaindividual2.menu_principal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.dasentregaindividual2.R;

public class SalirDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.dialogo_salir_titulo);
        builder.setMessage(R.string.dialogo_salir_mensaje);

        builder.setPositiveButton(R.string.salir_y_cerrar_sesion_mensaje,
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                seleccionarOpcion(1);
            }
        });

        builder.setNeutralButton(R.string.no_salir_mensaje, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                seleccionarOpcion(2);
            }
        });

        builder.setNegativeButton(R.string.cerrar_sesion_mensaje, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                seleccionarOpcion(3);
            }
        });

        return builder.create();
    }

    /*
    * Con esta función se envía un número en función de la opción del diálogo que se ha escogido.
    * Una vez escogido, se le envía ese dato al fragmento que lo ha invocado para realizar la acción
    * correspondiente. En este caso se trata del fragmento 'MenuPrincipalFragment'.
    */
    private void seleccionarOpcion(int opcionSalirId) {
        Bundle opcionSalir = new Bundle();
        opcionSalir.putInt("opcionSalirId", opcionSalirId);
        getParentFragmentManager().setFragmentResult("opcionSalir", opcionSalir);
    }
}