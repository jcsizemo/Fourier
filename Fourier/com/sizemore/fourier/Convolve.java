package com.sizemore.fourier;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Convolve {
	
	public final float[] OP_SHARPEN = {-0.11f,-0.11f,-0.11f,-0.11f,2,-0.11f,-0.11f,-0.11f,-0.11f};
	public final float[] OP_GLOW = {1,1,1,1,1,1,1,1,1};
	public final float[] OP_BLUR = {0,0.1666f,0,0.1666f,0.3333f,0.1666f,0,0.1666f,0};
	public final float[] OP_EDGE_DETECT = {-1,-2,-1,0,0,0,1,2,1};
	
	public int[] convolve(int[] pixels,int w, int h, int kernelDim, float[] kernel) {
		if (kernelDim%2 == 0 || kernelDim < 3) {
			throw new NumberFormatException("Only odd kernel dimensions allowed. Kernel dimension must be 3 or larger.");
		}
		int area = w*h;
		
		int[] convolved = new int[area];
		
		for (int i = 0; i < area; i++) {
			int result = 0;
			float[] matrix = getMatrix(pixels, i, h, kernelDim);		// i is central pix coordinate
			result = sumOfProducts(matrix, kernel);
			convolved[i] = result;
		}
		
		return convolved;
	}
	
	public float[] getMatrix(int[] pixels, int centralPixelIndex, int h, int matrixDim) {
		int matrixArea = matrixDim*matrixDim;
		int convOffset = (matrixDim-1)/2;		// determine the offset from the central pixel to grab neighboring pixels
		int x = centralPixelIndex/h;
		int y = centralPixelIndex - x*h;
		float[] matrix = new float[matrixArea];
		float[] zero = {0,0,0,0,0,0,0,0,0};
		
		for (int i = 0; i < matrixArea; i++) {
				try {
					int xIndex = x-convOffset+i / matrixDim;		 // every matrix dim, increments
					int yIndex = y - convOffset + i % matrixDim;	 // increments every iteration, bounded by matrix dim
					
					if (xIndex < 0 || xIndex >= (pixels.length/h) || yIndex < 0 || yIndex >= h) {
						return zero;
					}
					
					int index = xIndex*h + yIndex;
					matrix[i] = pixels[index];
				}
				catch (Exception e) {
					return zero;
				}
			}
		return matrix;
		}
		
	
	public int sumOfProducts(float[] matrix, float[] kernel) {

		int area = matrix.length;
		int resultR = 0;
		int resultG = 0;
		int resultB = 0;
		
		// reflect kernel
		float[] temp = new float[area];
		for (int i = 0; i <= (area-1)/2; i++) {
			temp[i] = kernel[area-i-1];
			temp[area-i-1] = kernel[i];
		}
		kernel = temp;
		
		for (int i = 0; i < area; i++) {
			resultR += (((int) matrix[i] & 0x00FF0000) >> 16) * kernel[i];
			resultG += (((int) matrix[i] & 0x0000FF00) >> 8) * kernel[i];
			resultB += ((int) matrix[i] & 0x000000FF) * kernel[i];
		}
		
		if (resultR > 255) {
			resultR = 0xFF;
		}
		if (resultG > 255) {
			resultG = 0xFF;
		}
		if (resultB > 255) {
			resultB = 0xFF;
		}
		if (resultR < 0) {
			resultR = 0;
		}
		if (resultG < 0) {
			resultG = 0;
		}
		if (resultB < 0) {
			resultB = 0;
		}
		resultR <<= 16;
		resultG <<= 8;
		
		int result = 0xFF000000 | resultR | resultG | resultB;
		return result;
	}
	
	public int[] transpose(int[] matrix, int w, int h) {
		int[] transpose = new int[matrix.length];
		int i = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				transpose[i++] = matrix[x*h + y];
			}
		}
		return transpose;
	}
	
}