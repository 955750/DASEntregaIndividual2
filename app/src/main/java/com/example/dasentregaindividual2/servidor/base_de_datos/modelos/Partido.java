package com.example.dasentregaindividual2.servidor.base_de_datos.modelos;

import androidx.annotation.NonNull;

public class Partido {

    private EquipoPartido[] equipos;
    private String fecha;
    private String hora;

    public Partido(
        EquipoPartido[] pEquipos,
        String pFecha,
        String pHora
    ) {
        this.equipos = pEquipos;
        this.fecha = pFecha;
        this.hora = pHora;
    }

    public EquipoPartido[] getEquipos() {
        return this.equipos;
    }

    public String getFecha() {
        return this.fecha;
    }

    public String getHora() {
        return this.hora;
    }

    @NonNull
    @Override
    public String toString() {
        return "Local = " + equipos[0].getNombre() +
                "; Visitante = " + equipos[1].getNombre() +
                "; Fecha = " + fecha +
                "; Hora = " + hora;
    }
}
