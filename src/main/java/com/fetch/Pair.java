package com.fetch;

public class Pair {
	private String payer;
	private int points;
	public Pair(String payer, int points) {
		this.payer = payer;
		this.points = points;
	}
	
	public String getPayer() {
		return payer;
	}
	
	public void setPayer(String payer) {
		this.payer = payer;
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	

}
