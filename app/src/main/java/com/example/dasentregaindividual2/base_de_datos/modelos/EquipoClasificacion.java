package com.example.dasentregaindividual2.base_de_datos.modelos;

public class EquipoClasificacion {

    private int posicion, escudoId, partidosGanadosTotales, partidosPerdidosTotales,
        puntosFavorTotales, puntosContraTotales, partidosGanadosUltimos10,
        partidosPerdidosUltimos10;
    private boolean esFavorito;
    private String nombre;


    public EquipoClasificacion(
        int pPosicion, int pEscudo, String pNombre, int pPartidosGanadosTotales,
        int pPartidosPerdidosTotales, int pPuntosFavorTotales, int pPuntosContraTotales,
        int pPartidosGanadosUltimos10, int pPartidosPerdidosUltimos10, boolean pEsFavorito
    ) {
        this.posicion = pPosicion;
        this.escudoId = pEscudo;
        this.nombre = pNombre;
        this.partidosGanadosTotales = pPartidosGanadosTotales;
        this.partidosPerdidosTotales = pPartidosPerdidosTotales;
        this.puntosFavorTotales = pPuntosFavorTotales;
        this.puntosContraTotales = pPuntosContraTotales;
        this.partidosGanadosUltimos10 = pPartidosGanadosUltimos10;
        this.partidosPerdidosUltimos10 = pPartidosPerdidosUltimos10;
        this.esFavorito = pEsFavorito;
    }

    public int getPosicion() {
        return posicion;
    }

    public int getEscudoId() {
        return escudoId;
    }

    public int getPartidosGanadosTotales() {
        return partidosGanadosTotales;
    }

    public int getPartidosPerdidosTotales() {
        return partidosPerdidosTotales;
    }

    public int getPuntosFavorTotales() {
        return puntosFavorTotales;
    }

    public int getPuntosContraTotales() {
        return puntosContraTotales;
    }

    public int getPartidosGanadosUltimos10() {
        return partidosGanadosUltimos10;
    }

    public int getPartidosPerdidosUltimos10() {
        return partidosPerdidosUltimos10;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean getEsFavorito() {
        return esFavorito;
    }
}
