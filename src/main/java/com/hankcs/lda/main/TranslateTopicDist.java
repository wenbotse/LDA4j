package com.hankcs.lda.main;

import java.util.Map;
import java.util.Map.Entry;

import com.hankcs.lda.LdaUtil;
import com.hankcs.lda.Vocabulary;
import com.travel.utils.ConfigTool;
import com.travel.utils.FileUtils;

public class TranslateTopicDist {

	static public int topic_term_num = Integer.parseInt(ConfigTool.props.getProperty("topic_term_num", "10"));
	static public int topic_term_num_is_need_val = Integer.parseInt(ConfigTool.props.getProperty("topic_term_num_is_need_val", "0"));
	public static void main(String args[]){
		Vocabulary voc = (Vocabulary) FileUtils.readOjbFromFile("Vocabulary");
		double[][] phi = (double[][]) FileUtils.readOjbFromFile("phi");
		
		Map<String, Double>[] topicDistArr = LdaUtil.translate(phi, voc, topic_term_num);
		int topicIdx = 1;
		for(Map<String,Double> topicDist : topicDistArr){
			System.out.print("topic i=" + (topicIdx++) +"	");
			
			for(Entry<String, Double> ent : topicDist.entrySet()){
				System.out.print(ent.getKey());
				if(topic_term_num_is_need_val==1)System.out.print("="+ent.getValue());
				System.out.print("	");
			}
			System.out.println();
		}
	}
}
