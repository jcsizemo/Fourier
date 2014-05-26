package RevThreads;

import java.awt.image.BufferedImage;
import java.util.List;

public class ConvolveThread extends Thread {
	
	BufferedImage image;
	Kernel kernel;
	List<ConvolveThread> threadList;
	int w;
	int h;
	int centralPixelIndex;
	boolean keepBrightness;
	int[] pixels;
	
	public ConvolveThread(BufferedImage image, Kernel kernel, int[] pixels, int centralPixelIndex, List<ConvolveThread> threadList, boolean keepBrightness) {
		this.image = image;
		this.kernel = kernel;
		this.threadList = threadList;
		this.w = image.getWidth();
		this.h = image.getHeight();
		this.centralPixelIndex = centralPixelIndex;
		this.keepBrightness = keepBrightness;
		this.pixels = pixels;
		threadList.add(this);
		this.start();
	}
	
	public void run() {
		try {
		float[] data = kernel.getData();
		int[] results = new int[w];
		for (int i = 0; i < w; i++) {
			int convolvedPixelValue = (int) sumOfProducts(getMatrix(centralPixelIndex + i, pixels), data);
			synchronized (image) {
			image.setRGB((centralPixelIndex + i) % w, (centralPixelIndex + i) / w, convolvedPixelValue);
			}
//			results[i] = convolvedPixelValue;
		}
//		image.setRGB(0, centralPixelIndex, w, h, results, 0, 0);
		synchronized (threadList) {
		threadList.remove(this);
		}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private float[] getMatrix(int centralPixelIndex, int[] pixels) {
		int matrixArea = kernel.getHeight()*kernel.getWidth();
		int yOffset = (kernel.getWidth()-1)/2;		// determine the offset from the central pixel to grab neighboring pixels
		int xOffset = (kernel.getHeight()-1)/2;
		int y = centralPixelIndex / w;
		int x = centralPixelIndex % w;
		float[] matrix = new float[matrixArea];
		
		for (int i = 0; i < matrixArea; i++) {
			int xIndex = x-xOffset+i % kernel.getWidth();		 // every matrix dim, increments
			int yIndex = y - yOffset + i / kernel.getHeight();	 // increments every iteration, bounded by matrix dim
					
			if (xIndex < 0 || xIndex >= (pixels.length/h) || yIndex < 0 || yIndex >= h) {
//				matrix[i] = 0;
//				continue;
				float[] zeroes = {0,0,0,0,0,0,0,0,0};
				return zeroes;
			}
					
			int index = xIndex + yIndex*w;
			matrix[i] = pixels[index];
			}
		return matrix;
		}
	
	private int sumOfProducts(float[] matrix, float[] data) {
		float r = 0;
		float g = 0;
		float b = 0;
		
		for (int i = 0; i < matrix.length; i++) {
			r += (((int)matrix[i] & 0xFF0000) >> 16) * data[i];
			g += (((int)matrix[i] & 0xFF00) >> 8) * data[i];
			b += (((int)matrix[i] & 0xFF)) * data[i];
		}
		if (keepBrightness) {
			float sum = kernel.getSum();
			r /= sum;
			b /= sum;
			g /= sum;
		}
		r = (r > 255) ? 255 : r;
		g = (g > 255) ? 255 : g;
		b = (b > 255) ? 255 : b;
		r = (r < 0) ? 0 : r;
		g = (g < 0) ? 0 : g;
		b = (b < 0) ? 0 : b;
		return (0xFF000000) | ((int) r << 16) | ((int) g << 8) | (int) b;
	}

}
