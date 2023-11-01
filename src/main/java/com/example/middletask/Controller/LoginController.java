package com.example.middletask.Controller;

import com.example.middletask.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Класс, содержащий логику элементов окна логина
public class LoginController {
    @FXML
    private TextField fieldUrl;
    @FXML
    private TextField fieldName;
    @FXML
    private TextField fieldPasswd;
    @FXML
    private Label labelState;

    // метод получает данные от пользователя и передаёт в основное окно, которое и запускает, закрывает окно логина
    // в случае неудачи выводит ошибку в верхней части окна
    @FXML
    protected void onLoginButtonClick(ActionEvent ae) throws IOException {
        String url = fieldUrl.getText();
        String user = fieldName.getText();
        String password = fieldPasswd.getText();

        try (Connection connection = DriverManager.getConnection (url, user,password)){
            Stage stage = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            MainController controller = fxmlLoader.getController();
            controller.setUrl(url);
            controller.setUser(user);
            controller.setPassword(password);

            stage.setTitle("Client-Order Management System");
            stage.setScene(scene);
            stage.setMinWidth(180);
            stage.show();

            Node source = (Node) ae.getSource();
            Stage loginStage = (Stage) source.getScene().getWindow();
            loginStage.close();
        } catch (SQLException e) {
            labelState.setText(e.toString());
        }
    }
}

