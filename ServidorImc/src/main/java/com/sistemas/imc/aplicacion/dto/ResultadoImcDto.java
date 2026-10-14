package com.sistemas.imc.aplicacion.dto;

/** DTO que contiene el resultado del cálculo del IMC para la capa de aplicación. */
public record ResultadoImcDto(double imc, String imcFormateado, String condicion, String observacion) {
}
