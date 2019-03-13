module jfiction {
    requires com.github.librepdf.openpdf;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.jsoup;

    exports rocks.notme.jfiction to javafx.graphics;
    opens rocks.notme.jfiction.jfx to javafx.fxml;
}