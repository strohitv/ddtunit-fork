package junitx.ddtunit.resources;

import java.util.HashSet;

public class FLY_BookingInfo {
	private FLY_Address address;
	private HashSet passengerFlyers;

	public FLY_BookingInfo() {
		// TODO Auto-generated constructor stub
	}

	public FLY_Address getAddress() {
		return address;
	}

	public void setAddress(FLY_Address address) {
		this.address = address;
	}

	public HashSet getPassengerFlyers() {
		return passengerFlyers;
	}

	public void setPassengerFlyers(HashSet passengerFlyers) {
		this.passengerFlyers = passengerFlyers;
	}

}
