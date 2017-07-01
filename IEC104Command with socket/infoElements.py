'''
Classes in this file are for creating the information elements
used in the IEC 104 protocol
'''

class info_elem_sco():
    '''
        single command: command used to change the state of operational
        equipment
    '''
    def __init__(self,scs,qoc_qu, qoc_se):
        self._scs = scs #single command state: off(0), on(1)
        self.qoc = info_elem_qoc(qoc_qu, qoc_se)

    @property
    def scs(self):
        return self._scs
    @property
    def qoc_qu(self):
        return self.qoc.qu
    @property
    def qoc_se(self):
        return self.qoc.s_e

class info_elem_dco():
    '''
        double command: same as single command except two bits are used to
        represent off(1)/on(2)
    '''
    def __init__(self,dcs,qoc_qu,qoc_se):
        self._dcs = dcs #double command state: not permitted(0,3), off(1), on(2)
        self.qoc = info_elem_qoc(qoc_qu, qoc_se)

    @property
    def dcs(self):
        return self._dcs

    @property
    def qoc_qu(self):
        return self.qoc.qu

    @property
    def qoc_se(self):
        return self.qoc.s_e


class info_elem_rco():
    '''
        regulating step command: pulse command that changes state of operational
        equipment in predetermined steps. Two bits used to represent rcs ramp up (2)
        and ramp down(1)
    '''
    def __init__(self,rcs,qoc_qu,qoc_se):
        self._rcs = rcs
        self.qoc = info_elem_qoc(qoc_qu, qoc_se)

    @property
    def rcs(self):
        return self._rcs

    @property
    def qoc_qu(self):
        return self.qoc.qu

    @property
    def qoc_se(self):
        return self.qoc.s_e


class info_elem_nva():
    '''
        normalized value: value is between -1 to +1-2^(-15)
    '''
    def __init__(self,nva):
        self._nva = nva

    @property
    def nva(self):
        return self._nva

class info_elem_qos():
    '''
        qualifer of set point command
    '''
    def __init__(self, ql, s_e):
        self._ql = ql #default is 0. Values 1-127 reserved
        self._s_e = s_e #execute(0), select(1)

    @property
    def ql(self):
        return self._ql

    @property
    def se(self):
        return self._s_e

class info_elem_sva():
    '''
        scaled value: defined for transmission of values such as current,
        voltage,power with their actual values (A,kV,MW).
        Example from IEC 101 specification:
        current: 103 A, transmitted value 103
        voltage: 10.3 kV, transmitted value 103
        
        asdu:scalval
    '''
    def __init__(self,sva):
        self._sva = sva

    @property
    def sva(self):
        return self._sva

class info_elem_shortfloat():
    '''
        short floating point number: R32-IEEE STD 754 in IEC104
        
        asdu: float
    '''
    def __init__(self,sfValue):
        self._sf_value = sfValue

    @property
    def sf(self):
        return self._sf_value

class info_elem_bsi():
    '''
        Binary state information: status information of operational equipment
        asdu: bitstring
    '''
    def __init__(self,bsi):
        self._bsi = bsi 

    @property
    def get_bsi(self):
        return self._bsi

class info_elem_qoi():
    '''
        qualifier of interrogation
        asdu:qoi
    '''
    def __init__(self,qoi):
        #0 not used, 20 global station interrogation,
        #21-36 interrogation of group 1-16
        self._qoi = qoi 

    @property
    def qoi(self):
        return self.qoi

class info_elem_qcc():
    '''
        qualifier of counter interrogation
    '''
    def __init__(self,rqt,freeze):
        self._rqt = rqt #request: value 1-5 used, 6-63 reserved
        '''
            freeze values: 0(read), 1(counter freeze w/o reset),
            2(counter freeze with reset), 3(counter reset)
        '''
        self._freeze = freeze

    @property
    def rqt(self):
        return self._rqt

    @property
    def freeze(self):
        return self._freeze

class info_elem_qrp():
    '''
        qualifier of reset process
    '''
    def __init__(self,qrp):
        self._qrp = qrp

    @property
    def qrp(self):
        return self._qrp
    
class info_elem_tsc():
    '''
        Test sequence counter: 16 bits, requesting station can choose any value
    '''
    def __init__(self,tsc):
        self._tsc = tsc

    @property
    def tsc(self):
        return self._tsc

class info_elem_cp56time2a():
    '''
        time tag that is 7 bytes long
    '''
    def __init__(self,ms,minute,hour,day,month,year):
        self._ms = ms/1000 #milliseconds
        self._minute = minute
        self._hour = hour
        self._day = day
        self._month = month
        self._year = year

    @property
    def cp56time2a_date(self):
        item_date = str(self._month)+'/'+str(self._day)+'/'+str(self._year)
        item_date += ' '+str(self._hour)+':'+str(self._minute)+':'+str(self._ms)
        return item_date

class info_elem_qoc():
    '''
        qualifer of command
    '''
    def __init__(self,qu,s_e):
        self._qu = qu   #only values 0-3 are defined 4-31 are reserved
        self._s_e = s_e #execute(0), select(1)

    @property
    def qu(self):
        return self._qu

    @property
    def s_e(self):
        return self._s_e
        

class info_elem_siq():
    '''
        IEC 104 single point information with quality descriptor
    '''
    def __init__(self,spi,bl,sb,nt,iv):
        self._spi = spi #off(0),on(1)
        self._bl = bl #not blocked(0), blocked(1)
        self._sb = sb #not substituted(0), substituted(1)
        self._nt = nt #topical(0), not topical(1)
        self._iv = iv #valid(0), not valid(1)

    def __str__(self):
        s = str(self.spi)+str(self.bl)+str(self.sb)
        s += str(self.nt)+str(self.iv)
        return s

    @property
    def spi(self):
        return self._spi

    @property
    def bl(self):
        return self._bl

    @property
    def sb(self):
        return self._sb

    @property
    def nt(self):
        return self._nt

    @property
    def iv(self):
        return self._iv


class info_elem_diq():
    '''
        IEC 104 double point information with quality descriptor
    '''
    def __init__(self,dpi,bl,sb,nt,iv):
        self._dpi = dpi
        self._bl = bl
        self._sb = sb
        self._nt = nt
        self._iv = iv

    @property
    def dpi(self):
        return self._dpi

    @property
    def bl(self):
        return self._bl

    @property
    def sb(self):
        return self._sb

    @property
    def nt(self):
        return self._nt

    @property
    def iv(self):
        return self._iv
'''    
new_siq = info_elem_siq(0,1,0,1,0)
print(new_siq.get_spi())

new_sco = info_elem_sco(1,0,0)
print(new_sco.get_scs())
print(new_sco.get_qoc_qu())
print(new_sco.get_qoc_se())
'''    
