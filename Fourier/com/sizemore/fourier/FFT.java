// John Sizemore, March 2011

package com.sizemore.fourier;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Vector;


public class FFT {
	
	//cooley-tukey algorithm, faster fast fourier transform
	public static Complex[] ffft(int M, int N, int[] pixels) {
		
		if (pixels.length == 1) {
			Complex[] c = new Complex[1];
			c[0] = new Complex(pixels[0],0);
			return c;
		}
		
		int closestPowerOf2M = 1 << (int) Math.ceil(Math.log(M) / Math.log(2));
		int closestPowerOf2N = 1 << (int) Math.ceil(Math.log(N) / Math.log(2));
		int newsize = closestPowerOf2M*closestPowerOf2N;
		int[] pixelsPowerOf2 = new int[newsize];
		System.arraycopy(pixels,0,pixelsPowerOf2,0,pixels.length);
		
		Complex[] items = new Complex[newsize/2];
		for (int i = 0; i < items.length; i++) {
			items[i] = new Complex(pixelsPowerOf2[2*i],0);
		}
		Complex[] q = ffft(items);
		for (int i = 0; i < items.length; i++) {
			items[i] = new Complex(pixelsPowerOf2[2*i + 1],0);
		}
		Complex[] r = ffft(items);
		Complex[] fourier = new Complex[newsize];
		
		for (int i = 0; i < newsize/2; i++) {
			double t = -2*i*Math.PI/newsize;
			Complex c = new Complex(Math.cos(t), Math.sin(t));
			fourier[i] = q[i].add(c.mult(r[i]));
			fourier[i + newsize/2] = q[i].sub(c.mult(r[i]));
		}
		
		return fourier;
	}
	
	//overload method for use with the complex numbers in the recursion
	public static Complex[] ffft(Complex[] pixels) {
		
		if (pixels.length == 1) {
			return new Complex[] {pixels[0]};
		}
		
		Complex[] items = new Complex[pixels.length/2];
		for (int i = 0; i < items.length; i++) {
			items[i] = pixels[2*i];
		}
		Complex[] q = ffft(items);
		for (int i = 0; i < items.length; i++) {
			items[i] = pixels[2*i+1];
		}
		Complex[] r = ffft(items);
		
		Complex[] fourier = new Complex[pixels.length];
		
		for (int i = 0; i < pixels.length/2; i++) {
			double t = -2*i*Math.PI/pixels.length;
			Complex c = new Complex(Math.cos(t), Math.sin(t));
			fourier[i] = q[i].add(c.mult(r[i]));
			fourier[i + pixels.length/2] = q[i].sub(c.mult(r[i]));
		}
		
		return fourier;
	}

	// the inverse (faster) fast fourier transform, cooley-tukey
	public static int[] iffft(Complex[] fourier) {
		
		for (int i = 0; i < fourier.length; i++) {
			fourier[i] = fourier[i].conj();
		}
		
		fourier = ffft(fourier);
		
		for (int i = 0; i < fourier.length; i++) {
			fourier[i] = fourier[i].conj();
		}
		
		for (int i = 0; i < fourier.length; i++) {
			fourier[i] = fourier[i].mult((double) 1/fourier.length);
		}
		
		int[] pixels = new int[fourier.length];
		
		// instead of get real, get it from the mag/phase
		for (int i = 0; i < fourier.length; i++) {
//			pixels[i] = (int) fourier[i].getReal();
			double newMag = fourier[i].getMag()*Math.cos(fourier[i].getPhase());
			newMag = Math.round(newMag);
			pixels[i] = (int) newMag;
		}
		return pixels;
	}
	
	// a little experiment with the "fourier clock"
	public static Complex[] newFFT(int M, int N, int[] pixels) {
		Complex[] fourier = new Complex[M*N];
		double angleFactor = 0;
		boolean isSquare = false;
		double angleMod = 0;
		if (M == N) {
			angleFactor = 2*Math.PI/M;	// variable angle is u*x + v*y
			isSquare = true;
			angleMod = M;
		}
		else {
			angleFactor = 2*Math.PI/(M*N); // variable angle is N*u*x + M*v*y
			angleMod = M*N;
		}
		
		if (isSquare) {
			for (int i = 0; i < M; i++) {
				double angle = i*angleFactor;
			}
		}
		else {
			for (int i = 0; i < M*N; i++) {
				double angle = i*angleFactor;
			}
		}
		
		return fourier;
	}
	
	// ye olde fashioned way
    public static Complex[] fft(int M, int N, int[] pixels) {
        
        Complex[] fourier = new Complex[M*N];
        int u = 0;
        int v = 0;
        Vector<Double> degreeList = new Vector<Double>();
       
        for (int a = 0; a < N*M; a++) {
            double reSum = 0;
            double imSum = 0;
            for (int y = 0; y < N; y++) {
                for (int x = 0; x < M; x++) {
                    double xVal = (double) (u*x)/M;            // repeats every M/2 (0 to 2*pi)
                    double yVal = (double) (v*y)/N;
                    double angle = 2*Math.PI*(xVal+yVal);
                    angle = -(angle%(2*Math.PI));
                    
					double degrees = Math.toDegrees(angle);
					BigDecimal bd = new BigDecimal(degrees,
							MathContext.DECIMAL32);
					bd.setScale(5, BigDecimal.ROUND_HALF_UP);
					degrees = bd.doubleValue() % 360;
					degrees = (Math.abs(degrees) < 1) ? 0 : degrees;
					if (!degreeList.contains(degrees)) {
						degreeList.add(degrees);
					}
					
                    reSum += ((double) pixels[x*N + y])*Math.cos(angle);
                    imSum += ((double) pixels[x*N + y])*Math.sin(angle);
                }
                fourier[a] = new Complex(reSum,imSum);
            }
           
            if ((v+1) >= N) {
                v = 0;
                u++;
            }
            else {
                v++;
            }
        }
        return fourier;
    }
	
    // ye olde fashioned way
	public static int[] ifft(int M, int N, Complex[] fourier) {

		int[] inverse = new int[M * N];
		double inv = 1 / ((double) M * (double) N);
		int u = 0;
		int v = 0;

		for (int a = 0; a < N * M; a++) {
			double reSum = 0;
			double imSum = 0;
			for (int y = 0; y < N; y++) {
				for (int x = 0; x < M; x++) {
					double xVal = (double) (u * x) / M;
					double yVal = (double) (v * y) / N;
					double angle = 2 * Math.PI * (xVal + yVal);
					reSum += (fourier[x * N + y]).getMag()* Math.cos(angle + fourier[x * N + y].getPhase());
					imSum += (fourier[x * N + y]).getMag()* Math.sin(angle + fourier[x * N + y].getPhase());
				}
				Complex c = new Complex(reSum*inv, inv*imSum);
				double newMag = c.getMag()*Math.cos(c.getPhase());
				newMag = Math.round(newMag);
				inverse[a] = (int) newMag;
			}

			if ((v + 1) >= N) {
				v = 0;
				u++;
			} else {
				v++;
			}
		}

		return inverse;
	}
	
}
