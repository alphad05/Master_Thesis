package model;
/*
 * Class for creating a load object
 */
public class LoadItem {
	private double loadPower;
	private double loadPowerBase;
	
	//load item with load power base included, and assuming power is not
	//already in per unit
	public LoadItem(double power, double lpBase) {
		loadPowerBase = lpBase;
		//change load to per unit
		loadPower = power / loadPowerBase;
	}
	
	//when no load power base is given
	public LoadItem(double power) {
		//default load power base if none provided
		this(power, 100.0);
	}
	
	public double getLoadPower() {
		return loadPower;
	}
	
	public double getLoadBase() {
		return loadPowerBase;
	}
	
	//assuming power is not already in per unit
	public void setLoadPower(double pow) {
		loadPower = pow / loadPowerBase;
	}
}
