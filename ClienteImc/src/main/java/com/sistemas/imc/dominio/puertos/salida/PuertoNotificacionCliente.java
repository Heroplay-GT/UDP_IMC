package com.sistemas.imc.dominio.puertos.salida;

import com.sistemas.imc.dominio.enums.EstadoConexion;
import com.sistemas.imc.dominio.modelos.EventoCliente;
import com.sistemas.imc.dominio.modelos.ResultadoImc;

public interface PuertoNotificacionCliente {
    void notificarEstado(EstadoConexion estado, String endpoint);

    void notificarEvento(EventoCliente evento);

    void notificarResultado(ResultadoImc resultado);

    void notificarError(String mensaje);
}
