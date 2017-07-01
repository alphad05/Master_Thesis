package model;

/*
 * Class for creating a branch item. A branch connects 
 * two nodes (buses) in the power system.
 */
public class BranchItem {
	private final double TOLERANCE = 0.01;
	private int fromNode; 
	private int toNode;
	//branchPower can be negative or positive indicating whether power
	//is flowing from the fromNode to the toNode or vice versa. When doing comparison
	//with branchPowerLim take the absolute value of branchPower.
	private double branchPower; //in per unit
	private double branchPowerLim; //in per unit
	private double branchBaseMVA;
	//will indicate if the From bus was found in the To column from the data
	private boolean reverse; 
	private boolean branchStatus; //branch status, false means it is cutoff, true means it is active
	private int branchNumber; //number of branch on the bus, will be >= 1
	
	//branch power is not initially given
	public BranchItem(int fNode, int tNode, double bPowLim, double bBase, boolean rev, boolean status, int bNum) {
		fromNode = fNode;
		toNode = tNode;
		branchBaseMVA = bBase;
		reverse = rev;
		//change to per unit and set branchPower initially to 0.0.
		branchPower = 0.0;
		branchPowerLim = bPowLim / branchBaseMVA;
		branchStatus = status;
		branchNumber = bNum;
	}
	
	public BranchItem(int fNode, int tNode, double bPowLim, double bBase, boolean rev, boolean status, int bNum, double bPower) {
		fromNode = fNode;
		toNode = tNode;
		branchBaseMVA = bBase;
		reverse = rev;
		//change to per unit and set branchPower initially to 0.0.
		branchPower = bPower / branchBaseMVA;
		branchPowerLim = bPowLim / branchBaseMVA;
		branchStatus = status;
		branchNumber = bNum;
	}
	
	public String branchToString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" Branch Number: "+branchNumber+" Status: "+branchStatus);
		if(reverse) {
			sb.append(" from bus: "+toNode+" to bus: "+fromNode);
		}else {
			sb.append(" from bus: "+fromNode+" to bus: "+toNode);
		}
		sb.append(" branch Power: "+branchPower);
		sb.append(" branch limit: "+branchPowerLim);
		return sb.toString();
	}
	
	//return whether branch is cutoff (false) or not
	public boolean getBranchStatus() {
		return branchStatus;
	}
	
	public void setBranchStatus(boolean stat) {
		branchStatus = stat;
	}
	
	public int getBranchNumber() {
		return branchNumber;
	}
	
	public int getFromBusNumber() {
		return fromNode;
	}
	
	public int getToBusNumber() {
		return toNode;
	}
	
	public double getBranchPower() {
		return branchPower;
	}
	
	public void setBranchPower(double nbPow) {
		//nbPow will not be in per unit so convert to per unit
		branchPower = nbPow / branchBaseMVA;
	}
	
	public double getBranchPowerLimit() {
		return branchPowerLim;
	}
	
	public double getBranchPowerBase() {
		return branchBaseMVA;
	}
	
	public boolean getReverse() {
		return reverse;
	}
	
	//if there is a branch power violation
	public boolean branchViolation(double nbPow) {
		//take the absolute value as nbPow can be negative or positive
		//and convert to per unit
		nbPow = Math.abs(nbPow / branchBaseMVA);
		if(nbPow > (branchPowerLim+TOLERANCE)) {
			return true;
		}
		return false;
	}
	

}
