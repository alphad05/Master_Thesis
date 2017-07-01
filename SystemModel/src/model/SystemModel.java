package model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

public class SystemModel {
	private static boolean violations = true;
	private static final int SUCCESS = 1;
	private Logger log = Logger.getLogger("IEEE.SystemLog");
	private static int catchViolations; //which violations should be caught
	
	//hashmap with string version of branch name and array of size 4 which contains 
	//branch number being predicted, predicted branch limit, slope, and y intercept
	private HashMap<String, ArrayList<String[]>> linRegression = new HashMap<String, ArrayList<String[]>>();
	
	public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException, IOException {
		SystemModel cmt = new SystemModel();
		
		//get which violations you want to catch: 
		//0: all violations, 1: voltage violations, 2: branch power violations, 3: generator violations, 4: voltage and branch power violations
		catchViolations = 0;
		System.out.println("Which violations would you like to catch (default 0):\n "
				+"0: all violations\n "
				+"1: voltage violations\n 2: branch power violations\n "
				+"3: generator violations\n 4: voltage and branch power violations");
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter: ");
		if(scan.hasNextInt()) {
			catchViolations = scan.nextInt();
		}
		scan.close();
		
		//set up logging
		cmt.setLogging();
		
		//create proxy, which will control Matlab
		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();
		
		//run setup with paths needed for MATPOWER tool and options for when running
		//power flow
		proxy.eval("run('matpower_paths.m');");
		proxy.eval("define_constants");
		proxy.eval("mpopt = mpoption;");
		
		//send buses which are considered critical to matlab
		/*Case 1: 7 with 1 connection
		 *Case 2: 4,5,6,14,22,24 with 2 connections on each bus
		 *Case 3: 1,2,3,8,13,17,18,19 with 3 connections on each bus
		 *Case 4: 11,12,15,16,20,23 with 4 connections on each bus
		 *Case 5: 9,10,21 with 5 connections on each bus
		 *Case 6 for newVals: 1,2,3,7,9,10,16,21,23
		 *All buses: 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24
		 */
		double[][] criticalComps = new double[][]{{1,2,3,7,9,10,16,21,23}};
		//populate the pred hashmap
		cmt.populateLinRegression();
		
		MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
		processor.setNumericArray("criticalComps", new MatlabNumericArray(criticalComps,null));
		
		//create hashmap that will contain the buses with load and generator information and initialize 
		//with base case 
		HashMap<Integer, BusItem> buses = cmt.initCase(processor, proxy);;
		cmt.initBranches(processor, buses);
		//save the initialcase with name of 'prevsolcase' using MatPower savecase function
		proxy.eval("savecase('prevsolcase',mpc)");
		
		//print out hashmap contents
		cmt.printBus(buses, criticalComps);
		
		//mimic getting packet by using user input
		/*load Command: load [bus#] [amount to increase/decrease by]
    	 *cut off command: cutoff [from bus] [to bus] [status]
		 *Amount to increase/decrease by for load is in percentage:
      	 *	example: load 4 .5   //this will decrease load by half
    	 *Status for cutoff command is either 1 (connected) or 0 (not connected)
		 */
		//String lcommand = "load 3 1";
		//String lcommand = "cutoff 1 2 0";
		//String[] comsValue = lcommand.split(" ");
		
		//set up server socket
		int port = 12345; //port that server is listening on
		ServerSocket serSoc = new ServerSocket(port);
		System.out.println("Waiting for Connection on: "+serSoc.getLocalSocketAddress());
		Socket soc = serSoc.accept();
		
		//Create input and output streams for socket connection
		BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
		PrintWriter out = new PrintWriter(soc.getOutputStream(),true);
		
		//Load properties file with IOA that corresponds to system component
		InputStream propStream = null;
		Properties props = new Properties();
		String propFile = "ieee24_iec.properties";
		propStream = new FileInputStream(propFile);
		props.load(propStream);
		
		String rc = ""; //string will contain the translated commands from the packets
		int packetsProcessed = 0;
		int packetsRejected = 0;
		int packetsAllowed = 0;
		boolean[] packStat = new boolean[58]; //true=packet is harmful, false=packet is safe
		int packStatIndex = 0;
		
		double[] testLoads = {54.0,192.5,199.1,499.5,103.6,256.5,192.0,273.0,271.6,70.2,252.0,325.92,327.6,586.656,699.3,154.0,105.3,475.5,278.74,458.64,570.6,334.488,200.2,126.1,401.3856,82.88,307.8,147.42};
		int testLoadsIndx = 0;
		while(!rc.equals("shutdown")) {
			//Copy prevsolcase to temporary file. 
			cmt.copyFiles("prevsolcase.m","tmpsolcase.m");		
			
			try {
				rc = in.readLine();
				System.out.println("Command: "+rc);
			}catch(SocketException e){
				System.out.println("Socket connection lost. Exiting...");
				break;
			}
			//comsValue is [command, IOA address value]
			String[] comsValue = rc.split(":"); //split rc into command and values
			System.out.println("running: "+comsValue[0]);
			if(!comsValue[0].equalsIgnoreCase("pass") &&!comsValue[0].equalsIgnoreCase("shutdown")) {
				String message = "";
				String[] commandValues = comsValue[1].trim().split(" ");
				String[] ioaTrans = props.getProperty(commandValues[0]).split(" ");
				String compType = ioaTrans[0];
				
				packetsProcessed++;
				
				//Check command and execute
				if(comsValue[0].equalsIgnoreCase("set_point_scaled")) {
					proxy.eval("disp('Command is "+rc+"')");
					System.out.println("Command is set_point");
					//get the type of component and the bus number(s) that component is on
					int bus = Integer.parseInt(ioaTrans[1]);
					proxy.setVariable("compType", compType);
					//proxy.eval("compType="+compType+";");

					double componentInc = Double.valueOf(commandValues[1]).doubleValue();
					//double componentInc = testLoads[testLoadsIndx];
					//testLoadsIndx++;
					double[][] vals = new double[][]{{bus, componentInc}};
					
					//load case
					proxy.eval("mpc = loadcase('prevsolcase');");
					
					//send vals array to matlab
					processor.setNumericArray("vals", new MatlabNumericArray(vals,null));
					
					//run matlab script to increase/decrease load and run powerflow
					proxy.eval("run('loadIncrease.m');");
					//Get modeled component values and update the model
					buses = cmt.runUpdate(buses, processor, proxy, criticalComps);
					
					//print out hashmap contents
					cmt.printBus(buses, criticalComps);
					
				}else if(comsValue[0].equalsIgnoreCase("cutoff") && compType.equalsIgnoreCase("Branch")) {
					proxy.eval("disp('Command is "+rc+"')");
					System.out.println("Command is cutoff");
					
					int fbus = Integer.valueOf(ioaTrans[1]).intValue();
					int tbus = Integer.valueOf(ioaTrans[2]).intValue();
					int status = Integer.valueOf(commandValues[1]).intValue();
					
					System.out.println("Status: "+status);
					
					System.out.println("fbus: "+fbus+" tbus: "+tbus);
					
					double[][] vals = new double[][]{{fbus, tbus, status}};
					
					//load the case to test
					proxy.eval("mpc = loadcase('prevsolcase');");
					
					processor.setNumericArray("vals", new MatlabNumericArray(vals,null));
					
					//run matlab script that performs powerflow after making change to branch
					proxy.eval("run('cutoff.m');");
					
					//Get modeled component values and update the model
					buses = cmt.runUpdate(buses, processor, proxy, criticalComps);
					
					//print out hashmap contents
					cmt.printBus(buses, criticalComps);
				}
				message = "Command received: "+rc;
				if(!violations) {
					out.println("Allow: "+rc);
				    cmt.log.info(message+" Allowed");
				    packetsAllowed++;
				    packStat[packStatIndex] = false;
				    packStatIndex++;
				}else {
					out.println("Reject: "+rc);
					cmt.log.info(message+" Rejected");
					packetsRejected++;
					packStat[packStatIndex] = true;
					packStatIndex++;
				}
				
			}else {
				out.println("passing");
			}
		}
		cmt.getResults(packetsProcessed, packetsAllowed, packetsRejected, packStat);
		
		in.close();
		out.close();
		soc.close();
		serSoc.close();
		//disconnect proxy from Matlab
		proxy.disconnect();
	}
	
	public void getResults(int packetsProcessed, int packetsAllowed, int packetsRejected, boolean[] packStat) {
		System.out.println("Packets Processed: "+packetsProcessed);
		System.out.println("Packets Allowed: "+packetsAllowed);
		System.out.println("Packets Rejected: "+packetsRejected);
		//use this one for ieee_24_traffic.py
		boolean[] correct = {false,false,false,false,false,false,false,false,false,true,true,false,false,true,false,false,true,true,false,true,false,false,false,false,true,false,false,false,false,true,false,true,false,false,false,false,false,false,false,true,true,true,false,false,false,true,true,true,true,true,false,true,false,true,false,true,true,true};
		//use this on for ieee_24_traffic2.py
		//boolean[] correct = {false,true,false,false,false,true,false,false,true,false,false,false,true,false,false,false,false,true,true,true,true,true,false,true,false,true,false,true,false,true,false,true,false,false,false,false,false,false,false};
		
		//for testing results
		int numFP = 0;
		int numFN = 0;
		int numTP = 0;
		int numTN = 0;
		for(int i = 0; i < correct.length; i++) {
			if(correct[i]==true && packStat[i]==true) { //true positive
				numTP++;
			}else if(correct[i]==false && packStat[i]==false) { //true negative
				numTN++;
			}else if(correct[i]==false && packStat[i]==true) { //false positive
				numFP++;
			}else if(correct[i]==true && packStat[i]==false) { //false negative
				numFN++;
			}
			System.out.print(packStat[i]+",");
		}
		System.out.println();
		System.out.println("FP: "+numFP+", FN: "+numFN+", TP: "+numTP+", TN: "+numTN);
	}
	
	//Set up logging to store packet status and violations
	public void setLogging() throws SecurityException, IOException {
		String logFile = "commands.log";
		FileHandler handle = new FileHandler(logFile,true);
		SimpleFormatter sf = new SimpleFormatter(); //put in txt format rather than xml
		handle.setFormatter(sf);
		log.addHandler(handle);
	}
	
	//print out contents of the buses hashmap which will contain bus, load, generator, and branch info
	public void printBus(HashMap<Integer,BusItem> buses, double[][] criticalComps) {
		for(int i = 0; i < criticalComps[0].length; i++) {
			int num = (int)criticalComps[0][i];
			System.out.println(buses.get(num).busToString());
		}
	}
	
	public void copyFiles(String file, String dst) throws IOException {
		CopyOption co = StandardCopyOption.REPLACE_EXISTING;
		Path src = Paths.get(file);
		Path tmp = Paths.get(dst);
		Files.copy(src, tmp, co);
	}
	
	
	//attempt to format the number of decimals
	public double formattedDecimal(double dig) {
		double d = (double)Math.round(dig*1000.0) / 1000.0;
		
		return d;
	}
	
	//run the code necessary to update the model containing the bus components to be observed, and report
	//any violations
	public HashMap<Integer, BusItem> runUpdate(HashMap<Integer, BusItem> buses, MatlabTypeConverter processor, MatlabProxy proxy, double[][] criticalComps) throws MatlabInvocationException, IOException {
		int runSuccess = (int)((double[])proxy.getVariable("runSuccess"))[0]; //1=success, 0=fail
		System.out.println("runSuccess: "+runSuccess);
		if(runSuccess==SUCCESS) {
			//Get number of violations that are detected by Matlab
			proxy.eval("run('checkViolations.m')");
			int matlabViolations = (int)((double[])proxy.getVariable("numViolations"))[0];
			log.info("Matlab Number of Violations: "+matlabViolations);
			//Add code to get critical component values and update the model
			//for the critical component
			proxy.eval("run('getLoadResults.m')");
			
			//get bus and load information
			double[][] loadRes =  processor.getNumericArray("cBusInfo").getRealArray2D();
			System.out.println(loadRes.length);
			
			//get generator information
			double[][] genRes = processor.getNumericArray("cBusUpdateGens").getRealArray2D();
			System.out.println(genRes.length);
			
			
			//get branch information
			double[][] branchRes = processor.getNumericArray("cBranchUpdate").getRealArray2D();
			System.out.println(branchRes.length);
			//create temporary copy of buses which will be used to update values
			HashMap<Integer,BusItem> tempBuses = copyMap(buses);
			
			//See if any violations occur and mimic dropping packet if there are violations
			violations = updateModel(tempBuses, loadRes, genRes, branchRes);
			//update bus voltage and load information. Need to include branch info
			//if no violations occurred set buses to the temporary buses, indicating packet should
			//be let through
			if(!violations) {
				System.out.println("Violations have not occurred!!!");
				buses = tempBuses;
				return tempBuses;
			}else {
				//violations occurred so set the 'prevsolcase.m' file to what it was before the new command ran
				copyFiles("tmpsolcase.m","prevsolcase.m");
			}
		}else {
			violations = true;
			log.warning("System convergence failed");
			copyFiles("tmpsolcase.m","prevsolcase.m");
		}
		
		return buses;
	}
	
	//Start to update the temporary model and if any violations occur return true, indicating that the packet should
	//be dropped.
	public boolean updateModel(HashMap<Integer,BusItem> tempBuses, double[][] loadRes, double[][] genRes, double[][] branchRes) {
		for(int i = 0; i < loadRes.length; i++) {
			//busar[0] -> bus number, busar[1] -> load value, busar[2] -> voltage
			double[] busar = loadRes[i];
			int bnum = (int)busar[0];
			if(tempBuses.containsKey(bnum)) {
				BusItem b = tempBuses.get(bnum);
				double newVolt = formattedDecimal(busar[2]);
				//check for voltage violations and if any don't update critical components
				//and don't save the new case file generated from the power flow
				if(catchViolations==0||catchViolations==1||catchViolations==4) {
					if(b.voltViolation(newVolt)) {
						System.out.println("Voltage violation @ bus "+bnum+" Voltage "+newVolt+" p.u");
						String message = "Voltage violation @ bus "+bnum+" Voltage "+newVolt+" p.u";
						log.warning(message); //log warning message for violation
						return true;
					}
				}
				//update the bus information
				b.setVoltage(newVolt);
				//update load info if bus has load
				if(b.getHasLoad()) {
					b.setLoadValue(busar[1]);
				}
				//update generator(s) info if bus has generator(s)
				if(b.getHasGen()) {
					//update list of generators
					for(int j = 0; j < genRes.length; j++) {
						double[] gRes = genRes[j];
						
						int gbNum = (int) gRes[0]; //generator bus number
						if(b.getBusNum()==gbNum) {
							double gRPow = formattedDecimal(gRes[1]); //real power
							double gQPow = formattedDecimal(gRes[4]); //reactive power
							int genIndx = (int)gRes[8];
							//genIndx - 1 because values in matlab start at 1, while arraylist in GeneratorItem index starts
							//at 0.
							//check for generator real and reactive power violations violations
							if(catchViolations==0||catchViolations==3){
								if(b.getGenerator(genIndx-1).genPowerViolations(gRPow, gQPow)) {
									System.out.println("Power violation @ bus "+bnum+" P: "+gRPow+" Q: "+gQPow+" p.u"+" gen#:"+(genIndx-1));
									String message = "Generator Power violation @ bus "+bnum+" P: "+gRPow+" Q: "+gQPow+" p.u"+" gen#:"+(genIndx-1);
									log.warning(message);
									return true;
								}
							}
							//update generators only if no violations have occurred.
							b.setGeneratorValue(gRPow, gQPow, genIndx - 1);
							gRes[0] = -1;
							genRes[j] = gRes;
							
						}
					}
				}
				
				//update branch data
				for(int br = 0; br < branchRes.length; br++) {
					//branchData = [From bus, To Bus, branch power, branch status, reverse, branch number on bus]
					//branch power depends on what reverse is. If reverse is 0 branch power is the real power injected 
					//into the 'from' (PF) end of the branch, otherwise it is the real power injected into the 'to' (PT) 
					//end of the branch.
					double[] branchData = branchRes[br];
					//System.out.println("BranchData length: "+branchData.length);
					boolean reverse = ((int)branchData[4]) == 0 ? false : true;
					//bNum will be part of the critical buses being modeled
					int bNum = (int) branchData[0];
					int conBus = (int) branchData[1];
					//keep track of which index in branchData contains the bus number of the bus being modeled
					int selIndex = 0; 
					//take the to bus number
					if(reverse) {
						bNum = (int) branchData[1];
						conBus = (int) branchData[0];
						selIndex = 1;
					}
					//try to update bus branch if the bus numbers are equal
					if(b.getBusNum()==bNum) {
						System.out.println("updating branch: "+bNum+"-"+conBus);
						boolean bStatus = ((int)branchData[3]) == 0 ? false : true;
						double brPower = formattedDecimal(branchData[2]);
						int brNumber = (int)branchData[5];
						
						
						
						//if there is a branch power violation
						if(catchViolations==0||catchViolations==2||catchViolations==4) {
							//check branch against predicted branches in pred hashmap
							if(checkLinRegression(bNum+"-"+conBus,branchData[2], b.getBranch(brNumber-1).getBranchPowerBase())) {
								return true;
							}
							if(b.getBranch(brNumber-1).branchViolation(brPower)) {
								System.out.println("Branch Power Violation: <"+bNum+"-"+conBus+"> Power: "+brPower+" lim: "+b.getBranch(brNumber-1).getBranchPowerLimit());
								String message = "Branch Power Violation: <"+bNum+"-"+conBus+"> Power: "+brPower+" lim: "+b.getBranch(brNumber-1).getBranchPowerLimit();
								log.warning(message);
								return true;
							}
						}
						//update specified branch status and power
						//brNumber - 1 because matlab starts at 0, and arraylist containing branches starts at 0;
						b.setBranchPower(brNumber-1, brPower);
						b.setBranchStatus(brNumber-1, bStatus);
						branchData[selIndex] = -1;
						branchRes[br] = branchData;
					}
				}
			}
		}
		
		return false;
	}
	
	//initiliaze branches that connect to the bus being modeled
	public void initBranches(MatlabTypeConverter processor, HashMap<Integer, BusItem> buses) throws MatlabInvocationException {
	  	//HashMap<Integer,BranchItem> branches = new HashMap<Integer,BranchItem>();
	  	double[][] branchInit = processor.getNumericArray("cBranchInit").getRealArray2D();
	  	for(int i = 0; i < branchInit.length; i++) {
			//bInit will be [From bus number, To bus number, max power allowed on branch, branch status, reverse, baseMVA, branch number]
			//rev will indicate whether the bus was found in the From (0) or To
			//(1) column in matpower case file
			double[] bInit = branchInit[i];
			
			boolean reverse = ((int)bInit[4]) == 0 ? false : true;
			boolean bStatus = ((int)bInit[3]) == 0 ? false : true;
			//bNum will be part of the critical buses being modeled
			int bNum = (int) bInit[0];
			int conBus = (int) bInit[1];
			//take the to bus number
			if(reverse) {
				bNum = (int) bInit[1];
				conBus = (int) bInit[0];
			}
			double maxPow = bInit[2]; //maximum power allowed on branch
			double bBaseMVA = bInit[5]; //baseMVA of power
			int branchNum = (int)bInit[6]; //branch number on the bus
			//maybe instead of creating new BusItem, add this branch to the buses being modeled
			//BusItem bus = new BusItem(buses.get(bNum));
			//create branch
		  	BranchItem branchIt = new BranchItem(bNum, conBus, maxPow, bBaseMVA, reverse, bStatus, branchNum);
		  	//add branch to bus
		  	buses.get(bNum).addBranch(branchIt);
		  	//branches.put(bNum,branchIt);
	  	}
	  	//return branches;
	}
	 
	
	//initialize the model 
	public HashMap<Integer, BusItem> initCase(MatlabTypeConverter processor, MatlabProxy proxy) throws MatlabInvocationException {
		HashMap<Integer, BusItem> buses = new HashMap<Integer, BusItem>();
		
		//load the initial case (base case)
		proxy.eval("mpc = loadcase('case24_ieee_rts_alpha');");
		
		//create BusItem component that will model the critical bus components and store them 
		//in a hashmap with the bus number as the key
		proxy.eval("run('initBus.m')");
		//initRes contains bus and load information
		double[][] initRes =  processor.getNumericArray("cBusInitLoads").getRealArray2D();
		//initGens contains bus and generator information
		double[][] initGens =  processor.getNumericArray("cBusInitGens").getRealArray2D();
		System.out.println("Generators: "+initGens.length);
		
		for(int i = 0; i < initRes.length; i++) {
			double[] ar = initRes[i];
			int busNum = (int) ar[0];
			double bVolt = ar[1];
			double maxVolt = ar[2];
			double minVolt = ar[3];
			double loadPower = ar[4];
			double basekv = ar[5];
			double basePower = ar[6];
			BusItem bI;
			//add load information if any
			if(loadPower > 0) {
				LoadItem l = new LoadItem(loadPower,basePower);
				bI = new BusItem(busNum, bVolt, l, maxVolt, minVolt, basekv);
			}else {
				bI = new BusItem(busNum, bVolt, maxVolt, minVolt, basekv);
			}
			
			buses.put(busNum, bI);
			
			//add generator information if any
			if(initGens.length > 0) {
				for(int g = 0; g < initGens.length; g++) {
					//array a will be same size for each generator item
					double[] genAr = initGens[g];
					int gBus = (int)genAr[0]; //bus on which generator is on
					//add generator to bus if buses hashmap contains the bus the generator belongs on
					if(buses.containsKey(gBus)) {
						double gRealPow = genAr[1]; //generator real power
						double gRMaxPow = genAr[2]; //generator real power max
						double gRMinPow = genAr[3]; //generator real power min
						double gQPow = genAr[4]; //generator reactive power
						double gQMaxPow = genAr[5]; //generator reactive power max
						double gQMinPow = genAr[6]; //generator reactive power min
						double gPowBase = genAr[7]; //generator power base
						int gNum = (int)genAr[8]; //generator number on the bus starting at 1;
						
						//create new generator item
						GeneratorItem gen = new GeneratorItem(gRealPow, gQPow, gRMaxPow, gRMinPow, gQMaxPow, gQMinPow, gPowBase, gNum);
						buses.get(gBus).addGenerators(gen);
						//remove from valid generators
						genAr[0] = -1;
						initGens[g] = genAr;
					}
				}
			}
			
		}
		
		return buses;
	}
	
	public boolean checkLinRegression(String predictorBranch, double predictorPower, double predictorPowerBase) {
		if(linRegression.containsKey(predictorBranch)) {
			log.info("Checking Predictor: "+predictorBranch);
			ArrayList<String[]> ar = linRegression.get(predictorBranch);
			predictorPower = predictorPower / predictorPowerBase;
			for(String[] brVals : ar) {
				//String[] brVals = pred.get(predictorBranch);
				double predLimit = Double.parseDouble(brVals[1]) + .01;
				double slope = Double.parseDouble(brVals[2]);
				double intercept = Double.parseDouble(brVals[3]);
				//predictorPower = predictorPower / predictorPowerBase;
				double predBranchVal =  intercept + (slope * predictorPower);
				log.info("y="+slope+"x "+intercept);
				log.info("Checking Predictor: "+predictorBranch+"predictorVal: "+predictorPower+" predValue: "+predBranchVal+" limit:"+predLimit);
				if(Math.abs(predBranchVal) > predLimit) {
					String mesg = "---Branch Violation @ <"+brVals[0]+"> Power: "+predBranchVal+" Limit: "+predLimit;
					System.out.println(mesg);
					log.warning(mesg);
					return true;
				}
			}
		}
		
		return false;
	}
	
	//populate the pred hashmap
	public void relations(String brString, String predBranch, String predLimit, String slope, String intercept) {
		String[] predBr = new String[4];
		predBr[0] = predBranch;
		predBr[1] = predLimit;
		predBr[2] = slope;
		predBr[3] = intercept;
		if(linRegression.containsKey(brString)) {
			ArrayList<String[]> ar = linRegression.get(brString);
			ar.add(predBr);
			linRegression.put(brString, ar);
		}else {
			ArrayList<String[]> ar = new ArrayList<String[]>();
			ar.add(predBr);
			linRegression.put(brString, ar);
		}
		//pred.put(brString,predBr);
	}
	
	
	public void populateLinRegression() {
		//populate pred hashmap
		
		//for newVals [predBranch,predlimit,slope,intercept]
		//crit comps: 1,2,3,7,9,10,16,21,23
		//"branch 16-17->18-17 y=-0.93752+0.81089x r^2=0.69942"
		relations("16-17","18-17","6.0","0.81089","-0.93752");
		//"branch 23-20->13-11 y=-1.0539+2.1936x r^2=0.98959"
		relations("23-20","13-11","6.0","2.1936","-1.0539");
		//"branch 23-20->14-11 y=3.2409+-2.0295x r^2=0.98232"
		relations("23-20","14-11","6.0","-2.0295","3.2409");
		//"branch 23-20->13-12 y=-0.87781+1.5988x r^2=0.99338"
		relations("23-20","13-12","6.0","1.5988","-0.87781");
		//"branch 23-20->19-20 y=0.56617+-0.84418x r^2=0.98499"
		relations("23-20","19-20","6.0","-0.84418","0.56617");
		//"branch 3-24->15-24 y=-0.026366+-1.033x r^2=0.99997"
		relations("3-24","15-24","6.0","-1.033","-0.026366");
		//"branch 21-22->22-17 y=3.0143+1.0238x r^2=0.99988"
		relations("21-22","22-17","6.0","1.0238","3.0143");
		
		
	}
	
	//Creates a copy of the hashmap that is passed in
	public HashMap<Integer,BusItem> copyMap(HashMap<Integer,BusItem> hm) {
		HashMap<Integer,BusItem> tempBuses = new HashMap<Integer,BusItem>();
		for(Map.Entry<Integer, BusItem> ent : hm.entrySet()) {
			tempBuses.put(ent.getKey(), new BusItem(ent.getValue()));
		}
		return tempBuses;
	}

}
