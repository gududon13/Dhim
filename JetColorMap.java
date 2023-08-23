package com.leophysics.dhim3drenderer;

import android.graphics.Bitmap;
import android.graphics.Color;

public class JetColorMap {
    private double[][] amplitude;
    private Bitmap bitmap;

    public JetColorMap(double[][] amplitude) {
        this.amplitude = amplitude;
        this.bitmap = Bitmap.createBitmap(amplitude.length, amplitude[0].length, Bitmap.Config.ARGB_8888);
        createJetColorMap();
    }

    private void createJetColorMap() {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int i = 0; i < amplitude.length; i++) {
            for (int j = 0; j < amplitude[i].length; j++) {
                if (amplitude[i][j] < min) {
                    min = amplitude[i][j];
                }
                if (amplitude[i][j] > max) {
                    max = amplitude[i][j];
                }
            }
        }

        double range = max - min;
        for (int i = 0; i < amplitude.length; i++) {
            for (int j = 0; j < amplitude[i].length; j++) {
                double value = amplitude[i][j];
                float normalizedValue = (float) ((value - min) / range);
                int color = getJetColor(normalizedValue);
                bitmap.setPixel(i, j, color);
            }
        }
    }

    private int getJetColor(float value) {
        int r, g, b;
        if (value < 0.0f) {
            value = 0.0f;
        } else if (value > 1.0f) {
            value = 1.0f;
        }
        if (value < 0.25f) {
            r = 0;
            g = (int) (255 * value * 4);
            b = 0;
        } else if (value < 0.5f) {
            r = 0;
            g = 0;
            b = (int) (255 * (0.5f - value) * 4);
        } else if (value < 0.75f) {
            r = (int) (255 * (value - 0.5f) * 4);
            g = 0;
            b = 0;
        } else {
            r = 255;
            g = (int) (255 * (1.0f - value) * 4);
            b = 0;
        }
        return Color.rgb(r, g, b);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}

