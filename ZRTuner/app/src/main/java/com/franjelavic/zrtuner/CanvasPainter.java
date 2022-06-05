package com.franjelavic.zrtuner;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;


import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static com.franjelavic.zrtuner.MainActivity.SHARED_PREFS_FILE;
import static com.franjelavic.zrtuner.MainActivity.REFERENCE_PITCH;
import static com.franjelavic.zrtuner.MainActivity.*;

class CanvasPainter {

    private static final double TOLERANCE = 6D;
    private static final int MAX_DEVIATION = 60;
    private static final int NUMBER_OF_MARKS_PER_SIDE = 6;
    private final Context context;

    private Canvas canvas;

    private TextPaint textPaint = new TextPaint(ANTI_ALIAS_FLAG);
    private TextPaint numbersPaint = new TextPaint(ANTI_ALIAS_FLAG);
    private Paint gaugePaint = new Paint(ANTI_ALIAS_FLAG);

    private int gaugeColorGreen;
    private int gaugeColor;
    private int numberColor;
    private int noteColor;

    private PitchDifference pitchDifference;

    private float gaugeWidth;
    private float x;
    private float y;

    private CanvasPainter(Context context) {
        this.context = context;
    }

    static CanvasPainter with(Context context) {
        return new CanvasPainter(context);
    }

    CanvasPainter paint(PitchDifference pitchDifference) {
        this.pitchDifference = pitchDifference;

        return this;
    }

    void on(Canvas canvas) {
        this.canvas = canvas;

        gaugeColor = R.color.fer_gray_light;
        gaugeColorGreen = Color.GREEN;
        numberColor = R.color.fer_gray_light;
        noteColor = R.color.fer_gray;

        gaugeWidth = 0.45f * canvas.getWidth();
        x = canvas.getWidth() / 2f;
        y = canvas.getHeight() / 2f;

        textPaint.setColor(noteColor);
        textPaint.setAlpha(255);
        int textSize = context.getResources().getDimensionPixelSize(R.dimen.noteTextSize);
        textPaint.setTextSize(textSize);

        drawGauge();

        if (pitchDifference != null) {
            int abs = (int) Math.abs(getNearestDeviation());

            if (abs <= MAX_DEVIATION) {
                showTuningQuality();

                drawGauge();

                drawIndicator();

                float x = canvas.getWidth() / 2f;
                float y = canvas.getHeight() * 0.75f;

                drawText(x, y, pitchDifference.closest, textPaint);
            }
        }
    }

    private void drawGauge() {

        int gaugeSize = context.getResources().getDimensionPixelSize(R.dimen.gaugeSize);
        gaugePaint.setStrokeWidth(gaugeSize);
        gaugePaint.setColor(gaugeColor);
        gaugePaint.setAlpha(255);

        int textSize = context.getResources().getDimensionPixelSize(R.dimen.numbersTextSize);
        numbersPaint.setTextSize(textSize);
        numbersPaint.setColor(numberColor);
        numbersPaint.setAlpha(255);

        canvas.drawLine(x - gaugeWidth, y, x + gaugeWidth, y, gaugePaint);

        float spaceWidth = gaugeWidth / NUMBER_OF_MARKS_PER_SIDE;
        int stepWidth = MAX_DEVIATION / NUMBER_OF_MARKS_PER_SIDE;

        for (int i = 0; i <= MAX_DEVIATION; i = i + stepWidth) {
            float factor = i / stepWidth;
            drawMark(y, x + factor * spaceWidth, i);
            drawMark(y, x - factor * spaceWidth, -i);
        }
    }

    private void drawIndicator() {
        float nearestDeviation = getNearestDeviation();

        String deviationText = Integer.toString((int) nearestDeviation);
        if (nearestDeviation > 0) {
            deviationText = "+" + deviationText;
        }

        Matrix matrix = new Matrix();
        float scalingFactor = numbersPaint.getTextSize() / 3;
        matrix.setScale(scalingFactor, scalingFactor);

        Path indicator = new Path();
        indicator.moveTo(0, -2); // point up
        indicator.lineTo(0.75f, 0); // point right
        indicator.lineTo(-0.75f, 0); // point left
        indicator.close();
        indicator.transform(matrix);

        float xPosition = x + (nearestDeviation * gaugeWidth / MAX_DEVIATION);
        float yPosition = y * 1.1f;
        indicator.offset(xPosition, yPosition);

        canvas.drawPath(indicator, gaugePaint);

        Paint deviationTextPaint = new Paint(ANTI_ALIAS_FLAG);
        int textSize = context.getResources().getDimensionPixelSize(R.dimen.numbersTextSize);
        deviationTextPaint.setColor(numberColor);
        deviationTextPaint.setAlpha(255);
        deviationTextPaint.setTextSize(textSize);
        deviationTextPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(deviationText, xPosition, yPosition * 1.05f, deviationTextPaint);
    }

    private static long startTime;
    private static Boolean stopWatchInitiated = false;

    private void showTuningQuality() {

        if (Math.abs(getNearestDeviation()) < TOLERANCE) {
            if (!stopWatchInitiated) stopWatch();

            if ((System.currentTimeMillis() - startTime) / 1000 >= 1)
                gaugeColor = gaugeColorGreen;
        } else {
            stopWatchInitiated = false;
        }
    }

    private void stopWatch() {
        stopWatchInitiated = true;
        startTime = System.currentTimeMillis();
    }

    private void drawMark(float y, float xPos, int mark) {
        String prefix = "";
        if (mark > 0) {
            prefix = "+";
        }
        String text = prefix + mark;

        int yOffset = (int) (numbersPaint.getTextSize() / 6);
        if (mark % 10 == 0) {
            yOffset *= 2;
        }
        if (mark % 20 == 0) {
            canvas.drawText(text, xPos - numbersPaint.measureText(text) / 2f,
                    y - numbersPaint.getTextSize(), numbersPaint);
            yOffset *= 2;
        }

        canvas.drawLine(xPos, y - yOffset, xPos, y + yOffset, gaugePaint);
    }

    private void drawText(float x, float y, Note note, Paint textPaint) {
        String noteText = note.getName().getNotation();
        float offset = textPaint.measureText(noteText) / 2f;

        String sign = note.getSign();
        String octave = String.valueOf(note.getOctave());

        TextPaint notePaint = new TextPaint(ANTI_ALIAS_FLAG);
        notePaint.setColor(noteColor);
        notePaint.setAlpha(255);
        int textSize = (int) (textPaint.getTextSize() / 2);
        notePaint.setTextSize(textSize);

        canvas.drawText(sign, x + offset * 1.25f, y - offset * 1.5f, notePaint);
        canvas.drawText(octave, x + offset * 1.25f, y + offset * 0.5f, notePaint);

        canvas.drawText(noteText, x - offset, y, textPaint);
    }

    private float getNearestDeviation() {
        return (float) pitchDifference.deviation;
    }
}
