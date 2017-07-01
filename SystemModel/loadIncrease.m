
%find row where bus is in the mpc.bus table
indBus = find(ismember(mpc.bus(:,1),vals(1,1)));
%vals will be in Amperes, kV, MW depending on compType
if strcmp(compType,'Load')
    %mpc.bus(vals(1,1),PD) = mpc.bus(vals(1,1),PD)*vals(1,2);
    %Column three is where power demand is located. Power demand is in MW
    mpc.bus(indBus,3) = vals(1,2);
elseif strcmp(compType,"Bus")
    %convert vals(1,2) which is the new value component should be to
    %per-unit. Column 10 contains the basekV of the bus
    compPerUnit = vals(1,2) / mpc.bus(vals(1,1),10);
    %column 8 of the bus rows contains the voltage magnitude to be changed
    mpc.bus(indBus,8) = compPerUnit;
end
results = runpf(mpc,mpopt,'solveoutput.txt','prevsolcase.m');
%whether or not powerflow was able to converge (1=success, 0=fail)
runSuccess = results.success;

    
    