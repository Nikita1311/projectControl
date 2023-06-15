module com.example.programcontrol {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires h2;
    requires commons.io;

    opens com.example.programcontrol to javafx.fxml;
    exports com.example.programcontrol;
}