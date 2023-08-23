package com.leophysics.dhim3drenderer;

import android.graphics.*;
import org.jtransforms.fft.*;

public class FFT2D extends DoubleFFT_2D
{
	public FFT2D(int width,int height){
		super(width,height);

	}
//	import edu.emory.mathcs.jtransforms.fft.DoubleFFT_2D;

	//public class FFT2D_J_Transform extends DoubleFFT_2D {

		public void fftshift2d(double[][] x) {
			int rows = x.length;
			int cols = x[0].length;
			int halfRows = rows / 2;
			int halfCols = cols / 2;

			// Swap first and third quadrants
			for (int i = 0; i < halfRows; i++) {
				for (int j = 0; j < halfCols; j++) {
					double temp = x[i][j];
					x[i][j] = x[i + halfRows][j + halfCols];
					x[i + halfRows][j + halfCols] = temp;
				}
			}

			// Swap second and fourth quadrants
			for (int i = 0; i < halfRows; i++) {
				for (int j = halfCols; j < cols; j++) {
					double temp = x[i][j];
					x[i][j] = x[i + halfRows][j - halfCols];
					x[i + halfRows][j - halfCols] = temp;
				}
			}

			// If number of rows is odd, swap central element with corresponding element in third quadrant
			if (rows % 2 != 0) {
				for (int j = 0; j < halfCols; j++) {
					double temp = x[halfRows][j];
					x[halfRows][j] = x[halfRows + halfRows][j + halfCols];
					x[halfRows + halfRows][j + halfCols] = temp;
				}
			}

			// If number of columns is odd, swap central element with corresponding element in second quadrant
			if (cols % 2 != 0) {
				for (int i = 0; i < halfRows; i++) {
					double temp = x[i][halfCols];
					x[i][halfCols] = x[i + halfRows][halfCols + halfCols];
					x[i + halfRows][halfCols + halfCols] = temp;
				}
			}
		}

		public void ifftshift2d(double[][] x) {
			int rows = x.length;
			int cols = x[0].length;
			int halfRows = rows / 2;
			int halfCols = cols / 2;

			// Swap third and first quadrants
			for (int i = 0; i < halfRows; i++) {
				for (int j = 0; j < halfCols; j++) {
					double temp = x[i][j];
					x[i][j] = x[i + halfRows][j + halfCols];
					x[i + halfRows][j + halfCols] = temp;
				}
			}

			// Swap fourth and second quadrants
			for (int i = halfRows; i < rows; i++) {
				for (int j = 0; j < halfCols; j++) {
					double temp = x[i][j];
					x[i][j] = x[i - halfRows][j + halfCols];
					x[i - halfRows][j + halfCols] = temp;
				}
			}

			// If number of rows is odd, swap central element with corresponding element in first quadrant
			if (rows % 2 != 0) {
				for (int j = 0; j < halfCols; j++){
					
					double temp = x[halfRows][j];
					x[halfRows][j] = x[halfRows + halfRows][j + halfCols];
					x[halfRows + halfRows][j + halfCols] = temp;
				}
			}

			// If number of columns is odd, swap central element with corresponding element in fourth quadrant
			if (cols % 2 != 0) {
				for (int i = 0; i < halfRows; i++) {
					double temp = x[i][halfCols];
					x[i][halfCols] = x[i + halfRows][halfCols + halfCols];
					x[i + halfRows][halfCols + halfCols] = temp;
				}
			}
		}
	public static void centerNonZeroRegion(double[][] matrix) {
		int numRows = matrix.length;
		int numCols = matrix[0].length;

		// Find the boundaries of the non-zero region
		int minRow = numRows;
		int maxRow = -1;
		int minCol = numCols;
		int maxCol = -1;
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				if (matrix[i][j] != 0) {
					minRow = Math.min(minRow, i);
					maxRow = Math.max(maxRow, i);
					minCol = Math.min(minCol, j);
					maxCol = Math.max(maxCol, j);
				}
			}
		}

		// Compute the displacement vector
		int centerRow = numRows / 2;
		int centerCol = numCols / 2;
		int displacementRow = centerRow - (minRow + maxRow) / 2;
		int displacementCol = centerCol - (minCol + maxCol) / 2;

		// Shift the non-zero region
		for (int i = minRow; i <= maxRow; i++) {
			for (int j = minCol; j <= maxCol; j++) {
				double val = matrix[i][j];
				matrix[i + displacementRow][j + displacementCol] = val;
				matrix[i][j] = 0;
			}
		}
	}
	
		
		public double[][] complexInnerProduct(double[][] A,double[][] B){
			
			int N = A.length;
			int M = A[0].length / 2;
			
			double[][] result = new double[N][2*M];
			
			
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < M; j++) {
					// Extract real and imaginary parts of complex numbers
					double Ar = A[i][2*j];
					double Ai = A[i][2*j+1];
					double Br = B[i][2*j];
					double Bi = B[i][2*j+1];

					// Compute the product of the complex numbers
					double Pr = Ar*Br - Ai*Bi;
					double Pi = Ar*Bi + Ai*Br;

					// Store the product in the result matrix
					result[i][2*j] = Pr;
					result[i][2*j+1] = Pi;
				}
			}
			
			return result;
		}
		
					
	public double[][] getMagnitude(double[][] complexMatrix) {
        int rows = complexMatrix.length;
        int cols = complexMatrix[0].length;
        double[][] mag = new double[rows][cols / 2]; // magnitude matrix

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols / 2; j++) {
                mag[i][j] =20*Math.log( Math.sqrt(complexMatrix[i][2 * j] * complexMatrix[i][2 * j] +
                                      complexMatrix[i][2 * j + 1] * complexMatrix[i][2 * j + 1]));
            }
        }

        return mag;
    }
	public double[][] geTRealPart(double[][] cmp){
		double[][] real = new double[cmp.length][cmp[0].length / 2]; // magnitude matrix
		
		for (int i = 0; i < cmp.length; i++) {
            for (int j = 0; j < cmp[0].length / 2; j++) {
                real[i][j] =(cmp[i][j]);    }
        }
		return real;
	}
	public double[][] getPhase(double[][] complexMatrix) {
        int rows = complexMatrix.length;
        int cols = complexMatrix[0].length;
        double[][] phase = new double[rows][cols / 2]; // phase matrix

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols / 2; j++) {
                phase[i][j] =Math.log(Math.atan2(complexMatrix[i][2 * j + 1], complexMatrix[i][2 * j]));
            }
        }

        return phase;
    }
	public double[][] getPhaseWithBaselineCorrected(double[][] phase){
		
		int rows = phase.length;
        int cols = phase[0].length;
		double baseline=ImageProcessor.calculateAverage(phase);
		
		for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols / 2; j++) {
                phase[i][j] =phase[i][j]-baseline;
            }
        }

        return phase;
    
		
	
	}
	
	

		/**
		 * Shifts the zero-frequency component to the center of the spectrum.
		 * 
		 * @param array the 2D array to be shifted
		 * @return the shifted 2D array
		 */
		public  double[][] fftshiftReal(double[][] array) {
			int numRows = array.length;
			int numCols = array[0].length;
			int numHalfRows = numRows / 2;
			int numHalfCols = numCols / 2;

			double[][] shifted = new double[numRows][numCols];
			for (int i = 0; i < numRows; i++) {
				for (int j = 0; j < numCols; j++) {
					shifted[(i + numHalfRows) % numRows][(j + numHalfCols) % numCols] = array[i][j];
				}
			}

			return shifted;
		}

		/**
		 * Shifts the zero-frequency component back to the corner of the spectrum.
		 * 
		 * @param array the 2D array to be shifted
		 * @return the shifted 2D array
		 */
		public  double[][] ifftshiftReal(double[][] array) {
			int numRows = array.length;
			int numCols = array[0].length;
			int numHalfRows = numRows - (numRows / 2);
			int numHalfCols = numCols - (numCols / 2);

			double[][] shifted = new double[numRows][numCols];
			for (int i = 0; i < numRows; i++) {
				for (int j = 0; j < numCols; j++) {
					shifted[i][j] = array[(i + numHalfRows) % numRows][(j + numHalfCols) % numCols];
				}
			}

			return shifted;
		}
		
	public  double[][] multiplyWithConjugate(double[][] a, double[][] b) {
		int rowsA = a.length;
		int colsA = a[0].length;
		int rowsB = b.length;
		int colsB = b[0].length;

		// Check if the dimensions of the matrices are compatible for multiplication
		if (colsA != rowsB) {
			throw new IllegalArgumentException("Matrices are not compatible for multiplication");
		}

		double[][] result = new double[rowsA][colsB];

		for (int i = 0; i < rowsA; i++) {
			for (int j = 0; j < colsB; j++) {
				double sum = 0.0;
				for (int k = 0; k < colsA; k++) {
					sum += a[i][k] * b[k][j] * (Math.abs(i-k) == Math.abs(j-k) ? -1 : 1);
				}
				result[i][j] = sum;
			}
		}

		return result;
	}
	
	
	
	//import org.jtransforms.fft.DoubleFFT_2D;

	
	
}
