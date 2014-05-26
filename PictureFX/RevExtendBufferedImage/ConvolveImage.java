package RevExtendBufferedImage;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class ConvolveImage extends BufferedImage {
	
	private Kernel kernel;
	private boolean keepBrightness = false;
	private int w;
	private int h;
	private int area;
	
	public ConvolveImage(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied, Hashtable properties) {
		super(cm, raster, isRasterPremultiplied, properties);
		this.w = getWidth();
		this.h = getHeight();
		this.area = w*h;
	}
	
	public ConvolveImage(int w, int h, int type) {
		super(w, h, type);
	}
	
	public ConvolveImage convolve() {
		if (null == kernel) {
			throw new IllegalStateException("No kernel set.");
		}
		ConvolveImage convolvedImage = new ConvolveImage(w, h, ConvolveImage.TYPE_4BYTE_ABGR);
		float[] data = kernel.getData();
		int[] results = new int[area];
		int[] pixels = getRGB(0, 0, getWidth(), getHeight(), new int[area], 0, w);
		for (int i = 0; i < pixels.length; i++) {
			int convolvedPixelValue = (int) sumOfProducts(getMatrix(i, pixels), data);
			results[i] = convolvedPixelValue;
		}
		convolvedImage.setRGB(0, 0, w, h, results, 0, w);
		return convolvedImage;
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
	
	public boolean getKeepBrightness() {
		return this.keepBrightness;
	}
	
	public void setKeepBrightness(boolean keepBrightness) {
		this.keepBrightness = keepBrightness;
	}
	
	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}
	
	public Kernel getKernel() {
		return this.kernel;
	}

	public static void printPixels(ConvolveImage image, String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			int h = image.getHeight();
			int w = image.getWidth();
			int[] pixels = new int[h*w];
			pixels = image.getRGB(0, 0, w, h, pixels, 0, w);
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					bw.write(pixels[x + y*w] + ",");
				}
				bw.newLine();
			}
			bw.close();
		}
		catch (IOException ioe) {
			System.out.println("Cannot write file.");
		}
	}

	public static void printImage(ConvolveImage image, String filename) {
		RenderedImage rImage = (RenderedImage) image;
		try {
			File file = new File(filename);
			ImageIO.write(rImage, "png", file);
		} catch (IOException ioe) {
			System.out.println("File not found.");
		}
	}
}
