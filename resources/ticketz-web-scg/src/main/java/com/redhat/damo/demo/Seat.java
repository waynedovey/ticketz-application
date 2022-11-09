package com.redhat.damo.demo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import io.quarkus.runtime.annotations.RegisterForReflection;


@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@RegisterForReflection
public class Seat {

	private int seatId;
	private int customerId;
	private String state;
	private String category;
	private String timestamp;

	/****************************************************/
	// Constuctors
	public Seat() {
		super();
	}

	public Seat(int seatId, int customerId, String state, String category, String timestamp) {
		this.seatId = seatId;
		this.customerId = customerId;
		this.state = state;
		this.category = category;
		this.timestamp = timestamp;
	}

	/****************************************************/
	// Getters and Setters
	public int getSeatId() {
		return seatId;
	}
	public void setSeatId(int seatId) {
		this.seatId = seatId;
	}

	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getState() {
		return this.state;
	}
	public void setState(String state) {
		this.state = state;
	}

	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public String getTimestamp() {
		return this.timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


   	/****************************************************/
	public boolean isAvailable() {
		return this.state.equals("available");
	}

	// I know, dodgy way to do this, but it's only a demo!
	public String toJsonString() {
		return "{\"seatId\":" + this.seatId + ",\"customerId\":" + this.customerId +
		       ",\"state\":\"" + this.state + "\",\"category\":\"" + this.category +
			   "\",\"timestamp\":\"" + this.timestamp + "\"}";
	}
}

