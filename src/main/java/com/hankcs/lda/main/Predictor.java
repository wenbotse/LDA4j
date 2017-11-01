package com.hankcs.lda.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hankcs.lda.Corpus;
import com.hankcs.lda.LdaGibbsSampler;
import com.hankcs.lda.LdaUtil;
import com.hankcs.lda.Vocabulary;
import com.travel.data.DoublePair;
import com.travel.utils.FileUtils;
import com.travel.utils.TravelUtils;

public class Predictor {

	public static void main(String args[]) throws IOException {
		Vocabulary voc = (Vocabulary) FileUtils.readOjbFromFile("Vocabulary");
		int[] document = Corpus.loadDocument("doc.txt", voc);
		double[][] phi = (double[][]) FileUtils.readOjbFromFile("phi");
		double[] tp = LdaGibbsSampler.inference(phi, document);
		Map<String, Double> tpProbMap = new HashMap<String, Double>();
		for (int i = 0; i < tp.length; i++) {
			// System.out.println("topic " + i + " prob:" + tp[i]);
			tpProbMap.put("" + i, tp[i]);
		}
		List<DoublePair> pairs = TravelUtils.sortDoubleMapByValue(tpProbMap);
		int[] idxs = new int[pairs.size()];
		int i = 0;
		for (DoublePair p : pairs) {
			idxs[i++] = Integer.parseInt(p.k);
			System.out.println("topic " + p.k + " =" + p.v);
		}

		Map<String, Double>[] topics = LdaUtil.translate(idxs, phi, voc, 10);
		for (int j = 0; j < 5; j++) {
			System.out.println("topic "+idxs[j]);
			LdaUtil.explain(topics[j]);
		}
	}
}
