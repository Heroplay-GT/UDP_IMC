package com.sistemas.imc.aplicacion.servicios;

import com.sistemas.imc.aplicacion.dto.CalcularImcCommand;
import com.sistemas.imc.aplicacion.dto.ResultadoImcDto;
import com.sistemas.imc.aplicacion.mapper.ImcMapper;
import com.sistemas.imc.aplicacion.puertos.entrada.CalcularImcInputPort;
import com.sistemas.imc.dominio.modelos.Calculo;
import com.sistemas.imc.dominio.modelos.Resultado;
import java.util.Objects;

/**
 * Servicio de aplicación que implementa el caso de uso de calcular el IMC.
 * Responsabilidad única: Orquestar la conversión del comando a dominio, invocar la lógica
 * del dominio y retornar el resultado en formato DTO.
 */
public final class CalcularImcService implements CalcularImcInputPort {

    private final ImcMapper imcMapper;

    public CalcularImcService(final ImcMapper imcMapper) {
        this.imcMapper = Objects.requireNonNull(imcMapper, "El mapper de cálculo es obligatorio.");
    }

    @Override
    public ResultadoImcDto calcular(final CalcularImcCommand comando) {
        final Calculo calculo = imcMapper.toDomain(comando);
        final Resultado resultado = calculo.calcular();
        return imcMapper.toDto(resultado);
    }
}
