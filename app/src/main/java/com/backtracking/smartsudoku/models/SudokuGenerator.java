package com.backtracking.smartsudoku.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SudokuGenerator {
    private Grid grid;
    private int solutionCount;
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
        List<Integer> positions = new ArrayList<>(81);
        for (int i = 0; i < 81; i++) {
            positions.add(i);
        }
        Collections.shuffle(positions); // Mélangez les positions pour retirer les nombres aléatoirement.

        int removedCount = 0;
        for (int position : positions) {
            if (removedCount >= attempts) break; // Limite atteinte, arrêtez la suppression.

            int x = position % 9;
            int y = position / 9;
            int backup = grid.get(x, y);

            if (backup != 0) {
                grid.set(0, x, y); // Essayez de retirer le nombre.

                if (isSolvableWithUniqueSolution()) {
                    removedCount++; // Suppression réussie, continuez.
                } else {
                    grid.set(backup, x, y); // La suppression a échoué, restaurez le nombre.
                }
            }
        }
    }

    private boolean isSolvableWithUniqueSolution() {
        solutionCount = 0;
        solve(0);
        return solutionCount == 1;
    }

    private boolean solve(int index) {
        if (index == 81) {
            solutionCount++; // Une solution complète trouvée.
            return solutionCount == 1; // Continue si une seule solution est trouvée, sinon arrête.
        }

        int x = index % 9;
        int y = index / 9;

        if (grid.get(x, y) != 0) { // Si la cellule est déjà remplie, passez à la suivante.
            return solve(index + 1);
        } else {
            for (int num = 1; num <= 9; num++) {
                if (isValidPlacement(x, y, num)) {
                    grid.set(num, x, y);
                    if (!solve(index + 1)) {
                        grid.set(0, x, y); // Réinitialisez si la solution actuelle mène à plus d'une solution ou si elle échoue.
                        return false; // Sortie anticipée si plus d'une solution est trouvée.
                    }
                    grid.set(0, x, y); // Réinitialisez après avoir vérifié cette branche.
                }
            }
        }

        if (solutionCount > 1) { // Si plus d'une solution est trouvée à ce stade, arrêtez la recherche.
            return false;
        }

        // Si aucune solution n'a été trouvée pour cette cellule, retournez vrai si nous sommes toujours dans le processus de recherche d'une première solution.
        return solutionCount < 2;
    }

    public Grid getGrid() {
        return grid;
    }



}
