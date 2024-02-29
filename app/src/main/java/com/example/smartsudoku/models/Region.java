package com.example.smartsudoku.models;

import java.util.ArrayList;
import java.util.List;

public class Region {
    private List<Case> cases;

    public Region(){
        cases = new ArrayList<>();

        for (int i=0 ; i < 9; i++){
            cases.add(new Case());
        }

    }

    public List<Case> getCases() {
        return cases;
    }

}
