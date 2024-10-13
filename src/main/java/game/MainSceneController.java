package game;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter.Black;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.application.Platform;
import javafx.event.ActionEvent;

public class MainSceneController {

    private int selectedFieldRow, selectedFieldCol;

    private SudokuField sudoku;

    private Button selectedButton;

    private boolean isSelectedFieldFree;

    private String color1 = "lightsteelblue", color2 = "lavenderblush";

    private MainController mainController;

    private Timer timer;
    private double secondsElapsed = 0.0;
    private Stage stage;
    private MediaPlayer mediaPlayer;

    @FXML
    private Label timerDisplay;

    @FXML
    private Button Menu;

    @FXML
    private GridPane field;

    @FXML
    private GridPane buttonGrid;

    @FXML
    private Label ErrorCounter;

    @FXML
    private Label count1;

    @FXML
    private Label count2;

    @FXML
    private Label count3;

    @FXML
    private Label count4;

    @FXML
    private Label count5;

    @FXML
    private Label count6;

    @FXML
    private Label count7;

    @FXML
    private Label count8;

    @FXML
    private Label count9;

    @FXML
    private Button hint;

    @FXML
    private Button norwegianFlag;

    @FXML
    private Button germanFlag;

    public MainSceneController(MainController mainController, Stage stage) {
        this.mainController = mainController;
        this.stage = stage;
        this.stage.setOnCloseRequest(event -> {
            this.timer.cancel();
        });
    }

    void createSudoku(String difficulty) {
        this.sudoku = new SudokuField();
        this.sudoku.start(difficulty);
        clearSudoku();
        activateButtons();
        this.ErrorCounter.setOpacity(1.0);
        this.ErrorCounter.setVisible(true);

        int i, j, fieldValue;
        for (Node node : field.getChildren()) {

            if (node instanceof Button) {
                node.setDisable(false);
                if (GridPane.getRowIndex(node) == null) {
                    GridPane.setRowIndex(node, 0);
                    i = 0;
                } else {
                    i = GridPane.getRowIndex(node);
                }
                if (GridPane.getColumnIndex(node) == null) {
                    GridPane.setColumnIndex(node, 0);
                    j = 0;
                } else {
                    j = GridPane.getColumnIndex(node);
                }
                fieldValue = sudoku.getValueOfField(i, j);
                if (fieldValue != 0) {
                    ((Button) node).setText(Integer.toString(fieldValue));
                }
                // Color fields initially
                if ((i < 3 || i >= 6) && (j < 3 || j >= 6) || (i >= 3 && i < 6) && (j >= 3 && j < 6)) {
                    ((Button) node).setStyle("-fx-background-color: " + this.color1 + ";");
                } else {
                    ((Button) node).setStyle("-fx-background-color: " + this.color2 + ";");
                }
            }

        }

        startUITimer();
        updateNumberCounter();
    }

    @FXML
    void selectField(ActionEvent event) {
        int row, col;
        String color = "KHAKI";
        String buttonText = ((Button) event.getSource()).getText();
        // Unselect previous button
        colorFields();
        Node node = ((Node) event.getSource());
        this.selectedButton = ((Button) node);
        this.selectedButton.setStyle("-fx-background-color: " + color + ";");
        row = GridPane.getRowIndex(node);
        col = GridPane.getColumnIndex(node);
        this.selectedFieldRow = row;
        this.selectedFieldCol = col;
        if (buttonText != null) {
            highlightSelectedNumbers(buttonText);
            this.isSelectedFieldFree = false;
        } else {
            unhighlightNumbers();
            this.isSelectedFieldFree = true;
        }
    }

    @FXML
    void inputButton(ActionEvent event) {
        int number = Integer.valueOf(((Button) event.getSource()).getText());
        putInNumber(number, false);
    }

    @FXML
    void inputKeyboard(KeyEvent event) {
        String keyValue = event.getText();
        try {
            int i = Integer.parseInt(keyValue);
            if (i != 0) {
                putInNumber(i, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void showHint(ActionEvent event) {
        int[] hintInfos = this.sudoku.giveHint();
        this.selectedFieldRow = hintInfos[0];
        this.selectedFieldCol = hintInfos[1];
        this.isSelectedFieldFree = true;
        putInNumber(hintInfos[2], true);
    }

    public void putInNumber(int number, boolean hint) {
        if (this.isSelectedFieldFree) {
            if (hint || this.sudoku.checkInputWithSolution(this.selectedFieldRow, this.selectedFieldCol, number)) {
                for (Node node : field.getChildren()) {
                    if (node instanceof Button) {
                        if (GridPane.getRowIndex(node) == this.selectedFieldRow
                                && GridPane.getColumnIndex(node) == this.selectedFieldCol) {
                            ((Button) node).setText(Integer.toString(number));                       
                            ((Button) node).setOpacity(1.0);
                        }
                    }
                }
                this.sudoku.putInNumber(this.selectedFieldRow, this.selectedFieldCol, number);
                this.sudoku.decreaseNumberCounter(number);
                updateNumberCounter();
                this.isSelectedFieldFree = false;
            } else {
                if (this.sudoku.getErrorCounter() == 3) {
                    endGame(true);
                }
                this.sudoku.increaseErrorCounter();
                this.ErrorCounter.setText("Mistakes:\n" + this.sudoku.getErrorCounter());

            }
            if (sudoku.wasSolved()) {
                endGame(false);
            }
        }
    }

    public void clearSudoku() {
        for (Node node : field.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setText(null);
            }
        }
    }

    public void activateButtons() {
        this.buttonGrid.setDisable(false);
        this.buttonGrid.setVisible(true);
    }

    public Color getButtonColor(Button b) {
        Background background = b.getBackground();
        BackgroundFill fill = background.getFills().get(0);
        return (Color) fill.getFill();
    }

    public void endGame(boolean hasLost) {
        this.timer.cancel();
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.addGame(this.sudoku.getDifficulty(), this.sudoku.getErrorCounter(), this.secondsElapsed,
                this.sudoku.wasSolved());
        Alert alert = new Alert(AlertType.INFORMATION);
        if (hasLost) {
            alert.setTitle("Game over");
            alert.setContentText("Game is over!");
        } else {
            alert.setTitle("Game over");
            alert.setContentText("You won!");

        }
        alert.setHeaderText(null);
        Scene alertScene = alert.getDialogPane().getScene();
        alertScene.getStylesheets().add(getClass().getResource("../mainMenu.css").toExternalForm());
        alert.showAndWait();
        this.mainController.loadFXML("MainMenu.fxml");
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
        }
    }

    @FXML
    void backToMenu(ActionEvent event) {
        this.timer.cancel();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Back to menu");
        alert.setHeaderText(null);
        // alert.setTitle("Confirmation");
        // alert.setHeaderText("Back to menu");
        alert.setContentText("Are you sure, you want to quit?");
        Scene alertScene = alert.getDialogPane().getScene();
        alertScene.getStylesheets().add(getClass().getResource("../mainMenu.css").toExternalForm());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            this.mainController.loadFXML("MainMenu.fxml");
            if (this.mediaPlayer != null) {
                this.mediaPlayer.stop();
            }
        } else {
            startUITimer();
        }
    }

    @FXML
    void playMusic(ActionEvent event) {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
        }
        String flag = ((Button) event.getSource()).getId();
        if (flag.equals("germanFlag")) {
            music("../germanAnthem.mp3");
        } else {
            music("../norwegianAnthem.mp3");
        }
    }

    public void setColors(Color primaryColor, Color secondaryColor) {
        this.color1 = colorToRgbaString(primaryColor);
        this.color2 = colorToRgbaString(secondaryColor);
    }

    public String colorToRgbaString(Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        double alpha = color.getOpacity();
        return String.format("rgba(%d, %d, %d, %.2f)", red, green, blue, alpha);
    }

    public void startUITimer() {
        this.timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MainSceneController.this.secondsElapsed += 0.1;
                int hours = (int) (secondsElapsed / 3600);
                int minutes = (int) ((secondsElapsed % 3600) / 60);
                double seconds = secondsElapsed % 60;
                String formattedTime = String.format("%d:%02d:%.1f", hours, minutes, seconds);
                Platform.runLater(() -> timerDisplay.setText(formattedTime));

            }
        }, 0, 100);
    }

    public void colorFields() {
        int i, j;
        for (Node node : this.field.getChildren()) {
            if (node instanceof Button) {
                node.setDisable(false);
                if (GridPane.getRowIndex(node) == null) {
                    GridPane.setRowIndex(node, 0);
                    i = 0;
                } else {
                    i = GridPane.getRowIndex(node);
                }
                if (GridPane.getColumnIndex(node) == null) {
                    GridPane.setColumnIndex(node, 0);
                    j = 0;
                } else {
                    j = GridPane.getColumnIndex(node);
                }
                if ((i < 3 || i >= 6) && (j < 3 || j >= 6) || (i >= 3 && i < 6) && (j >= 3 && j < 6)) {
                    ((Button) node).setStyle("-fx-background-color: " + this.color1 + ";");
                } else {
                    ((Button) node).setStyle("-fx-background-color: " + this.color2 + ";");
                }
            }
        }
    }

    public void highlightSelectedNumbers(String buttonText) {
        for (Node node : this.field.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                if (button.getText() != null) {
                    String buttonStyle = button.getStyle();
                    if (button.getText().equals(buttonText)) {
                        button.setStyle("-fx-font-weight: bold;" + buttonStyle);
                    } else {
                        button.setStyle("-fx-font-weight: normal;" + buttonStyle);
                    }

                }
            }
        }
    }

    public void unhighlightNumbers() {
        for (Node node : this.field.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setStyle("-fx-font-weight: normal;" + button.getStyle());
            }
        }
    }

    public void updateNumberCounter() {
        this.count1.setText(Integer.toString(sudoku.getCountOfFreeNumbers()[0]));
        this.count2.setText(Integer.toString(sudoku.getCountOfFreeNumbers()[1]));
        this.count3.setText(Integer.toString(sudoku.getCountOfFreeNumbers()[2]));
        this.count4.setText(Integer.toString(sudoku.getCountOfFreeNumbers()[3]));
        this.count5.setText(Integer.toString(sudoku.getCountOfFreeNumbers()[4]));
        this.count6.setText(Integer.toString(sudoku.getCountOfFreeNumbers()[5]));
        this.count7.setText(Integer.toString(sudoku.getCountOfFreeNumbers()[6]));
        this.count8.setText(Integer.toString(sudoku.getCountOfFreeNumbers()[7]));
        this.count9.setText(Integer.toString(sudoku.getCountOfFreeNumbers()[8]));
        for (Node node : this.buttonGrid.getChildren()) {
            if (node instanceof VBox) {
                String numString = ((Label) ((VBox) node).getChildren().get(1)).getText();
                if (numString.equals("0")) {
                    ((VBox) node).getChildren().get(0).setDisable(true);
                }
            }
        }
    }

    public void music(String pathToAnthem) {
        Media media = new Media(this.getClass().getResource(pathToAnthem).toString());
        this.mediaPlayer = new MediaPlayer(media);
        this.mediaPlayer.play();
        this.mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            }
        });

    }
}
