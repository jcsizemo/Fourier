package com.sizemore.fourier;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageToolkit {
	
	private BufferedImage image = null;
	
	public ImageToolkit() {
	}
	
	
	public void printPixels(BufferedImage image, String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			int[] pixels = getPixels(image);
			int h = image.getHeight();
			int w = image.getWidth();
			
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					bw.write(pixels[x*h + y] + ",");
				}
				bw.newLine();
			}
			bw.close();
		}
		catch (IOException ioe) {
			System.out.println("Cannot write file.");
		}
		
	}
	
	public void printPixels(int[] pixels, int w, int h, String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					bw.write(pixels[x*h + y] + ",");
				}
				bw.newLine();
			}
			bw.close();
		}
		catch (IOException ioe) {
			System.out.println("Cannot write file.");
		}
		
	}
	
	public BufferedImage getImage(String filename) {
		try {
			File file = new File(filename);
			BufferedImage image = ImageIO.read(file);
			int w = image.getWidth();
			int h = image.getHeight();
			BufferedImage aImage = new BufferedImage(w,h,BufferedImage.TYPE_4BYTE_ABGR);
			int[] pixels = new int[w*h];
			pixels = image.getRGB(0, 0, w, h, pixels, 0, w);
			for (int i = 0; i < pixels.length; i++) {
				pixels[i] &= 0x00FFFFFF;
			}
			aImage.setRGB(0, 0, w, h, pixels, 0, w);
			
			return image;									// Change return value to "aImage" to return an image with an alpha mask
		} catch (IOException ioe) {
			System.out.println("File not found.");
			return null;
		}
	}

	public int[] getPixels(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[] pixels = new int[w*h];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				pixels[i*h + j] = image.getRGB(i, j);
			}
		}
//		pixels = image.getRGB(0, 0, w, h, pixels, 0, w);
		return pixels;
	}

	public void writeImage(BufferedImage image, int[] pixels, String filename) {
		int w = image.getWidth();
		int h = image.getHeight();
		
		for (int x = 0; x < w; x++) {								
			for (int y = 0; y < h; y++) {
				image.setRGB(x, y, pixels[x*h+y]);
			}
		}

		RenderedImage rImage = (RenderedImage) image;
		try {
			File file = new File(filename);
			ImageIO.write(rImage, "png", file);
		} catch (IOException ioe) {
			System.out.println("File not found.");
		}
	}
	
	// outputs both a magnitude and phase image
	public void writeImage(BufferedImage magImage, Complex[] fourier, String filename) {
		int w = magImage.getWidth();
		int h = magImage.getHeight();
		BufferedImage phaseImage = new BufferedImage(w,h,magImage.getType());
		String phaseFilename = filename.replaceFirst(".png", "phase.png");
		
		double maxMag = 0;
		
		for (int i = 0; i < w*h; i++) {
			if (fourier[i].getMag() > maxMag) {
				maxMag = fourier[i].getMag();
			}
		}
		double magScale = (0xFF)/(Math.log10(1 + maxMag));
		double phaseScale = (0xFF)/(Math.log10(1 + (2*Math.PI)));
		
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				double mag = fourier[x * h + y].getMag();
				double phase = fourier[x * h + y].getPhase();
				phase += Math.PI;
				mag = Math.round(magScale*(Math.log10(1+mag)));
				phase = Math.round(phaseScale*(Math.log10(1+phase)));
				int greyMag = (int) mag << 16 | (int) mag << 8 | (int) mag;
				int greyPhase = (int) phase << 16 | (int) phase << 8 | (int) phase;
				magImage.setRGB((x + w/2)%w, (y + h/2)%h, (int) greyMag | 0xFF000000);
				phaseImage.setRGB((x + w/2)%w, (y + h/2)%h, (int) greyPhase | 0xFF000000);
			}
		}

		RenderedImage mImage = (RenderedImage) magImage;
		RenderedImage pImage = (RenderedImage) phaseImage;
		try {
			File mFile = new File(filename);
			File pFile = new File(phaseFilename);
			ImageIO.write(mImage, "png", mFile);
			ImageIO.write(pImage, "png", pFile);
		} catch (IOException ioe) {
			System.out.println("File not found.");
		}
	}
}
