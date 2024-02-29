package com.example.smartsudoku.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Grille {
    private final List<Region> regions;


    /**
     * Constructeur de Grille
     * @param difficulty permet de determiner la taille de la grille (n^2) (EX: (3) -> 3x3 ...)
     */
    public Grille(Integer difficulty){
        regions = new ArrayList<>();

        for (int i=0 ; i < Math.pow(difficulty, 2); i++){
            regions.add(new Region());
        }

        generate();

    }

    public List<Region> getRegions() {
        return regions;
    }

    private void generate(){
        // On tire au hasard une dixaine de case dans la grille et y mettre des nombres au hasard mais qui respectent les règles du sudoku
        for (int i=0; i<10; i++){
            // On tire au hasard une case
            int randomRegion = (int) (Math.random() * regions.size());
            int randomCase = (int) (Math.random() * regions.get(randomRegion).getCases().size());
            // On tire au hasard un nombre
            int randomValue = (int) (Math.random() * 9) + 1;
            // On vérifie que le nombre n'est pas déjà présent dans la ligne, la colonne et la région
            if (checkValue(randomRegion, randomCase, randomValue)){
                regions.get(randomRegion).getCases().get(randomCase).setValue(randomValue);
            }
        }

        System.out.println(this);

        // Résoudre la grille
        solve();

        // On tire au hasard des positions dans la grille et on les vide
        for (int i=0; i<regions.size(); i++){
            for (int j=0; j<regions.get(i).getCases().size(); j++){
                if (Math.random() > 0.5){
                    regions.get(i).getCases().get(j).setValue(null);
                }
            }
        }

        System.out.println("Grille générée :");
        for (int i=0; i<regions.size(); i++){
            for (int j=0; j<regions.get(i).getCases().size(); j++){
                System.out.print(regions.get(i).getCases().get(j).getValue() + " ");
            }
            System.out.println();
        }
    }

    private void solve() {
        // On parcourt toutes les cases de la grille
        for (int i=0; i<regions.size(); i++){
            for (int j=0; j<regions.get(i).getCases().size(); j++){
                // Si la case est vide
                if (regions.get(i).getCases().get(j).getValue() == null){
                    // On essaye de mettre un nombre
                    for (int k=1; k<=9; k++){
                        if (checkValue(i, j, k)){
                            regions.get(i).getCases().get(j).setValue(k);
                            solve();
                            // Si on a réussi à résoudre la grille, on arrête
                            if (isSolved()){
                                return;
                            }
                            // Sinon on remet la case à vide
                            regions.get(i).getCases().get(j).setValue(null);
                        }
                    }
                    // Si on a essayé tous les nombres sans succès, on arrête
                    return;
                }
            }
        }
    }

    private boolean isSolved() {
        // On vérifie que toutes les cases sont remplies
        for (Region region : regions){
            for (Case aCase : region.getCases()){
                if (aCase.getValue() == null){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkValue(int region, int no_case, int value) {
        // On vérifie que le nombre n'est pas déjà présent dans la ligne, la colonne et la région
        return checkRegion(region, value) && checkLine(region, no_case, value) && checkColumn(region, no_case, value);
    }

    private boolean checkColumn(int region, int noCase, int value) {
        // On vérifie que le nombre n'est pas déjà présent dans la colonne
        int column = region % 3;
        for (int i=0; i<3; i++){
            if (i != noCase){
                for (int j=0; j<3; j++){
                    if (!regions.get(column + i*3).getCases().get(j).isSet()){
                        return true;
                    }
                    if (regions.get(column + i*3).getCases().get(j).getValue() == value){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkLine(int region, int noCase, int value) {
        // On vérifie que le nombre n'est pas déjà présent dans la ligne
        int line = region / 3;
        int start = line * 3;
        int end = start + 3;
        for (int i=start; i<end; i++){
            if (i != region){
                for (int j=0; j<3; j++){
                    if (!regions.get(i).getCases().get(j).isSet()){
                        return true;
                    }
                    if (regions.get(i).getCases().get(j).getValue() == value){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkRegion(int region, int value) {
        // On vérifie que le nombre n'est pas déjà présent dans la région
        for (Case aCase : regions.get(region).getCases()){
            if (aCase.getValue() != null && aCase.getValue() == value){
                return false;
            }
        }
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        // Affichage 3x3 et par région
        List<String> result = new ArrayList<>();
        for (int i=0; i<regions.size(); i++){
            for (int j=0; j<regions.get(i).getCases().size(); j++){
                if (j % 3 == 0){
                    result.add(" ");
                }
                result.set(result.size()-1, result.get(result.size()-1) + regions.get(i).getCases().get(j).getValue() + " ");
            }

        }
        // Join like real grid
        List <String> finalResult = new ArrayList<>();
        for (int i=0; i<result.size()/3; i++){
            finalResult.add(result.get(i*3) + " | " + result.get(i*3+1) + " | " + result.get(i*3+2));
        }
        // Sep line for each 3x3
        for (int i=0; i<finalResult.size(); i++){
            if (i % 3 == 0){
                finalResult.add(i, "-----------------");
            }
        }
        return String.join("\n", finalResult);

    }
}
