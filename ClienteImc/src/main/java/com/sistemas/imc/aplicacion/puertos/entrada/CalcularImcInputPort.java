package com.sistemas.imc.aplicacion.puertos.entrada;

import com.sistemas.imc.aplicacion.dto.CalcularImcCommand;
import com.sistemas.imc.dominio.modelos.ResultadoImc;
import java.util.concurrent.CompletableFuture;

public interface CalcularImcInputPort {
    CompletableFuture<ResultadoImc> calcular(CalcularImcCommand comando);
}
