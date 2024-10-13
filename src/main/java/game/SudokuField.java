package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SudokuField {
    private int[][] fields;
    private int[][] solutionField;
    private Integer[] allNumbers = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    private ArrayList<Integer> numberList;
    private int errorCounter;
    private ArrayList<int[]> fieldList;
    private int numberOfSolutions;
    private boolean isSolved;
    private int[] countOfFreeNumbers;
    private String difficulty;

    public SudokuField() {
        this.fields = new int[9][9];
        this.countOfFreeNumbers = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        this.errorCounter = 0;
        this.numberOfSolutions = 0;
        this.isSolved = false;
    }

    public void start(String difficulty) {
        this.difficulty = difficulty;
        int difficultyIntValue = calculateDifficulty(difficulty);
        this.numberList = new ArrayList<>();
        this.fieldList = new ArrayList<>();
        Collections.addAll(this.numberList, this.allNumbers);
        if (findSolution(0, 0)) {
            int[][] board = new int[9][9];
            this.solutionField = new int[9][9];
            for (int i = 0; i < this.fields.length; i++) {
                for (int j = 0; j < this.fields.length; j++) {
                    this.fieldList.add(new int[] { i, j });
                    board[i][j] = this.fields[i][j];
                    this.solutionField[i][j] = this.fields[i][j];
                }

            }
            int emptyFields = 0;
            Collections.shuffle(this.fieldList);
            while (!this.fieldList.isEmpty() && (81 - emptyFields) > difficultyIntValue) {
                this.numberOfSolutions = 0;
                int i = this.fieldList.get(0)[0];
                int j = this.fieldList.get(0)[1];
                board[i][j] = 0;
                if (findUniqueSolution(0, 0, board)) {
                    this.countOfFreeNumbers[this.fields[i][j] - 1]++;
                    this.fields[i][j] = 0;
                    emptyFields++;
                } else {
                    board[i][j] = this.fields[i][j];
                }
                this.fieldList.remove(0);
            }
        }
    }

    public boolean findUniqueSolution(int row, int col, int[][] board) {
        if (row == 9) {
            this.numberOfSolutions++;
            return this.numberOfSolutions == 1; // If more than one solution found, stop further processing
        }

        if (col == 9) {
            return findUniqueSolution(row + 1, 0, board);
        }

        if (board[row][col] != 0) {
            return findUniqueSolution(row, col + 1, board);
        }

        for (int num : this.allNumbers) {
            if (check(num, row, col, board)) {
                board[row][col] = num;
                if (!findUniqueSolution(row, col + 1, board)) {
                    board[row][col] = 0; // Reset before returning
                    return false;
                }
                board[row][col] = 0; // Reset before trying the next number
            }
        }

        return this.numberOfSolutions == 1; // Check if we have exactly one solution
    }

    public boolean findSolution(int row, int col) {

        if (row == 9) {
            return true;
        }
        if (col == 9) {
            return findSolution(row + 1, 0);
        }
        if (this.fields[row][col] != 0) {
            return findSolution(row, col + 1);
        }

        Collections.shuffle(numberList, new Random());

        for (int num : numberList) {
            if (check(num, row, col, this.fields)) {
                this.fields[row][col] = num;
                if (findSolution(row, col + 1)) {
                    return true;
                }
            }
        }
        this.fields[row][col] = 0;
        return false;

    }

    public boolean check(int num, int row, int col, int[][] board) {

        // Check Row
        for (int i = 0; i < board.length; i++) {
            if (board[row][i] == num) {
                return false;
            }
        }

        // Check Col
        for (int i = 0; i < board.length; i++) {
            if (board[i][col] == num) {
                return false;
            }
        }

        // Check Square
        for (int i = row - row % 3; i < (row - row % 3) + 3; i++) {
            for (int j = col - col % 3; j < (col - col % 3) + 3; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    public void makePuzzle(int emptyFields) {
        int i, j;
        while (emptyFields > 0) {
            i = new Random().nextInt(9);
            j = new Random().nextInt(9);
            if (this.fields[i][j] != 0) {
                this.fields[i][j] = 0;
                emptyFields--;
            }
        }
    }

    public int calculateDifficulty(String difficulty) {
        switch (difficulty) { // difficulty = Minimum number of filled fields
            case "Easy":
                return 45;
            case "Medium":
                return 35;
            case "Hard":
                return 30;
            default:
                return 0;
        }
    }

    public void print() {
        for (int i = 0; i < this.fields.length; i++) {
            for (int j = 0; j < this.fields[i].length; j++) {
                if (j != 0 && j % 3 == 0) {
                    System.out.print("  " + Integer.toString(this.fields[i][j]) + " ");
                } else {
                    System.out.print(Integer.toString(this.fields[i][j]) + " ");
                }
            }
            if (i != 0 && (i + 1) % 3 == 0) {
                System.out.print("\n\n");
            } else {
                System.out.print('\n');
            }
        }

    }

    public boolean wasSolved() {
        return this.isSolved;
    }

    public int getValueOfField(int i, int j) {
        return this.fields[i][j];
    }

    public int[][] getField() {
        return this.fields;
    }

    public int getErrorCounter() {
        return this.errorCounter;
    }

    public void increaseErrorCounter() {
        this.errorCounter++;
    }

    public int[] getCountOfFreeNumbers() {
        return this.countOfFreeNumbers;
    }

    public void decreaseNumberCounter(int number) {
        this.countOfFreeNumbers[number - 1]--;
        // Check if solved
        this.isSolved = true;
        for (int i : this.countOfFreeNumbers) {
            if (i != 0) {
                this.isSolved = false;
            }
        }
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public int[][] getSolutionField() {
        return this.solutionField;
    }

    // Get empty field to give a hint
    private int[] getEmptyField() {
        ArrayList<int[]> list = new ArrayList<>();
        for (int i = 0; i < this.fields.length; i++) {
            for (int j = 0; j < this.fields.length; j++) {
                if (this.fields[i][j] == 0) {
                    list.add(new int[] { i, j });
                }
            }
        }
        Collections.shuffle(list);
        return list.get(0);
    }

    public int[] giveHint() {
        int[] cords = getEmptyField();
        putInNumber(cords[0], cords[1], this.solutionField[cords[0]][cords[1]]);
        int[] result = new int[3];
        result[0] = cords[0]; // i
        result[1] = cords[1]; // j
        result[2] = this.solutionField[cords[0]][cords[1]]; // value#
        return result;
    }

    public void putInNumber(int row, int col, int number) {
        this.fields[row][col] = number;
    }

    public boolean checkInputWithSolution(int row, int col, int number) {
        if (this.solutionField[row][col] != number) {
            return false;
        } else {
            return true;
        }
    }
}
