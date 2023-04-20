package com.example.dasentregaindividual2.servidor.base_de_datos.modelos;

public class EquipoPartido {

    private String escudoBase64;
    private String nombre;
    private String rachaUltimosPartidos;
    private int puntos;


    public EquipoPartido(
        String pEscudoBase64,
        String pNombre,
        String pRachaUltimosPartidos,
        int pPuntos
    ) {
        this.escudoBase64 = pEscudoBase64;
        this.nombre = pNombre;
        this.rachaUltimosPartidos = pRachaUltimosPartidos;
        this.puntos = pPuntos;
    }

    public String getEscudoBase64() {
        return escudoBase64;
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
