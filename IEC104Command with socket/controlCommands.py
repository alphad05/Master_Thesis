import infoElements as infoEl
''' IEC 104 control commands
process information commands
45:C_SC_NA_1, 46:C_DC_NA_1, 47:C_RC_NA_1, 48:C_SE_NA_1, 49:C_SE_NB_1,
50:C_SE_NC_1, 51:C_BO_NA_1, 58:C_SC_TA_1, 59:C_DC_TA_1, 60:C_RC_TA_1,
61:C_SE_TA_1, 62:C_SE_TB_1, 63:C_SE_TC_1, 64:C_BO_TA_1,
System information commands
100:C_IC_NA_1, 101:C_CI_NA_1, 102:C_RD_NA_1, 103:C_CS_NA_1, 105:C_RP_NC_1,
107:C_TS_TA_1
'''

class c_sc_na_1():
    '''
        class for the IEC 104 single command and single
        command with time tag (c_sc_ta_1)
        
        Command used to turn things off/on (will use to simulate branch cutoff command)
        
        SCO information element
        CoT <control>: 6 or 8
    '''
    def __init__(self,scs,sco_qu,sco_se,cot,ioa):
        self.sco = infoEl.info_elem_sco(scs,sco_qu,sco_se)
        self._cot = cot
        self._ioa = ioa

    @property
    def cot(self):
        return self._cot
    
    @property
    def ioa(self):
        return self._ioa
    
    
    def get_command_info(self):
        com_exec = self.sco.qoc_se
        #either cot is not 6 or 8, or the sco execute value is 1
        #meaning to prepare instead of execute
        com = "pass:" #whether to let packet pass through without checking
        if com_exec=="0" and (self._cot=="6" or self._cot=="8"):
            com_value = self.sco.scs
            com = "cutoff: "+str(self._ioa)+" "+str(com_value)
            #com = "cutoff: "+str(42)+" "+str(com_value)
            
        return com

    
class c_dc_na_1():
    '''
        class for the IEC 104 double command and double
        command with time tag (c_dc_ta_1)

        Command used to turn things off/on (will use to simulate branch cutoff command)
        
        DCO information element
        CoT <control>: 6 or 8
    '''
    def __init__(self,dcs,dco_qu,dco_se,cot,ioa):
        self.dco = infoEl.info_elem_dco(dcs,dco_qu,dco_se)
        self._cot = cot
        self._ioa = ioa
        
    
    @property
    def ioa(self):
        return self._ioa

    @property
    def cot(self):
        return self._cot

    def get_command_info(self):
        com_exec = self.dco.qoc_se
        com = "pass:"

        if com_exec=="0" and (self._cot=="6" or self._cot=="8"):
            #off is 1 and on is 2 so reduce by 1 so value is 0(off) and 1(on)
            com_value = self.dco.dcs - 1
            com = "cutoff: "+str(self._ioa)+" "+str(com_value)
            #com = "cutoff: "+str(45)+" "+str(com_value)

        return com
    
class c_rc_na_1():
    '''
        class for the IEC 104 regulating step command and
        regulating step command with time tag CP56Time2a (c_rc_ta_1)

        RCO information element
        CoT <control>: 6 or 8
    '''
    def __init__(self,rcs,rco_qu,rco_se,cot,ioa):
        self.rco = infoEl.info_elem_rco(rcs,rco_qu,rco_se)
        self._cot = cot
        self._ioa = ioa
        
    
    @property
    def ioa(self):
        return self._ioa

    @property
    def cot(self):
        return self._cot

    def get_command_info(self):
        com_exec = self.rco.qoc_se
        com = "pass:"
        
        if com_exec=="0" and (self._cot=="6" or self._cot=="8"):
            #rcs ramp down is 1 and ramp up is 2 so reduce by 1 so
            #value is 0(ramp down) and 1(ramp up)
            com_value = self.rco.rcs - 1
            com = "ramp: "+str(self._ioa)+" "+str(com_value)

        return com
    

class c_se_na_1():
    '''
        class for the IEC 104 set point command, normalized value
        and set point command, normalized value with time tag CP56Time2a
        (c_se_ta_1)

        NVA and QOS information elements
        CoT <control>: 6 or 8
    '''
    def __init__(self,nva,qos_ql,qos_se,cot,ioa):
        self.nva = infoEl.info_elem_nva(nva)
        self.qos = infoEl.info_elem_qos(qos_ql,qos_se)
        self._cot = cot
        self._ioa = ioa

    @property
    def ioa(self):
        return self._ioa

    @property
    def cot(self):
        return self._cot

    def get_command_info(self):
        com_exec = self.qos.se
        com = "pass:"
        
        if com_exec=="0" and (self._cot=="6" or self._cot=="8"):
            com_value = self.nva.nva
            com = "set_point_norm: "+str(self._ioa)+" "+str(com_value)

        return com

class c_se_nb_1():
    '''
        class for the IEC 104 set point command, scaled value and
        set point command, scaled value with time tag CP56Time2a (c_se_tb_1)

        SVA and QOS information elements
        CoT <control>: 6 or 8
    '''
    def __init__(self,sva,qos_ql,qos_se,cot,ioa):
        self.sva = infoEl.info_elem_sva(sva)
        self.qos = infoEl.info_elem_qos(qos_ql,qos_se)
        self._cot = cot
        self._ioa = ioa

    @property
    def ioa(self):
        return self._ioa

    @property
    def cot(self):
        return self._cot

    def get_command_info(self):
        com_exec = self.qos.se
        com = "pass:"
        
        if com_exec=="0" and (self._cot=="6" or self._cot=="8"):
            com_value = self.sva.sva
            com = "set_point_scaled: "+str(self._ioa)+" "+str(com_value)

        return com



class c_se_nc_1():
    '''
        class for the IEC 104 set point command, short floating point number
        and set point command, short floating point number with time tag
        CP56Time2a (c_se_tc_1)

        Short floating point number and QOS information elements
        CoT <control>: 6 or 8
    '''
    def __init__(self,sf,qos_ql,qos_se,cot,ioa):
        self.sf = infoEl.info_elem_shortfloat(sf)
        self.qos = infoEl.info_elem_qos(qos_ql,qos_se)
        self._cot = cot
        self._ioa = ioa

    @property
    def ioa(self):
        return self._ioa

    @property
    def cot(self):
        return self._cot

    def get_command_info(self):
        com_exec = self.qos.se
        com = "pass:"
        
        if com_exec=="0" and (self._cot=="6" or self._cot=="8"):
            com_value = self.sf.sf
            com = "set_point_sfp: "+str(self._ioa)+" "+str(com_value)

        return com



class c_bo_na_1():
    '''
        class for the IEC 104 bitstring of 32 bits and bitstring of 32 bits
        with time tag CP56Time2a (c_bo_ta_1)

        Not sure if the 32 bits would target a different device for each bit

        BSI information element
        CoT <control>: 6 or 8 (8 not allowed for c_bo_ta_1)
    '''


class c_ic_na_1():
    '''
        class for IEC 104 interrogation command

        QOI information element
        IOA=0
        CoT <control>: 6 or 8
    '''


class c_ci_na_1():
    '''
        class for IEC 104 counter interrogation command

        QCC information element
        IOA=0
        CoT <control>: 6 or 8
    '''


class c_cs_na_1():
    '''
        class for IEC 104 clock synchronization command

        CP56Time2a information element
        IOA=0
        Cot <control>: 6
    '''


class c_rp_na_1():
    '''
        class for IEC 104 reset process command

        QRP information element
        IOA=0
        CoT <control>:6
    '''

class c_ts_ta_1():
    '''
        class for IEC 104 test command with time tag CP56Time2a

        TSC information element
        IOA=0
        CoT <control>: 6 
    '''

class c_rd_na_1():
    '''
        class for IEC 104 read command

        only information object address
        CoT <control>: 5
    '''
