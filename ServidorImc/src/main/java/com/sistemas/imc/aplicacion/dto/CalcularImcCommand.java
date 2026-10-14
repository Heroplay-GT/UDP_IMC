package com.sistemas.imc.aplicacion.dto;

/** Comando DTO para solicitar el cálculo del IMC con valores numéricos primitivos. */
public record CalcularImcCommand(double peso, double altura) {
}
