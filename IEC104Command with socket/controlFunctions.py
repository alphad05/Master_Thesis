import controlCommands as cc

#function other translations functions would call with the field names they need
#if using this function make sure to convert values (int,float) in the classes
def generic_com_translation(asdu_info,func,value,qualifier,sel_exec,cot,ioa):
    type_class = type_identification_class[func] #class to create
    val = asdu_info.get_field(value)
    qual = asdu_info.get_field(qualifier)
    s_e = asdu_info.get_field(sel_exec)
    cause = asdu_info.get_field(cot)
    info_addr = asdu_info.get_field(ioa)
    type_class = type_class(val,qual,s_e,cause,info_addr)
   
    transcommand = type_class.get_command_info()
    return transcommand

#function to call if function type is 45 (C_SC_NA_1) or 58 (C_SC_TA_1)
def c_sc_translation(asdu_info,func):
    type_class = type_identification_class[func] #class to create
    scs = int(asdu_info.get_field("sco_on"),10)
    print(scs)
    sco_qu = asdu_info.get_field("sco_qu")
    sco_se = asdu_info.get_field("sco_se")
    cot = asdu_info.get_field("causetx")
    ioa = asdu_info.get_field("ioa")
    type_class = type_class(scs,sco_qu,sco_se,cot,ioa)
   
    transcommand = type_class.get_command_info()
    return transcommand


#function to call if function type is 46 (C_DC_NA_1) or 59 (C_DC_TA_1)
def c_dc_translation(asdu_info,func):
    type_class = type_identification_class[func]
    dcs = int(asdu_info.get_field("dco_on"),10)

    dco_qu = asdu_info.get_field("dco_qu")
    dco_se = asdu_info.get_field("dco_se")
    cot = asdu_info.get_field("causetx")
    ioa = asdu_info.get_field("ioa")
    type_class = type_class(dcs,dco_qu,dco_se,cot,ioa)
    transcommand = type_class.get_command_info()

    return transcommand



#function to call if function type is 47 (C_RC_NA_1) or 60 (C_RC_TA_1)
def c_rc_translation(asdu_info,func):
    type_class = type_identification_class[func]
    rcs = int(asdu_info.get_field("rco_up"),10)

    rco_qu = asdu_info.get_field("rco_qu")
    rco_se = asdu_info.get_field("rco_se")
    cot = asdu_info.get_field("causetx")
    ioa = asdu_info.get_field("ioa")

    type_class = type_class(rcs,rco_qu,rco_se,cot,ioa)
    transcommand = type_class.get_command_info()

    return transcommand


#function to call if function type is 48 (C_SE_NA_1) or 61 (C_SE_TA_1)
def c_se_na_translation(asdu_info,func):
    type_class = type_identification_class[func]
    #nva value is a decimal value 
    nva = float(asdu_info.get_field("normval"))
    qos_ql = asdu_info.get_field("qos_ql")
    qos_se = asdu_info.get_field("qos_se")
    cot = asdu_info.get_field("causetx")
    ioa = asdu_info.get_field("ioa")

    type_class = type_class(nva,qos_ql,qos_se,cot,ioa)
    transcommand = type_class.get_command_info()

    return transcommand


#function to call if function type is 49 (C_SE_NB_1) or 62 (C_SE_TB_1)
def c_se_nb_translation(asdu_info,func):
    type_class = type_identification_class[func]
    #sva stores either current, voltage, or power.
    #would have to distinguish by using configuration file, most likely
    sva = int(asdu_info.get_field("scalval"),10)
    qos_ql = asdu_info.get_field("qos_ql")
    qos_se = asdu_info.get_field("qos_se")
    cot = asdu_info.get_field("causetx")
    ioa = asdu_info.get_field("ioa")

    type_class = type_class(sva,qos_ql,qos_se,cot,ioa)
    transcommand = type_class.get_command_info()

    return transcommand

#function to call if function type is 50 (C_SE_NC_1) or 63 (C_SE_TC_1)
def c_se_nc_translation(asdu_info,func):
    type_class = type_identification_class[func]
    #stores short floating point number
    sf_val = float(asdu_info.get_field("float"))
    qos_ql = asdu_info.get_field("qos_ql")
    qos_se = asdu_info.get_field("qos_se")
    cot = asdu_info.get_field("causetx")
    ioa = asdu_info.get_field("ioa")

    type_class = type_class(sf_val,qos_ql,qos_se,cot,ioa)
    transcommand = type_class.get_command_info()

    return transcommand

#classes to create based on asdu type identification
type_identification_class = {45:cc.c_sc_na_1, 58:cc.c_sc_na_1, 46:cc.c_dc_na_1, 59:cc.c_dc_na_1,
                             47:cc.c_rc_na_1, 60:cc.c_rc_na_1, 48:cc.c_se_na_1, 61:cc.c_se_na_1,
                             49:cc.c_se_nb_1, 62:cc.c_se_nb_1, 50:cc.c_se_nc_1, 63:cc.c_se_nc_1
                             }
#method to call based on asdu type identification
type_functions = {45:c_sc_translation, 58:c_sc_translation, 46:c_dc_translation, 59:c_dc_translation,
                  47:c_rc_translation, 60:c_rc_translation, 48:c_se_na_translation, 61:c_se_na_translation,
                  49:c_se_nb_translation, 62:c_se_nb_translation, 50:c_se_nc_translation, 63:c_se_nc_translation
                  }
