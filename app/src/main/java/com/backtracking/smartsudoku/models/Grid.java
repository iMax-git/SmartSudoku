package com.backtracking.smartsudoku.models;

import java.util.Arrays;

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

    public int[] getColumn(int colIndex) {
        int[] column = new int[9];
        for (int i=0; i<9; ++i) {
            column[i] = this.cells[i*9 + colIndex];
        }
        return column;
    }

    public int[] getRow(int rowIndex) {
        int[] row = new int[9];
        for (int i=0; i<9; ++i) {
            row[i] = this.cells[rowIndex*9 + i];
        }
        return row;
    }

    public int[] getRegion(int regionIndex) {
        int[] region = new int[9];
        final int rx = regionIndex%3*3;
        final int ry = regionIndex/3*3;
        for (int i=0; i<3; ++i) {
            for (int j=0; j<3; ++j) {
                region[j*3+i] = get(rx+i, ry+j);
            }
        }
        return region;
    }

    /*
        below methods not tested yet
    */

    private int coordToIndex(int x, int y) {
        return y*9+x;
    }

    private int[] indexToCoord(int index) {
        return new int[]{ index%9, index/9 };
    }

    private int coordToRegion(int x, int y) {
        return indexToRegion(coordToIndex(x,y));
    }

    private int indexToRegion(int index) {
        return index%3;
    }
}
