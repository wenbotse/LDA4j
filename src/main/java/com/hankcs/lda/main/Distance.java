package com.hankcs.lda.main;

import java.io.IOException;

public class Distance {

	public double distance(double[] td1, double[] td2){
		if(td1.length != td2.length)return 0.;
		double dis = 0.;
		for (int i = 0; i < td1.length; i++) {
			dis += (td1[i] * Math.log(td1[i] / td2[i]));
		}
		return dis;
	}
	
	
	public static void main(String args[]) throws IOException{
		
		Predictor predictor = new Predictor();
		double[] td1 = predictor.predict("doc.txt");
		double[] td2 = predictor.predict("doc.txt.2");
		System.out.println(new Distance().distance(td1, td2));
	}
}
