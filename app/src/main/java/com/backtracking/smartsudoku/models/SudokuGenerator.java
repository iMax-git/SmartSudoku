package com.backtracking.smartsudoku.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SudokuGenerator {
    private Grid grid;

    public SudokuGenerator() {
        grid = new Grid();
        fillGrid();
    }

    private boolean fillGrid() {
        return fillCell(0);
    }

    private boolean fillCell(int index) {
        if (index == 81) {
            return true; // Toute la grille a été remplie avec succès.
        }

        int x = index % 9;
        int y = index / 9;

        List<Integer> numbers = shuffledNumbers();
        for (int num : numbers) {
            if (isValidPlacement(x, y, num)) {
                grid.set(num, x, y);
                if (fillCell(index + 1)) {
                    return true;
                }
                grid.set(0, x, y); // Réinitialiser si le placement ne mène pas à une solution complète
            }
        }

        return false;
    }

    private boolean isValidPlacement(int x, int y, int value) {
        // Vérifie la ligne
        for (int i = 0; i < 9; i++) {
            if (value == grid.get(x, i) || value == grid.get(i, y)) {
                return false;
            }
        }

        // Vérifie le carré 3x3
        int startX = (x / 3) * 3;
        int startY = (y / 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (value == grid.get(startX + i, startY + j)) {
                    return false;
                }
            }
        }

        return true;
    }

    private List<Integer> shuffledNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        return numbers;
    }

    // Retire aléatoirement des nombres pour créer un puzzle
    public void removeNumbers(int attempts) {
        while (attempts > 0) {
            int x = (int) (Math.random() * 9);
            int y = (int) (Math.random() * 9);
            if (grid.get(x, y) != 0) {
                grid.set(0, x, y);
                attempts--;
            }
        }
    }

    public Grid getGrid() {
        return grid;
    }

}
