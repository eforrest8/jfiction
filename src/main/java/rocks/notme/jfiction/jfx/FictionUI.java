package rocks.notme.jfiction.jfx;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import rocks.notme.jfiction.Jfiction;
import rocks.notme.jfiction.Story;

public class FictionUI extends Application {

    private Stage stage;
    @FXML private TextField fxStoryUrl;
    @FXML private TextField fxAuthorText;
    @FXML private ProgressBar progBar;
    @FXML private Button fxDownloadButton;

    @Override
    public void start(Stage primaryStage) {
        Scene scene;
        try {
            AnchorPane root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/FictionUI.fxml"));
            scene = new Scene(root, 420, 240);
        }
        catch (Exception e) {
            System.err.println("Unable to load FXML! " + e.getMessage());
            scene = new Scene(new Label("ERROR! FXML failed to load! :(((((((("));
        }

        //AnchorPane anchorPane = new AnchorPane();
        //Scene scene = new Scene();

        stage = primaryStage;
        stage.setTitle("Java Fanfiction Downloader");
        stage.setScene(scene);
        stage.show();
    }

    @FXML public void Download_OnClick() {
        //validate
        if (!Validator.isValid(fxStoryUrl.getText(), fxAuthorText.getText())) {
            new Alert(Alert.AlertType.ERROR, Validator.getMessage()).showAndWait();
        } else { //all good, hopefully
            fxDownloadButton.setDisable(true);
            Service<Void> service = new Service<>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<>() {
                        @Override
                        public Void call() {
                            try {
                                Story story = new Story(fxStoryUrl.getText().trim(), fxAuthorText.getText().trim());
                                story.progressProperty().addListener(
                                        (obs, oldProgress, newProgress) -> updateProgress(story.getProgress(), 1)
                                );
                                story.doLongProcessing();
                                Jfiction.createPDF(story);
                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }
                            return null;
                        }
                    };
                }
                @Override
                protected void succeeded() {
                    new Alert(Alert.AlertType.INFORMATION, "Download Complete!").showAndWait();
                    fxDownloadButton.setDisable(false);
                }
            };
            progBar.progressProperty().bind(service.progressProperty());
            service.restart();
        }
    }

    @FXML public void About_OnClick() {
        new Alert(Alert.AlertType.INFORMATION, "This is the about dialog! I'll make it useful later.").showAndWait();
    }

    private static final class Validator {
        private static String message = "";
        static boolean isValid(String id, String author) {
            if (id.isEmpty()) {
                message = "Story ID must not be empty.";
            } else if (author.isEmpty()) {
                message = "Author must not be empty.";
            } else if (!id.matches("^[0-9]{7}$")) {
                message = "\"" + id + "\" is not a valid Story ID.\nA Story ID must be a seven digit number.";
            } else {
                return true;
            }
            return false;
        }
        static String getMessage() {
            return message;
        }
    }
}
