package com.maihaoche.volvo.ui.common.chart;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by gujian
 * Time is 2017/8/18
 * Email is gujian@maihaoche.com
 */

public class PipChartFormatter implements IValueFormatter {
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        String string = (String) entry.getData();

        return string;
    }
}
