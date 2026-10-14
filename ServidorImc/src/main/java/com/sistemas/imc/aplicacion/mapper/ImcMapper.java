package com.sistemas.imc.aplicacion.mapper;

import com.sistemas.imc.aplicacion.dto.CalcularImcCommand;
import com.sistemas.imc.aplicacion.dto.ResultadoImcDto;
import com.sistemas.imc.aplicacion.excepciones.ComandoInvalidoException;
import com.sistemas.imc.dominio.modelos.Calculo;
import com.sistemas.imc.dominio.modelos.Resultado;
import com.sistemas.imc.dominio.vo.Altura;
import com.sistemas.imc.dominio.vo.Peso;
import java.util.Objects;

/**
 * Mapper de la capa de aplicación. Responsabilidad única: Convertir comandos/DTOs de cálculo
 * a entidades del dominio y viceversa.
 */
public final class ImcMapper {

    public Calculo toDomain(final CalcularImcCommand comando) {
        if (Objects.isNull(comando)) {
            throw new ComandoInvalidoException("El comando de cálculo no puede ser nulo.");
        }
        return toDomain(comando.peso(), comando.altura());
    }

    public Calculo toDomain(final double kilogramos, final double metros) {
        return new Calculo(new Peso(kilogramos), new Altura(metros));
    }

    public ResultadoImcDto toDto(final Resultado resultado) {
        if (Objects.isNull(resultado)) {
            throw new ComandoInvalidoException("El resultado del cálculo no puede ser nulo.");
        }
        return new ResultadoImcDto(
                resultado.getImc(),
                resultado.getImcFormateado(),
                resultado.getCondicion(),
                resultado.getObservacion());
    }
}
