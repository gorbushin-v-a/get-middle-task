package com.example.middletask.Controller;

import com.example.middletask.Model.Client;
import com.example.middletask.Model.Order;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


// Класс, содержащий логику элементов основного окна
public class MainController {

    // Данные для подключения к бд и количество записей для на одной странице (пагинация)
    private String url;
    private String user;
    private String password;
    private int rowsPerPage = 10;

    // Элементы вкладки "Клиент", связаны с интерфейсом приложения
    private final FileChooser clientFileChooser = new FileChooser();
    private ObservableList<Client> listClients;

    @FXML
    private TableView<Client> client;
    @FXML
    private Pagination paginationClient;
    @FXML
    private TableColumn<Client, String> clientID;
    @FXML
    private TableColumn<Client, String> clientFirstname;
    @FXML
    private TableColumn<Client, String> clientLastname;
    @FXML
    private TableColumn<Client, String> clientEmail;
    @FXML
    private TableColumn<Client, String> clientPhone;
    @FXML
    private TextField clientFirstnameField;
    @FXML
    private TextField clientLastnameField;
    @FXML
    private TextField clientEmailField;
    @FXML
    private TextField clientPhoneField;
    @FXML
    private TextField clientIDField;

    // Элементы вкладки "Заказ", связаны с интерфейсом приложения
    private ObservableList<Order> listOrders;
    private final FileChooser orderFileChooser = new FileChooser();

    @FXML
    private TableView<Order> order;
    @FXML
    private Pagination paginationOrder;
    @FXML
    private TableColumn<Order, String> orderID;
    @FXML
    private TableColumn<Order, String> orderProduct;
    @FXML
    private TableColumn<Order, String> orderClient;
    @FXML
    private TableColumn<Order, String> orderDate;
    @FXML
    private TableColumn<Order, String> orderPrice;
    @FXML
    private TextField orderIDField;
    @FXML
    private TextField orderProductField;
    @FXML
    private ChoiceBox<String> orderChoiceBox;
    @FXML
    private DatePicker orderDatePicker;
    @FXML
    private TextField orderPriceField;

    // основной метод отображения информации в приложении
    // Отображает информацию в таблицах и выпадающем меню клиентов
    public void initialize(){
        Platform.runLater(() -> {
            listClients = FXCollections.observableArrayList();
            paginationClient.setPageFactory(this::createClientPage);
            ObservableList<String> listNamesClients = FXCollections.observableArrayList();

            clientID.setCellValueFactory(new PropertyValueFactory<>("id"));
            clientFirstname.setCellValueFactory(new PropertyValueFactory<>("firstname"));
            clientLastname.setCellValueFactory(new PropertyValueFactory<>("lastname"));
            clientEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            clientPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

            try (Connection connection = DriverManager.getConnection (url, user,password)){
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM Clients");
                ResultSet resultSet = ps.executeQuery();
                while(resultSet.next()){
                    listClients.add(new Client(
                            resultSet.getInt("ID"),
                            resultSet.getString("FIRSTNAME"),
                            resultSet.getString("LASTNAME"),
                            resultSet.getString("EMAIL"),
                            resultSet.getString("PHONE")
                    ));
                    listNamesClients.add(resultSet.getString("FIRSTNAME")
                            +" "+resultSet.getString("LASTNAME"));
                }
                client.setItems(listClients);
                orderChoiceBox.setItems(listNamesClients);
                paginationClient.setPageCount(listClients.size() / (rowsPerPage+1) + 1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            listOrders = FXCollections.observableArrayList();
            paginationOrder.setPageFactory(this::createOrderPage);

            orderID.setCellValueFactory(new PropertyValueFactory<>("id"));
            orderProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
            orderClient.setCellValueFactory(new PropertyValueFactory<>("client"));
            orderDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            orderPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

            try (Connection connection = DriverManager.getConnection (url, user,password)){
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM Orders");
                ResultSet resultSet = ps.executeQuery();
                while(resultSet.next()){
                    listOrders.add(new Order(
                            resultSet.getInt("ID"),
                            resultSet.getString("PRODUCT"),
                            resultSet.getString("CLIENT"),
                            resultSet.getString("DATE"),
                            resultSet.getString("PRICE")
                    ));
                }
                order.setItems(listOrders);
                paginationOrder.setPageCount(listOrders.size() / (rowsPerPage+1) + 1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    // Методы, связанные с кнопками и дающие им функциональность
    // Вкладка "Клиенты"
    // Метод добавления клиента
    @FXML
    protected void onAddClientButtonClick(){
        if(!(clientFirstnameField.getText().isEmpty()&&clientLastnameField.getText()
                .isEmpty()&&clientEmailField.getText().isEmpty()&&clientPhoneField.getText().isEmpty())) {
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO Clients(firstname, lastname, email, phone) VALUES('"
                                + clientFirstnameField.getText() + "', '"
                                + clientLastnameField.getText() + "', '"
                                + clientEmailField.getText() + "', '"
                                + clientPhoneField.getText() + "')");
                ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            initialize();
        }
    }

    // Метод удаления клиента
    @FXML
    protected void onDeleteClientButtonClick(){
        if(!clientIDField.getText().replaceAll("\\D", "").isEmpty()) {
            try (Connection connection = DriverManager.getConnection (url, user,password)){
                PreparedStatement ps = connection.prepareStatement("DELETE FROM Clients WHERE ID="
                        +clientIDField.getText().replaceAll("\\D", "")+";");
                ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            initialize();
        }
    }

    // Метод изменения клиента
    @FXML
    protected void onUpdateClientButtonClick(){
        if(!clientIDField.getText().replaceAll("[^\\d]", "").isEmpty()) {
            try (Connection connection = DriverManager.getConnection (url, user,password)){
                String query = "UPDATE Clients ";
                boolean flag = false;
                if(!clientFirstnameField.getText().isEmpty()){
                    query += " SET FIRSTNAME='"+clientFirstnameField.getText()+"'";
                    flag = true;
                }
                if(!clientLastnameField.getText().isEmpty()){
                    if(flag){
                        query += ", LASTNAME='"+clientLastnameField.getText()+"'";
                    }else{
                        query += "SET LASTNAME='"+clientLastnameField.getText()+"'";
                        flag = true;
                    }
                }
                if(!clientEmailField.getText().isEmpty()){
                    if(flag){
                        query += ", EMAIL='"+clientEmailField.getText()+"'";
                    }else{
                        query += "SET EMAIL='"+clientEmailField.getText()+"'";
                        flag = true;
                    }
                }
                if(!clientPhoneField.getText().isEmpty()){
                    if(flag){
                        query += ", PHONE='"+clientPhoneField.getText()+"'";
                    }else{
                        query += "SET PHONE='"+clientPhoneField.getText()+"'";
                        flag = true;
                    }
                }
                if(flag){
                    PreparedStatement ps = connection.prepareStatement(query +" WHERE ID="
                            +clientIDField.getText().replaceAll("[^\\d]", "")+";");
                    ps.execute();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            initialize();
        }
    }

    // Метод поиска/фильтрации клиентов
    @FXML
    protected void onSearchClientButtonClick(){
        String queryWhere = "";
        boolean flag = false;
        boolean flagID = false;
        if(!clientIDField.getText().replaceAll("[^\\d]", "").isEmpty()){
            queryWhere += " ID='"+clientIDField.getText().replaceAll("[^\\d]", "")+"'";
            flagID = true;
        }else{
            if(!clientFirstnameField.getText().isEmpty()){
                queryWhere += " FIRSTNAME='"+clientFirstnameField.getText()+"'";
                flag = true;
            }
            if(!clientLastnameField.getText().isEmpty()){
                if(flag){
                    queryWhere += " AND LASTNAME='"+clientLastnameField.getText()+"'";
                }else{
                    queryWhere += " LASTNAME='"+clientLastnameField.getText()+"'";
                    flag = true;
                }
            }
            if(!clientEmailField.getText().isEmpty()){
                if(flag){
                    queryWhere += " AND EMAIL='"+clientEmailField.getText()+"'";
                }else{
                    queryWhere += " EMAIL='"+clientEmailField.getText()+"'";
                    flag = true;
                }
            }
            if(!clientPhoneField.getText().isEmpty()){
                if(flag){
                    queryWhere += " AND PHONE='"+clientPhoneField.getText()+"'";
                }else{
                    queryWhere += " PHONE='"+clientPhoneField.getText()+"'";
                    flag = true;
                }
            }
        }
        if(flag || flagID){
            listClients = FXCollections.observableArrayList();
            paginationClient.setPageFactory(this::createClientPage);
            ObservableList<String> listNamesClients = FXCollections.observableArrayList();

            clientID.setCellValueFactory(new PropertyValueFactory<>("id"));
            clientFirstname.setCellValueFactory(new PropertyValueFactory<>("firstname"));
            clientLastname.setCellValueFactory(new PropertyValueFactory<>("lastname"));
            clientEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            clientPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

            try (Connection connection = DriverManager.getConnection (url, user,password)){

                PreparedStatement ps = connection.prepareStatement("SELECT * FROM Clients WHERE"+queryWhere);
                ResultSet resultSet = ps.executeQuery();
                while(resultSet.next()){
                    listClients.add(new Client(
                            resultSet.getInt("ID"),
                            resultSet.getString("FIRSTNAME"),
                            resultSet.getString("LASTNAME"),
                            resultSet.getString("EMAIL"),
                            resultSet.getString("PHONE")
                    ));
                    listNamesClients.add(resultSet.getString("FIRSTNAME")+" "
                            +resultSet.getString("LASTNAME"));
                }
                client.setItems(listClients);
                orderChoiceBox.setItems(listNamesClients);
                paginationClient.setPageCount(listClients.size() / rowsPerPage + 1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            initialize();
        }
    }

    // Метод загрузки клиентов из CSV файла
    // Открывает дополнительное окно выбора файла
    @FXML
    protected void onOpenFileClientButtonClick(ActionEvent ae){
        configureFileChooser(clientFileChooser);
        Node source = (Node) ae.getSource();
        Window stage = source.getScene().getWindow();
        File file = clientFileChooser.showOpenDialog(stage);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            readInputStreamToDB(inputStream);
            initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Метод сохранения клиентов в CSV файл
    // Открывает дополнительное окно выбора файла
    @FXML
    protected void onSaveFileClientButtonClick(ActionEvent ae){
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        fileChooser.setTitle("Save CSV");
        Node source = (Node) ae.getSource();
        Window stage = source.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        List<String[]> dataLines = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection (url, user,password)){
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Clients");
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                dataLines.add(new String[]
                        {String.valueOf(resultSet.getInt(1)),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getString(4) });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PrintWriter pw = new PrintWriter(file,"Cp1251")) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // Вкладка "Заказы"
    // Метод добавления заказа
    @FXML
    protected void onAddOrderButtonClick(){
        if(!(orderProductField.getText().isEmpty()&&orderChoiceBox.getValue()
                .isEmpty()&&orderDatePicker.getValue().toString().isEmpty()&&orderPriceField.getText().isEmpty())) {
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO Orders(product, client, date, price) VALUES('"
                                + orderProductField.getText() + "', '"
                                + orderChoiceBox.getValue() + "', '"
                                + orderDatePicker.getValue().toString() + "', '"
                                + orderPriceField.getText() + "')");
                ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            initialize();
        }
    }

    // Метод удаления заказа
    @FXML
    protected void onDeleteOrderButtonClick(){
        if(!orderIDField.getText().replaceAll("\\D", "").isEmpty()) {
            try (Connection connection = DriverManager.getConnection (url, user,password)){
                PreparedStatement ps = connection.prepareStatement("DELETE FROM Orders WHERE ID="
                        +orderIDField.getText().replaceAll("\\D", "")+";");
                ps.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            initialize();
        }
    }

    // Метод изменения заказа
    @FXML
    protected void onUpdateOrderButtonClick(){
        if(!orderIDField.getText().replaceAll("[^\\d]", "").isEmpty()) {
            try (Connection connection = DriverManager.getConnection (url, user,password)){
                String query = "UPDATE Orders ";
                boolean flag = false;
                if(!orderProductField.getText().isEmpty()){
                    query += " SET PRODUCT='"+orderProductField.getText()+"'";
                    flag = true;
                }
                if(!orderChoiceBox.getValue().isEmpty()){
                    if(flag){
                        query += ", CLIENT='"+orderChoiceBox.getValue()+"'";
                    }else{
                        query += "SET CLIENT='"+orderChoiceBox.getValue()+"'";
                        flag = true;
                    }
                }
                if(!(orderDatePicker.getValue() == null)){
                    if(flag){
                        query += ", DATE='"+orderDatePicker.getValue().toString()+"'";
                    }else{
                        query += "SET DATE='"+orderDatePicker.getValue().toString()+"'";
                        flag = true;
                    }
                }
                if(!orderPriceField.getText().isEmpty()){
                    if(flag){
                        query += ", PRICE='"+orderPriceField.getText()+"'";
                    }else{
                        query += "SET PRICE='"+orderPriceField.getText()+"'";
                        flag = true;
                    }
                }
                if(flag){
                    PreparedStatement ps = connection.prepareStatement(query +" WHERE ID="
                            +orderIDField.getText().replaceAll("[^\\d]", "")+";");
                    ps.execute();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            initialize();
        }
    }

    // Метод поиска/фильтрации заказов
    @FXML
    protected void onSearchOrderButtonClick() {
        String queryWhere = "";
        boolean flag = false;
        boolean flagID = false;
        if (!orderIDField.getText().replaceAll("[^\\d]", "").isEmpty()) {
            queryWhere += " ID='" + orderIDField.getText().replaceAll("[^\\d]", "") + "'";
            flagID = true;
        } else {
            if (!orderProductField.getText().isEmpty()) {
                queryWhere += " PRODUCT='" + orderProductField.getText() + "'";
                flag = true;
            }
            if (!orderChoiceBox.getValue().isEmpty()) {
                if (flag) {
                    queryWhere += " AND CLIENT='" + orderChoiceBox.getValue() + "'";
                } else {
                    queryWhere += " CLIENT='" + orderChoiceBox.getValue() + "'";
                    flag = true;
                }
            }
            if (!(orderDatePicker.getValue() == null)) {
                if (flag) {
                    queryWhere += " AND DATE='" + orderDatePicker.getValue().toString() + "'";
                } else {
                    queryWhere += " DATE='" + orderDatePicker.getValue().toString() + "'";
                    flag = true;
                }
            }
            if (!orderPriceField.getText().isEmpty()) {
                if (flag) {
                    queryWhere += " AND PRICE='" + orderPriceField.getText() + "'";
                } else {
                    queryWhere += " PRICE='" + orderPriceField.getText() + "'";
                    flag = true;
                }
            }
        }
        if (flag || flagID) {
            listOrders = FXCollections.observableArrayList();
            paginationOrder.setPageFactory(this::createOrderPage);

            orderID.setCellValueFactory(new PropertyValueFactory<>("id"));
            orderProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
            orderClient.setCellValueFactory(new PropertyValueFactory<>("client"));
            orderDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            orderPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

            try (Connection connection = DriverManager.getConnection(url, user, password)) {

                PreparedStatement ps = connection.prepareStatement("SELECT * FROM Orders WHERE" + queryWhere);
                ResultSet resultSet = ps.executeQuery();
                while(resultSet.next()){
                    listOrders.add(new Order(
                            resultSet.getInt("ID"),
                            resultSet.getString("PRODUCT"),
                            resultSet.getString("CLIENT"),
                            resultSet.getString("DATE"),
                            resultSet.getString("PRICE")
                    ));
                }
                order.setItems(listOrders);
                paginationOrder.setPageCount(listOrders.size() / (rowsPerPage+1) + 1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            initialize();
        }
    }

    // Метод загрузки заказов из CSV файла
    // Открывает дополнительное окно выбора файла
    @FXML
    protected void onOpenFileOrderButtonClick(ActionEvent ae){
        configureFileChooser(orderFileChooser);
        Node source = (Node) ae.getSource();
        Window stage = source.getScene().getWindow();
        File file = orderFileChooser.showOpenDialog(stage);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            readInputOrderStreamToDB(inputStream);
            initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Метод сохранения заказов в CSV файл
    // Открывает дополнительное окно выбора файла
    @FXML
    protected void onSaveFileOrderButtonClick(ActionEvent ae){
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        fileChooser.setTitle("Save CSV");
        Node source = (Node) ae.getSource();
        Window stage = source.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        List<String[]> dataLines = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection (url, user,password)){

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Orders");
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                dataLines.add(new String[]{
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5) });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PrintWriter pw = new PrintWriter(file,"Cp1251")) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Дополнительные методы
    // Сеттеры для записи в приватные поля
    public void setUrl(String url) {
        this.url = url;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    // Дополнительные медоты к методам сохранения в CSV
    // Преобразуют записи в подходящий для данного формата вариант
    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }
    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    // Метод настройки окна выбора файла
    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
    }

    // вспомогательные методы загрузки данных из файла
    // читают и записывают в базу данных
    private void readInputStreamToDB(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "Cp1251"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO Clients(firstname, lastname, email, phone) VALUES('"
                                    + parts[0] + "', '"
                                    + parts[1] + "', '"
                                    + parts[2] + "', '"
                                    + parts[3] + "')");
                    ps.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private void readInputOrderStreamToDB(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "Cp1251"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO Orders(product, client, date, price) VALUES('"
                                    + parts[0] + "', '"
                                    + parts[1] + "', '"
                                    + parts[2] + "', '"
                                    + parts[3] + "')");
                    ps.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // методы, создающие страницы таблицы
    private Node createClientPage(int pageIndex) {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, listClients.size());
        client.setItems(FXCollections.observableArrayList(listClients.subList(fromIndex, toIndex)));
        return new BorderPane(client);
    }
    private Node createOrderPage(int pageIndex) {
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, listOrders.size());
        order.setItems(FXCollections.observableArrayList(listOrders.subList(fromIndex, toIndex)));
        return new BorderPane(order);
    }
}
