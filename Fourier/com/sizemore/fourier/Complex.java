package com.sizemore.fourier;

public class Complex {
	
	private double real;
	private double imag;
	private double mag;
	private double phase;
	
	public Complex(double re, double im) {
		this.real = re;
		this.imag = im;
		this.mag = Math.hypot(this.real, this.imag);
		this.phase = Math.atan2(this.imag, this.real);
	}
	
	public double getMag() {
		return mag;
	}
	
	public double getPhase() {
		return phase;
	}
	
	public Complex conj(){
		return new Complex(real,-imag);
	}
	
	public double getReal(){
		return real;
	}
	
	public double getImag(){
		return imag;
	}
	
	public Complex add(Complex c) {
		return new Complex(this.real + c.real, this.imag + c.imag);
	}
	
	public Complex sub(Complex c) {
		return new Complex(this.real - c.real, this.imag - c.imag);
	}
	
	public Complex mult(double d) {
		return new Complex(this.real*d,this.imag*d);
	}
	
	public Complex mult(Complex c) {
		double real = this.real*c.real - this.imag*c.imag;
		double imag = this.real*c.imag + this.imag*c.real;
		return new Complex(real,imag);
	}
	
	public Complex test(double exponential) {
		double real = Math.cos(exponential);
		double imag = Math.sin(exponential);
		double magExp = Math.hypot(real, imag);
		double phaseExp = Math.atan2(imag,real);
		double mag = getMag()*magExp;
		double phase = getPhase()+phaseExp;
		return new Complex(mag*Math.cos(phase),mag*Math.sin(phase));
	}
	
	public void scaleMag(){
		
	}

}
