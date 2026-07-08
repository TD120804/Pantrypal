package com.example.pantrypal.ui;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class CustomGaugeView extends View {

    private Paint arcPaint, needlePaint, centerPaint;
    private float value = 0;

    public CustomGaugeView(Context context) {
        super(context);
        init();
    }

    public CustomGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(30f);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);

        needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        needlePaint.setColor(Color.parseColor("#D6B25E"));
        needlePaint.setStrokeWidth(5f);

        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(Color.parseColor("#4A3B35"));
    }

    public void setValue(float val) {
        this.value = val;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getWidth() == 0 || getHeight() == 0) return; // 🛑 prevents crash

        float width = getWidth();
        float height = getHeight();

        float radius = Math.min(width, height) / 2f - 30;
        float cx = width / 2f;
        float cy = height - 20;

        RectF rect = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);

        // 🎨 FIXED GRADIENT
        SweepGradient gradient = new SweepGradient(cx, cy,
                new int[]{
                        Color.parseColor("#B7E4C7"),
                        Color.parseColor("#FFE8A3"),
                        Color.parseColor("#F5B7B1")
                },
                null);

        Matrix matrix = new Matrix();
        matrix.postRotate(180, cx, cy); // 🔥 IMPORTANT FIX
        gradient.setLocalMatrix(matrix);

        arcPaint.setShader(gradient);

        // Draw arc
        canvas.drawArc(rect, 180, 180, false, arcPaint);

        // Needle
        float angle = 180 + (value / 100f) * 180;
        float rad = (float) Math.toRadians(angle);

        float nx = cx + radius * (float) Math.cos(rad);
        float ny = cy + radius * (float) Math.sin(rad);

        canvas.drawLine(cx, cy, nx, ny, needlePaint);

        // Center dot
        canvas.drawCircle(cx, cy, 12, centerPaint);
    }
}