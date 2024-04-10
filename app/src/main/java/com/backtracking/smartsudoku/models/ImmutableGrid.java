package com.backtracking.smartsudoku.models;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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


    public int get(int index) {
        return this.cells[index];
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
            int number = this.get(i, rowIndex);
            if (number != 0) {
                row.add(number);
            }
        }
        return row;
    }


    public List<Integer> getColumn(int colIndex) {
        List<Integer> column = new ArrayList<>();
        for (int i=0; i<9; ++i) {
            int number = this.get(colIndex, i);
            if (number != 0) {
                column.add(number);
            }
        }
        return column;
    }


    public List<Integer> getRegion(int regionIndex) {
        List<Integer> region = new ArrayList<>();
        final int rx = regionIndex%3*3;
        final int ry = regionIndex/3*3;
        for (int i=0; i<3; ++i) {
            for (int j=0; j<3; ++j) {
                int number = this.get(rx+i, ry+j);
                if (number != 0) {
                    region.add(number);
                }
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


    public static ImmutableGrid fromString(String str) {
        byte[] cells = new byte[81];
        for (int i=0; i<81; ++i) {
            cells[i] = (byte) Character.digit(str.charAt(i),10);
        }
        return new ImmutableGrid(cells);
    }


    public List<Integer> getNonZeroIndices() {
        List<Integer> indices = new ArrayList<>();
        for (int index=0; index<81; ++index) {
            if (this.cells[index]>0) {
                indices.add(index);
            }
        }
        return indices;
    }


    public static class Coord {
        public final int x, y;
        Coord(int x, int y) { this.x=x; this.y=y; }
    }

    public static Coord indexToCoord(int index) { return new Coord(index%9, index/9); }


    // untested methods

    public static int coordToIndex(int x, int y) {
        return y*9+x;
    }


    public static int coordToRegion(int x, int y) {
        return indexToRegion(coordToIndex(x,y));
    }


    public static int indexToRegion(int index) {
//        return index%3;
        return (index / 9 / 3) * 3 + (index % 9 / 3);
    }
    // /untested methods


    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ImmutableGrid)) {
            return super.equals(obj);
        }
        ImmutableGrid grid = (ImmutableGrid) obj;
        return Arrays.equals(this.cells, grid.cells);
    }

    /**
     * Serialize the grid to a string
     */
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 81; ++i) {
            sb.append(Character.forDigit((char) this.cells[i], 10));
        }
        return sb.toString();
    }

    /**
     * Deserialize the grid from a string
     */
    public static ImmutableGrid deserialize(String str) {
        byte[] cells = new byte[81];
        for (int i = 0; i < 81; ++i) {
            cells[i] = (byte) Character.digit(str.charAt(i), 10);
        }
        return new ImmutableGrid(cells);
    }
}
