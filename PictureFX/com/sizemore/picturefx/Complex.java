package com.sizemore.picturefx;


/* John Sizemore
 * Complex.java
 * 
 * Description:
 * This class defines a complex number. The constructor of this object is overloaded (meaning more than one constructor
 * method) to create an object based on either Cartesian or polar numbers. The first constructor takes a real and imaginary
 * number in Cartesian coordinates while the second takes a magnitude and phase value.
 * 
 * FUNCTIONS:
 * 
 * getMag() - returns magnitude
 * getPhase() - returns phase
 * conj() - returns conjugate
 * getReal() - returns real part
 * getImag() - returns imaginary part
 * mult() - multiplies two complex numbers
 * div() - divides two complex numbers
 */

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
	
	public Complex(float mag, float phase) {
		this.mag = mag;
		this.phase = phase;
		this.real = mag*Math.cos(phase);
		this.imag = mag*Math.sin(phase);
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
	
	public Complex mult(Complex b) {
		Complex a = this;
		return new Complex(a.mag * b.mag, a.phase + b.phase);
	}
	
	public Complex divide(Complex b) {
		Complex a = this;
		return new Complex(a.mag / b.mag, a.phase - b.phase);
	}

}
