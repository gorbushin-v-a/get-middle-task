module com.example.middletask {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.middletask to javafx.fxml;
    exports com.example.middletask;
}