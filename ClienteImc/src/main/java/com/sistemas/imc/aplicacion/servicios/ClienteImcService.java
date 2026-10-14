package com.sistemas.imc.aplicacion.servicios;

import com.sistemas.imc.aplicacion.dto.CalcularImcCommand;
import com.sistemas.imc.aplicacion.dto.ConectarCommand;
import com.sistemas.imc.aplicacion.excepciones.ClienteRedException;
import com.sistemas.imc.aplicacion.mapper.ClienteMapper;
import com.sistemas.imc.aplicacion.puertos.entrada.CalcularImcInputPort;
import com.sistemas.imc.aplicacion.puertos.entrada.GestionarConexionInputPort;
import com.sistemas.imc.dominio.enums.EstadoConexion;
import com.sistemas.imc.dominio.modelos.DatosImc;
import com.sistemas.imc.dominio.modelos.EventoCliente;
import com.sistemas.imc.dominio.modelos.ResultadoImc;
import com.sistemas.imc.dominio.puertos.salida.ClienteUdpPort;
import com.sistemas.imc.dominio.puertos.salida.PuertoNotificacionCliente;
import com.sistemas.imc.dominio.vo.DestinoServidor;
import java.lang.System.Logger.Level;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * Servicio de aplicación que orquesta la conexión y el cálculo del IMC contra el
 * servidor UDP. Responsabilidad única: coordinar el puerto de salida de red, el mapper y las
 * notificaciones hacia la interfaz de usuario, ejecutando las operaciones de red fuera del
 * hilo de eventos de Swing.
 */
public final class ClienteImcService implements GestionarConexionInputPort, CalcularImcInputPort {

    private static final System.Logger LOG = System.getLogger(ClienteImcService.class.getName());

    private final ClienteUdpPort clienteUdp;
    private final PuertoNotificacionCliente notificador;
    private final ClienteMapper mapper;
    private final Executor executor;

    public ClienteImcService(
            final ClienteUdpPort clienteUdp,
            final PuertoNotificacionCliente notificador,
            final ClienteMapper mapper,
            final Executor executor) {
        this.clienteUdp = Objects.requireNonNull(clienteUdp, "El puerto UDP del cliente es obligatorio.");
        this.notificador = Objects.requireNonNull(notificador, "El notificador es obligatorio.");
        this.mapper = Objects.requireNonNull(mapper, "El mapper es obligatorio.");
        this.executor = Objects.requireNonNull(executor, "El executor es obligatorio.");
    }

    @Override
    public CompletableFuture<Void> conectar(final ConectarCommand comando) {
        final DestinoServidor destino = mapper.toDestino(comando);
        notificador.notificarEstado(EstadoConexion.CONECTANDO, destino.endpoint());
        return CompletableFuture.runAsync(() -> {
            try {
                clienteUdp.conectar(destino);
                notificador.notificarEstado(EstadoConexion.CONECTADO, destino.endpoint());
                notificador.notificarEvento(new EventoCliente("CONEXIÓN", "Conectado a " + destino.endpoint()));
            } catch (final ClienteRedException excepcion) {
                LOG.log(Level.WARNING, "Falló la operación UDP conectar", excepcion);
                notificador.notificarEstado(EstadoConexion.DESCONECTADO, destino.endpoint());
                notificador.notificarError(excepcion.getMessage());
                throw new CompletionException(excepcion);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> desconectar() {
        return CompletableFuture.runAsync(() -> {
            try {
                clienteUdp.desconectar();
                notificador.notificarEstado(EstadoConexion.DESCONECTADO, "");
                notificador.notificarEvento(new EventoCliente("CONEXIÓN", "Cliente desconectado."));
            } catch (final ClienteRedException excepcion) {
                LOG.log(Level.WARNING, "Falló la operación UDP desconectar", excepcion);
                notificador.notificarError(excepcion.getMessage());
                throw new CompletionException(excepcion);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<ResultadoImc> calcular(final CalcularImcCommand comando) {
        final DatosImc datos = mapper.toDatos(comando);
        return CompletableFuture.supplyAsync(() -> {
            try {
                final ResultadoImc resultado = clienteUdp.solicitarCalculo(datos);
                notificador.notificarResultado(resultado);
                return resultado;
            } catch (final ClienteRedException excepcion) {
                LOG.log(Level.WARNING, "Falló la operación UDP calcular", excepcion);
                notificador.notificarError(excepcion.getMessage());
                throw new CompletionException(excepcion);
            }
        }, executor);
    }

    @Override
    public boolean estaConectado() {
        return clienteUdp.estaConectado();
    }
}
