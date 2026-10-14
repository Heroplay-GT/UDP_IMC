package com.sistemas.imc.adaptadores.notificacion;

import com.sistemas.imc.dominio.enums.EstadoConexion;
import com.sistemas.imc.dominio.modelos.EventoCliente;
import com.sistemas.imc.dominio.modelos.ResultadoImc;

public interface ObservadorCliente {
    void onEstado(EstadoConexion estado, String endpoint);

    void onEvento(EventoCliente evento);

    void onResultado(ResultadoImc resultado);

    void onError(String mensaje);
}
