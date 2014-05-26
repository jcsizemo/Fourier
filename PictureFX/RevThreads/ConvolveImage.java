package RevThreads;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		List<ConvolveThread> threadList = new ArrayList<ConvolveThread>();
		BufferedImage convolvedImage = new BufferedImage(w,h,BufferedImage.TYPE_4BYTE_ABGR);
		for (int i = 0; i < h; i++) {
			new ConvolveThread(convolvedImage, kernel, pixels, i*w, threadList, keepBrightness);
		}
		
		while (threadList.size() > 0) {
		}
		
		
		
		
		return convolvedImage;
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
