package RevNoThreads;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ConvolveImage implements Image {

	private BufferedImage image;
	private int[] pixels;
	private int w;
	private int h;
	private boolean keepBrightness;
	private Kernel kernel;
	
	public ConvolveImage(String filename, Kernel kernel, boolean keepBrightness) {
		try {
			File file = new File(filename);
			this.image = ImageIO.read(file);
			this.kernel = kernel;
			if (kernel.getData().length > image.getHeight()*image.getWidth()) {
				throw new IllegalArgumentException("Kernel area must be smaller or equal to image area");
			}
			this.w = image.getWidth();
			this.h = image.getHeight();
			this.keepBrightness = keepBrightness;
			this.pixels = image.getRGB(0, 0, w, h, new int[w*h], 0, w);
		}
		catch (IOException ioe) {
			System.out.println("Could not create ConvolveImage: " + ioe.getMessage());
		}
	}
	
	public BufferedImage convolve() {
		BufferedImage convolvedImage = new BufferedImage(w,h,BufferedImage.TYPE_4BYTE_ABGR);
		float[] data = kernel.getData();
		int[] results = new int[w*h];
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
	

	@Override
	public int getWidth() {
		return this.w;
	}

	@Override
	public int getHeight() {
		return this.h;
	}

	@Override
	public BufferedImage getImage() {
		return this.image;
	}

	@Override
	public void setImage(BufferedImage image) {
		this.image = image;
		this.w = image.getWidth();
		this.h = image.getHeight();
		this.pixels = new int[w*h];
		this.pixels = image.getRGB(0, 0, w, h, pixels, 0, w);
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

	public static void printPixels(BufferedImage image, String filename) {
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

	public static void printImage(BufferedImage image, String filename) {
		RenderedImage rImage = (RenderedImage) image;
		try {
			File file = new File(filename);
			ImageIO.write(rImage, "png", file);
		} catch (IOException ioe) {
			System.out.println("File not found.");
		}
	}
}
