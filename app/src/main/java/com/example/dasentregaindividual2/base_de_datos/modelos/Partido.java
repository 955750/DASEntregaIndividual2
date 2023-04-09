package com.example.dasentregaindividual2.base_de_datos.modelos;

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
}
