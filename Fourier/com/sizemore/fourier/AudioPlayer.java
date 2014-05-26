package com.sizemore.fourier;

public class AudioPlayer {
	
	public static void main(String[] args) {
		new AudioPlayer();
	}
	
	public AudioPlayer() {
		double fs = 44100;
		double amplitude = 10;
		int numSamples = 2*1024;
		
		Complex[] cosineWave = AudioToolkit.getSinusoid(amplitude, fs, 60, numSamples);
//		Complex[] sound = SignalsToolkit.readSoundFile("myvoice.wav");
		Complex[] fourier = AudioToolkit.fft(cosineWave);
		cosineWave = AudioToolkit.ifft(fourier);
		AudioToolkit.writeCSV(cosineWave, fs, true, "tester2.csv");
		
	}
}
