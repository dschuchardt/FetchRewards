package com.fetch;

import java.time.Instant;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Transaction {
	
	private @Id @GeneratedValue Long id;
	private String payer;
	private int points;
	private Instant timestamp;
	private boolean isSpend = false;
	private int pointsAvailableToSpend;
	
	Transaction(){}
	
	Transaction(String payer, int points, Instant timestamp) {
		this.payer = payer;
		this.points = points;
		this.timestamp = timestamp;
		this.pointsAvailableToSpend = points;
		if(getPoints() < 0) {
			this.isSpend = true;
			this.pointsAvailableToSpend = 0;
		}
	}
//getters
	public Long getId() {
		return id;
	}
	
	public String getPayer() {
		return payer;
	}

	public int getPoints() {
		return points;
	}
	
	public Instant getTimestamp() {
		return timestamp;
	}

	public boolean isSpend() {
		return isSpend;
	}
	
	public int getPointsAvailableToSpend() {
		return pointsAvailableToSpend;
	}
	
//Setters	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setPayer(String payer) {
		this.payer = payer;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public void setSpend(boolean isSpend) {
		this.isSpend = isSpend;
	}

	public void setPointsAvailableToSpend(int pointsAvailableToSpend) {
		this.pointsAvailableToSpend = pointsAvailableToSpend;
	}
	
//Overrides
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.payer, this.points, this.timestamp, this.isSpend, this.pointsAvailableToSpend);
	}

	@Override
	public String toString() {
		return "Transaction{" + "id=" + this.id + ", payer='" + this.payer + '\'' + ", points='" + this.points + '\'' + ", timestamp='" 
		+ this.timestamp + '\'' + ", isSpend='" + this.isSpend + '\'' + ", pointsAvailableToSpend='" + this.pointsAvailableToSpend + '\'' + '}';
	}
	
}
