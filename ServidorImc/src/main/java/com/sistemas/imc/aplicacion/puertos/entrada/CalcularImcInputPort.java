package com.sistemas.imc.aplicacion.puertos.entrada;

import com.sistemas.imc.aplicacion.dto.CalcularImcCommand;
import com.sistemas.imc.aplicacion.dto.ResultadoImcDto;

/** Puerto de entrada (Caso de Uso) para el cálculo del IMC. */
public interface CalcularImcInputPort {
    ResultadoImcDto calcular(CalcularImcCommand comando);
}
