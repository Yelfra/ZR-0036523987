package com.franjelavic.zrtuner;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class PitchMeter extends View {

    private CanvasPainter canvasPainter;
    private PitchDifference pitchDifference;

    public PitchMeter(Context context) {
        super(context);
        canvasPainter = CanvasPainter.with(getContext());
    }

    public PitchMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        canvasPainter = CanvasPainter.with(getContext());
    }

    public void setPitchDifference(PitchDifference pitchDifference) {
        this.pitchDifference = pitchDifference;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvasPainter.paint(pitchDifference).on(canvas);
    }
}
