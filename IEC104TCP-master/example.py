from iec104client import *
#from iec104_tcp_packets import *
#change file name to use different traffic
from ieee_24_traffic import *

server_ip = '192.168.31.133'
client = iec104_tcp_client(server_ip)
client.connect()
for p in plist:
    print client.sendOne(p)
	




