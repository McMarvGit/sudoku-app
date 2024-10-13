package game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

public class StatisticsController {

    @FXML
    private Button Menu;

    @FXML
    private Button Reset;

    @FXML
    private Label numberOfGames;

    @FXML
    private Label winRate;

    @FXML
    private Label avgMistakes;

    @FXML
    private Label avgDuration;

    @FXML
    private ChoiceBox<String> difficultyBox;

    private MainController mainController;
    private DatabaseManager databaseManager;

    public StatisticsController(MainController mainController) {
        this.mainController = mainController;
        this.databaseManager = new DatabaseManager();
    }

    @FXML
    public void initialize() {
        this.difficultyBox.getItems().addAll("All", "Easy", "Medium", "Hard");
        this.difficultyBox.setValue("All");
        showStats(this.difficultyBox.getValue());
    }

    @FXML
    void backToMenu(ActionEvent event) {
        this.mainController.loadFXML("MainMenu.fxml");
    }

    @FXML
    void resetStatistics(ActionEvent event) {
        this.databaseManager.deleteStats();
        showStats("All");
    }

    @FXML
    void handleChoiceBoxAction(ActionEvent event) {
        showStats(this.difficultyBox.getValue());
    }

    private void showStats(String choiceBoxValue) {
        this.numberOfGames.setText(" " + Integer.toString(this.databaseManager.getGamesPlayed(choiceBoxValue)));
        this.winRate.setText(" " + Double.toString(this.databaseManager.getWinRate(choiceBoxValue)) + "%");
        this.avgMistakes.setText(" " + Double.toString(this.databaseManager.getAvgMistakes(choiceBoxValue)));
        this.avgDuration.setText(" " + this.databaseManager.getAvgDuration(choiceBoxValue));
    }
}
