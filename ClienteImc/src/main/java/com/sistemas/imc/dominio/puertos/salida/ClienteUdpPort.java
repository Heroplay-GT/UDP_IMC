package com.sistemas.imc.dominio.puertos.salida;

import com.sistemas.imc.aplicacion.excepciones.ClienteRedException;
import com.sistemas.imc.dominio.modelos.DatosImc;
import com.sistemas.imc.dominio.modelos.ResultadoImc;
import com.sistemas.imc.dominio.vo.DestinoServidor;

public interface ClienteUdpPort {
    void conectar(DestinoServidor destino) throws ClienteRedException;

    void desconectar() throws ClienteRedException;

    ResultadoImc solicitarCalculo(DatosImc datos) throws ClienteRedException;

    boolean estaConectado();
}
