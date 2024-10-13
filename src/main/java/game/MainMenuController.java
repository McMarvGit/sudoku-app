package game;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML
    private Button deskButton;

    @FXML
    private Button startButton;

    @FXML
    private Button settingsButton;

    private MainController mainController;
    private SettingsController settingsController;

    public MainMenuController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void createGame(ActionEvent event) {
        if (this.mainController.chooseDifficulty()) {
            this.mainController.loadFXML("MainScene.fxml");
        }
    }

    @FXML
    void openSettings(ActionEvent event) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../settings.fxml"));
            this.settingsController = new SettingsController(this.mainController);
            this.settingsController.setStage(stage);
            String styleSheet = getClass().getResource("../mainMenu.css").toExternalForm();
            fxmlLoader.setController(this.settingsController);
            Parent root = fxmlLoader.load();
            stage.setTitle("Settings");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(styleSheet);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); // Blockiert die Interaktion mit anderen Fenstern
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openStatistics(ActionEvent event) {
        this.mainController.loadFXML("Statistics.fxml");
    }

    @FXML
    void backToDesktop(ActionEvent e) {
        Platform.exit();
    }

}
