package com.safeguardFamily.diabezone.ui.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.safeguardFamily.diabezone.ui.graph.draw.data.Chart;
import com.safeguardFamily.diabezone.ui.graph.draw.data.InputData;
import com.safeguardFamily.diabezone.ui.graph.utils.ValueUtils;

import java.util.ArrayList;
import java.util.List;

public class ChartView extends View implements ChartManager.AnimationListener {

    private ChartManager chartManager;

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec) / 2;
        chartManager.chart().setWidth(width);
        chartManager.chart().setHeight(height);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        chartManager.drawer().draw(canvas);
    }

    @Override
    public void onAnimationUpdated() {
        invalidate();
    }

    public void setData(@Nullable List<InputData> dataList) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }

        final Chart chart = chartManager.chart();
        chart.setInputData(dataList);
        chartManager.drawer().updateTitleWidth();

        post(() -> {
            chart.setDrawData(ValueUtils.getDrawData(chart));
            chartManager.animate();
        });
    }

    private void init() {
        chartManager = new ChartManager(getContext(), this);
    }
}
