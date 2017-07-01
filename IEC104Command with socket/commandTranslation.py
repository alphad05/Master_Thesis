import os
import socket
import pyshark
import controlFunctions as cf

file = "ieee_24_traffic.pcapng" #file containing the wireshark capture
#file = "ieee_24_traffic_2.pcapng"
captured = pyshark.FileCapture(file) #get the packets
#dir(typeIdentification)

#socket information
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM) #
port = 12345 #port that server will be listening on
#server hostname, which in this case is localhost. Change this to actual server name if not local host
host = socket.gethostname()
s.connect((host,port))
numpackets = 0
for pack in captured:
	
    #Layer format <Ethernet, IP, TCP, 104APCI,104ASDU, ..>
    #if there are more 104 APDUs in packet then they will be
    #following each other
    lay = pack.layers 
    if lay[2].dstport=="2404":
        if "<104APCI Layer>" in str(lay):
            lay = lay[3:] #get only the APDU data
            
            #get the apci and the asdu data if any
            for i in range(0,len(lay),2):
                apci = lay[i] #apci portion of apdu
                #print(apci.ApduLen)
                if int(apci.Type,16)==0:
                    #print("I Type")
                    apciType = "I"
                elif int(apci.Type,16)==1:
                    #print("S Type")
                    apciType = "S"
                elif int(apci.Type,16)==3:
                    #print("U Type")
                    apciType = "U"
                else:
                    print(apci)
                    print("Wrong type")
                #print(apci)
                transCommand = "pass" #translated command
                if apciType=="I": #only dealing with I type formats for now
                    asdu = lay[i+1] #asdu portion of apdu
                    funcType = int(asdu.get_field("typeid"),10) #typeid is returned as a string so convert to base 10 number
                    print(funcType)
                    if funcType in cf.type_identification_class:
                        #if funcType==45:
                            transCommand = cf.type_functions[funcType](asdu,funcType)

                            #send transCommand to java program through socket
                            sendCom = bytearray(transCommand+'\r\n','utf-8')
                            print("Sending: "+transCommand)
                            s.sendall(sendCom)

                            #receive response
                            rec = s.recv(1024)
                            numpackets = numpackets + 1
                            if rec:
                                print("Received: "+str(rec,'utf-8'))
                            
                            #print(transCommand)
                            #print(asdu.field_names) #print out the valid fields in the asdu
                    
                            print(asdu)
                   
                #printnt(transCommand)
print("Closing socket connection...")
shutdown_mes = bytearray("shutdown\r\n",'utf-8');
s.sendall(shutdown_mes)
s.close()
print(str(numpackets))
