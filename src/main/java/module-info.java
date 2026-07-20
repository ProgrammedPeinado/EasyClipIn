module com.easyclipin.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.prefs;


    opens com.easyclipin.demo1 to javafx.fxml;
    exports com.easyclipin.demo1;
}