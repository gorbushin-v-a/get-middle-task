module com.example.middletask {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
//    requires java.desktop;



    opens com.example.middletask.Model to javafx.base;
    opens com.example.middletask.Controller to javafx.fxml;
//    opens com.example.middletask to javafx.fxml;
    exports com.example.middletask;
//    exports com.example.middletask.Controller;
}