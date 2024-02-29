package com.backtracking.smartsudoku;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class GridDrawer {
    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private Paint blackPaint = new Paint();
    private Bitmap bmp;
    private Canvas canvas;
    private boolean[][] cellChecked;

    public GridDrawer(Bitmap bmp) {
        this.bmp = bmp;
        this.canvas = new Canvas(this.bmp);
//        canvas.drawColor(0xff0000);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        calculateDimensions();
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
        calculateDimensions();
    }

    public int getNumColumns() { return numColumns; }

    public int getNumRows() { return numRows; }

    public int getWidth() {
        return this.bmp.getWidth();
    }

    public int getHeight() {
        return this.bmp.getHeight();
    }

    private void calculateDimensions() {
        if (numColumns < 1 || numRows < 1) {
            return;
        }

        cellWidth = getWidth() / numColumns;
        cellHeight = getHeight() / numRows;

        cellChecked = new boolean[numColumns][numRows];
    }

    public void draw() {
        this.canvas.drawColor(Color.WHITE);

        if (numColumns == 0 || numRows == 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                if (cellChecked[i][j]) {

                    this.canvas.drawRect(i * cellWidth, j * cellHeight,
                            (i + 1) * cellWidth, (j + 1) * cellHeight,
                            blackPaint);
                }
            }
        }

        for (int i = 1; i < numColumns; i++) {
            this.canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, blackPaint);
        }

        for (int i = 1; i < numRows; i++) {
            this.canvas.drawLine(0, i * cellHeight, width, i * cellHeight, blackPaint);
        }
    }
}
