module com.example.finaltraining1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.ooxml;
    requires org.apache.commons.compress;


    opens com.example.finaltraining1 to javafx.fxml;
    exports com.example.finaltraining1;
}