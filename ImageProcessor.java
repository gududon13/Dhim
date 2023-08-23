package com.leophysics.dhim3drenderer;


import android.graphics.*;
import java.util.*;

public class ImageProcessor
{
	private Bitmap ref;
	private Bitmap Obj;
	private Bitmap fftref,maskappliedref;
	private Bitmap fftobj,maskappliedobj;
	private Bitmap ifftref,ifftrefc;
	private Bitmap ifftobj,ifftobjc;
	private FFT2D fft;
	private double[][] phase;
	//Context Context;
	double[][] holder1,holder2,refdo,objdo;
	public ImageProcessor(Bitmap Ref,Bitmap obj ){
	
		this.ref=Ref;
		this.Obj=obj;
		Obj=reduceGaussianNoise(obj,2);
		ref=reduceGaussianNoise(ref,2);
		//Obj=removeHammingNoise(obj,4);
		//ref=removeHammingNoise(ref,4);
		fftdone();
		
		
	}
	
	public double[][] getPhase(){
		return phase;
	}

	public Bitmap getifftRefc()
	{
		// TODO: Implement this method
		return ifftrefc;
	}

	public Bitmap getIfftObjc()
	{
		// TODO: Implement this method
		return ifftobjc;
	}
	public Bitmap getOlRbj(){
		return Obj;
	}
	
	public static Bitmap reduceGaussianNoise(Bitmap inputImage, int radius) {
		// Create a new bitmap with the same dimensions as the input image
		Bitmap outputImage = Bitmap.createBitmap(inputImage.getWidth(), inputImage.getHeight(), inputImage.getConfig());

		// Convert the input image to a grayscale bitmap
		Bitmap grayImage = Bitmap.createBitmap(inputImage.getWidth(), inputImage.getHeight(), Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(grayImage);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		canvas.drawBitmap(inputImage, 0, 0, paint);

		// Apply the Gaussian filter to the grayscale image
		int[] pixels = new int[grayImage.getWidth() * grayImage.getHeight()];
		grayImage.getPixels(pixels, 0, grayImage.getWidth(), 0, 0, grayImage.getWidth(), grayImage.getHeight());
		int[] resultPixels = new int[grayImage.getWidth() * grayImage.getHeight()];
		int width = grayImage.getWidth();
		int height = grayImage.getHeight();
		int index = 0;
		int size = 2 * radius + 1;
		int[] window = new int[size * size];
		float sum = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				index = y * width + x;
				sum = 0;
				for (int i = -radius; i <= radius; i++) {
					for (int j = -radius; j <= radius; j++) {
						if (x + j >= 0 && x + j < width && y + i >= 0 && y + i < height) {
							int pixel = pixels[(y + i) * width + (x + j)];
							int gray = (int) (Color.red(pixel) * 0.299 + Color.green(pixel) * 0.587 + Color.blue(pixel) * 0.114);
							window[(i + radius) * size + (j + radius)] = gray;
							sum += gray;
						}
					}
				}
				float mean = sum / (size * size);
				float variance = 0;
				for (int i = 0; i < size * size; i++) {
					variance += (window[i] - mean) * (window[i] - mean);
				}
				variance /= (size * size);
				float stdDev = (float) Math.sqrt(variance);
				int newPixel = (int) Math.round(mean);
				if (stdDev > 0) {
					float factor = (float) (1.0 / (2 * stdDev * stdDev));
					float[] kernel = new float[size * size];
					sum = 0;
					for (int i = 0; i < size * size; i++) {
						kernel[i] = (float) Math.exp(-((i % size - radius) * (i % size - radius) + (i / size - radius) * (i / size - radius)) * factor);
						sum += kernel[i];
					}
					for (int i = 0; i < size * size; i++) {
						kernel[i] /= sum;
					}
					float filteredPixel = 0;
					for (int i = 0; i < size * size; i++) {
						filteredPixel += kernel[i] * window[i                ];
					}
					newPixel = (int) Math.round(filteredPixel);
				}
				if (newPixel < 0) {
					newPixel = 0;
				} else if (newPixel > 255) {
					newPixel = 255;
				}
				resultPixels[index] = Color.rgb(newPixel, newPixel, newPixel);
			}
		}
		outputImage.setPixels(resultPixels, 0, outputImage.getWidth(), 0, 0, outputImage.getWidth(), outputImage.getHeight());

		return outputImage;
	}
	
	public static Bitmap removeHammingNoise(Bitmap grayscaleBitmap, int radius) {
		
		int width=grayscaleBitmap.getWidth();
		int height=grayscaleBitmap.getHeight();
			int[] pixels = new int[width * height];
		grayscaleBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		int[] resultPixels = new int[width * height];

		double[] window = new double[2 * radius + 1];
		for (int i = 0; i < window.length; i++) {
			window[i] = 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (window.length - 1));
		}

		for (int y = radius; y < height - radius; y++) {
			for (int x = radius; x < width - radius; x++) {
				int index = y * width + x;
				double sum = 0.0;
				double weightSum = 0.0;
				for (int j = -radius; j <= radius; j++) {
					for (int i = -radius; i <= radius; i++) {
						int pixelIndex = (y + j) * width + (x + i);
						double weight = window[radius + i] * window[radius + j];
						sum += weight * Color.red(pixels[pixelIndex]);
						weightSum += weight;
					}
				}
				int newPixel = (int) Math.round(sum / weightSum);
				if (newPixel < 0) {
					newPixel = 0;
				} else if (newPixel > 255) {
					newPixel = 255;
				}
				resultPixels[index] = Color.rgb(newPixel, newPixel, newPixel);
			}
		}
		outputBitmap.setPixels(resultPixels, 0, outputBitmap.getWidth(), 0, 0, outputBitmap.getWidth(), outputBitmap.getHeight());
		return outputBitmap;
	}
	
	
	
						
						
	
	
	public static double[][] subtractArrays(double[][] array1, double[][] array2) {
		int numRows = array1.length;
		int numCols = array1[0].length;
		double[][] result = new double[numRows][numCols];
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				result[i][j] = array1[i][j] - array2[i][j];
			}
		}
		return result;
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
	
	
	public double[][] correctPhasemaop(double[][] magnitude,double[][] corrected){
		int width=magnitude.length;
		int height=magnitude[0].length;
		double[][] complex = new double[height][2* width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                complex[i][2*j] = magnitude[i][j] * Math.cos(corrected[i][j]);
                complex[i][2*j + 1] = magnitude[i][j] * Math.sin(corrected[i][j]);
            }
			
	}
		return complex;
	}
	public double[][] backgroundsubstracted(double[][] Object,double[][] ref){
		double[][] substract=new double[Object.length][Object[0].length];
		
		
		return substract;
	}
	
	
	/**public static double[][] calculatePhase(double[][] O_sample, double[][] O_flat) {
		/*
		 * Function: Returns the phase calculated from IFFT images of the given Object and Sample Images after noise removal
		 */

		/**int height = O_sample.length;
		int width = O_sample[0].length;

		double[][] phase_sample = new double[height][width];
		double[][] phase_flat = new double[height][width];

		// Calculate phase of O_sample and O_flat
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				phase_sample[y][x] = Math.atan2(O_sample[y][x], 0);
				phase_flat[y][x] = Math.atan2(O_flat[y][x], 0);
			}
		}

		double[][] expSamplePhase = new double[height][width];
		double[][] expFlatPhase = new double[height][width];

		// Calculate exponential phase
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				expSamplePhase[y][x] = Math.exp(1j * phase_sample[y][x]);
				expFlatPhase[y][x] = Math.exp(-1j * phase_flat[y][x]);
			}
		}

		double[][] finalPhase = new double[height][width];

		// Calculate final phase
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				finalPhase[y][x] = Math.atan2(expFlatPhase[y][x] * expSamplePhase[y][x] * Math.exp(1j * Math.PI / 2), 0);
			}
		}

		return finalPhase;
	}**/
	
	
	public Bitmap getFftRef(){
		return fftref;
	}
	public Bitmap getFFtObj()
	{
		return fftobj;
	}
	public Bitmap getIFFtreff(){
		return ifftref;
	}
	public Bitmap getIFFtObj(){
		return ifftobj;
	}
	public Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();    

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);

		return bmpGrayscale;
	}
	public Bitmap toHSV(double[][] data) {
		int width = data.length;
		int height = data[0].length;
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		float[] hsv = new float[3];
		int[] pixels = new int[width * height];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// Convert data to HSV values
				hsv[0] = (float) (data[j][i] * 50);
				hsv[1] = 1.0f;
				hsv[2] = 1.0f;

				// Convert HSV to RGB color space
				int color = Color.HSVToColor(hsv);

				// Set pixel color in bitmap
				pixels[i * width + j] = color;
			}
		}

		bmp.setPixels(pixels, 0, width, 0, 0, width, height);

		return bmp;
	}
	
	
	private void Fftimg(Bitmap bitmap){
		
		
		
	}
	public double[][] sidedRectangularmask(int width,int height,int size,boolean isLeft){
		double[][] matrix=new double[width][height];
	//int n = matrix.length;
		//int center = size;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (i <= size) {
					matrix[i][j] = 1.0;
				} else {
					matrix[i][j] = 0.0;
				}
			}
		
	}
		
		return matrix;
	}
	public static double[][] createCircularMask(int width, int height, int centerX, int centerY, int radius) {
		double[][] mask = new double[width][height];
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
				if (distance <= radius) {
					mask[y][x] = 1;
				} else {
					mask[y][x] = 0;
				}
			}
		}
		return mask;
	}
	public static double[][] createCircularMask2(int width, int height, int centerX, int centerY, int radius) {
		double[][] mask = new double[width][height];
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
				if (distance <= radius) {
					mask[y][x] = 0;
				} else {
					mask[y][x] = 1;
				}
			}
		}
		return mask;
	}
	public static double[][] createStripMask(int width, int height, int centerX, int centerY, int radius) {
		double[][] mask = new double[width][height];
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				double distance = Math.sqrt(Math.pow(y - centerY, 2));
				if (distance <= radius) {
					mask[y][x] = 1;
				} else {
					mask[y][x] = 0;
				}
			}
		}
		return mask;
	}
	
	public static double[][] createStripMask1(int width, int height, int centerX, int centerY, int radius) {
		double[][] mask = new double[width][height];
		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				double distance = Math.sqrt(Math.pow(y - centerY, 2));
				if (distance <= radius) {
					mask[y][x] = 0;
				} else {
					mask[y][x] = 1;
				}
			}
		}
		return mask;
	}
	
	
	public double[][] bitmapToDoubleArray(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		double[][] data = new double[width][2*height];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int color = bitmap.getPixel(j, i);
				int red = Color.red(color);
				int green = Color.green(color);
				int blue = Color.blue(color);

				// Calculate grayscale intensity value
				double intensity = 0.2989 * red + 0.5870 * green + 0.1140 * blue;

				// Store intensity value in double array
				data[j][i] = intensity;
			}
		}

		return data;
	}
	
	
	public Bitmap doubleArrayToBitmap(double[][] data) {
		int width = data.length;
		int height = data[0].length;

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// Convert intensity value to RGB color
				int color = Color.rgb((int)data[j][i], (int)data[j][i], (int)data[j][i]);

				// Set pixel value in bitmap
				bitmap.setPixel(j, i, color);
			}
		}

		return bitmap;
	}
	public Bitmap doubleArrayToBitmap1(double[][] data) {
		int width = data.length;
		int height = data[0].length/2;

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// Convert intensity value to RGB color
				int color = Color.rgb((int)data[j][i], (int)data[j][i], (int)data[j][i]);

				// Set pixel value in bitmap
				bitmap.setPixel(j, i, color);
			}
		}

		return bitmap;
	}
	
	
	public static double[][] ApplyMask2(double[][] fft_data,double[][] mask){


		double[][] mask_applied=new double[fft_data.length][fft_data[0].length];
		int numRows = fft_data.length;
		int numCols = fft_data[0].length;

// Loop over each element of the complex matrix and apply the mask
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j ++) { // step by 2 to handle the real and imaginary parts separately
				// Apply the mask to the real part of the complex element
				mask_applied[i][j]=(fft_data[i][j])*mask[j][i];

			}
		}

		return mask_applied;
	}
	
	public double[][] ApplyMask(double[][] fft_data,double[][] mask){
		
		
		double[][] mask_applied=new double[fft_data.length][fft_data[0].length];
		int numRows = fft_data.length;
		int numCols = fft_data[0].length;

// Loop over each element of the complex matrix and apply the mask
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols/2; j ++) { // step by 2 to handle the real and imaginary parts separately
				// Apply the mask to the real part of the complex element
			mask_applied[i][2*j]=	fft_data[i][2*j] * mask[j][i];

				// Apply the mask to the imaginary part of the complex element
				
				mask_applied[i][2*j+1]= fft_data[i][2*j+1] *mask[j][i];
				
			}
		}
		
		return mask_applied;
	}
	public static Bitmap convertToHSV(double[][] values) {
		// Find the minimum and maximum values in the array
		double minValue = Double.MAX_VALUE;
		double maxValue = Double.MIN_VALUE;
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				minValue = Math.min(minValue, values[i][j]);
				maxValue = Math.max(maxValue, values[i][j]);
			}
		}

		// Normalize the values so they fall within the range [0, 1]
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				values[i][j] = (values[i][j] - minValue) / (maxValue - minValue);
			}
		}

		// Convert the values to HSV colors
		Bitmap bitmap = Bitmap.createBitmap(values.length, values[0].length, Bitmap.Config.RGB_565);
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				float hue = (float) (values[i][j] * 0.7); // Scale the hue to avoid pure red
				float saturation = 1.0f;
				float brightness = 1.0f;
				float[] hsv = new float[] {brightness, saturation, hue};
				int color = Color.HSVToColor(hsv);
				bitmap.setPixel(i, j, color);
			}
		}

		return bitmap;
	}
	
	public  static double[][] getCenterPeriferiPhase(double[][] phase,int x,int y){
		
		
		
		
		double center=phase[x][y];
		double[][] mask=createCircularMask(phase.length,phase[0].length,x,y,128);
		mask=ApplyMask2(phase,mask);
		//double average= calculateAverage(phase);
		//double perifer= calculateAverage(mask);
		

		
		//double[] data={center,perifer};
		return mask;
		
	}
	
	

	
	
	
	
	
	
	public void fftdone(){
		fft=new FFT2D(Obj.getWidth(),Obj.getHeight());
		
	    
		double[][] objdouble= bitmapToDoubleArray(Obj);
		double[][] refdouble= bitmapToDoubleArray(ref);
		//objdouble=subtractArrays(objdouble,refdouble);
		fft.complexForward(objdouble);
		fft.complexForward(refdouble);
		fft.fftshift2d(objdouble);
		fft.fftshift2d(refdouble);
		holder1=fft.getMagnitude(objdouble);
		fftobj=doubleArrayToBitmap(holder1);
		
		int[] arrau=voidGetMaxXY(holder1);
		double[][] mask= createCircularMask(refdouble.length,(refdouble[0].length)/2,arrau[0],arrau[1],38);
		
		//double[][] mask= createCircularMask(refdouble.length,(refdouble[0].length)/2,(Obj.getWidth()/2)-18 ,(Obj.getHeight()/2)+85,32);
		double[][] maskapplied=ApplyMask(objdouble,mask);
		double[][] maskapplied2=ApplyMask(refdouble,mask);
	    holder1=fft.getMagnitude(maskapplied);
		holder2=fft.getMagnitude(maskapplied2);
		maskappliedobj=doubleArrayToBitmap(holder1);
		maskappliedref=doubleArrayToBitmap(holder2);
		
		//fft.centerNonZeroRegion(maskapplied);
		//fft.centerNonZeroRegion(maskapplied2);
		
		double[][] magnatude1=fft.getMagnitude(maskapplied);
		//double[][] magnatude2=fft.getMagnitude(maskapplied2);
		ifftobjc=doubleArrayToBitmap(magnatude1);
		
		fft.complexInverse(maskapplied,true);
		    fft.complexInverse(maskapplied2,true);
		double[][] gpold1=new double[Obj.getWidth()][Obj.getHeight()];
		double[][] gpold2=new double[Obj.getWidth()][Obj.getHeight()];
		
		gpold1=fft.geTRealPart(maskapplied);
		ifftobj=doubleArrayToBitmap(gpold1);
		gpold2=fft.geTRealPart(maskapplied2);
		ifftref=doubleArrayToBitmap(gpold2);
		gpold1=getPhase(gpold1,gpold2);
		
		fft.realForward(gpold1);
		gpold1=fft.fftshiftReal(gpold1);
		double[][] mask5=createCircularMask(gpold1.length,gpold1[0].length,gpold1.length/2,gpold1[1].length/2,25);
		mask5=ApplyMask2(gpold1,mask5);
		mask5=fft.ifftshiftReal(mask5);
		fft.realInverse(mask5,true);
		phase=mask5;
	JetColorMap cmap=new JetColorMap(mask5);
		// ifftrefc= graytorgb(mask5);
		ifftrefc=cmap.getBitmap();
	}

	private int[] voidGetMaxXY(double[][] hold)
	{
		double[][] mask=createCircularMask2(hold.length,hold[0].length,hold.length/2,hold[0].length/2,35);
		mask=ApplyMask2(hold,mask);
		double currentMax = Double.NEGATIVE_INFINITY;

		int[] array=new int[2];
		
		for (int i = 0; i < mask.length; i++) {
			for (int j = 0; j < mask[i].length; j++) {
				if (mask[i][j] >= currentMax) {
					currentMax = mask[i][j];
					array[0]=i;
					array[1]=j;
				}
			}
		}
		
		return array;
	}
	
	public Bitmap graytorgb(double[][] bit){
		
		
		double[][] grayBitmap = bit; // Your grayscale bitmap

		int width = grayBitmap.length;
		int height = grayBitmap[0].length;

		Bitmap colorBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double pixel = grayBitmap[x][y];
				int value=(int)(pixel*255);
				int value1=(int)(pixel*240);
				int color;
				if (pixel > 3) { // High amplitude, set to red
					color = Color.RED;
				} else if (pixel >2) { // Medium amplitude, set to yellow
					color = Color.argb(255,value,0,0);
				} else if(pixel>1)
				{ // Low amplitude, set to green
					//color = Color.argb(255,0,0,(int)(pixel*150));
					color=Color.YELLOW;
				
				}
				else if(pixel>0){
					color=Color.argb(255,value,0,0);
					
					
				}
				
				
				else
				{
					color=Color.TRANSPARENT;
				}
				colorBitmap.setPixel(x, y, color);
				}

				
			}
		
		
		return colorBitmap;
	}
	public Bitmap getMaskAppliedRef(){
		return maskappliedref;
	}
	public Bitmap getMaskAppliedObj(){
		return maskappliedobj;
	}
	
	public Bitmap removeNoise(Bitmap bitmap) {
		Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

		int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

		int[] resultPixels = new int[bitmap.getWidth() * bitmap.getHeight()];

		for (int y = 1; y < bitmap.getHeight() - 1; y++) {
			for (int x = 1; x < bitmap.getWidth() - 1; x++) {
				int index = y * bitmap.getWidth() + x;
				int pixel = pixels[index];

				int r1 = Color.red(pixels[index - 1]);
				int r2 = Color.red(pixels[index + 1]);
				int r3 = Color.red(pixels[index - bitmap.getWidth()]);
				int r4 = Color.red(pixels[index + bitmap.getWidth()]);

				int g1 = Color.green(pixels[index - 1]);
				int g2 = Color.green(pixels[index + 1]);
				int g3 = Color.green(pixels[index - bitmap.getWidth()]);
				int g4 = Color.green(pixels[index + bitmap.getWidth()]);

				int b1 = Color.blue(pixels[index - 1]);
				int b2 = Color.blue(pixels[index + 1]);
				int b3 = Color.blue(pixels[index - bitmap.getWidth()]);
				int b4 = Color.blue(pixels[index + bitmap.getWidth()]);

				int avgR = (r1 + r2 + r3 + r4 + Color.red(pixel)) / 5;
				int avgG = (g1 + g2 + g3 + g4 + Color.green(pixel)) / 5;
				int avgB = (b1 + b2 + b3 + b4 + Color.blue(pixel)) / 5;

				resultPixels[index] = Color.rgb(avgR, avgG, avgB);
			}
		}

		resultBitmap.setPixels(resultPixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

		return resultBitmap;
	}
	
	public double[][] getPhase(double[][] ref1 ,double[][] obj1){
		
		
		double[][] phase=new double[ref1.length][ref1[0].length];
		for(int i=0; i<ref1.length; i++){
			for(int j=0; j<ref1[0].length; j++){
				
				phase[i][j]=(Math.atan2(obj1[i][j],ref1[i][j]));
				
				}
		}
		double baselinecorrection=ImageProcessor.calculateAverage(phase);
		for(int i=0; i<ref1.length; i++){
			for(int j=0; j<ref1[0].length; j++){

				phase[i][j]=phase[i][j]-baselinecorrection;

			}
		}
		return phase;
	}
	
	
	public  double[][] matrixMultiplication(double[][] a, double[][] b) {
		int rowsA = a.length;
		int colsA = a[0].length;
		int colsB = b[0].length;
		double[][] c = new double[rowsA][colsB];

		for (int i = 0; i < rowsA; i++) {
			for (int j = 0; j < colsB; j++) {
				for (int k = 0; k < colsA; k++) {
					c[i][j] += a[i][k] * b[k][j];
				}
			}
		}

		return c;
	}
	
	public static double calculateAverage(double[][] array) {
        double sum = 0.0;
        int totalElements = 0;

        // Flatten the 2D array and calculate sum and total number of elements
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
				if(array[i][j]!=0)
				{
                sum += array[i][j];
                totalElements++;
				}
            }
        }

        // Calculate the average
        double average = sum / totalElements;
        return average;
    }
	
	
}
