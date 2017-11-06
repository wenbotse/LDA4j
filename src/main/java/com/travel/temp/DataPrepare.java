package com.travel.temp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.travel.utils.FileUtils;
import com.travel.utils.SegmentTools;
import com.travel.utils.TravelUtils;

public class DataPrepare {
	private List<String> punctions = FileUtils.readLinesFromFile("punctuation.txt");
	private List<String> stopwords = FileUtils.readLinesFromFile("stopword.txt");
	private Set<String> punctionSet = new HashSet<String>();
	private Set<String> stopwordsSet = new HashSet<String>();
	public void init(){
		punctionSet.addAll(punctions);
		stopwordsSet.addAll(stopwords);
	}
	public String process(String content){
		StringBuilder sb = new StringBuilder();
		if(TravelUtils.isEmpty(content))return "";
		String[] terms = SegmentTools.nativePosSegments(content);
		for(String term : terms){
			String[] f = term.split("/");
			if(f.length!=2)continue;
			term = f[0];
			if(TravelUtils.isEmpty(term))continue;
			if(punctionSet.contains(term))continue;
			if(stopwordsSet.contains(term))continue;
			if(!TravelUtils.isChineseSentence(term, 0.9))continue;
			sb.append(term).append(" ");
		}
		System.out.println(sb.toString());
		return sb.toString();
	}
	public static void main(String args[]){
		new DataPrepare().process("这里的风景很美");
	}
}
