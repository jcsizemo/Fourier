package RevExtendBufferedImage;

import java.awt.image.BufferedImage;
import java.util.List;

public class ConvolveThread extends Thread {
	
	BufferedImage image;
	char letter;
	long length;
	List<ConvolveThread> threadList;
	
	public ConvolveThread(char letter, long length, List<ConvolveThread> threadList) {
		this.letter = letter;
		this.length = length;
		this.threadList = threadList;
		threadList.add(this);
		this.start();
	}
	
	public void run() {
		while(length > 0) {
			System.out.println(letter);
			length--;
		}
		threadList.remove(this);
	}
	

}
