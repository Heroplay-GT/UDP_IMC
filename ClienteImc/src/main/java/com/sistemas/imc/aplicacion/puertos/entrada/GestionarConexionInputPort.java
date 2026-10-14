package com.sistemas.imc.aplicacion.puertos.entrada;

import com.sistemas.imc.aplicacion.dto.ConectarCommand;
import java.util.concurrent.CompletableFuture;

public interface GestionarConexionInputPort {
    CompletableFuture<Void> conectar(ConectarCommand comando);

    CompletableFuture<Void> desconectar();

    boolean estaConectado();
}
