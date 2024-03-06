package com.backtracking.smartsudoku.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Grid class
 * Simple model for holding a Sudoku game grid data.
 */
public class Grid {
    private final byte[] cells = new byte[81];

    public Grid() {
        Arrays.fill(this.cells, (byte)0);
    }

    public int get(int x, int y) {
        // return (int)this.cells[y*9+x]; // Cast considered redundant
        return this.cells[y*9+x];
    }

    public void set(int value, int x, int y) {
        this.cells[y*9+x] = (byte)value;
    }

    public List<Integer> getColumn(int colIndex) {
        List<Integer> column = new ArrayList<>();
        for (int i=0; i<9; ++i) {
            column.add((int)this.cells[i*9 + colIndex]);
        }
        return column;
    }

    public List<Integer> getRow(int rowIndex) {
        List<Integer> row = new ArrayList<>();
        for (int i=0; i<9; ++i) {
            row.add((int)this.cells[rowIndex*9 + i]);
        }
        return row;
    }

    public List<Integer> getRegion(int regionIndex) {
        List<Integer> region = new ArrayList<>();
        final int rx = regionIndex%3*3;
        final int ry = regionIndex/3*3;
        for (int i=0; i<3; ++i) {
            for (int j=0; j<3; ++j) {
                region.add(this.get(rx+i, ry+j));
            }
        }
        return region;
    }

    /*
        below methods not tested yet
    */

    static public int coordToIndex(int x, int y) {
        return y*9+x;
    }

    static public int indexToX(int index) { return index%9; }

    static public int indexToY(int index) { return index/9; }

    static public int coordToRegion(int x, int y) {
        return indexToRegion(coordToIndex(x,y));
    }

    static public int indexToRegion(int index) {
        return index%3;
    }
    
}
