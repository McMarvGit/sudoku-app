package game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SettingsController {

    @FXML
    private ColorPicker primaryColorPicker;

    @FXML
    private ColorPicker secondaryColorPicker;

    @FXML
    private Button cancelButton;

    @FXML
    private Button applyButton;

    private Stage stage;
    private MainController mainController;


    public SettingsController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void cancelSettings(ActionEvent event) {
        if(this.stage != null){
            stage.close();
        }
    }

    @FXML
    void saveSettings(ActionEvent event) {
        saveColors(this.primaryColorPicker.getValue(), this.secondaryColorPicker.getValue());
        if(this.stage != null){
            stage.close();
        }
    }

    private void saveColors(Color primaryColor, Color secondaryColor) {
        this.mainController.saveColors(primaryColor, secondaryColor);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
