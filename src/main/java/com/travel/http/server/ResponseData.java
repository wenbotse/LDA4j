package com.travel.http.server;

import java.util.List;

public class ResponseData {

	final static public int RESPONSE_OK=1;
	final static public int RESPONSE_FAILED=0;
	private int code;
	private String pvid;
	private List<String> urlMd5s;
	private List<String> types;
	private String data;
	private int num;
	
	public List<String> getUrlMd5s() {
		return urlMd5s;
	}
	public void setUrlMd5s(List<String> urlMd5s) {
		this.urlMd5s = urlMd5s;
	}
	public List<String> getTypes() {
		return types;
	}
	public void setTypes(List<String> types) {
		this.types = types;
	}
	public String getPvid() {
		return pvid;
	}
	public void setPvid(String pvid) {
		this.pvid = pvid;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
}
