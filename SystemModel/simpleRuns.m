%used to generate power flow analysis data based on command. Commands can
%be type manually or stored in a variable.

run('matpower_paths.m');
define_constants

%run default case once
mpc = loadcase('case24_ieee_rts_alpha.m'); %case file
%mpc = loadcase('case9.m');
mpopt = mpoption;
savecase('prevsolcase.m',mpc);
results = runpf(mpc,mpopt);
checkViolations
if(numViolations > 0)
    results = loadcase('prevsolcase.m');
else
    results.bus(:,VM)
    results.branch(:,PF) / 100
    savecase('prevsolcase.m',results);
end
busTypes = [2;2;1;1;1;1;2;1;1;1;1;1;3;2;2;2;1;2;1;1;2;2;2;1];
%Sequence A Commands:
commanss =["load 1 .5" "cutoff 2 4 0" "cutoff 9 8 0" "load 9 1.097" "cutoff 10 5 0" "cutoff 16 15 0" "load 19 1.099" "load 18 1.498" "cutoff 21 22 0" "cutoff 1 3 0" "load 4 1.392" "cutoff 9 8 1" "load 8 1.497" "cutoff 10 8 0" "cutoff 21 22 1" "load 20 1.5" "cutoff 23 20 0" "load 10 1.4" "load 14 1.397" "cutoff 17 16 0" "cutoff 9 4 0" "cutoff 9 8 0" "load 1 1.296" "load 3 1.4" "cutoff 3 24 0" "load 14 1.199" "cutoff 16 15 1" "cutoff 1 3 1" "load 3 1.2976" "cutoff 3 24 0" "load 14 1.803" "cutoff 14 11 0" "load 18 1.4008" "load 9 .802" "load 1 1.5" "cutoff 16 15 1" "cutoff 10 5 1" "cutoff 10 8 1" "load 15 1.4984" "load 19 1.39698" "cutoff 19 20 0" "cutoff 13 23 0" "cutoff 22 17 0" "cutoff 19 20 1" "cutoff 21 15 0" "load 3 1.4006" "load 15 1.2" "load 19 1.2014" "load 9 1.2987" "load 2 1.2989" "cutoff 22 17 1" "load 19 1.2006" "load 4 0.7961" "load 8 1.1992" "cutoff 21 15 1" "cutoff 7 8 0" "load 1 1.4" "cutoff 13 11 0"];
commandI = 1;
while 1
    %get command: commands are either to increase/decrease load on a bus or cut
    %off a branch:
    %form of commands:
    %load Command: load [bus#] [amount to increase/decrease by]
    %cut off command: cutoff [from bus] [to bus] [status]
    %Amount to increase/decrease by for load is in percentage:
    %   example: load 4 .5   //this will decrease load by half
    %Status for cutoff command is either 1 (connected) or 0 (not connected)
    prompt = commanss(commandI); %comment out if entering commands manually
    %prompt = 'Enter your command: '; %uncomment if entering commands
    %manually
    commandI = commandI + 1;
    
    %get input and split command into parts
    %command = strsplit(input(prompt,'s')); %uncomment if entering commands
    %manually
    command = strsplit(prompt); %command out if entering commands manually
    if strcmp(command{1},'load')
        disp('command is load');
        bus = str2num(command{2});
        incdec = str2double(command{3});
        %mpc = loadcase('prevsolcase.m');
        results.bus(bus,PD) = results.bus(bus,PD)*incdec;
        results = runpf(results,mpopt);
    elseif strcmp(command{1},'cutoff')
        disp('command is cutoff');
        fbus = str2num(command{2});
        tbus = str2num(command{3});
        status = str2num(command{4});
        userBranch = strcat(num2str(fbus),{' '},num2str(tbus));

        br = results.branch(:,F_BUS);
        tBus = results.branch(:,T_BUS);
        branchStatusColumn = 11;
        branches = cell(size(br,1),1);
        for i=1:size(br,1)
            strr = strcat(num2str(br(i)), {' '}, num2str(tBus(i)));
            branches{i,1} = strr{1};
        end
        %get the index of the branch
        indxs = find(ismember(branches,userBranch{1}));
        results.branch(indxs,branchStatusColumn) = status;
        %change bus type back to what it was before finding new isolated
        results.bus(:,BUS_TYPE) = busTypes;
        [groups,isol] = find_islands(results)
        isolatedSize = size(isol)
        if isolatedSize(2) > 0
            for j=1:isolatedSize(:,1)
                isol(j)
                results.bus(isol(j),BUS_TYPE) = 4
            end
        end
        results = runpf(results,mpopt);
        results.bus
    else
        disp('command not valid');
    end
    checkViolations
    if numViolations > 0 | results.success==0
        disp('violations occured');
        results = loadcase('prevsolcase.m');
    else
        results.bus(:,VM)
        results.branch(:,PF) / 100
        savecase('prevsolcase.m',results);
    end
    numViolations
    disp(prompt)
    disp(command{1})
    pause
    
end
