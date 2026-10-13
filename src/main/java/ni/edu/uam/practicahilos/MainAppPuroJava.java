package ni.edu.uam.practicahilos;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Implementación 100% en código Java puro (sin FXML y sin CSS).
 * Todos los controles, jerarquías de vista, colores, tipografías y bordes
 * se configuran exclusivamente mediante clases y métodos de la API de Java.
 * 
 * Además, garantiza que al detener/pausar cualquier proceso en curso,
 * el avance no se pierda y pueda continuarse limpiamente.
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
    private int progresoClientes = 0; // Persistencia de avance

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
    private int progresoProductos = 0; // Persistencia de avance

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
    private int progresoBasico = 0; // Persistencia de avance

    // ---------------------------------------------------------
    // Componentes del Experimento de Bloqueo UI (Paso 6)
    // ---------------------------------------------------------
    private Label lblEstadoBloqueo;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(12);
        root.setPadding(new Insets(16, 20, 16, 20));
        root.setBackground(new Background(new BackgroundFill(Color.rgb(241, 245, 249), CornerRadii.EMPTY, Insets.EMPTY)));

        // Encabezado Corporativo (Configurado 100% con clases Java)
        VBox headerBox = new VBox(3);
        Label lblTitulo = new Label("NicaData Solutions S.A.");
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        lblTitulo.setTextFill(Color.rgb(15, 23, 42));

        Label lblSubtitulo = new Label("Sistema de Procesamiento Concurrente (100% Java Puro - Cero CSS)");
        lblSubtitulo.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        lblSubtitulo.setTextFill(Color.rgb(100, 116, 139));
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
    // CONSTRUCCIÓN DE VISTAS (100% JAVA PURO, SIN CSS)
    // =========================================================================

    private VBox crearVistaMonitor() {
        VBox content = new VBox(14);
        content.setPadding(new Insets(14, 10, 10, 10));

        // --- Tarjeta Clientes ---
        VBox cardClientes = new VBox(8);
        aplicarEstiloCardJava(cardClientes);

        HBox headerClientes = new HBox(10);
        headerClientes.setAlignment(Pos.CENTER_LEFT);
        Label lblTitClientes = new Label("Procesamiento de Clientes");
        lblTitClientes.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        lblTitClientes.setTextFill(Color.rgb(30, 41, 59));

        Region spacerC = new Region();
        HBox.setHgrow(spacerC, Priority.ALWAYS);

        lblPorcentajeClientes = new Label("0% (0/100)");
        lblPorcentajeClientes.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblPorcentajeClientes.setTextFill(Color.rgb(37, 99, 235));
        headerClientes.getChildren().addAll(lblTitClientes, spacerC, lblPorcentajeClientes);

        lblEstadoClientes = new Label("Esperando inicio");
        lblEstadoClientes.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        lblEstadoClientes.setTextFill(Color.rgb(51, 65, 85));

        barraClientes = new ProgressBar(0);
        barraClientes.setMaxWidth(Double.MAX_VALUE);
        barraClientes.setPrefHeight(22);

        HBox footerClientes = new HBox(10);
        footerClientes.setAlignment(Pos.CENTER_RIGHT);
        Label lblInfoC = new Label("Meta: 100 registros | 80 ms/reg");
        lblInfoC.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        lblInfoC.setTextFill(Color.rgb(100, 116, 139));
        Region spacerC2 = new Region();
        HBox.setHgrow(spacerC2, Priority.ALWAYS);

        btnIniciarClientes = new Button("Iniciar Clientes");
        aplicarBotonPrimarioJava(btnIniciarClientes);
        btnIniciarClientes.setOnAction(e -> iniciarClientes());

        btnDetenerClientes = new Button("Detener");
        btnDetenerClientes.setDisable(true);
        aplicarBotonPeligroJava(btnDetenerClientes);
        btnDetenerClientes.setOnAction(e -> detenerClientes());

        btnReiniciarClientes = new Button("Reiniciar");
        aplicarBotonSecundarioJava(btnReiniciarClientes);
        btnReiniciarClientes.setOnAction(e -> reiniciarClientes());

        footerClientes.getChildren().addAll(lblInfoC, spacerC2, btnIniciarClientes, btnDetenerClientes, btnReiniciarClientes);
        cardClientes.getChildren().addAll(headerClientes, lblEstadoClientes, barraClientes, footerClientes);

        // --- Tarjeta Productos ---
        VBox cardProductos = new VBox(8);
        aplicarEstiloCardJava(cardProductos);

        HBox headerProductos = new HBox(10);
        headerProductos.setAlignment(Pos.CENTER_LEFT);
        Label lblTitProd = new Label("Procesamiento de Productos");
        lblTitProd.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        lblTitProd.setTextFill(Color.rgb(30, 41, 59));

        Region spacerP = new Region();
        HBox.setHgrow(spacerP, Priority.ALWAYS);

        lblPorcentajeProductos = new Label("0% (0/50)");
        lblPorcentajeProductos.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblPorcentajeProductos.setTextFill(Color.rgb(5, 150, 105));
        headerProductos.getChildren().addAll(lblTitProd, spacerP, lblPorcentajeProductos);

        lblEstadoProductos = new Label("Esperando inicio");
        lblEstadoProductos.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        lblEstadoProductos.setTextFill(Color.rgb(51, 65, 85));

        barraProductos = new ProgressBar(0);
        barraProductos.setMaxWidth(Double.MAX_VALUE);
        barraProductos.setPrefHeight(22);

        HBox footerProductos = new HBox(10);
        footerProductos.setAlignment(Pos.CENTER_RIGHT);
        Label lblInfoP = new Label("Meta: 50 registros | 140 ms/reg");
        lblInfoP.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        lblInfoP.setTextFill(Color.rgb(100, 116, 139));
        Region spacerP2 = new Region();
        HBox.setHgrow(spacerP2, Priority.ALWAYS);

        btnIniciarProductos = new Button("Iniciar Productos");
        aplicarBotonPrimarioJava(btnIniciarProductos);
        btnIniciarProductos.setOnAction(e -> iniciarProductos());

        btnDetenerProductos = new Button("Detener");
        btnDetenerProductos.setDisable(true);
        aplicarBotonPeligroJava(btnDetenerProductos);
        btnDetenerProductos.setOnAction(e -> detenerProductos());

        btnReiniciarProductos = new Button("Reiniciar");
        aplicarBotonSecundarioJava(btnReiniciarProductos);
        btnReiniciarProductos.setOnAction(e -> reiniciarProductos());

        footerProductos.getChildren().addAll(lblInfoP, spacerP2, btnIniciarProductos, btnDetenerProductos, btnReiniciarProductos);
        cardProductos.getChildren().addAll(headerProductos, lblEstadoProductos, barraProductos, footerProductos);

        // --- Controles Globales ---
        HBox globalBox = new HBox(12);
        globalBox.setAlignment(Pos.CENTER);
        globalBox.setPadding(new Insets(4, 0, 0, 0));

        Button btnIniciarAmbos = new Button("Iniciar Ambos Procesos");
        aplicarBotonPrimarioJava(btnIniciarAmbos);
        btnIniciarAmbos.setOnAction(e -> { iniciarClientes(); iniciarProductos(); });

        Button btnDetenerAmbos = new Button("Detener Todos");
        aplicarBotonPeligroJava(btnDetenerAmbos);
        btnDetenerAmbos.setOnAction(e -> { detenerClientes(); detenerProductos(); });

        Button btnReiniciarAmbos = new Button("Reiniciar Todos");
        aplicarBotonSecundarioJava(btnReiniciarAmbos);
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
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        lblEstadoBasico = new Label("Esperando inicio");
        lblEstadoBasico.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));

        barraBasica = new ProgressBar(0);
        barraBasica.setPrefWidth(420);
        barraBasica.setPrefHeight(24);

        lblPorcentajeBasico = new Label("0%");
        lblPorcentajeBasico.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblPorcentajeBasico.setTextFill(Color.rgb(37, 99, 235));

        HBox botonesBox = new HBox(12);
        botonesBox.setAlignment(Pos.CENTER);

        btnIniciarBasico = new Button("Iniciar");
        aplicarBotonPrimarioJava(btnIniciarBasico);
        btnIniciarBasico.setOnAction(e -> iniciarBasico());

        btnDetenerBasico = new Button("Detener");
        btnDetenerBasico.setDisable(true);
        aplicarBotonPeligroJava(btnDetenerBasico);
        btnDetenerBasico.setOnAction(e -> detenerBasico());

        btnReiniciarBasico = new Button("Reiniciar");
        aplicarBotonSecundarioJava(btnReiniciarBasico);
        btnReiniciarBasico.setOnAction(e -> reiniciarBasico());

        botonesBox.getChildren().addAll(btnIniciarBasico, btnDetenerBasico, btnReiniciarBasico);
        content.getChildren().addAll(lblTitulo, lblEstadoBasico, barraBasica, lblPorcentajeBasico, botonesBox);
        return content;
    }

    private VBox crearVistaBloqueo() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(18, 20, 18, 20));

        VBox cardExp = new VBox(8);
        aplicarEstiloCardJava(cardExp);
        Label lblExpTit = new Label("Experimento: Bloqueo del JavaFX Application Thread");
        lblExpTit.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        lblExpTit.setTextFill(Color.rgb(30, 41, 59));

        Label lblExpDesc = new Label("Al presionar el botón, se ejecutará Thread.sleep(5000) en el hilo principal de la UI. La ventana se congelará durante 5 segundos.");
        lblExpDesc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        lblExpDesc.setWrapText(true);

        HBox btnRow = new HBox(15);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        Button btnBloqueante = new Button("Ejecutar Proceso Bloqueante (5s)");
        aplicarBotonPeligroJava(btnBloqueante);
        btnBloqueante.setOnAction(e -> procesoBloqueante());

        lblEstadoBloqueo = new Label("Estado: Normal (UI Desbloqueada)");
        lblEstadoBloqueo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblEstadoBloqueo.setTextFill(Color.rgb(51, 65, 85));
        btnRow.getChildren().addAll(btnBloqueante, lblEstadoBloqueo);

        cardExp.getChildren().addAll(lblExpTit, lblExpDesc, btnRow);

        VBox cardPregunta = new VBox(8);
        aplicarEstiloCardJava(cardPregunta);
        Label lblPreguntaTit = new Label("Pregunta de Análisis: ¿Por qué no debemos ejecutar procesos prolongados dentro de eventos de JavaFX?");
        lblPreguntaTit.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblPreguntaTit.setTextFill(Color.rgb(30, 41, 59));

        Label lblPreguntaResp = new Label("JavaFX opera bajo un Modelo de Hilo Único (JavaFX Application Thread). Si se realiza una llamada bloqueante en este hilo, se detiene el procesamiento de eventos del mouse/teclado y el redibujado de la escena a 60 FPS. Por eso, toda tarea pesada debe delegarse a hilos secundarios en segundo plano (Thread/Runnable) y comunicarse con la UI exclusivamente mediante Platform.runLater().");
        lblPreguntaResp.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        lblPreguntaResp.setTextFill(Color.rgb(51, 65, 85));
        lblPreguntaResp.setWrapText(true);
        cardPregunta.getChildren().addAll(lblPreguntaTit, lblPreguntaResp);

        content.getChildren().addAll(cardExp, cardPregunta);
        return content;
    }

    // =========================================================================
    // MÉTODOS DE ESTILO CON CLASES JAVA PURAS (CERO CSS)
    // =========================================================================

    private void aplicarEstiloCardJava(VBox box) {
        box.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        box.setBorder(new Border(new BorderStroke(Color.rgb(226, 232, 240), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1))));
        box.setPadding(new Insets(16));
    }

    private void aplicarBotonPrimarioJava(Button btn) {
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        btn.setTextFill(Color.WHITE);
        btn.setBackground(new Background(new BackgroundFill(Color.rgb(37, 99, 235), new CornerRadii(6), Insets.EMPTY)));
        btn.setPadding(new Insets(8, 16, 8, 16));
    }

    private void aplicarBotonPeligroJava(Button btn) {
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        btn.setTextFill(Color.WHITE);
        btn.setBackground(new Background(new BackgroundFill(Color.rgb(220, 38, 38), new CornerRadii(6), Insets.EMPTY)));
        btn.setPadding(new Insets(8, 16, 8, 16));
    }

    private void aplicarBotonSecundarioJava(Button btn) {
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        btn.setTextFill(Color.rgb(51, 65, 85));
        btn.setBackground(new Background(new BackgroundFill(Color.rgb(241, 245, 249), new CornerRadii(6), Insets.EMPTY)));
        btn.setBorder(new Border(new BorderStroke(Color.rgb(203, 213, 225), BorderStrokeStyle.SOLID, new CornerRadii(6), new BorderWidths(1))));
        btn.setPadding(new Insets(8, 16, 8, 16));
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
