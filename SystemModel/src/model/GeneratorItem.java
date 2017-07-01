package model;

/*
 * Class for creating a generator item
 */
public class GeneratorItem {
	private final double TOLERANCE = 0.01;
	private double gPower;
	private double gqPower;
	private double maxRPower;
	private double minRPower;
	private double maxQPower;
	private double minQPower;
	private double gBasePow; //in MVA
	private int genNumber; //generator number on bus
	
	public GeneratorItem(double gp, double gqpower, double maxpower, double minpower, double maxqPower, double minqPower, double genbase, int gNum) {
		gBasePow = genbase;
		//change to per unit
		gPower = gp / gBasePow;
		gqPower = gqpower / gBasePow;
		maxRPower = maxpower / gBasePow;
		minRPower = minpower / gBasePow;
		maxQPower = maxqPower / gBasePow;
		minQPower = minqPower / gBasePow;
		genNumber = gNum;
	}
	
	public double getGenRealPower() {
		return gPower;
	}
	
	public double getBasePower() {
		return gBasePow;
	}
	
	public int getGenNumber() {
		return genNumber;
	}
	
	public double getGenQPower() {
		return gqPower;
	}
	
	public double getRealMaxPower() {
		return maxRPower;
	}
	
	public double getRealMinPower() {
		return minRPower;
	}
	
	public double getQMaxPower() {
		return maxQPower;
	}
	
	public double getQMinPower() {
		return minQPower;
	}
	
	//set the real power
	public void setGenPower(double gpow, double gqpow) {
		gPower = gpow / gBasePow;
		gqPower = gqpow / gBasePow;
	}
	
	public void setQGenPower(double gqpow) {
		gqPower = gqpow / gBasePow;
	}
	
	//check for real power violations on generator
	public boolean genPowerViolations(double gpow, double gqpow) {
		//convert to per unit
		gpow = gpow / gBasePow;
		gqpow = gqpow / gBasePow;
		if(Math.abs(gpow) > (maxRPower+TOLERANCE) || Math.abs(gpow) < (minRPower-TOLERANCE) || gqpow > (maxQPower+TOLERANCE) || gqpow < (minQPower-TOLERANCE)) {
			return true;
		}
		return false;
	}
	
	public boolean genQPowerViolations(double gqpow) {
		//convert to per unit
		gqpow = gqpow / gBasePow;
		if(gqpow > (maxQPower+TOLERANCE) || gqpow < (minQPower-TOLERANCE)) {
			return true;
		}
		return false;
	}
	
}
