package com.sizemore.picturefx;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Vector;


public class FFT {
	
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
