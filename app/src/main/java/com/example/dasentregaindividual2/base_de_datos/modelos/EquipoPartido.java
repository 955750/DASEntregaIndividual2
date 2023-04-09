package com.example.dasentregaindividual2.base_de_datos.modelos;

public class EquipoPartido {

    private int escudoId;
    private String nombre;
    private String rachaUltimosPartidos;
    private int puntos;


    public EquipoPartido(
        int pEscudo,
        String pNombre,
        String pRachaUltimosPartidos,
        int pPuntos
    ) {
        this.escudoId = pEscudo;
        this.nombre = pNombre;
        this.rachaUltimosPartidos = pRachaUltimosPartidos;
        this.puntos = pPuntos;
    }

    public int getEscudoId() {
        return escudoId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRachaUltimosPartidos() {
        return rachaUltimosPartidos;
    }

    public int getPuntos() {
        return puntos;
    }
}
