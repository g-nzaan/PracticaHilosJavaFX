module ni.edu.uam.practicahilos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens ni.edu.uam.practicahilos to javafx.fxml;
    exports ni.edu.uam.practicahilos;
}
