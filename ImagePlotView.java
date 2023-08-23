package com.leophysics.dhim3drenderer;

import android.content.*;
import android.graphics.*;
import android.view.*;

public class ImagePlotView extends View {

    private double[][] phase;
    private int width;
    private int height;

    public ImagePlotView(Context context, double[][] phase, int width, int height) {
        super(context);
        this.phase = phase;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Iterate through the 2D array and draw each pixel
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double value = phase[i][j];
                int color = Color.HSVToColor(new float[]{
												 (((int)value + 1) / 2) * 360,
												 1.0f,
												 1.0f
											 });
				Paint paint=new Paint();
				paint.setColor(color);
                canvas.drawPoint(1f*i, 1f*j, paint);
            }
        }

        // Draw a scale bar
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText("Phase", width / 2, height - 20, paint);
        for (int i = 0; i < 10; i++) {
            int value = i;
            int color = Color.HSVToColor(new float[]{
											 ((value + 1) / 2) * 360,
											 1.0f,
											 1.0f
										 });
			Paint paint1 = new Paint();
			paint.setColor(color);						 
										 
            canvas.drawRect(i * width / 10, height - 40, (i + 1) * width / 10, height - 20, paint1);
            canvas.drawText(String.valueOf(value), i * width / 10 + width / 20, height - 20, paint);
        }
    }
}

