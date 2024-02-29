package com.example.smartsudoku.models;

public class Case {
    private Integer value;

    public Case(){
        value = null;
    }

    public boolean isSet(){
        return value != null;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

}
