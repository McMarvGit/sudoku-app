package game;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Locale;

public class MainController extends Application {
//Test commit 
    private Stage mainStage;

    private MainSceneController mainSceneController;
    private MainMenuController mainMenuController;
    private StatisticsController statisticsController;
    private String difficulty;

    private Color primaryColor;
    private Color secondaryColor;

    @Override
    public void start(Stage stage) {
        Locale.setDefault(Locale.ENGLISH);
        this.mainStage = stage;
        this.mainStage.setTitle("Sudoku");
        loadFXML("MainMenu.fxml");
    }

    public static void main(String[] args) {
        launch();
    }

    public void loadFXML(String fxml) {
        try {
            FXMLLoader loader;
            Parent root;
            String styleSheet = "";

            switch (fxml) {
                case "MainMenu.fxml":
                    loader = new FXMLLoader(MainController.class.getResource("../MainMenu.fxml"));
                    this.mainMenuController = new MainMenuController(this);
                    loader.setController(this.mainMenuController);
                    root = loader.load();
                    styleSheet = getClass().getResource("../mainMenu.css").toExternalForm();
                    break;
                case "MainScene.fxml":
                    loader = new FXMLLoader(MainController.class.getResource("../MainScene.fxml"));
                    this.mainSceneController = new MainSceneController(this, this.mainStage);
                    loader.setController(this.mainSceneController);
                    root = loader.load();
                    if (this.primaryColor != null && this.secondaryColor != null) {
                        this.mainSceneController.setColors(this.primaryColor, this.secondaryColor);
                    }
                    this.mainSceneController.createSudoku(this.difficulty);
                    styleSheet = getClass().getResource("../mainScene.css").toExternalForm();
                    break;
                case "Statistics.fxml":
                    loader = new FXMLLoader(MainController.class.getResource("../Statistics.fxml"));
                    this.statisticsController = new StatisticsController(this);
                    loader.setController(this.statisticsController);
                    root = loader.load();
                    styleSheet = getClass().getResource("../statistics.css").toExternalForm();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown FXML file: " + fxml);
            }

            Scene scene = new Scene(root);
            scene.getStylesheets().add(styleSheet);
            this.mainStage.setScene(scene);
            this.mainStage.show();
            ;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean chooseDifficulty() {
        Stage selectStage = new Stage();
        selectStage.initModality(Modality.APPLICATION_MODAL);
        selectStage.initOwner(this.mainStage);
        VBox dialogBox = new VBox(20);
        dialogBox.setAlignment(Pos.CENTER);

        Button easyButton = new Button("Easy");
        easyButton.setOnAction(event -> {
            this.difficulty = "Easy";
            selectStage.close();
        });
        Button mediumButton = new Button("Medium");
        mediumButton.setOnAction(event -> {
            this.difficulty = "Medium";
            selectStage.close();
        });
        Button hardButton = new Button("Hard");
        hardButton.setOnAction(event -> {
            this.difficulty = "Hard";
            selectStage.close();
        });
        dialogBox.getChildren().addAll(easyButton, mediumButton, hardButton);

        Scene dialogScene = new Scene(dialogBox, 300, 200);
        dialogScene.getStylesheets().add(getClass().getResource("../mainMenu.css").toExternalForm());
        selectStage.setScene(dialogScene);
        double mainStageX = mainStage.getX();
        double mainStageY = mainStage.getY();
        double mainStageWidth = mainStage.getWidth();
        double mainStageHeight = mainStage.getHeight();
        selectStage.setX(mainStageX + (mainStageWidth - dialogBox.getWidth()) / 2);
        selectStage.setY(mainStageY + (mainStageHeight - dialogBox.getHeight()) / 2);

        selectStage.setOnCloseRequest(event -> {
            this.difficulty = null; 
        });

        selectStage.showAndWait();

        if (this.difficulty == null) {
            return false; 
        }

        return true; 
    }

    public void saveColors(Color primaryColor, Color secondaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }
}