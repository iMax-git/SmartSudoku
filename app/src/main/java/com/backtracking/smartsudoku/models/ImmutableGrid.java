package com.backtracking.smartsudoku.models;

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
