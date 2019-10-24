package com.myschool.bean;

public class Page {
	
	private int start;

	private int code;
	
	private int size;
	

	public Page() {
		
	}
	
	public Page(int code, int size) {
		this.code = code;
		this.size = size;
		this.start = (code-1) * size;
	}
	
	public int getStart() {
		this.start = (code-1) * size;
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
}
