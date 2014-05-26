package RevExtendBufferedImage;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PictureFX {
	
	public static void main(String[] args) {
		new PictureFX();
	}
	
	private PictureFX() {
		String path = "/Users/John/Desktop/josh.png";
		ConvolveImage cImage = null;
		try {
			ColorModel cm;
			WritableRaster raster;
			BufferedImage image = ImageIO.read(new File(path));
			cm = image.getColorModel();
			raster = image.getRaster();
			cImage = new ConvolveImage(cm, raster, image.isAlphaPremultiplied(), null);
		}
		catch (IOException ioe) {
			System.out.println("Cannot find image.");
		}
		
		int kernelWidth = 3;
		int kernelHeight = 3;
		// float[] kernelData = {-1,-1,-1,-1,8,-1,-1,-1,-1}; AWESOME 8-BIT KERNEL
		 float[] kernelData = {0.2f,0.2f,0.2f,0.2f,0.2f,0.2f,0.2f,0.2f,0.2f};
		Kernel k = new Kernel(kernelWidth, kernelHeight, kernelData);
		cImage.setKernel(k);
		
		long start = System.nanoTime();
		ConvolveImage convolved = cImage.convolve();
		long end = System.nanoTime();
		long deltaTime = end-start;
		float secs = (float) deltaTime / 1000000000;
		System.out.println(secs);
		
		ConvolveImage.printImage(convolved, "/Users/John/Desktop/joshFiltered7.png");
		
	}

}
