package com.leophysics.dhim3drenderer;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.jtransforms.fft.DoubleFFT_2D;

public class PhaseReconstruction {

    public static Bitmap reconstructPhase(Bitmap hologram, double theta) {
        // Convert the Bitmap to a double array
        int width = hologram.getWidth();
        int height = hologram.getHeight();
        double[][] input = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = hologram.getPixel(j, i);
                int intensity = Color.red(pixel);
                input[i][j] = intensity;
            }
        }

        // Apply window function to the input array
        double[][] windowed = applyWindow(input, height, width);

        // Perform 2D FFT on the windowed array
        DoubleFFT_2D fft = new DoubleFFT_2D(height, width);
        double[][] freq = new double[height][2 * width];
        fft.realForwardFull(windowed);

        // Calculate magnitude and phase of the frequency domain
        double[][] magnitude = new double[height][width];
        double[][] phase = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double re = freq[i][2*j];
                double im = freq[i][2*j + 1];
                magnitude[i][j] = Math.sqrt(re*re + im*im);
                phase[i][j] = Math.atan2(im, re);
            }
        }

        // Shift the phase map so that the zero frequency component is at the center of the image
        double[][] shifted = shiftPhase(phase, height, width);

        // Apply phase correction factor
        double[][] corrected = applyPhaseCorrection(shifted, height, width, theta);

        // Perform inverse 2D FFT on the corrected phase map
        double[][] complex = new double[height][2 * width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                complex[i][2*j] = magnitude[i][j] * Math.cos(corrected[i][j]);
                complex[i][2*j + 1] = magnitude[i][j] * Math.sin(corrected[i][j]);
            }
        }
        fft.realInverseFull(complex, true);

        // Convert the output array to a Bitmap
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int intensity = (int) windowed[i][j];
                int pixel = Color.rgb(intensity, intensity, intensity);
                output.setPixel(j, i, pixel);
            }
        }
        return output;
    }

    private static double[][] applyWindow(double[][] input, int height, int width) {
        double[][] windowed = new double[height][width];
        double[] window = new double[height];
        for (int i = 0; i < height; i++) {
            window[i] = 0.54 - 0.46*Math.cos(2*Math.PI*i/(height - 1));
        }
        for (int i =    0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				windowed[i][j] = input[i][j] * window[i];
			}
		}
		return windowed;
	}

	private static double[][] shiftPhase(double[][] phase, int height, int width) {
		double[][] shifted = new double[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				shifted[i][j] = phase[i][j] * Math.pow(-1, i+j);
			}
		}
		return shifted;
	}

	private static double[][] applyPhaseCorrection(double[][] phase, int height, int width, double theta) {
		double[][] corrected = new double[height][width];
		double k = 2*Math.PI/633e-9;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				double fx = (j - width/2.0) / width;
				double fy = (i - height/2.0) / height;
				double factor = k * theta * Math.sqrt(1 - fx*fx - fy*fy);
				corrected[i][j] = phase[i][j] - factor;
			}
		}
		return corrected;
	}
}

