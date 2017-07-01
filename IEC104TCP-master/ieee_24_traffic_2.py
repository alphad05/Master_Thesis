#This is a modified program form the original example.py program that was in the IEC104TCP-master github folder.
#This file will generate traffic for three different commands from the IEC104 protocols. Those commands are:
#single point (C_SC_NA, 45), double point (C_DC_NA,46), and set point scaled (C_SE_NB, 49). The common ASDU address (Addr) 
#will be the same for each packet and the Originator address (OA) will be set to zero (0).
#C_SC_NA:SCO
#C_DC_NA:DCO
#C_SE_NB:SVA and QOS

#command outcomes are in "Second test volts" tab in the "test data" excel file. Traffic capture is in ieee_24_traffic_2.pcapng

	
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
	#--------first sequence--------- 
	#cut-off branch 9-3 (IOA=49) -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(49,SCO_Off_Np_Ex)]),
	#cut-off branch 15-24 (IOA=63) -> failure: 
	('START','auto','if',[45,1,Cause_Act,0,3,(63,SCO_Off_Np_Ex)]),
	#connect branch 9-3 (IOA=49) -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(49,SCO_On_Np_Ex)]),
	
	#cut-off branch 23-12 (IOA=77) -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(77,SCO_Off_Np_Ex)]),
	#cut-off branch 10-8 (IOA=56) -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(56,SCO_Off_Np_Ex)]),
	#cut-off branch 7-8 (IOA=48) -> failure: 
	('START','auto','if',[45,1,Cause_Act,0,3,(48,SCO_Off_Np_Ex)]),
	#connect branch 23-12 (IOA=77) -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(77,SCO_On_Np_Ex)]),
	
	#cut-off branch 1-3 (IOA=43) -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(43,SCO_Off_Np_Ex)]),
	#cut-off branch 7-8 (IOA=48) -> failure: 
	('START','auto','if',[45,1,Cause_Act,0,3,(48,SCO_Off_Np_Ex)]),
	#connect branch 1-3 (IOA=43) -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(43,SCO_On_Np_Ex)]),
	
	#cut-off branch 13-11 (IOA=59) -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(59,SCO_Off_Np_Ex)]),
	#cut-off branch 10-5 (IOA=54) -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(54,SCO_Off_Np_Ex)]),
	#cut-off branch 10-6 (IOA=55) -> failure: 
	('START','auto','if',[45,1,Cause_Act,0,3,(55,SCO_Off_Np_Ex)]),
	#connect branch 10-5 (IOA=54) -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(54,SCO_On_Np_Ex)]),
	#connect branch 10-8 (IOA=56) -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(56,SCO_On_Np_Ex)]),
	#connect branch 13-11 (IOA=59) -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(59,SCO_On_Np_Ex)]),
	
	
	#load 3 2.0 -> pass
	('START','auto','if',[49,1,Cause_Act,0,3,(27,360,QOS_Ex)]),
	#load 6 2.0 -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(30,272,QOS_Ex)]),
	#load 3 2.5 -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(27,450,QOS_Ex)]),
	#load 6 2.5 -> fail
	('START','auto','if',[49,1,Cause_Act,0,3,(30,340,QOS_Ex)]),
	#load 8 2.5 fail
	('START','auto','if',[49,1,Cause_Act,0,3,(32,428,QOS_Ex)]),
	
	#cutoff branch 2-4 (IOA=45): fail
	('START','auto','if',[45,1,Cause_Act,0,3,(45,SCO_Off_Np_Ex)]),
	#connect branch 2-4 0 back: firewall should block previous packet if violation occured so might not be a need for this packet. -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(45,SCO_On_Np_Ex)]),
	#cutoff branch 3-24 (IOA=47): failure
	('START','auto','if',[45,1,Cause_Act,0,3,(47,SCO_Off_Np_Ex)]),
	#connect branch 3-24 back -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(47,SCO_On_Np_Ex)]),
	#cutoff branch 10-6 (IOA=55): failure
	('START','auto','if',[45,1,Cause_Act,0,3,(55,SCO_Off_Np_Ex)]),
	#connect branch 10-6 back -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(55,SCO_On_Np_Ex)]),
	#cutoff branch 7-8 (IOA=48): failure
	('START','auto','if',[45,1,Cause_Act,0,3,(48,SCO_Off_Np_Ex)]),
	#connect branch 7-8 back -> pass
	('START','auto','if',[45,1,Cause_Act,0,3,(48,SCO_On_Np_Ex)]),
	#cutoff branch 16-14 (IOA=64): failure
	('START','auto','if',[46,1,Cause_Act,0,3,(64,DCO_Off_Np_Ex)]),
	#connect branch 16-14 back -> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(64,DCO_On_Np_Ex)]),
	#cutoff branch 15-24 (IOA=63): failure
	('START','auto','if',[46,1,Cause_Act,0,3,(63,DCO_Off_Np_Ex)]),
	#connect branch 15-24 back -> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(63,DCO_On_Np_Ex)]),
	#cutoff branch 17-16 (IOA=66) -> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(66,DCO_Off_Np_Ex)]),
	#connect branch 17-16 back - > pass
	('START','auto','if',[46,1,Cause_Act,0,3,(66,DCO_On_Np_Ex)]),
	#cutoff branch 19-16 (IOA=70) -> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(70,DCO_Off_Np_Ex)]),
	#connect branch 19-16 back -> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(70,DCO_On_Np_Ex)]),
	#cutoff branch 18-17 (IOA=67) -> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(67,DCO_Off_Np_Ex)]),
	#connect branch 18-17 back -> pass
	('START','auto','if',[46,1,Cause_Act,0,3,(67,DCO_On_Np_Ex)]),
	
]