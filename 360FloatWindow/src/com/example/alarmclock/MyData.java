package com.example.alarmclock;

import java.io.Serializable;

public class MyData implements Serializable
{
	int hour;
	int minute;
	boolean open;
	boolean ring;
	boolean vibrator;
	int arrAlarmNumber;
	
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	public boolean isRing() {
		return ring;
	}
	public void setRing(boolean ring) {
		this.ring = ring;
	}
	public boolean isVibrator() {
		return vibrator;
	}
	public void setVibrator(boolean vibrator) {
		this.vibrator = vibrator;
	}
	public int getArrAlarmNumber() {
		return arrAlarmNumber;
	}
	public void setArrAlarmNumber(int arrAlarmNumber) {
		this.arrAlarmNumber = arrAlarmNumber;
	}
	
	
}
