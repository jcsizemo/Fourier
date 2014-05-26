package com.sizemore.picturefx;

public class Kernel {
	private int w;
	private int h;
	private float[] data;
	private float sum = 0;
	
	public Kernel(int w, int h, float[] data) {
		validate(w, h, data);
	}
	
	public float[] reflect() {
		int area = w*h;
		float[] reflect = new float[area];
		for (int i = 0; i <= (area-1)/2; i++) {
			reflect[i] = data[area-i-1];
			reflect[area-i-1] = data[i];
		}
		return reflect;
	}
	
	public float[] transpose() {
		float[] transpose = new float[data.length];
		int i = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				transpose[i++] = data[x*h + y];
			}
		}
		return transpose;
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}

	public float[] getData() {
		return data;
	}

	public float getSum() {
		return sum;
	}
	
	public void validate(int w, int h, float[] data) {
		if (w%2 == 0 || h%2 == 0) {
			throw new IllegalArgumentException("Both width and height must be odd numbers.");
		}
		if (w < 0 || h < 0) {
			throw new IllegalArgumentException("Both width and height must be positive");
		}
		if (data.length != w*h) {
			throw new IllegalArgumentException("Data array size must be equal to w*h");
		}
		this.w = w;
		this.h = h;
		this.data = data;
		for (float value : data) {
			this.sum += value;
		}
	}
	
	public void setKernelInformation(int w, int h, float[] data) {
		validate(w, h, data);
	}
}
