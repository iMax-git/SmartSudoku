package com.backtracking.smartsudoku;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class GridDrawer {
    private int columnCount, rowCount;
    private int cellSize;
    private Paint painter = new Paint();
    private Bitmap bmp;
    private Canvas canvas;

    /**
     * @param bmp the size of the provided bitmap is the effective size of the grid in pixels
     */
    public GridDrawer(Bitmap bmp) {
        this.bmp = bmp;
        this.canvas = new Canvas(this.bmp);
        this.painter.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /**
     *
     * @param bmp the size of the provided bitmap is the effective size of the grid in pixels
     * @param columnCount number of columns
     * @param rowCount number of rows
     */
    public GridDrawer(Bitmap bmp, int columnCount, int rowCount) {
        this(bmp);
        this.columnCount = columnCount;
        this.rowCount = rowCount;
        this.calculateDimensions();
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
        calculateDimensions();
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
        calculateDimensions();
    }

    public int getColumnCount() { return this.columnCount; }

    public int getRowCount() { return this.rowCount; }

    public int getWidth() {
        return this.bmp.getWidth();
    }

    public int getHeight() {
        return this.bmp.getHeight();
    }

    private void calculateDimensions() {
        if (columnCount < 1 || rowCount < 1) {
            return;
        }
        cellSize = getWidth() / columnCount;
    }

    public void draw() {
        this.canvas.drawColor(Color.WHITE);

        if (columnCount == 0 || rowCount == 0) {
            return;
        }

        final float cellBoderWidth = 1.f;
        final float gridBorderWidth = 2.f;

        /*
         * draw cells
         */
        final int width = getWidth();
        final int height = getHeight();
        this.painter.setStrokeWidth(cellBoderWidth);

        for (int i=1; i < columnCount; ++i) {
            this.canvas.drawLine(
                    i * cellSize + gridBorderWidth,
                    gridBorderWidth,
                    i * cellSize + gridBorderWidth,
                    height + gridBorderWidth,
                    painter
            );
        }

        for (int i=1; i < rowCount; ++i) {
            this.canvas.drawLine(
                    gridBorderWidth,
                    i * cellSize + gridBorderWidth,
                    width + gridBorderWidth,
                    i * cellSize + gridBorderWidth,
                    painter
            );
        }

        /*
         * outline regions
         */
        final int regionWidth = 3* cellSize;
        final int regionHeight = 3*cellSize;
        this.painter.setStrokeWidth(gridBorderWidth);

        for (int i=0; i <= columnCount/3; ++i) {
            this.canvas.drawLine(
                    i * regionWidth + gridBorderWidth,
                    gridBorderWidth,
                    i * regionWidth + gridBorderWidth,
                    height,
                    painter
            );
        }
        for (int i=0; i <= rowCount/3; ++i) {
            this.canvas.drawLine(
                    gridBorderWidth,
                    i * regionHeight + gridBorderWidth,
                    width,
                    i * regionHeight + gridBorderWidth,
                    painter
            );
        }
    }
}
