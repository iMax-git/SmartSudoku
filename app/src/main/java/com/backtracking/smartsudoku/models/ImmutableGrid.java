package com.backtracking.smartsudoku.models;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ImmutableGrid class
 * Simple model for holding a Sudoku game grid data.
 */
public class ImmutableGrid {
    private final byte[] cells = new byte[81];

    public ImmutableGrid() {
        Arrays.fill(this.cells, (byte)0);
    }

    protected ImmutableGrid(byte[] cells) {
        System.arraycopy(cells, 0, this.cells, 0, this.cells.length);
    }

    public int get(int x, int y) {
        return this.cells[y*9+x];
    }

    @CheckResult
    public ImmutableGrid set(int x, int y, int value) {
        ImmutableGrid grid = new ImmutableGrid(cells);
        grid.cells[y*9+x] = (byte)value;
        return grid;
    }

    public List<Integer> getRow(int rowIndex) {
        List<Integer> row = new ArrayList<>();
        for (int i=0; i<9; ++i) {
            row.add((int)this.cells[rowIndex*9 + i]);
        }
        return row;
    }

    public List<Integer> getColumn(int colIndex) {
        List<Integer> column = new ArrayList<>();
        for (int i=0; i<9; ++i) {
            column.add((int)this.cells[i*9 + colIndex]);
        }
        return column;
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

    @Override
    @NonNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<81; ++i) {
            sb.append(Character.forDigit((char)this.cells[i],10));
        }
        return sb.toString();
    }

    /*
        below methods not tested yet
    */

    public static int coordToIndex(int x, int y) {
        return y*9+x;
    }

    public static int indexToX(int index) { return index%9; }

    public static int indexToY(int index) { return index/9; }

    public static int coordToRegion(int x, int y) {
        return indexToRegion(coordToIndex(x,y));
    }

    public static int indexToRegion(int index) {
        return index%3;
    }

    public static ImmutableGrid fromString(String str) {
        byte[] cells = new byte[81];
        for (int i=0; i<81; ++i) {
            cells[i] = (byte) Character.digit(str.charAt(i),10);
        }
        return new ImmutableGrid(cells);
    }
    
}
