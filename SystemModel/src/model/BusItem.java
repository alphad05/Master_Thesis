package model;

import java.util.ArrayList;

/*
 * Class for creating a bus item
 */
public class BusItem {
	private final double TOLERANCE = 0.01;
	private int busNum; //bus number
	private double voltageValue; //bus voltage assuming in per units already
	private double baseKV; //bus voltage base
	private boolean hasLoad; //bus has load connected
	private LoadItem iLoad; //create load item
	private boolean hasGen; //bus has generator connected
	//for when there are multiple generators on a single bus
	private ArrayList<GeneratorItem> multiGen = new ArrayList<GeneratorItem>(); 
	//for adding branches
	private ArrayList<BranchItem> branches = new ArrayList<BranchItem>();
	//private GeneratorItem iGen; //create generator item
	private double maxVoltage; //maximum allowed voltage on bus
	private double minVoltage; //minimum allowed voltage on bus
	
	//copy values of the passed in BusItem
	public BusItem(BusItem bi) {
		busNum = bi.getBusNum();
		voltageValue = bi.getVoltage();
		baseKV = bi.getVoltageBase();
		hasLoad = bi.getHasLoad();
		hasGen = bi.getHasGen();
		iLoad = bi.getLoadItem();
		maxVoltage = bi.getMaxVoltage();
		minVoltage = bi.getMinVoltage();
		multiGen = copyGenerators(bi.getGeneratorItems());
		branches = copyBranches(bi.getBranches());
	}
	
	public ArrayList<BranchItem> copyBranches(ArrayList<BranchItem> b) {
		ArrayList<BranchItem> cpBranch = new ArrayList<BranchItem>();
		for(int i = 0; i < b.size(); i++) {
			BranchItem oldBranch = b.get(i);
			int fNode = oldBranch.getFromBusNumber();
			int tNode = oldBranch.getToBusNumber();
			double bBase = oldBranch.getBranchPowerBase();
			double bPowLim = oldBranch.getBranchPowerLimit()*bBase;
			boolean rev = oldBranch.getReverse();
			boolean status = oldBranch.getBranchStatus();
			int bNum = oldBranch.getBranchNumber();
			double bbPower = oldBranch.getBranchPower()*bBase;
			BranchItem nBranch = new BranchItem(fNode, tNode, bPowLim, bBase, rev, status, bNum, bbPower);
			cpBranch.add(nBranch);
		}
		
		return cpBranch;
	}
	
	public ArrayList<GeneratorItem> copyGenerators(ArrayList<GeneratorItem> g) {
		ArrayList<GeneratorItem> cpGens = new ArrayList<GeneratorItem>();
		for(int i = 0; i < g.size(); i++) {
			GeneratorItem oldGen = g.get(i);
			double genBase = oldGen.getBasePower();
			double gp = oldGen.getGenQPower()*genBase;
			double gqpower = oldGen.getGenQPower()*genBase;
			double maxpower = oldGen.getRealMaxPower()*genBase;
			double minpower = oldGen.getRealMinPower()*genBase;
			double maxqPower = oldGen.getQMaxPower()*genBase;
			double minqPower = oldGen.getQMinPower()*genBase;
			int gNum = oldGen.getGenNumber();
			GeneratorItem gi = new GeneratorItem(gp, gqpower, maxpower, minpower, maxqPower, minqPower, genBase, gNum); 
			cpGens.add(gi);
		}
		return cpGens;
	}
	
	//Only bus number and bus voltage are given meaning bus has no loads and no generators
	public BusItem(int bnum, double voltVal) {
		//if only bus number and voltage are passed, set default max and min
		//voltage to 1.05 and 0.95 respectively. Set default base to 138 kV.
		this(bnum, voltVal, 1.05, 0.95, 138);
	}
	
	//bus number, bus voltage, and the maximum and minimum voltage
	//on the bus
	public BusItem(int bnum, double voltVal, double maxVolt, double minVolt, double basekv) {
		busNum = bnum;
		voltageValue = voltVal;
		baseKV = basekv;
		hasLoad = false;
		hasGen = false;
		maxVoltage = maxVolt;
		minVoltage = minVolt;
	}
	
	public String busToString() {
		StringBuilder s = new StringBuilder();
		s.append("bus: "+busNum);
		s.append(" Voltage: "+voltageValue);
		s.append(" Volt base: "+baseKV);
		s.append(" Max Volt: "+maxVoltage);
		s.append(" Min Volt: "+minVoltage);
		if(getHasLoad()) {
			s.append(" Load: "+iLoad.getLoadPower());
			s.append(" Load base: "+iLoad.getLoadBase());
		}
		if(getHasGen()) {
			for(int i = 0; i < multiGen.size(); i++) {
				GeneratorItem gen = multiGen.get(i);
				s.append(" \n Generator<"+gen.getGenNumber()+">: P "+gen.getGenRealPower());
				s.append(" Q "+gen.getGenQPower());
			}
		}
		
		for(int i = 0; i < branches.size(); i++) {
			BranchItem bi = branches.get(i);
			s.append(" \n"+bi.branchToString());
		}
		
		return s.toString();
		
	}
	
	//hasLoad is true
	public BusItem(int bnum, double voltVal, LoadItem iload, double maxVolt, double minVolt, double basekv) {
		busNum = bnum;
		voltageValue = voltVal;
		baseKV = basekv;
		hasLoad = true;
		hasGen = false;
		iLoad = iload;
		maxVoltage = maxVolt;
		minVoltage = minVolt;
	}
	
	//hasGenerator is true
	public BusItem(int bnum, double voltVal, GeneratorItem gen, double maxVolt, double minVolt, double basekv) {
		busNum = bnum;
		voltageValue = voltVal;
		baseKV = basekv;
		hasLoad = false;
		hasGen = true;
		//multiGen = new ArrayList<GeneratorItem>();
		//iGen = gen;
		multiGen.add(gen);
		maxVoltage = maxVolt;
		minVoltage = minVolt;
	}
	
	//hasLoad and hasGen are both true
	public BusItem(int bnum, double voltVal, LoadItem iload, GeneratorItem gen, double maxVolt, double minVolt, double basekv) {
		busNum = bnum;
		voltageValue = voltVal;
		baseKV = basekv;
		hasLoad = true;
		iLoad = iload;
		hasGen = true;
		maxVoltage = maxVolt;
		minVoltage = minVolt;
		multiGen.add(gen);
	}
	
	public void addGenerators(GeneratorItem gen) {
		if(!hasGen) {
			hasGen = true;
		}
		multiGen.add(gen);
	}
	
	public void addBranch(BranchItem bran) {
		branches.add(bran);
	}
	
	public ArrayList<BranchItem> getBranches() {
		return branches;
	}
	
	public BranchItem getBranch(int bInd) {
		if(bInd >= 0 & bInd < branches.size()) {
			return branches.get(bInd); 
		}
		return null;
	}
	
	public void setBranchPower(int bInd, double bPower) {
		if(bInd >= 0 & bInd < branches.size()) {
			branches.get(bInd).setBranchPower(bPower); 
		}
	}
	
	public void setBranchStatus(int bInd, boolean stat) {
		if(bInd >= 0 & bInd < branches.size()) {
			branches.get(bInd).setBranchStatus(stat); 
		}
	}
	
	//add a load to the bus. Assuming only single load is allowed on 
	//a bus, but this could be changed
	public void addLoad(LoadItem ld) {
		if(!hasLoad) {
			hasLoad = true;
			iLoad = ld;
		}
	}
	
	public void setVoltage(double volt) {
		voltageValue = volt;
	}
	
	public void setLoadValue(double lValue) {
		iLoad.setLoadPower(lValue);
	}
	
	//Set specified generator real and reactive power value
	public void setGeneratorValue(double gValue, double gqValue, int genIndex) {
		if(genIndex < multiGen.size() & genIndex >= 0) {
			System.out.println(" setting P:"+gValue+" Q:"+gqValue);
			multiGen.get(genIndex).setGenPower(gValue, gqValue);
			//multiGen.get(genIndex).setQGenPower(gqValue);
		}
	}
	
	public int getBusNum() {
		return busNum;
	}
	
	public double getVoltage() {
		return voltageValue;
	}
	
	public double getVoltageBase() {
		return baseKV;
	}
	
	public boolean getHasLoad() {
		return hasLoad;
	}
	
	public LoadItem getLoadItem() {
		if(getHasLoad()) {
			return iLoad;
		}
		return null;
	}
	
	public boolean getHasGen() {
		return hasGen;
	}
	
	//return list of generators from 1 to n generators
	public ArrayList<GeneratorItem> getGeneratorItems() {
		//if(getHasGen()) {
			return multiGen;
		//}
		//return ;
	}
	
	//return the indicated generator item
	public GeneratorItem getGenerator(int gindex) {
		if(gindex < multiGen.size() & gindex >= 0) {
			return multiGen.get(gindex);
		}
		return null;
	}
	
	public double getMaxVoltage() {
		return maxVoltage;
	}
	
	public double getMinVoltage() {
		return minVoltage;
	}
	
	//bus voltage is greater than maxVoltage or less than minVoltage
	public boolean voltViolation(double volt) {
		if(volt > (maxVoltage+TOLERANCE) || volt < (minVoltage-TOLERANCE)) {
			return true;
		}
		return false;
	}
	
}
