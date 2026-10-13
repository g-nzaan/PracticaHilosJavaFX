package ni.edu.uam.practicahilos;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

/**
 * Controlador principal para la práctica de concurrencia en JavaFX (100% Java).
 * Implementa el procesamiento básico (Pasos 1-4), el experimento de bloqueo de interfaz (Paso 6)
 * y el Reto Final de procesamiento concurrente multi-hilo (Clientes y Productos).
 * 
 * Incluye soporte de detención controlada con preservación de estado:
 * al detener un proceso en curso, el avance no se pierde y puede reanudarse / continuarse
 * desde el último registro procesado.
 */
public class ProcesadorController {

    // =========================================================================
    // Componentes del Caso Básico (Pasos 1-4)
    // =========================================================================
    @FXML
    private Label lblEstado;

    @FXML
    private Label lblPorcentaje;

    @FXML
    private ProgressBar barraProgreso;

    @FXML
    private Button btnIniciar;

    @FXML
    private Button btnDetener;

    @FXML
    private Button btnReiniciar;

    private Thread hiloProceso;
    // Estado de progreso persistente (0 a 100) para reanudar sin pérdida
    private int progresoActual = 0;

    // =========================================================================
    // Componentes del Reto Final: Monitor Concurrente (Paso 11)
    // =========================================================================
    @FXML
    private Label lblEstadoClientes;

    @FXML
    private ProgressBar barraClientes;

    @FXML
    private Label lblPorcentajeClientes;

    @FXML
    private Button btnIniciarClientes;

    @FXML
    private Button btnDetenerClientes;

    @FXML
    private Button btnReiniciarClientes;

    private Thread hiloClientes;
    // Estado persistente de clientes (0 a 100)
    private int progresoClientes = 0;

    @FXML
    private Label lblEstadoProductos;

    @FXML
    private ProgressBar barraProductos;

    @FXML
    private Label lblPorcentajeProductos;

    @FXML
    private Button btnIniciarProductos;

    @FXML
    private Button btnDetenerProductos;

    @FXML
    private Button btnReiniciarProductos;

    private Thread hiloProductos;
    // Estado persistente de productos (0 a 50)
    private int progresoProductos = 0;

    // =========================================================================
    // Componentes del Experimento de Bloqueo UI (Paso 6)
    // =========================================================================
    @FXML
    private Label lblEstadoBloqueo;

    @FXML
    private Button btnBloqueante;

    // =========================================================================
    // LÓGICA DEL PROCESADOR BÁSICO (Pasos 1 a 4 con Continuación / Reanudación)
    // =========================================================================

    @FXML
    private void iniciarProceso() {
        // Evitar iniciar un proceso dos veces simultáneamente
        if (hiloProceso != null && hiloProceso.isAlive()) {
            return;
        }

        // Si ya había completado el 100%, reiniciar el ciclo desde 0
        if (progresoActual >= 100) {
            progresoActual = 0;
            barraProgreso.setProgress(0);
            lblPorcentaje.setText("0%");
        }

        final boolean esReanudacion = (progresoActual > 0);
        if (esReanudacion) {
            lblEstado.setText("Reanudando procesamiento desde registro " + (progresoActual + 1) + "...");
        } else {
            lblEstado.setText("Procesando registros...");
        }

        btnIniciar.setDisable(true);
        btnDetener.setDisable(false);
        btnReiniciar.setDisable(true);

        final int inicio = progresoActual + 1;

        Runnable tarea = () -> {
            try {
                for (int i = inicio; i <= 100; i++) {
                    // Verificación de bandera de interrupción
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    // Simula carga de trabajo (100 ms por registro)
                    Thread.sleep(100);

                    // Guardar progreso actual para que nunca se pierda
                    progresoActual = i;
                    final int avance = i;

                    // Actualización segura del hilo de JavaFX
                    Platform.runLater(() -> {
                        barraProgreso.setProgress(avance / 100.0);
                        lblPorcentaje.setText(avance + "%");
                    });
                }

                Platform.runLater(() -> {
                    lblEstado.setText("Proceso finalizado (100% completado)");
                    btnIniciar.setText("Iniciar");
                    habilitarBotones();
                });

            } catch (InterruptedException e) {
                // Restablecer la bandera de interrupción
                Thread.currentThread().interrupt();

                final int pausado = progresoActual;
                Platform.runLater(() -> {
                    lblEstado.setText("Proceso detenido en " + pausado + "%. Progreso conservado.");
                    btnIniciar.setText("Continuar (" + pausado + "%)");
                    habilitarBotones();
                });
            }
        };

        hiloProceso = new Thread(tarea);
        hiloProceso.setName("Hilo-Procesamiento");
        hiloProceso.setDaemon(true);
        hiloProceso.start();
    }

    @FXML
    private void detenerProceso() {
        if (hiloProceso != null && hiloProceso.isAlive()) {
            btnDetener.setDisable(true);
            lblEstado.setText("Solicitando detención...");
            hiloProceso.interrupt();
        }
    }

    @FXML
    private void reiniciarProceso() {
        // No reiniciar si el hilo está activamente procesando
        if (hiloProceso != null && hiloProceso.isAlive()) {
            return;
        }

        progresoActual = 0;
        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Esperando inicio");
        btnIniciar.setText("Iniciar");

        habilitarBotones();
    }

    private void habilitarBotones() {
        btnIniciar.setDisable(false);
        btnDetener.setDisable(true);
        btnReiniciar.setDisable(false);
    }

    // =========================================================================
    // LÓGICA DEL RETO FINAL: MONITOR CONCURRENTE CON CONTINUACIÓN (Paso 11)
    // =========================================================================

    /**
     * Inicia o continúa el procesamiento concurrente de Clientes (100 registros a 80 ms).
     */
    @FXML
    private void iniciarClientes() {
        if (hiloClientes != null && hiloClientes.isAlive()) {
            return;
        }

        if (progresoClientes >= 100) {
            progresoClientes = 0;
            barraClientes.setProgress(0);
            lblPorcentajeClientes.setText("0% (0/100)");
        }

        final boolean esReanudacion = (progresoClientes > 0);
        if (esReanudacion) {
            lblEstadoClientes.setText("Continuando clientes desde " + (progresoClientes + 1) + "/100...");
        } else {
            lblEstadoClientes.setText("Procesando clientes...");
        }

        btnIniciarClientes.setDisable(true);
        btnDetenerClientes.setDisable(false);
        btnReiniciarClientes.setDisable(true);

        final int totalClientes = 100;
        final int tiempoEsperaMs = 80; // Tiempo diferenciado
        final int inicio = progresoClientes + 1;

        Runnable tareaClientes = () -> {
            try {
                for (int i = inicio; i <= totalClientes; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    Thread.sleep(tiempoEsperaMs);

                    progresoClientes = i;
                    final int avance = i;
                    Platform.runLater(() -> {
                        barraClientes.setProgress(avance / (double) totalClientes);
                        lblPorcentajeClientes.setText(avance + "% (" + avance + "/" + totalClientes + ")");
                    });
                }

                Platform.runLater(() -> {
                    lblEstadoClientes.setText("Completado (100 clientes procesados)");
                    btnIniciarClientes.setText("Iniciar Clientes");
                    habilitarBotonesClientes();
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                final int pausado = progresoClientes;
                Platform.runLater(() -> {
                    lblEstadoClientes.setText("Detenido en cliente " + pausado + "/100. Progreso conservado.");
                    btnIniciarClientes.setText("Continuar Clientes (" + pausado + "%)");
                    habilitarBotonesClientes();
                });
            }
        };

        hiloClientes = new Thread(tareaClientes);
        hiloClientes.setName("Hilo-Clientes");
        hiloClientes.setDaemon(true);
        hiloClientes.start();
    }

    @FXML
    private void detenerClientes() {
        if (hiloClientes != null && hiloClientes.isAlive()) {
            btnDetenerClientes.setDisable(true);
            lblEstadoClientes.setText("Solicitando detención...");
            hiloClientes.interrupt();
        }
    }

    @FXML
    private void reiniciarClientes() {
        if (hiloClientes != null && hiloClientes.isAlive()) {
            return;
        }

        progresoClientes = 0;
        barraClientes.setProgress(0);
        lblPorcentajeClientes.setText("0% (0/100)");
        lblEstadoClientes.setText("Esperando inicio");
        btnIniciarClientes.setText("Iniciar Clientes");
        habilitarBotonesClientes();
    }

    private void habilitarBotonesClientes() {
        btnIniciarClientes.setDisable(false);
        btnDetenerClientes.setDisable(true);
        btnReiniciarClientes.setDisable(false);
    }

    /**
     * Inicia o continúa el procesamiento concurrente de Productos (50 registros a 140 ms).
     */
    @FXML
    private void iniciarProductos() {
        if (hiloProductos != null && hiloProductos.isAlive()) {
            return;
        }

        if (progresoProductos >= 50) {
            progresoProductos = 0;
            barraProductos.setProgress(0);
            lblPorcentajeProductos.setText("0% (0/50)");
        }

        final boolean esReanudacion = (progresoProductos > 0);
        if (esReanudacion) {
            lblEstadoProductos.setText("Continuando productos desde " + (progresoProductos + 1) + "/50...");
        } else {
            lblEstadoProductos.setText("Procesando productos...");
        }

        btnIniciarProductos.setDisable(true);
        btnDetenerProductos.setDisable(false);
        btnReiniciarProductos.setDisable(true);

        final int totalProductos = 50;
        final int tiempoEsperaMs = 140; // Tiempo diferenciado
        final int inicio = progresoProductos + 1;

        Runnable tareaProductos = () -> {
            try {
                for (int i = inicio; i <= totalProductos; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    Thread.sleep(tiempoEsperaMs);

                    progresoProductos = i;
                    final int avance = i;
                    Platform.runLater(() -> {
                        barraProductos.setProgress(avance / (double) totalProductos);
                        lblPorcentajeProductos.setText(avance + "% (" + avance + "/" + totalProductos + ")");
                    });
                }

                Platform.runLater(() -> {
                    lblEstadoProductos.setText("Completado (50 productos procesados)");
                    btnIniciarProductos.setText("Iniciar Productos");
                    habilitarBotonesProductos();
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                final int pausado = progresoProductos;
                Platform.runLater(() -> {
                    int porc = (int) Math.round((pausado / (double) totalProductos) * 100.0);
                    lblEstadoProductos.setText("Detenido en producto " + pausado + "/50. Progreso conservado.");
                    btnIniciarProductos.setText("Continuar Productos (" + porc + "%)");
                    habilitarBotonesProductos();
                });
            }
        };

        hiloProductos = new Thread(tareaProductos);
        hiloProductos.setName("Hilo-Productos");
        hiloProductos.setDaemon(true);
        hiloProductos.start();
    }

    @FXML
    private void detenerProductos() {
        if (hiloProductos != null && hiloProductos.isAlive()) {
            btnDetenerProductos.setDisable(true);
            lblEstadoProductos.setText("Solicitando detención...");
            hiloProductos.interrupt();
        }
    }

    @FXML
    private void reiniciarProductos() {
        if (hiloProductos != null && hiloProductos.isAlive()) {
            return;
        }

        progresoProductos = 0;
        barraProductos.setProgress(0);
        lblPorcentajeProductos.setText("0% (0/50)");
        lblEstadoProductos.setText("Esperando inicio");
        btnIniciarProductos.setText("Iniciar Productos");
        habilitarBotonesProductos();
    }

    private void habilitarBotonesProductos() {
        btnIniciarProductos.setDisable(false);
        btnDetenerProductos.setDisable(true);
        btnReiniciarProductos.setDisable(false);
    }

    /**
     * Inicia o reanuda ambos procesos concurrentemente sin perder avance previo.
     */
    @FXML
    private void iniciarTodos() {
        iniciarClientes();
        iniciarProductos();
    }

    /**
     * Detiene ambos procesos concurrentemente preservando su avance individual.
     */
    @FXML
    private void detenerTodos() {
        detenerClientes();
        detenerProductos();
    }

    /**
     * Reinicia ambos procesos a 0%.
     */
    @FXML
    private void reiniciarTodos() {
        reiniciarClientes();
        reiniciarProductos();
    }

    // =========================================================================
    // EXPERIMENTO DE BLOQUEO DE LA INTERFAZ (Paso 6)
    // =========================================================================

    /**
     * Demostración del Paso 6: Si se ejecuta Thread.sleep() en el JavaFX
     * Application Thread, la interfaz se congela completamente durante 5 segundos.
     */
    @FXML
    private void procesoBloqueante() {
        if (lblEstadoBloqueo != null) {
            lblEstadoBloqueo.setText("Procesando (UI Bloqueada durante 5s)...");
        }

        try {
            // ¡PELIGRO! Esto duerme el hilo de la interfaz gráfica
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (lblEstadoBloqueo != null) {
            lblEstadoBloqueo.setText("Finalizado (UI Desbloqueada)");
        }
    }

    // =========================================================================
    // CIERRE Y LIMPIEZA
    // =========================================================================

    /**
     * Libera e interrumpe ordenadamente todos los hilos al cerrar la ventana.
     */
    public void detenerAlCerrar() {
        if (hiloProceso != null && hiloProceso.isAlive()) {
            hiloProceso.interrupt();
        }
        if (hiloClientes != null && hiloClientes.isAlive()) {
            hiloClientes.interrupt();
        }
        if (hiloProductos != null && hiloProductos.isAlive()) {
            hiloProductos.interrupt();
        }
    }
}
