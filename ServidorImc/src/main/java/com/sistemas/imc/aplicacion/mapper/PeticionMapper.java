package com.sistemas.imc.aplicacion.mapper;

import com.sistemas.imc.aplicacion.dto.ProcesarPeticionUdpCommand;
import com.sistemas.imc.aplicacion.excepciones.ComandoInvalidoException;
import com.sistemas.imc.dominio.vo.Destinatario;
import java.util.Objects;

/**
 * Mapper de la capa de aplicación. Responsabilidad única: Convertir datos de comandos de
 * petición a objetos de valor del dominio.
 */
public final class PeticionMapper {

    public Destinatario toDestinatario(final ProcesarPeticionUdpCommand comando) {
        if (Objects.isNull(comando)) {
            throw new ComandoInvalidoException("El comando de petición no puede ser nulo.");
        }
        return new Destinatario(comando.ipCliente(), comando.puertoCliente());
    }
}
