package rocks.notme.jfiction;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Jfiction extends Application {
    public static void main(String[] args) {
        //TODO: document everything with javadocs
        //launch the UI
        Jfiction.launch();
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene;
        Stage stage;
        try {
            //AnchorPane root = FXMLLoader.load(getClass().getResource("fxml/FictionUI.fxml"));
            //AnchorPane root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/FictionUI.fxml"));
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Jfiction.this.getClass().getResource("/fxml/FictionUI.fxml"));
            AnchorPane root = loader.load();

            //fill the choicebox, this could probably be done more elegantly
            ChoiceBox<WriterType> fxOutputType;
            for (Node n: root.getChildren()) {
                if (n.getId().equals("fxOutputType")) {
                    fxOutputType = (ChoiceBox<WriterType>)n;
                    fxOutputType.getItems().setAll(WriterType.values());
                    fxOutputType.setValue(WriterType.EPUB);
                }
            }
            scene = new Scene(root, 420, 240);
        }
        catch (Exception e) {
            System.err.println("Unable to load FXML! " + e.getMessage());
            e.printStackTrace();
            scene = new Scene(new Label("ERROR! FXML failed to load! :(((((((("));
        }

        stage = primaryStage;
        stage.setTitle("Java Fanfiction Downloader");
        stage.setScene(scene);
        stage.show();
    }
}
