package com.travel.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.travel.data.DoublePair;
import com.travel.data.Pair;

public class TravelUtils {
	final static Log log = LogFactory.getLog(TravelUtils.class);
	static public boolean isEquals(String val1,String val2){
		if(val1==null && val2==null)return true;
		if(val1==null || val2==null)return false;
		if(val1.equals(val2))return true;
		return false;
	}
	static public boolean isEquals(Integer val1,Integer val2){
		if(val1==null && val2==null)return true;
		if(val1==null || val2==null)return false;
		if(val1.intValue()==val2.intValue())return true;
		return false;
	}
	static public boolean isEquals(Double val1,Double val2){
		if(val1==null && val2==null)return true;
		if(val1==null || val2==null)return false;
		if(val1.intValue()==val2.intValue())return true;
		return false;
	}
	static public int parseInt(String val){
		if(TravelUtils.isEmpty(val))return 0;
		try{
			return Integer.parseInt(val);
		}catch(Exception e){
		}
		return 0;
	}
	/**
	 * @param to
	 * @param from
	 * @return
	 */
	static public List<String> mergeList(List<String> to, List<String> from) {
		Set<String> set = new TreeSet<String>();
		for(String s:to){
			if(TravelUtils.isNotEmpty(s))set.add(s);
		}
		for(String s:from){
			if(TravelUtils.isNotEmpty(s))set.add(s);
		}
		List<String> list = new ArrayList<String>(set.size());
		list.addAll(set);
		return list;
	}
	static public boolean isEmpty(String val){
		if(val==null)return true;
		val = val.trim();
		if(val.isEmpty())return true;
		return false;
	}
	static public boolean isNotEmpty(String val){
		return !isEmpty(val);
	}
	static public String lastSubString(String val,int n){
		val = trim(val);
		int len = val.length();
		if(n>len)return val;
		val = val.substring(len-n);
		return val;
	}
	static public List<String> mergeArray(String[] to, String[] from) {
		List<String> tList = Arrays.asList(to);
		List<String> fList = Arrays.asList(from);
		return mergeList(tList, fList);
	}
	static public String list2Str(List<String> list, String delemiter) {
		StringBuilder sb = new StringBuilder();
		for (int i=0,len=list.size();i<len;i++) {
			String s = list.get(i);
			if(TravelUtils.isEmpty(s))continue;
			sb.append(s);
			if(i!=len-1)sb.append(delemiter);
		}
		return sb.toString();
	}
	static public String list2Str(List<String> list,String delemiter,int limitLength){
		StringBuilder sb = new StringBuilder();
		for (int i=0,len=list.size();i<len;i++) {
			String s = list.get(i);
			if(TravelUtils.isEmpty(s))continue;
			if((sb.length()+s.length())>limitLength)break;
			sb.append(s);
			if(i!=len-1)sb.append(delemiter);
		}
		return sb.toString();
	}
	static public String list2UniqueLimitStr(List<String> list,String delemiter,int limitLength){
		if(list.isEmpty())return "";
		Set<String> set = new HashSet<String>(list.size()*2);
		set.addAll(list);
		list.clear();
		list.addAll(set);
		StringBuilder sb = new StringBuilder();
		for (int i=0,len=list.size();i<len;i++) {
			if(sb.length()>limitLength)break;
			String s = list.get(i);
			if(TravelUtils.isEmpty(s))continue;
			sb.append(s);
			if(i!=len-1)sb.append(delemiter);
		}
		return sb.toString();
	}
	static public String list2UniqueStr(List<String> list, String delemiter) {
		if(list.isEmpty())return "";
		Set<String> set = new HashSet<String>(list.size()*2);
		set.addAll(list);
		list = new LinkedList<String>();
		list.addAll(set);
		StringBuilder sb = new StringBuilder();
		for (int i=0,len=list.size();i<len;i++) {
			String s = list.get(i);
			if(TravelUtils.isEmpty(s))continue;
			sb.append(s);
			if(i!=len-1)sb.append(delemiter);
		}
		return sb.toString();
	}

	/**
	 * 使用Pattern和Matcher类的方法
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNumber(String s) {

		String regex = "^[1-9][0-9]*\\.[0-9]+$|^[1-9][0-9]*$|^0+\\.[0-9]+$";
		Pattern pattern = Pattern.compile(regex);
		char c = s.charAt(0);
		if (c == '+' || c == '-') {
			s = s.substring(1);
		}
		Matcher matcher = pattern.matcher(s);
		boolean bool = matcher.matches();
		if (bool) {
			return true;
		} else {
			return false;
		}
	}

	static public Double parseDouble(String val) {
		if (val == null || val.isEmpty() || !isNumber(val))
			return 0.;
		return Double.parseDouble(val);
	}
	static public Double parseDoubleAndScale(String val) {
		return processDoubleScale(parseDouble(val),2);
	}
	
	static public double processDoubleScale(Double val){
		return processDoubleScale(val,2);
	}
	static public double processDoubleScale(Double val,int scale){
		if(val==null
				||Double.compare(val.doubleValue(),Double.NaN)==0){
			return 0.;
		}
		try {
			BigDecimal b = new BigDecimal(val.toString());
			b = new BigDecimal(val.toString())
				.setScale(scale, BigDecimal.ROUND_HALF_UP);
			return b.doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.;
	}
	static public double divide(Double dividend,Double divisor){
		if(dividend==null||divisor==null)return 0;
		if(dividend==0||divisor==0)return 0;
		double result = dividend/divisor;
		return processDoubleScale(result);
	}
	static public String formatDate(Date ts){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(ts);
	}
	static public double nextGaussian(double mean,double max){
		Random random = new Random();
		double r = random.nextGaussian()/1.0+mean;
		if(r>max||r<0)return mean;
		return processDoubleScale(r,1);
	}
	public static void main(String args[]){
//		for(int i=0;i<100;i++){
//			System.out.println(nextGaussian(3, 5));
//		}
		System.out.println(lastSubString("123456789", 6));
		System.out.println(extractZh("Mobaco服装店"));
		extractEn("Mobaco服装店");
		System.out.println(smartLength("Mobaco服装店"));
		System.out.println(extractTermSet("Al-Tannoura Egyptian Heritage Dance Troupe"));
	}
	static public void sleep(int sec){
		try {
			TimeUnit.SECONDS.sleep(sec);
		} catch (InterruptedException e) {
			log.warn("Exception",e);
		}
	}
	static public int strongLength(String len){
		if(len == null)return 0;
		return len.trim().length();
	}
	static public boolean isChineseChars(String val){
		return Pattern.matches("[\\u4E00-\\u9FA5]+", val);
	}
	public static boolean isChineseSentence(String str,double scale){
		if(TravelUtils.isEmpty(str))return false;
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		String zh = "";
		while(m.find()) {
			zh+=m.group();
		}
		double zhLen = zh.length();
		double len = smartLength(str);
		if(zhLen/len>scale){
			return true;
		}else{
			return false;
		}
	}
	public static boolean isContainZhOrEn(String str){
		if(isContainChinese(str))return true;
		if(isContainEnglish(str))return true;
		return false;
	}
	public static boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
	public static boolean isContainEnglish(String str) {
		Pattern p = Pattern.compile("[a-zA-Z]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
	public static int sentenceSize(String sentence){
			if(isEmpty(sentence))return 0;
			String[] fields = sentence.split(" ");
			int len = 0;
			for(String field:fields){
				if(isContainChinese(field)){
					len+=field.length();
				}else len++;
			}
			return len;
	}
	public static List<String> extractEn(String sentence){
		List<String> enTerms = new LinkedList<String>();
		if(isEmpty(sentence))return enTerms;
		String regexEn = "([a-zA-Z]+)";
		Pattern pattern = Pattern.compile(regexEn);
		Matcher matcher = pattern.matcher(sentence);
		while(matcher.find()){
			enTerms.add(matcher.group(1));
		}
		return enTerms;
	}
	public static List<String> extractZh(String sentence){
		List<String> zhTerms = new LinkedList<String>();
		if(isEmpty(sentence))return zhTerms;
		for(int i=0;i<sentence.length();i++){
			String s = sentence.substring(i, i+1);
			if(isNotEmpty(s))zhTerms.add(s);
		}
		return zhTerms;
	}
	/**
	 * 去除字符串中头部和尾部所包含的空格（包括:空格(全角，半角)、制表符、换页符等）
	 * @param s
	 * @return
	 */
	public static String trim(String s){
		String result = "";
		if(null!=s && !"".equals(s)){
			result = s.replaceAll("^[　*| *| *|\\s*]*", "").replaceAll("[　*| *| *|\\s*]*$", "");
		}
		return result;
	}
	static public String replaceStrVal(String str){
		if(TravelUtils.isEmpty(str))return "";
		str = str.replaceAll("([a-zA-Z]+)", "=");
		str = str.replaceAll("([1-9\\.]+)", "=");
		return trim(str);
	}
	static public int smartLength(String len){
		if(TravelUtils.isEmpty(len))return 0;
		if(!isContainChinese(len)){
			return len.split("\\s").length;
		}else{
			return replaceStrVal(len).length();
		}
	}
	static public int smartLengthNew(String sentence){
		return extractTermSet(sentence).size();
	}
	static public Set<String> extractTermSet(String name) {
		Set<String> nameSet = new HashSet<String>();
		List<String> enTerms = TravelUtils.extractEn(name);
		enTerms.remove("s");
		enTerms.remove("'");
		name = name.replaceAll("'s", "");
		for (String t : enTerms) {
			name = name.replaceAll("\\s"+t+"\\s", " ");
			name = name.replaceAll("^"+t+"\\s", " ");
			name = name.replaceAll("\\s"+t+"$", " ");
		}
		name = name.replaceAll("\\s", "");
		nameSet.addAll(enTerms);
		nameSet.addAll(TravelUtils.extractZh(name));
		return nameSet;
	}
	/**
	 * 计算两个字符串的相似度
	 * @param name1
	 * @param name2
	 * @return 1:name1 is equals to name2<br/>0.9: name1 is similar to name2<br/>0.75：name1 is not similar to name2
	 */
	static public double computeSimilarity(String name1,String name2){
		Set<String> nameSet1 = TravelUtils.extractTermSet(name1);
		Set<String> nameSet2 = TravelUtils.extractTermSet(name2);
		
//		log.info("nameSet1="+nameSet1);
//		log.info("nameSet2="+nameSet2);
		Set<String> commonTerm = new TreeSet<String>();
		double commentSize = 0.;
		for(String n1:nameSet1){
			if(nameSet2.contains(n1)){
				commonTerm.add(n1);
				commentSize++;
			}
		}
//		log.info("common term:"+commonTerm);
		double score = commentSize/TravelUtils.smartLengthNew(name1);
		double score2 = commentSize/TravelUtils.smartLengthNew(name2);
		if(nameSet1.size()!=nameSet2.size())score=score-0.01;
		if(score>=0.99 && score2<0.8)score=score-0.2;
		if(score>=0.99 && score2<0.5)score=score-0.2;
//		log.info(commentSize+"/"+TravelUtils.smartLengthNew(name1)+"="+score);
		return score;
	}
	static public int smartLengthWithoutPunctuation(String str){
		str = replacePunctuation(str);
		return smartLength(str);
	}
	static public String replacePunctuation(String str){
		String punc = "[～~,.?;!，。？；！\\s]";
		str = str.replaceAll(punc, "");
		return str;
	}
	static public List<Pair> sortMapByValue(Map<String,Integer> map){
		List<Pair> list = new ArrayList<Pair>(map.size());
		for(Entry<String,Integer> ent:map.entrySet()){
			Pair p = new Pair(ent.getKey(),ent.getValue());
			list.add(p);
		}
		list.sort(new Comparator<Pair>(){
			@Override
			public int compare(Pair o1, Pair o2) {
				if ((-o1.value+o2.value)>0)return 1;
				if ((-o1.value+o2.value)<0)return -1;
				return 0;
			}
		});
		return list;
	}
	static public List<DoublePair> sortDoubleMapByValue(Map<String,Double> map){
		List<DoublePair> list = new ArrayList<DoublePair>(map.size());
		for(Entry<String,Double> ent:map.entrySet()){
			DoublePair p = new DoublePair(ent.getKey(),ent.getValue());
			list.add(p);
		}
		list.sort(new Comparator<DoublePair>(){
			@Override
			public int compare(DoublePair o1, DoublePair o2) {
				if ((-o1.v+o2.v)>0)return 1;
				if ((-o1.v+o2.v)<0)return -1;
				return 0;
			}
		});
		return list;
	}
	
	static public boolean isEmpty(List<?> list){
		if(list == null || list.isEmpty())return true;
		return false;
	}
}
