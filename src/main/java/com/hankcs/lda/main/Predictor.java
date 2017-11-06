package com.hankcs.lda.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hankcs.lda.Corpus;
import com.hankcs.lda.LdaGibbsSampler;
import com.hankcs.lda.LdaUtil;
import com.hankcs.lda.Vocabulary;
import com.travel.data.DoublePair;
import com.travel.temp.DataPrepare;
import com.travel.utils.ConfigTool;
import com.travel.utils.FileUtils;
import com.travel.utils.TravelUtils;

public class Predictor {
	static public int show_topic_num = Integer.parseInt(ConfigTool.props.getProperty("show_topic_num", "10"));
	static public int is_show_topic_dist = Integer.parseInt(ConfigTool.props.getProperty("is_show_topic_dist","1"));
	private Vocabulary voc;
	private double[][] phi;
	Map<String, Double>[] topicMapArray;
	public Predictor(){
		phi = (double[][]) FileUtils.readOjbFromFile("phi");
		voc = (Vocabulary) FileUtils.readOjbFromFile("Vocabulary");
		topicMapArray = LdaUtil.translate(phi, voc, 10);
	}
	public double[] predict(String file) throws IOException{
		int[] document = Corpus.loadDocument(file, voc);
		long start = System.currentTimeMillis();
		double[] tp = LdaGibbsSampler.inference(phi, document);
		long end = System.currentTimeMillis();
		System.out.println("predict cost time=" + (end - start));
		if (is_show_topic_dist == 1) {
			debug(tp);
		}
		
		return tp;
	}
	public List<String> debug(double[] tp){
		long start = System.currentTimeMillis();
		List<String> results = new LinkedList<String>();
		{
			Map<String, Double> tpProbMap = new HashMap<String, Double>();
			for (int i = 0; i < tp.length; i++) {
				tpProbMap.put("" + i, tp[i]);
			}
			List<DoublePair> pairs = TravelUtils.sortDoubleMapByValue(tpProbMap);
			int[] idxs = new int[pairs.size()];
			int i = 0;
			for (DoublePair p : pairs) {
				idxs[i++] = Integer.parseInt(p.k);
				System.out.println("topic " + p.k + " =" + p.v);
				results.add("topic " + p.k + " =" + TravelUtils.processDoubleScale(p.v,6));
				if(i==10)break;
			}
			Map<String, Double>[] topics = LdaUtil.translate(idxs, phi, voc, 20, topicMapArray);
			for (int j = 0; j < show_topic_num; j++) {
				System.out.println("No."+ j +" topic "+idxs[j]);
				results.add("<font color=\"red\">No."+ j +" topic "+idxs[j]+"</font>");
				results.addAll(LdaUtil.explain(topics[j]));
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("debug cost time=" + (end - start));
		return results;
	}
	private DataPrepare dataPrepare = new DataPrepare();
	public List<String> predictTopic(String content) throws IOException{
		int[] document = Corpus.convertContent2Document(dataPrepare.process(content), voc);
		long start = System.currentTimeMillis();
		double[] tp = LdaGibbsSampler.inference(phi, document);
		long end = System.currentTimeMillis();
		System.out.println("cost time=" + (end - start));
		return debug(tp);
	}
	public static void main(String args[]) throws IOException {
		new Predictor().predict("doc.txt");
	}
}
