package com.sizemore.fourier;

import java.awt.image.BufferedImage;
import java.util.Date;

public class ImageDrawer {

	public static void main(String[] args) {
		System.out.println(new Date());
		new ImageDrawer();
		System.out.println(new Date());
	}
	
	public ImageDrawer(){
		ImageToolkit toolkit = new ImageToolkit();
		BufferedImage image = toolkit.getImage("/Users/John/Desktop/me.jpg");
		int[] pixels = toolkit.getPixels(image);

		//pixels = Convolve.convolve(pixels, image.getWidth(), image.getHeight(), 3, Convolve.OP_EDGE_DETECT);
		
		Complex[] fourier = FFT.ffft(image.getWidth(), image.getHeight(), pixels);
		pixels = FFT.iffft(fourier);
		toolkit.writeImage(image, pixels, "/Users/John/Desktop/output2.png");
		
		
		
	}
}
