package RevNoThreads;

import java.awt.image.BufferedImage;
//import java.awt.image.ConvolveOp;
//import java.io.File;
//import java.io.IOException;
//import java.util.Arrays;
//
//import javax.imageio.ImageIO;

public class PictureFX {
	
	public static void main(String[] args) {
		new PictureFX();
	}
	
	private PictureFX() {
		int kernelWidth = 3;
		int kernelHeight = 3;
		float[] kernelData = {-1,2,-3,4,-5,-6,7,8,-9};
		Kernel k = new Kernel(kernelWidth, kernelHeight, kernelData);
		String path = "/Users/John/Desktop/courtneyScaled.png";
		ConvolveImage cImage = new ConvolveImage(path, k, true);
		long start = System.nanoTime();
		BufferedImage image = cImage.convolve();
		long end = System.nanoTime();
		long deltaTime = end-start;
		float secs = (float) deltaTime / 1000000000;
		System.out.println(secs);
		ConvolveImage.printImage(image, "C:\\Users\\John Sizemore\\Desktop\\output.png");
		
//		try {
//			BufferedImage src = ImageIO.read(new File(path));
//			BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
//			java.awt.image.Kernel k2 = new java.awt.image.Kernel(3, 3, kernelData);
//			ConvolveOp cOp = new ConvolveOp(k2, ConvolveOp.EDGE_ZERO_FILL, null);
//			dst = cOp.filter(src, dst);
//			int[] pix = new int[dst.getWidth()*dst.getHeight()];
//			Arrays.fill(pix, 0xFF000000);
//			for (int i = 0; i < pix.length; i++) {
//				pix[i] |= dst.getRGB(i % dst.getWidth(), i / dst.getWidth());
//			}
//			dst.setRGB(0, 0, dst.getWidth(), dst.getHeight(), pix, 0, dst.getWidth());
//			ConvolveImage.printImage(dst, "C:\\Users\\John Sizemore\\Desktop\\outputJava.png");
//			int w = cImage.getWidth();
//			int h = cImage.getHeight();
//			int[] myPix = image.getRGB(0, 0, w, h, new int[w*h], 0, w);
//			int[] javaPix = dst.getRGB(0, 0, w, h, new int[w*h], 0, w);
//			for (int i = 0; i < myPix.length; i++) {
//				if (myPix[i] != javaPix[i]) {
//					System.out.println(myPix[i] + " != " + javaPix[i]);
//					int r = (myPix[i] & 0xFF0000) >> 16;
//					int g = (myPix[i] & 0xFF00) >> 8;
//					int b = myPix[i] & 0xFF;
//					r = (javaPix[i] & 0xFF0000) >> 16;
//					g = (javaPix[i] & 0xFF00) >> 8;
//					b = javaPix[i] & 0xFF;
//					int griddle = 1;
//				}
//			}
//		}
//		catch (IOException ioe) {}
		
	}

}
