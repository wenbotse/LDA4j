package com.hankcs.lda.main;

import java.util.Map;

import com.hankcs.lda.Corpus;
import com.hankcs.lda.LdaGibbsSampler;
import com.hankcs.lda.LdaUtil;
import com.travel.utils.ConfigTool;
import com.travel.utils.FileUtils;

public class ParallelTrainJob {
	public static void main(String args[]) throws Exception {
		int topic_num = Integer.parseInt(ConfigTool.props.getProperty("topic_num", "30"));
		// 1. Load corpus from disk
		Corpus corpus = Corpus.loadByLine("rex_process_1.txt");
		// 2. Create a LDA sampler
		LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(
				corpus.getDocument(), corpus.getVocabularySize());
		// 3. Train it
		ldaGibbsSampler.parallelGibbs(topic_num);
		// 4. The phi matrix is a LDA model, you can use LdaUtil to explain it.
		double[][] phi = ldaGibbsSampler.getPhi();
		Map<String, Double>[] topicMap = LdaUtil.translate(phi,
				corpus.getVocabulary(), 10);
		LdaUtil.explain(topicMap);
		// 5. TODO:Predict. I'm not sure whether it works, it is not stable.
		FileUtils.delete("Vocabulary");
		FileUtils.writeObj2File("Vocabulary", corpus.getVocabulary());
		FileUtils.delete("phi");
		FileUtils.writeObj2File("phi", phi);
		int[] document = Corpus.loadDocument("doc.txt",
				corpus.getVocabulary());
		double[] tp = LdaGibbsSampler.inference(phi, document);
		for (int i = 0; i < tp.length; i++) {
			System.out.println("topic " + i + " prob:" + tp[i]);
		}
		Map<String, Double> topic = LdaUtil.translate(tp, phi,
				corpus.getVocabulary(), 10);
		LdaUtil.explain(topic);
	}
}
