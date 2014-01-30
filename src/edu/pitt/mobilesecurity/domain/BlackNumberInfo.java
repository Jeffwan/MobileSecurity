package edu.pitt.mobilesecurity.domain;

public class BlackNumberInfo {

	String number;
	String mode;
	
	public BlackNumberInfo(String number, String mode) {
		super();
		this.number = number;
		this.mode = mode;
	}
	
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}


	@Override
	public String toString() {
		return "BlackNumberInfo [number=" + number + ", mode=" + mode + "]";
	}
	
}
