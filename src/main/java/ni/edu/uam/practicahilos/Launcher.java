package ni.edu.uam.practicahilos;

/**
 * Clase auxiliar de inicio para permitir la ejecución de JavaFX
 * directamente sin requerir parámetros de módulo en línea de comandos.
 * Permite ejecutar la versión FXML por defecto o la versión 100% Java puro con el argumento "puro".
 */
public class Launcher {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("puro")) {
            MainAppPuroJava.main(args);
        } else {
            MainApp.main(args);
        }
    }
}
