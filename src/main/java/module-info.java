module game {
    requires javafx.controls;
    requires javafx.fxml;

    //opens game to javafx.fxml;
    exports game.gui;
    opens game to javafx.graphics;
}
