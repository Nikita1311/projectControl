package com.example.programcontrol;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    public TextField nameField;
    public TextField passField;

    public void userEnter(int id) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("projects-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ((ProjectsController)fxmlLoader.getController()).init(id);
        stage.setTitle("Панель проектов");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseAdapter.getDBConnection();
    }
    public void login() throws IOException {
        int id;
        if((id=userCheck())!=-1)
               userEnter(id);
        else new Alert(Alert.AlertType.ERROR,"Пользователь не найден!", ButtonType.OK).show();
    }
    public int userCheck()
    {
        return DatabaseAdapter.getUser(nameField.getText(),passField.getText());
    }
    public void register() throws IOException {
        int id;
        if((id=DatabaseAdapter.addUser(new User.UserBuilder(nameField.getText(),passField.getText()).build()))!=-1)
            userEnter(id);
        else new Alert(Alert.AlertType.ERROR,"Пользователь не создан!", ButtonType.OK).show();
    }
}