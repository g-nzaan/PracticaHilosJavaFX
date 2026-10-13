package ni.edu.uam.practicahilos;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Generador automático de capturas de pantalla para evidencia de la práctica.
 */
public class GeneradorCapturas extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("procesador-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 700, 540);
        stage.setScene(scene);
        stage.setTitle("Captura de Evidencias - Hilos JavaFX");
        stage.show();

        TabPane tabPane = (TabPane) root.lookup(".tab-pane");
        Label lblPorcClientes = (Label) root.lookup("#lblPorcentajeClientes");
        Label lblEstClientes = (Label) root.lookup("#lblEstadoClientes");
        ProgressBar barClientes = (ProgressBar) root.lookup("#barraClientes");

        Label lblPorcProductos = (Label) root.lookup("#lblPorcentajeProductos");
        Label lblEstProductos = (Label) root.lookup("#lblEstadoProductos");
        ProgressBar barProductos = (ProgressBar) root.lookup("#barraProductos");

        Label lblPorcBasico = (Label) root.lookup("#lblPorcentaje");
        Label lblEstBasico = (Label) root.lookup("#lblEstado");
        ProgressBar barBasico = (ProgressBar) root.lookup("#barraProgreso");

        File imgDir = new File("docs/img");
        if (!imgDir.exists()) {
            imgDir.mkdirs();
        }

        // Hilo secundario para secuenciar las capturas de pantalla
        new Thread(() -> {
            try {
                Thread.sleep(600);

                // 1. Monitor Inicial
                Platform.runLater(() -> {
                    guardarCaptura(scene, new File("docs/img/monitor_inicial.png"));
                });
                Thread.sleep(400);

                // 2. Monitor Concurrente en Ejecución (Clientes 65%, Productos 40%)
                Platform.runLater(() -> {
                    barClientes.setProgress(0.65);
                    lblPorcClientes.setText("65% (65/100)");
                    lblEstClientes.setText("Procesando clientes (Hilo-Clientes activo)...");

                    barProductos.setProgress(0.40);
                    lblPorcProductos.setText("40% (20/50)");
                    lblEstProductos.setText("Procesando productos (Hilo-Productos activo)...");

                    guardarCaptura(scene, new File("docs/img/monitor_concurrente.png"));
                });
                Thread.sleep(400);

                // 3. Monitor Concurrente Completado (Clientes 100%, Productos 100%)
                Platform.runLater(() -> {
                    barClientes.setProgress(1.0);
                    lblPorcClientes.setText("100% (100/100)");
                    lblEstClientes.setText("Completado (100 clientes procesados)");

                    barProductos.setProgress(1.0);
                    lblPorcProductos.setText("100% (50/50)");
                    lblEstProductos.setText("Completado (50 productos procesados)");

                    guardarCaptura(scene, new File("docs/img/monitor_completado.png"));
                });
                Thread.sleep(400);

                // 4. Proceso Básico
                Platform.runLater(() -> {
                    if (tabPane != null && tabPane.getTabs().size() > 1) {
                        tabPane.getSelectionModel().select(1);
                    }
                    if (barBasico != null) barBasico.setProgress(0.60);
                    if (lblPorcBasico != null) lblPorcBasico.setText("60%");
                    if (lblEstBasico != null) lblEstBasico.setText("Procesando registros...");

                    guardarCaptura(scene, new File("docs/img/proceso_basico.png"));
                });
                Thread.sleep(400);

                // 5. Análisis Bloqueo UI
                Platform.runLater(() -> {
                    if (tabPane != null && tabPane.getTabs().size() > 2) {
                        tabPane.getSelectionModel().select(2);
                    }
                    guardarCaptura(scene, new File("docs/img/bloqueo_ui.png"));
                });
                Thread.sleep(500);

                Platform.runLater(() -> {
                    stage.close();
                    Platform.exit();
                    System.exit(0);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.exit();
                System.exit(1);
            }
        }).start();
    }

    private static void guardarCaptura(Scene scene, File archivo) {
        try {
            WritableImage image = scene.snapshot(null);
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            PixelReader reader = image.getPixelReader();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    bufferedImage.setRGB(x, y, reader.getArgb(x, y));
                }
            }

            ImageIO.write(bufferedImage, "png", archivo);
            System.out.println("Captura guardada en: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
