package edu.albany.cs.scoreFuncs;

import java.util.Arrays;

public class Stat {
	double[] data;
	int size;

	public Stat(double[] data) {
		double[] d = new double[data.length];
		for(int i = 0 ; i < data.length ; i++){
			d[i] = data[i];
		}
		this.data =  d;
		size = data.length;
	}

	double getMean() {
		double sum = 0.0;
		for (double a : data)
			sum += a;
		return sum / size;
	}
	
	double mean(double[] x) {
		double sum = 0.0;
		for (double a : x)
			sum += a;
		return sum / (x.length+0.0D);
	}

	double getVariance() {
		double mean = getMean();
		double temp = 0;
		for (double a : data)
			temp += (mean - a) * (mean - a);
		return temp / size;
	}

	double getStdDev() {
		return Math.sqrt(getVariance());
	}

	public double median() {
		Arrays.sort(data);
		if (data.length % 2 == 0) {
			return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
		} else {
			return data[data.length / 2];
		}
	}
	
	public double mad(){
		double[] abs = new double[data.length];
		for(int i = 0 ; i < data.length ; i++){
			abs[i] = Math.abs( data[i] - mean(data));
		}
		return mean(abs);
	}
}