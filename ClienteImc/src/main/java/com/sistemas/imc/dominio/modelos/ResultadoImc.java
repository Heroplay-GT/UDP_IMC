package com.sistemas.imc.dominio.modelos;

import com.sistemas.imc.dominio.excepciones.RespuestaServidorException;
import java.util.Objects;

public record ResultadoImc(double imc, String imcFormateado, String condicion, String observacion) {
    public ResultadoImc {
        if (!Double.isFinite(imc) || Objects.isNull(imcFormateado) || imcFormateado.isBlank()
                || Objects.isNull(condicion) || condicion.isBlank()
                || Objects.isNull(observacion)) {
            throw new RespuestaServidorException("El resultado recibido está incompleto o es inválido.");
        }
    }
}
