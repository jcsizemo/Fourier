package com.sizemore.fourier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioToolkit {
	
	private static final String deskPath = "/Users/John/Desktop/";
	
	public enum FileExtensions {
		MP3(".mp3"),
		OGG(".ogg"),
		WAV(".wav");
		
		String extension;
		
		FileExtensions(String ext) {
			this.extension = ext;
		}
		
		String getExtension() {
			return this.extension;
		}
	}

	public static Complex[] getPulse(double gain, double fs, int numSamples) {
		Complex[] pulse = new Complex[numSamples];
		
		for (int i = 0; i < numSamples; i++) {
			if (i > numSamples/2) {
				pulse[i] = new Complex(0,0);
			}
			else {
				pulse[i] = new Complex(1,0);
			}
		}
		return pulse;
	}
	
	public static Complex[] getSinusoid(double amp, double fs, double freq, int samples) {
		Complex[] sinusoid = new Complex[samples];
		double ts = 1/fs;
		
		for (int i = 0; i < samples; i++) {
			double result = new Double(amp*Math.cos(2*Math.PI*freq*i*ts));
			sinusoid[i] = new Complex(result,0);
		}
		return sinusoid;
	}
	
	public static Complex[] fft(Complex[] signal) {
		int M = signal.length;
		Complex[] fourier = new Complex[M];
		
		for (int k = 0; k < M; k++) {
			double realSum = 0;
			double imagSum = 0;
			for (int m = 0; m < M; m++) {
				double exponential = 2*Math.PI*k*m/M;
				Complex mult = signal[m].mult(exponential);
				realSum += mult.getReal();
				imagSum += mult.getImag();
			}
			fourier[k] = new Complex(realSum,-imagSum);
			System.out.println(k);
		}
		return fourier;
	}
	
	public static Complex[] ifft(Complex[] fourier) {			// conjugate of fourier, FFT, conjugate again, multiply by 1/samples
		double M = fourier.length;
		double invM = 1/M;
		Complex[] timeDomainSignal = new Complex[fourier.length];
		for (int m = 0; m < M; m++) {
			double realSum = 0;
			double imagSum = 0;
			for (int k = 0; k < M; k++) {
				double exponential = 2*Math.PI*k*m/M;
				Complex mult = fourier[k].mult(exponential);
				realSum += mult.getReal();
				imagSum += mult.getImag();
			}
			timeDomainSignal[m] = new Complex(realSum*invM,-imagSum*invM);
		}
		return timeDomainSignal;
	}
	
	public static void writeCSV(Complex[] signal, double fs, boolean timeDomain, String filename)  {
		try {
			if (!filename.toLowerCase().endsWith(".csv")) {
				filename = filename + ".csv";
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/John/Desktop/" + filename));
			if (timeDomain) {
				for (int i = 0; i < signal.length; i++) {
					bw.write(Double.toString((double) i/(fs))+","+Double.toString(signal[i].getMag()*Math.cos(signal[i].getPhase())));
					bw.newLine();
				}		
			}
			else {
				for (int i = 0; i < signal.length; i++) {
					bw.write(Double.toString(signal[i].getMag()));
					bw.newLine();
				}
			}
			bw.close();
		}
		catch (IOException ioe) {
		}
	}
	
	public static Complex[] getNoise(double amp, int samples) {
		Complex[] noise = new Complex[samples];
		for (int i = 0; i < samples; i++) {
			noise[i] = new Complex(Math.random() * amp,0);
		}
		return noise;
	}
	
	public static Complex[] readSoundFile(String filename) {
		try {
			File file = new File(deskPath + filename);
			AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
			AudioFormat format = aff.getFormat();
			AudioInputStream ais = AudioSystem.getAudioInputStream(file);
			int numSamples = aff.getByteLength();
//			int powerOf2 = 1;
//			while (0 != numSamples/powerOf2) {
//				powerOf2 <<= 1;
//			}
			byte[] buffer = new byte[numSamples];
			int bytesToRead = numSamples/4;
			int totalBytesToRead = numSamples;
			int offset = 0;
			int bytesRead = 0;
			
			SourceDataLine line = AudioSystem.getSourceDataLine(format);
			ais = AudioSystem.getAudioInputStream(format, ais);
			line.open();
			line.start();
			while (bytesRead < totalBytesToRead) {
				ais.read(buffer, offset, bytesToRead);
				offset += bytesToRead;
				bytesRead += bytesToRead;
			}
			Complex[] soundBuf = new Complex[totalBytesToRead];
			for (int i = 0; i < totalBytesToRead; i++) {
				soundBuf[i] = new Complex(buffer[i],0);
			}
			
			return soundBuf;

		}
		catch (IOException ioe) {
			System.out.println("File not found.");
			return null;
		}
		catch (LineUnavailableException lue) {
			System.out.println("Line unavailable.");
			return null;
		}
		catch (UnsupportedAudioFileException uafe) {
			System.out.println("Unsupported file type.");
			return null;
		}
	}
	
}
