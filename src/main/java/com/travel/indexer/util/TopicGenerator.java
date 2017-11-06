package com.travel.indexer.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.travel.data.DoublePair;
import com.travel.utils.ConfigTool;
import com.travel.utils.FileUtils;
import com.travel.utils.TravelUtils;

public class TopicGenerator {

	private Vocabulary voc;
	private double[][] phi;
	private int iter;
	private int maxTpNum;
	private double minTpProb;
	public void afterPropertiesSet() throws Exception {
		phi = (double[][]) FileUtils.readOjbFromFile("config/phi");
		voc = (Vocabulary) FileUtils.readOjbFromFile("config/Vocabulary.re");
		iter = Integer.parseInt(ConfigTool.props.getProperty("infer_iteration", "1000"));
		maxTpNum = Integer.parseInt(ConfigTool.props.getProperty("max_tp_num", "3"));
		
	}
	private double[] generator(String[] words){
		int[] doc = loadDocument(words, voc);
		double[] tDist = inference(2.0, 0.5, phi, doc);
		return tDist;
	}
	public List<DoublePair> generatorTpDist(String[] words){
		double[] tDist = generator(words);
		Map<String, Double> map = new HashMap<String, Double>();
		int i = 0;
		for(double tp : tDist){
			i++;
			map.put("tp_"+i, TravelUtils.processDoubleScale(tp, 4));
		}
		List<DoublePair> results = TravelUtils.sortDoubleMapByValue(map);
		return tpChooser(results);
	}
	private List<DoublePair> tpChooser(List<DoublePair> tpDistPairs){
		int idx = -1;
		for(DoublePair p : tpDistPairs){
			if(idx >= maxTpNum)break;
			if(p.v < minTpProb)break;
			idx++;
		}
		if(idx < 0)return null;
		return tpDistPairs.subList(0, idx);
	}
	private static int[] loadDocument(String[] words, Vocabulary vocabulary){
		List<Integer> wordList = new LinkedList<Integer>();
		for (String word : words) {
			if (word.trim().length() < 2)
				continue;
			Integer id = vocabulary.getId(word);
			if (id != null)
				wordList.add(id);
		}
		int[] result = new int[wordList.size()];
		int i = 0;
		for (Integer integer : wordList) {
			result[i++] = integer;
		}
		return result;
	}
	
	
	public double[] inference(double alpha, double beta, double[][] phi,
			int[] doc) {
		int K = phi.length;
		int V = phi[0].length;

		int[][] nw = new int[V][K];
		int[] nd = new int[K];
		int[] nwsum = new int[K];
		int ndsum = 0;

		int N = doc.length;
		int[] z = new int[N];
		for (int n = 0; n < N; n++) {
			int topic = (int) (Math.random() * K);
			z[n] = topic;
			nw[doc[n]][topic]++;
			nd[topic]++;
			nwsum[topic]++;
		}
		ndsum = N;
		for (int i = 0; i < iter; i++) {
			for (int n = 0; n < z.length; n++) {
				int topic = z[n];
				nw[doc[n]][topic]--;
				nd[topic]--;
				nwsum[topic]--;
				ndsum--;

				double[] p = new double[K];
				for (int k = 0; k < K; k++) {
					p[k] = phi[k][doc[n]] * (nd[k] + alpha)
							/ (ndsum + K * alpha);
				}
				for (int k = 1; k < p.length; k++) {
					p[k] += p[k - 1];
				}
				double u = Math.random() * p[K - 1];
				for (topic = 0; topic < p.length; topic++) {
					if (u < p[topic])
						break;
				}

				nw[doc[n]][topic]++;
				nd[topic]++;
				nwsum[topic]++;
				ndsum++;
				z[n] = topic;
			}
		}
		double[] theta = new double[K];
		for (int k = 0; k < K; k++) {
			theta[k] = (nd[k] + alpha) / (ndsum + K * alpha);
		}
		return theta;
	}
}

class Vocabulary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3269879031768769858L;
	Map<String, Integer> word2idMap;
	String[] id2wordMap;

	public Vocabulary() {
		word2idMap = new TreeMap<String, Integer>();
		id2wordMap = new String[1024];
	}

	public Integer getId(String word) {
		return getId(word, false);
	}

	public String getWord(int id) {
		return id2wordMap[id];
	}

	public Integer getId(String word, boolean create) {
		Integer id = word2idMap.get(word);
		if (!create)
			return id;
		if (id == null) {
			id = word2idMap.size();
		}
		word2idMap.put(word, id);
		if (id2wordMap.length - 1 < id) {
			resize(word2idMap.size() * 2);
		}
		id2wordMap[id] = word;

		return id;
	}

	private void resize(int n) {
		String[] nArray = new String[n];
		System.arraycopy(id2wordMap, 0, nArray, 0, id2wordMap.length);
		id2wordMap = nArray;
	}

	private void loseWeight() {
		if (size() == id2wordMap.length)
			return;
		resize(word2idMap.size());
	}

	public int size() {
		return word2idMap.size();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < id2wordMap.length; i++) {
			if (id2wordMap[i] == null)
				break;
			sb.append(i).append("=").append(id2wordMap[i]).append("\n");
		}
		return sb.toString();
	}
}

