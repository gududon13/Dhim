package com.leophysics.dhim3drenderer;
import android.content.*;
import android.graphics.*;
import android.widget.*;

public class RulerBarImageView extends ImageView
{
	public RulerBarImageView(Context context){
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Get the dimensions of the view
		int viewWidth = getWidth();
		int viewHeight = getHeight();

		// Draw the ruler bar
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(5);
		canvas.drawLine(0, viewHeight - 50, viewWidth, viewHeight - 50, paint);

		// Draw the tick marks and labels
		paint.setTextSize(20);
		paint.setStrokeWidth(3);
		int tickSpacing = viewWidth / 10;
		for (int i = 0; i <= 10; i++) {
			int tickX = i * tickSpacing;
			canvas.drawLine(tickX, viewHeight - 50, tickX, viewHeight - 40, paint);
			String label = String.valueOf(i * 10);
			float labelWidth = paint.measureText(label);
			canvas.drawText(label, tickX - labelWidth / 2, viewHeight - 20, paint);
		}
	}
	
	
}
