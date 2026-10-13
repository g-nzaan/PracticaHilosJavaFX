package ni.edu.uam.practicahilos;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

/**
 * Controlador principal para la práctica de concurrencia en JavaFX.
 * Implementa el procesamiento básico (Pasos 1-4), el experimento de bloqueo de interfaz (Paso 6)
 * y el Reto Final de procesamiento concurrente multi-hilo (Clientes y Productos).
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

    // =========================================================================
    // Componentes del Experimento de Bloqueo UI (Paso 6)
    // =========================================================================
    @FXML
    private Label lblEstadoBloqueo;

    @FXML
    private Button btnBloqueante;

    // =========================================================================
    // LÓGICA DEL PROCESADOR BÁSICO (Pasos 1 a 4)
    // =========================================================================

    @FXML
    private void iniciarProceso() {
        // Evitar iniciar un proceso dos veces simultáneamente
        if (hiloProceso != null && hiloProceso.isAlive()) {
            return;
        }

        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Procesando registros...");

        btnIniciar.setDisable(true);
        btnDetener.setDisable(false);
        btnReiniciar.setDisable(true);

        Runnable tarea = () -> {
            try {
                for (int i = 1; i <= 100; i++) {
                    // Verificación de bandera de interrupción
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    // Simula carga de trabajo (100 ms por registro)
                    Thread.sleep(100);

                    final int avance = i;

                    // Actualización segura del hilo de JavaFX
                    Platform.runLater(() -> {
                        barraProgreso.setProgress(avance / 100.0);
                        lblPorcentaje.setText(avance + "%");
                    });
                }

                Platform.runLater(() -> {
                    lblEstado.setText("Proceso finalizado");
                    habilitarBotones();
                });

            } catch (InterruptedException e) {
                // Restablecer el estado de interrupción del hilo
                Thread.currentThread().interrupt();

                Platform.runLater(() -> {
                    lblEstado.setText("Proceso detenido");
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
        if (hiloProceso != null && hiloProceso.isAlive()) {
            return;
        }

        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Esperando inicio");

        habilitarBotones();
    }

    private void habilitarBotones() {
        btnIniciar.setDisable(false);
        btnDetener.setDisable(true);
        btnReiniciar.setDisable(false);
    }

    // =========================================================================
    // LÓGICA DEL RETO FINAL: MONITOR DE PROCESOS CONCURRENTES (Paso 11)
    // =========================================================================

    /**
     * Inicia el procesamiento concurrente de Clientes (100 registros a 80 ms).
     */
    @FXML
    private void iniciarClientes() {
        if (hiloClientes != null && hiloClientes.isAlive()) {
            return;
        }

        barraClientes.setProgress(0);
        lblPorcentajeClientes.setText("0% (0/100)");
        lblEstadoClientes.setText("Procesando clientes...");

        btnIniciarClientes.setDisable(true);
        btnDetenerClientes.setDisable(false);
        btnReiniciarClientes.setDisable(true);

        final int totalClientes = 100;
        final int tiempoEsperaMs = 80; // Tiempo diferenciado

        Runnable tareaClientes = () -> {
            try {
                for (int i = 1; i <= totalClientes; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    Thread.sleep(tiempoEsperaMs);

                    final int avance = i;
                    Platform.runLater(() -> {
                        barraClientes.setProgress(avance / (double) totalClientes);
                        lblPorcentajeClientes.setText(avance + "% (" + avance + "/" + totalClientes + ")");
                    });
                }

                Platform.runLater(() -> {
                    lblEstadoClientes.setText("Completado (100 clientes procesados)");
                    habilitarBotonesClientes();
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Platform.runLater(() -> {
                    lblEstadoClientes.setText("Proceso de clientes detenido");
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

        barraClientes.setProgress(0);
        lblPorcentajeClientes.setText("0% (0/100)");
        lblEstadoClientes.setText("Esperando inicio");
        habilitarBotonesClientes();
    }

    private void habilitarBotonesClientes() {
        btnIniciarClientes.setDisable(false);
        btnDetenerClientes.setDisable(true);
        btnReiniciarClientes.setDisable(false);
    }

    /**
     * Inicia el procesamiento concurrente de Productos (50 registros a 140 ms).
     */
    @FXML
    private void iniciarProductos() {
        if (hiloProductos != null && hiloProductos.isAlive()) {
            return;
        }

        barraProductos.setProgress(0);
        lblPorcentajeProductos.setText("0% (0/50)");
        lblEstadoProductos.setText("Procesando productos...");

        btnIniciarProductos.setDisable(true);
        btnDetenerProductos.setDisable(false);
        btnReiniciarProductos.setDisable(true);

        final int totalProductos = 50;
        final int tiempoEsperaMs = 140; // Tiempo diferenciado

        Runnable tareaProductos = () -> {
            try {
                for (int i = 1; i <= totalProductos; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    Thread.sleep(tiempoEsperaMs);

                    final int avance = i;
                    Platform.runLater(() -> {
                        barraProductos.setProgress(avance / (double) totalProductos);
                        lblPorcentajeProductos.setText(avance + "% (" + avance + "/" + totalProductos + ")");
                    });
                }

                Platform.runLater(() -> {
                    lblEstadoProductos.setText("Completado (50 productos procesados)");
                    habilitarBotonesProductos();
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Platform.runLater(() -> {
                    lblEstadoProductos.setText("Proceso de productos detenido");
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

        barraProductos.setProgress(0);
        lblPorcentajeProductos.setText("0% (0/50)");
        lblEstadoProductos.setText("Esperando inicio");
        habilitarBotonesProductos();
    }

    private void habilitarBotonesProductos() {
        btnIniciarProductos.setDisable(false);
        btnDetenerProductos.setDisable(true);
        btnReiniciarProductos.setDisable(false);
    }

    /**
     * Inicia ambos procesos concurrentemente.
     */
    @FXML
    private void iniciarTodos() {
        iniciarClientes();
        iniciarProductos();
    }

    /**
     * Detiene ambos procesos concurrentemente.
     */
    @FXML
    private void detenerTodos() {
        detenerClientes();
        detenerProductos();
    }

    /**
     * Reinicia ambos procesos concurrentemente.
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
