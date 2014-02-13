package uk.co.benjaminelliott.spectrogramandroid;

import android.util.Log;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class BandpassButterworth {
	
	private final int sampleRate;
	private final int order; 
	private final double minFreq;	
	private final double maxFreq;
	private final double dcGain; //DC gain
	private DoubleFFT_1D dfft1d; //DoubleFFT_1D constructor must be supplied with an 'n' value, where n = data size

	
	BandpassButterworth(int sampleRate, int order, double minFreq, double maxFreq, double dcGain) {
		this.sampleRate = sampleRate;
		this.order = order;
		this.minFreq = minFreq;
		this.maxFreq = maxFreq;
		this.dcGain = dcGain;
		
	}
	
	private double[] generateGain(int length) {
		//find low-pass filter with cut-off at (maxFreq-minFreq)/2 then shift its 
		//centre to (minFreq+maxFreq)/2
		double[] gain = new double[length];
		double cutoff = (maxFreq-minFreq); // by not dividing by 2, effectively multiply by 2 because of imaginary component for each bin
		int bins = length; //number of frequency bins 
		double binWidth = ((double)sampleRate) / ((double)bins);
		double centre = (minFreq+maxFreq)/(2*binWidth);
		Log.d("","Bin width: "+binWidth+" bins: "+bins+" cutoff: "+cutoff+" centre: "+centre+" order: "+order+" length: "+length);
		for (int i = 0; i < length; i++) {
			gain[i] = dcGain/Math.sqrt((1+Math.pow((double)binWidth*((double)i-centre)/cutoff, 2.0*order)));
		}
		return gain;
		
	}

	
	void applyBandpassFilter(short[] samples) {
		int length = samples.length;
		System.out.println();
		System.out.println();
		dfft1d = new DoubleFFT_1D(length);
		double[] fftSamples = new double[length*2];
		double[] gain = generateGain(length);
		int d = 0; while (true){for (double g : gain) {System.out.print(" "+g+" "); d++; if (d% 50 == 0) {System.out.println(); System.out.print(d+"   ");}} break;}
		for (int i = 0; i < length; i++) fftSamples[i] = samples[i];
		dfft1d.realForward(fftSamples);
		for (int i = 0; i < length; i++) fftSamples[i] *= gain[i];
		dfft1d.realInverse(fftSamples, true);
		for (int i = 0; i < length; i++) samples[i] = (short)fftSamples[i];

	}
	

}