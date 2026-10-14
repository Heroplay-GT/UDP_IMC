package com.sistemas.imc.aplicacion.mapper;

import com.sistemas.imc.aplicacion.dto.CalcularImcCommand;
import com.sistemas.imc.aplicacion.dto.ConectarCommand;
import com.sistemas.imc.dominio.modelos.DatosImc;
import com.sistemas.imc.dominio.vo.Altura;
import com.sistemas.imc.dominio.vo.DestinoServidor;
import com.sistemas.imc.dominio.vo.Peso;
import java.util.Objects;

public final class ClienteMapper {
    public DestinoServidor toDestino(final ConectarCommand comando) {
        Objects.requireNonNull(comando, "El comando de conexión es obligatorio.");
        return new DestinoServidor(comando.host(), comando.puerto());
    }

    public DatosImc toDatos(final CalcularImcCommand comando) {
        Objects.requireNonNull(comando, "El comando de cálculo es obligatorio.");
        return new DatosImc(new Peso(comando.peso()), new Altura(comando.altura()));
    }
}
