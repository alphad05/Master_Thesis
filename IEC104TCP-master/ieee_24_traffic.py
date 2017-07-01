#This is a modified program form the original example.py program that was in the IEC104TCP-master github folder.
#This file will generate traffic for three different commands from the IEC104 protocols. Those commands are:
#single point (C_SC_NA, 45), double point (C_DC_NA,46), and set point scaled (C_SE_NB, 49). The common ASDU address (Addr) 
#will be the same for each packet and the Originator address (OA) will be set to zero (0).
#C_SC_NA:SCO
#C_DC_NA:DCO
#C_SE_NB:SVA and QOS

#command outcomes are in "Origtest Volt" tab in the "test data" excel file. Traffic capture is saved in commandcapture_orig.pcapng

# u frame types
STARTDT_ACT=0x07
STARTDT_CON=0x0b
STOPDT_ACT=0x13
STOPDT_CON=0x23
TESTFR_ACT=0x43
TESTFR_CON=0x83

#Sending Reasons
Cause_Act=0x06
Cause_Deact=0x08

#Control values
SCO_Off_Np_Ex=0x00 #no pulse execute
SCO_On_Np_Ex=0x01
SCO_Off_Sp_Ex=0x04 #short pulse execute
SCO_On_Sp_Ex=0x05
SCO_Off_Np_Se=0x80
SCO_On_Np_Se=0x81

DCO_Off_Np_Se=0x81
DCO_Off_Np_Ex=0x01
DCO_On_Np_Se=0x82
DCO_On_Np_Ex=0x02

RCO_Down_Np_Se=0x81
RCO_Up_Np_Se=0x82

QOS_Ex = 0x00

plist=[
	#-------------------- typeID,Variable structure qualifier,CoT,OA,Addr,IOA,IOA information------------------------
	#load 1 .5 (54) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(25,54,QOS_Ex)]),
	#cutoff 2 4 0 -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(45,SCO_Off_Np_Ex)]),
	#cutoff 9 8 0 -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(51,SCO_Off_Np_Ex)]),
	#load 9 1.1	(192.5) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(33,192,QOS_Ex)]),
	#cutoff 10 5 0	-> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(54,SCO_Off_Np_Ex)]),
	#cutoff 16 15 0	-> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(65,SCO_Off_Np_Ex)]),
	#load 19 1.1	(199.1) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(40,199,QOS_Ex)]),
	#load 18 1.5	(499.5) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(39,499,QOS_Ex)]),
	#cutoff 21 22 0	-> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(75,SCO_Off_Np_Ex)]),
	#cutoff 1 3 0	-> fail
	('START','auto','if',[45,1,Cause_Act,0,3,(43,SCO_Off_Np_Ex)]),
	#load 4 1.4	(103.6) -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(28,103,QOS_Ex)]),
	#cutoff 9 8 1	-> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(51,SCO_On_Np_Ex)]),
	#load 8 1.5	(256.5) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(32,256,QOS_Ex)]),
	#cutoff 10 8 0	-> fail
	('START','auto','if',[46,1,Cause_Act,0,3,(56,DCO_Off_Np_Ex)]),
	#cutoff 21 22 1	-> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(75,DCO_On_Np_Ex)]),
	#load 20 1.5	(192) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(41,192,QOS_Ex)]),
	#cutoff 23 20 0	-> faol
	('START','auto','if',[46,1,Cause_Act,0,3,(78,DCO_Off_Np_Ex)]),
	#load 10 1.4 (273) -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(34,273,QOS_Ex)]),
	#load 14 1.4	(271.6) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(36,271,QOS_Ex)]),
	#cutoff 17 16 0	-> fail
	('START','auto','if',[46,1,Cause_Act,0,3,(66,DCO_Off_Np_Ex)]),
	#cutoff 9 4 0	-> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(50,DCO_Off_Np_Ex)]),
	#cutoff 9 8 0  -> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(51,DCO_Off_Np_Ex)]),
	#load 1 1.3 (70.2) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(25,70,QOS_Ex)]),
	
	#load 3 1.4	(252) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(27,252,QOS_Ex)]),
	#cutoff 3 24 0	-> fail
	('START','auto','if',[46,1,Cause_Act,0,3,(47,DCO_Off_Np_Ex)]),
	#load 14 1.2	(325.92) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(36,325,QOS_Ex)]),
	#cutoff 16 15 1 -> -> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(65,DCO_On_Np_Ex)]),
	#cutoff 1 3 1	-> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(43,DCO_On_Np_Ex)]),
	#load 3 1.3 (327.6) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(27,327,QOS_Ex)]),
	
	#cutoff 3 24 0	-> fail
	('START','auto','if',[46,1,Cause_Act,0,3,(47,DCO_Off_Np_Ex)]),
	#load 14 1.8	(586.656) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(36,586,QOS_Ex)]),
	#cutoff 14 11 0	-> fail
	('START','auto','if',[46,1,Cause_Act,0,3,(62,DCO_Off_Np_Ex)]),
	#load 18 1.4 (699.3) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(39,699,QOS_Ex)]),
	
	#load 9 .8	(154) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(33,154,QOS_Ex)]),
	#load 1 1.5	(105.3) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(25,105,QOS_Ex)]),
	#cutoff 16 15 1 -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(65,SCO_On_Np_Ex)]),
	#cutoff 10 5 1	-> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(54,SCO_On_Np_Ex)]),
	#cutoff 10 8 1	-> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(56,SCO_On_Np_Ex)]),
	#load 15 1.5 (475.5) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(37,475,QOS_Ex)]),
	
	#load 19 1.4 (278.74) -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(40,278,QOS_Ex)]),
	
	#cutoff 19 20 0 -> fail
	('START','auto','if',[46,1,Cause_Act,0,3,(71,DCO_Off_Np_Ex)]),
	
	#cutoff 13 23 0 -> fail
	('START','auto','if',[46,1,Cause_Act,0,3,(61,DCO_Off_Np_Ex)]),
	
	#cutoff 22 17 0 -> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(76,DCO_Off_Np_Ex)]),
	#cutoff 19 20 1	-> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(71,DCO_On_Np_Ex)]),
	#cutoff 21 15 0 -> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(73,DCO_Off_Np_Ex)]),
	
	#load 3 1.4	(458.64) -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(27,458,QOS_Ex)]),
	#load 15 1.2	(570.6) -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(37,570,QOS_Ex)]),
	#load 19 1.2	(334.488) -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(40,334,QOS_Ex)]),
	#load 9 1.3	(200.2) -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(33,200,QOS_Ex)]),
	#load 2 1.3 (126.1) -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(26,126,QOS_Ex)]),
	
	#cutoff 22 17 1 -> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(76,DCO_On_Np_Ex)]),
	
	#load 19 1.2 (401.3856) -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(40,401,QOS_Ex)]),
	
	#load 4 .8 (82.88) -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(28,82,QOS_Ex)]),
	
	#load 8 1.2 (307.8) -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(32,307,QOS_Ex)]),
	
	#cutoff 21 15 1 -> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(74,DCO_On_Np_Ex)]),
	
	#cutoff 7 8 0 -> fail
	('START','auto','if',[46,1,Cause_Act,0,3,(48,DCO_Off_Np_Ex)]),
	
	#load 1 1.4 (147.42) -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(25,147,QOS_Ex)]),

	#cutoff 13 11 0 -> fail
	('START','auto','if',[46,1,Cause_Act,0,3,(59,DCO_Off_Np_Ex)]),
]