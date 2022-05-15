module CH4 {
    opens sample;
    opens sample.View;
    opens sample.Model;
    opens sample.Controller to javafx.fxml;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
}