package ni.edu.uam.practicahilos;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Implementación 100% en código Java puro (sin FXML).
 * Construye la interfaz gráfica mediante código imperativo Java y gestiona
 * los hilos de ejecución con Thread y Runnable, garantizando que el avance
 * no se pierda al pausar/detener y continuar.
 */
public class MainAppPuroJava extends Application {

    // ---------------------------------------------------------
    // Componentes del Reto Final: Monitor de Clientes
    // ---------------------------------------------------------
    private Label lblEstadoClientes;
    private ProgressBar barraClientes;
    private Label lblPorcentajeClientes;
    private Button btnIniciarClientes;
    private Button btnDetenerClientes;
    private Button btnReiniciarClientes;
    private Thread hiloClientes;
    private int progresoClientes = 0; // Persistencia del progreso

    // ---------------------------------------------------------
    // Componentes del Reto Final: Monitor de Productos
    // ---------------------------------------------------------
    private Label lblEstadoProductos;
    private ProgressBar barraProductos;
    private Label lblPorcentajeProductos;
    private Button btnIniciarProductos;
    private Button btnDetenerProductos;
    private Button btnReiniciarProductos;
    private Thread hiloProductos;
    private int progresoProductos = 0; // Persistencia del progreso

    // ---------------------------------------------------------
    // Componentes del Caso Básico (Pasos 1-5 de la Guía)
    // ---------------------------------------------------------
    private Label lblEstadoBasico;
    private ProgressBar barraBasica;
    private Label lblPorcentajeBasico;
    private Button btnIniciarBasico;
    private Button btnDetenerBasico;
    private Button btnReiniciarBasico;
    private Thread hiloBasico;
    private int progresoBasico = 0; // Persistencia del progreso

    // ---------------------------------------------------------
    // Componentes del Experimento de Bloqueo UI (Paso 6)
    // ---------------------------------------------------------
    private Label lblEstadoBloqueo;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(12);
        root.setPadding(new Insets(16, 20, 16, 20));
        root.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #f1f5f9;");

        // Encabezado Corporativo
        VBox headerBox = new VBox(3);
        Label lblTitulo = new Label("NicaData Solutions S.A.");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label lblSubtitulo = new Label("Sistema de Procesamiento Concurrente (100% Java)");
        lblSubtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b;");
        headerBox.getChildren().addAll(lblTitulo, lblSubtitulo);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // Pestaña 1: Monitor Empresarial Concurrente (Reto Final)
        Tab tabMonitor = new Tab("Monitor Empresarial (Reto Final)", crearVistaMonitor());

        // Pestaña 2: Proceso Básico (Pasos 1-5)
        Tab tabBasico = new Tab("Proceso Básico (Pasos 1-5)", crearVistaBasico());

        // Pestaña 3: Experimento Bloqueo UI (Paso 6)
        Tab tabBloqueo = new Tab("Análisis Bloqueo UI (Paso 6)", crearVistaBloqueo());

        tabPane.getTabs().addAll(tabMonitor, tabBasico, tabBloqueo);

        root.getChildren().addAll(headerBox, new Separator(), tabPane);

        Scene scene = new Scene(root, 700, 540);
        stage.setTitle("Práctica - Hilos en JavaFX | 100% Java | NicaData Solutions S.A.");
        stage.setScene(scene);
        stage.setMinWidth(640);
        stage.setMinHeight(520);
        stage.show();

        stage.setOnCloseRequest(event -> detenerAlCerrar());
    }

    // =========================================================================
    // CONSTRUCCIÓN GRÁFICA EN CÓDIGO JAVA PURO (SIN FXML)
    // =========================================================================

    private VBox crearVistaMonitor() {
        VBox content = new VBox(14);
        content.setPadding(new Insets(14, 10, 10, 10));

        // --- Tarjeta Clientes ---
        VBox cardClientes = new VBox(8);
        aplicarEstiloCard(cardClientes);

        HBox headerClientes = new HBox(10);
        headerClientes.setAlignment(Pos.CENTER_LEFT);
        Label lblTitClientes = new Label("Procesamiento de Clientes");
        lblTitClientes.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Region spacerC = new Region();
        HBox.setHgrow(spacerC, Priority.ALWAYS);
        lblPorcentajeClientes = new Label("0% (0/100)");
        lblPorcentajeClientes.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");
        headerClientes.getChildren().addAll(lblTitClientes, spacerC, lblPorcentajeClientes);

        lblEstadoClientes = new Label("Esperando inicio");
        lblEstadoClientes.setStyle("-fx-font-size: 13px; -fx-text-fill: #334155;");

        barraClientes = new ProgressBar(0);
        barraClientes.setMaxWidth(Double.MAX_VALUE);
        barraClientes.setPrefHeight(22);

        HBox footerClientes = new HBox(10);
        footerClientes.setAlignment(Pos.CENTER_RIGHT);
        Label lblInfoC = new Label("Meta: 100 registros | 80 ms/reg");
        lblInfoC.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        Region spacerC2 = new Region();
        HBox.setHgrow(spacerC2, Priority.ALWAYS);

        btnIniciarClientes = new Button("Iniciar Clientes");
        btnIniciarClientes.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 7 14;");
        btnIniciarClientes.setOnAction(e -> iniciarClientes());

        btnDetenerClientes = new Button("Detener");
        btnDetenerClientes.setDisable(true);
        btnDetenerClientes.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 7 14;");
        btnDetenerClientes.setOnAction(e -> detenerClientes());

        btnReiniciarClientes = new Button("Reiniciar");
        btnReiniciarClientes.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 7 14;");
        btnReiniciarClientes.setOnAction(e -> reiniciarClientes());

        footerClientes.getChildren().addAll(lblInfoC, spacerC2, btnIniciarClientes, btnDetenerClientes, btnReiniciarClientes);
        cardClientes.getChildren().addAll(headerClientes, lblEstadoClientes, barraClientes, footerClientes);

        // --- Tarjeta Productos ---
        VBox cardProductos = new VBox(8);
        aplicarEstiloCard(cardProductos);

        HBox headerProductos = new HBox(10);
        headerProductos.setAlignment(Pos.CENTER_LEFT);
        Label lblTitProd = new Label("Procesamiento de Productos");
        lblTitProd.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Region spacerP = new Region();
        HBox.setHgrow(spacerP, Priority.ALWAYS);
        lblPorcentajeProductos = new Label("0% (0/50)");
        lblPorcentajeProductos.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #059669;");
        headerProductos.getChildren().addAll(lblTitProd, spacerP, lblPorcentajeProductos);

        lblEstadoProductos = new Label("Esperando inicio");
        lblEstadoProductos.setStyle("-fx-font-size: 13px; -fx-text-fill: #334155;");

        barraProductos = new ProgressBar(0);
        barraProductos.setMaxWidth(Double.MAX_VALUE);
        barraProductos.setPrefHeight(22);

        HBox footerProductos = new HBox(10);
        footerProductos.setAlignment(Pos.CENTER_RIGHT);
        Label lblInfoP = new Label("Meta: 50 registros | 140 ms/reg");
        lblInfoP.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        Region spacerP2 = new Region();
        HBox.setHgrow(spacerP2, Priority.ALWAYS);

        btnIniciarProductos = new Button("Iniciar Productos");
        btnIniciarProductos.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 7 14;");
        btnIniciarProductos.setOnAction(e -> iniciarProductos());

        btnDetenerProductos = new Button("Detener");
        btnDetenerProductos.setDisable(true);
        btnDetenerProductos.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 7 14;");
        btnDetenerProductos.setOnAction(e -> detenerProductos());

        btnReiniciarProductos = new Button("Reiniciar");
        btnReiniciarProductos.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 7 14;");
        btnReiniciarProductos.setOnAction(e -> reiniciarProductos());

        footerProductos.getChildren().addAll(lblInfoP, spacerP2, btnIniciarProductos, btnDetenerProductos, btnReiniciarProductos);
        cardProductos.getChildren().addAll(headerProductos, lblEstadoProductos, barraProductos, footerProductos);

        // --- Controles Globales ---
        HBox globalBox = new HBox(12);
        globalBox.setAlignment(Pos.CENTER);
        globalBox.setPadding(new Insets(4, 0, 0, 0));

        Button btnIniciarAmbos = new Button("▶ Iniciar Ambos Procesos");
        btnIniciarAmbos.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16;");
        btnIniciarAmbos.setOnAction(e -> { iniciarClientes(); iniciarProductos(); });

        Button btnDetenerAmbos = new Button("⏹ Detener Todos");
        btnDetenerAmbos.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16;");
        btnDetenerAmbos.setOnAction(e -> { detenerClientes(); detenerProductos(); });

        Button btnReiniciarAmbos = new Button("↺ Reiniciar Todos");
        btnReiniciarAmbos.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 16;");
        btnReiniciarAmbos.setOnAction(e -> { reiniciarClientes(); reiniciarProductos(); });

        globalBox.getChildren().addAll(btnIniciarAmbos, btnDetenerAmbos, btnReiniciarAmbos);

        content.getChildren().addAll(cardClientes, cardProductos, globalBox);
        return content;
    }

    private VBox crearVistaBasico() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25, 25, 25, 25));

        Label lblTitulo = new Label("Procesador de registros");
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        lblEstadoBasico = new Label("Esperando inicio");
        lblEstadoBasico.setStyle("-fx-font-size: 14px;");

        barraBasica = new ProgressBar(0);
        barraBasica.setPrefWidth(420);
        barraBasica.setPrefHeight(24);

        lblPorcentajeBasico = new Label("0%");
        lblPorcentajeBasico.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

        HBox botonesBox = new HBox(12);
        botonesBox.setAlignment(Pos.CENTER);

        btnIniciarBasico = new Button("Iniciar");
        btnIniciarBasico.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16;");
        btnIniciarBasico.setOnAction(e -> iniciarBasico());

        btnDetenerBasico = new Button("Detener");
        btnDetenerBasico.setDisable(true);
        btnDetenerBasico.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16;");
        btnDetenerBasico.setOnAction(e -> detenerBasico());

        btnReiniciarBasico = new Button("Reiniciar");
        btnReiniciarBasico.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 16;");
        btnReiniciarBasico.setOnAction(e -> reiniciarBasico());

        botonesBox.getChildren().addAll(btnIniciarBasico, btnDetenerBasico, btnReiniciarBasico);
        content.getChildren().addAll(lblTitulo, lblEstadoBasico, barraBasica, lblPorcentajeBasico, botonesBox);
        return content;
    }

    private VBox crearVistaBloqueo() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(18, 20, 18, 20));

        VBox cardExp = new VBox(8);
        aplicarEstiloCard(cardExp);
        Label lblExpTit = new Label("Experimento: Bloqueo del JavaFX Application Thread");
        lblExpTit.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label lblExpDesc = new Label("Al presionar el botón, se ejecutará Thread.sleep(5000) en el hilo principal de la UI. La ventana se congelará durante 5 segundos.");
        lblExpDesc.setWrapText(true);

        HBox btnRow = new HBox(15);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        Button btnBloqueante = new Button("⚠ Ejecutar Proceso Bloqueante (5s)");
        btnBloqueante.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16;");
        btnBloqueante.setOnAction(e -> procesoBloqueante());

        lblEstadoBloqueo = new Label("Estado: Normal (UI Desbloqueada)");
        lblEstadoBloqueo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #334155;");
        btnRow.getChildren().addAll(btnBloqueante, lblEstadoBloqueo);

        cardExp.getChildren().addAll(lblExpTit, lblExpDesc, btnRow);

        VBox cardPregunta = new VBox(8);
        aplicarEstiloCard(cardPregunta);
        Label lblPreguntaTit = new Label("Pregunta de Análisis: ¿Por qué no debemos ejecutar procesos prolongados dentro de eventos de JavaFX?");
        lblPreguntaTit.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1e293b;");
        Label lblPreguntaResp = new Label("JavaFX opera bajo un Modelo de Hilo Único (JavaFX Application Thread). Si se realiza una llamada bloqueante en este hilo, se detiene el procesamiento de eventos del mouse/teclado y el redibujado de la escena a 60 FPS. Por eso, toda tarea pesada debe delegarse a hilos secundarios en segundo plano (Thread/Runnable) y comunicarse con la UI exclusivamente mediante Platform.runLater().");
        lblPreguntaResp.setWrapText(true);
        lblPreguntaResp.setStyle("-fx-text-fill: #334155; -fx-font-size: 12px;");
        cardPregunta.getChildren().addAll(lblPreguntaTit, lblPreguntaResp);

        content.getChildren().addAll(cardExp, cardPregunta);
        return content;
    }

    private void aplicarEstiloCard(VBox box) {
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 16; -fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 3);");
    }

    // =========================================================================
    // LÓGICA DE CONCURRENCIA CON PERSISTENCIA DE PROGRESO AL PAUSAR / CONTINUAR
    // =========================================================================

    private void iniciarBasico() {
        if (hiloBasico != null && hiloBasico.isAlive()) return;

        if (progresoBasico >= 100) {
            progresoBasico = 0;
            barraBasica.setProgress(0);
            lblPorcentajeBasico.setText("0%");
        }

        if (progresoBasico > 0) {
            lblEstadoBasico.setText("Continuando desde registro " + (progresoBasico + 1) + "...");
        } else {
            lblEstadoBasico.setText("Procesando registros...");
        }

        btnIniciarBasico.setDisable(true);
        btnDetenerBasico.setDisable(false);
        btnReiniciarBasico.setDisable(true);

        final int inicio = progresoBasico + 1;

        Runnable tarea = () -> {
            try {
                for (int i = inicio; i <= 100; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    Thread.sleep(100);

                    progresoBasico = i;
                    final int avance = i;
                    Platform.runLater(() -> {
                        barraBasica.setProgress(avance / 100.0);
                        lblPorcentajeBasico.setText(avance + "%");
                    });
                }
                Platform.runLater(() -> {
                    lblEstadoBasico.setText("Proceso finalizado (100%)");
                    btnIniciarBasico.setText("Iniciar");
                    btnIniciarBasico.setDisable(false);
                    btnDetenerBasico.setDisable(true);
                    btnReiniciarBasico.setDisable(false);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                final int pausado = progresoBasico;
                Platform.runLater(() -> {
                    lblEstadoBasico.setText("Detenido en " + pausado + "%. Progreso conservado.");
                    btnIniciarBasico.setText("Continuar (" + pausado + "%)");
                    btnIniciarBasico.setDisable(false);
                    btnDetenerBasico.setDisable(true);
                    btnReiniciarBasico.setDisable(false);
                });
            }
        };

        hiloBasico = new Thread(tarea);
        hiloBasico.setName("Hilo-Basico-100Java");
        hiloBasico.setDaemon(true);
        hiloBasico.start();
    }

    private void detenerBasico() {
        if (hiloBasico != null && hiloBasico.isAlive()) {
            btnDetenerBasico.setDisable(true);
            lblEstadoBasico.setText("Solicitando detención...");
            hiloBasico.interrupt();
        }
    }

    private void reiniciarBasico() {
        if (hiloBasico != null && hiloBasico.isAlive()) return;
        progresoBasico = 0;
        barraBasica.setProgress(0);
        lblPorcentajeBasico.setText("0%");
        lblEstadoBasico.setText("Esperando inicio");
        btnIniciarBasico.setText("Iniciar");
        btnIniciarBasico.setDisable(false);
        btnDetenerBasico.setDisable(true);
        btnReiniciarBasico.setDisable(false);
    }

    private void iniciarClientes() {
        if (hiloClientes != null && hiloClientes.isAlive()) return;

        if (progresoClientes >= 100) {
            progresoClientes = 0;
            barraClientes.setProgress(0);
            lblPorcentajeClientes.setText("0% (0/100)");
        }

        if (progresoClientes > 0) {
            lblEstadoClientes.setText("Continuando clientes desde " + (progresoClientes + 1) + "/100...");
        } else {
            lblEstadoClientes.setText("Procesando clientes...");
        }

        btnIniciarClientes.setDisable(true);
        btnDetenerClientes.setDisable(false);
        btnReiniciarClientes.setDisable(true);

        final int total = 100;
        final int retardo = 80;
        final int inicio = progresoClientes + 1;

        Runnable tarea = () -> {
            try {
                for (int i = inicio; i <= total; i++) {
                    if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
                    Thread.sleep(retardo);
                    progresoClientes = i;
                    final int avance = i;
                    Platform.runLater(() -> {
                        barraClientes.setProgress(avance / (double) total);
                        lblPorcentajeClientes.setText(avance + "% (" + avance + "/" + total + ")");
                    });
                }
                Platform.runLater(() -> {
                    lblEstadoClientes.setText("Completado (100 clientes procesados)");
                    btnIniciarClientes.setText("Iniciar Clientes");
                    btnIniciarClientes.setDisable(false);
                    btnDetenerClientes.setDisable(true);
                    btnReiniciarClientes.setDisable(false);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                final int pausado = progresoClientes;
                Platform.runLater(() -> {
                    lblEstadoClientes.setText("Detenido en cliente " + pausado + "/100. Progreso conservado.");
                    btnIniciarClientes.setText("Continuar Clientes (" + pausado + "%)");
                    btnIniciarClientes.setDisable(false);
                    btnDetenerClientes.setDisable(true);
                    btnReiniciarClientes.setDisable(false);
                });
            }
        };

        hiloClientes = new Thread(tarea);
        hiloClientes.setName("Hilo-Clientes-100Java");
        hiloClientes.setDaemon(true);
        hiloClientes.start();
    }

    private void detenerClientes() {
        if (hiloClientes != null && hiloClientes.isAlive()) {
            btnDetenerClientes.setDisable(true);
            lblEstadoClientes.setText("Solicitando detención...");
            hiloClientes.interrupt();
        }
    }

    private void reiniciarClientes() {
        if (hiloClientes != null && hiloClientes.isAlive()) return;
        progresoClientes = 0;
        barraClientes.setProgress(0);
        lblPorcentajeClientes.setText("0% (0/100)");
        lblEstadoClientes.setText("Esperando inicio");
        btnIniciarClientes.setText("Iniciar Clientes");
        btnIniciarClientes.setDisable(false);
        btnDetenerClientes.setDisable(true);
        btnReiniciarClientes.setDisable(false);
    }

    private void iniciarProductos() {
        if (hiloProductos != null && hiloProductos.isAlive()) return;

        if (progresoProductos >= 50) {
            progresoProductos = 0;
            barraProductos.setProgress(0);
            lblPorcentajeProductos.setText("0% (0/50)");
        }

        if (progresoProductos > 0) {
            lblEstadoProductos.setText("Continuando productos desde " + (progresoProductos + 1) + "/50...");
        } else {
            lblEstadoProductos.setText("Procesando productos...");
        }

        btnIniciarProductos.setDisable(true);
        btnDetenerProductos.setDisable(false);
        btnReiniciarProductos.setDisable(true);

        final int total = 50;
        final int retardo = 140;
        final int inicio = progresoProductos + 1;

        Runnable tarea = () -> {
            try {
                for (int i = inicio; i <= total; i++) {
                    if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
                    Thread.sleep(retardo);
                    progresoProductos = i;
                    final int avance = i;
                    Platform.runLater(() -> {
                        barraProductos.setProgress(avance / (double) total);
                        lblPorcentajeProductos.setText(avance + "% (" + avance + "/" + total + ")");
                    });
                }
                Platform.runLater(() -> {
                    lblEstadoProductos.setText("Completado (50 productos procesados)");
                    btnIniciarProductos.setText("Iniciar Productos");
                    btnIniciarProductos.setDisable(false);
                    btnDetenerProductos.setDisable(true);
                    btnReiniciarProductos.setDisable(false);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                final int pausado = progresoProductos;
                Platform.runLater(() -> {
                    int porc = (int) Math.round((pausado / (double) total) * 100.0);
                    lblEstadoProductos.setText("Detenido en producto " + pausado + "/50. Progreso conservado.");
                    btnIniciarProductos.setText("Continuar Productos (" + porc + "%)");
                    btnIniciarProductos.setDisable(false);
                    btnDetenerProductos.setDisable(true);
                    btnReiniciarProductos.setDisable(false);
                });
            }
        };

        hiloProductos = new Thread(tarea);
        hiloProductos.setName("Hilo-Productos-100Java");
        hiloProductos.setDaemon(true);
        hiloProductos.start();
    }

    private void detenerProductos() {
        if (hiloProductos != null && hiloProductos.isAlive()) {
            btnDetenerProductos.setDisable(true);
            lblEstadoProductos.setText("Solicitando detención...");
            hiloProductos.interrupt();
        }
    }

    private void reiniciarProductos() {
        if (hiloProductos != null && hiloProductos.isAlive()) return;
        progresoProductos = 0;
        barraProductos.setProgress(0);
        lblPorcentajeProductos.setText("0% (0/50)");
        lblEstadoProductos.setText("Esperando inicio");
        btnIniciarProductos.setText("Iniciar Productos");
        btnIniciarProductos.setDisable(false);
        btnDetenerProductos.setDisable(true);
        btnReiniciarProductos.setDisable(false);
    }

    private void procesoBloqueante() {
        if (lblEstadoBloqueo != null) {
            lblEstadoBloqueo.setText("Procesando (UI Bloqueada durante 5s)...");
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (lblEstadoBloqueo != null) {
            lblEstadoBloqueo.setText("Finalizado (UI Desbloqueada)");
        }
    }

    private void detenerAlCerrar() {
        if (hiloBasico != null && hiloBasico.isAlive()) hiloBasico.interrupt();
        if (hiloClientes != null && hiloClientes.isAlive()) hiloClientes.interrupt();
        if (hiloProductos != null && hiloProductos.isAlive()) hiloProductos.interrupt();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
