package ni.edu.uam.practicahilos;

/**
 * Clase principal de inicio 100% en código Java.
 * Ejecuta la versión en Java puro (sin FXML ni CSS) por defecto.
 */
public class Launcher {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("fxml")) {
            MainApp.main(args);
        } else {
            MainAppPuroJava.main(args);
        }
    }
}
