package com.maihaoche.volvo.view.seattable;

/**
 * Created by brantyu on 17/8/8.
 * 库位对象
 */

public class Carport {
    private int mRow;
    private int mColumn;

    public Carport(int row, int column) {
        mRow = row;
        mColumn = column;
    }

    public int getRow() {
        return mRow;
    }

    public void setRow(int row) {
        mRow = row;
    }

    public int getColumn() {
        return mColumn;
    }

    public void setColumn(int column) {
        mColumn = column;
    }
}
