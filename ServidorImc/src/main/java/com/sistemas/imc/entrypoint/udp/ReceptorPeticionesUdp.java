package com.sistemas.imc.entrypoint.udp;

import com.sistemas.imc.adaptadores.red.CanalUdp;
import com.sistemas.imc.aplicacion.dto.ProcesarPeticionUdpCommand;
import com.sistemas.imc.aplicacion.puertos.entrada.ProcesarPeticionUdpInputPort;
import com.sistemas.imc.dominio.modelos.EventoServidor;
import com.sistemas.imc.dominio.puertos.salida.PuertoNotificacionEvento;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Entrypoint primario para las peticiones UDP entrantes. Responsabilidad única: Escuchar
 * paquetes datagrama en segundo plano, extraer la información de la carga útil y despachar el
 * comando al puerto de entrada.
 *
 * <p>La recepción y el procesamiento se ejecutan en hilos separados: el bucle de escucha solo
 * lee del socket y delega cada comando a un pool de despacho, de modo que un cálculo lento
 * (o muchos clientes simultáneos) nunca retrasa la próxima llamada a {@code receive()}.
 */
public final class ReceptorPeticionesUdp {

    private static final int BUFFER_SIZE = 2048;
    private static final long ESPERA_CIERRE_POOL_SEGUNDOS = 3L;

    private final CanalUdp canalUdp;
    private final ProcesarPeticionUdpInputPort inputPort;
    private final PuertoNotificacionEvento notificador;

    private Thread hiloEscucha;
    private ExecutorService poolDespacho;
    private volatile boolean escuchando = false;

    public ReceptorPeticionesUdp(
            final CanalUdp canalUdp,
            final ProcesarPeticionUdpInputPort inputPort,
            final PuertoNotificacionEvento notificador) {
        this.canalUdp = Objects.requireNonNull(canalUdp, "El canal UDP es obligatorio.");
        this.inputPort = Objects.requireNonNull(inputPort, "El puerto de entrada es obligatorio.");
        this.notificador = Objects.requireNonNull(notificador, "El notificador es obligatorio.");
    }

    public synchronized void iniciar() {
        if (escuchando) {
            return;
        }
        escuchando = true;
        poolDespacho = crearPoolDespacho();
        hiloEscucha = new Thread(
                this::cicloEscucha, "Thread-ReceptorPeticionesUdp-" + canalUdp.getPuertoActual());
        hiloEscucha.setDaemon(true);
        hiloEscucha.start();
    }

    public synchronized void detener() {
        escuchando = false;
        if (Objects.nonNull(hiloEscucha) && hiloEscucha.isAlive()) {
            hiloEscucha.interrupt();
        }
        hiloEscucha = null;
        cerrarPoolDespacho();
    }

    public boolean isEscuchando() {
        return escuchando;
    }

    private void cicloEscucha() {
        final byte[] buffer = new byte[BUFFER_SIZE];

        while (escuchando && canalUdp.isAbierto()) {
            try {
                final DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                canalUdp.recibir(paquete);

                final String texto = new String(paquete.getData(), 0, paquete.getLength(), StandardCharsets.UTF_8).trim();
                final InetAddress direccion = paquete.getAddress();
                final int puerto = paquete.getPort();

                final ProcesarPeticionUdpCommand comando = new ProcesarPeticionUdpCommand(direccion.getHostAddress(), puerto,
                        texto);

                despacharAsincronamente(comando);

            } catch (final SocketException excepcion) {
                if (!escuchando) {
                    break;
                }
                notificarError("Error de socket al recibir paquete: " + excepcion.getMessage());
            } catch (final IOException excepcion) {
                if (escuchando) {
                    notificarError("Error I/O al recibir datagrama: " + excepcion.getMessage());
                }
            }
        }
    }

    private void despacharAsincronamente(final ProcesarPeticionUdpCommand comando) {
        final ExecutorService pool = poolDespacho;
        if (Objects.isNull(pool)) {
            return;
        }
        try {
            pool.execute(() -> procesarSinPropagarFallos(comando));
        } catch (final RejectedExecutionException excepcion) {
            notificarError("Petición descartada, el pool de despacho está cerrado: " + excepcion.getMessage());
        }
    }

    private void procesarSinPropagarFallos(final ProcesarPeticionUdpCommand comando) {
        try {
            inputPort.procesar(comando);
        } catch (final RuntimeException excepcion) {
            notificarError("Fallo al procesar la petición de " + comando.ipCliente() + ":" + comando.puertoCliente()
                    + " -> " + excepcion.getMessage());
        }
    }

    private static ExecutorService crearPoolDespacho() {
        final AtomicInteger contador = new AtomicInteger(1);
        return Executors.newCachedThreadPool(tarea -> {
            final Thread hilo = new Thread(tarea, "Despachador-Udp-" + contador.getAndIncrement());
            hilo.setDaemon(true);
            return hilo;
        });
    }

    private void cerrarPoolDespacho() {
        final ExecutorService pool = poolDespacho;
        poolDespacho = null;
        if (Objects.isNull(pool)) {
            return;
        }
        pool.shutdown();
        try {
            if (!pool.awaitTermination(ESPERA_CIERRE_POOL_SEGUNDOS, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (final InterruptedException excepcion) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void notificarError(final String mensaje) {
        notificador.notificarEvento(new EventoServidor("ERROR", "SOCKET", mensaje));
    }
}
