run('matpower_paths.m');
define_constants;

%run default case once
%mpc = loadcase('case24_ieee_rts_alpha.m');
mpopt = mpoption;
%results = runpf(mpc,mpopt,'solveoutput.txt','prevsolcase.m');

%while 1
    %get command: commands are either to increase/decrease load on a bus or cut
    %off a branch:
    %form of commands:
    %load Command: load [bus#] [amount to increase/decrease by]
    %cut off command: cutoff [from bus] [to bus] [status]
    %Amount to increase/decrease by for load is in percentage:
    %   example: load 4 .5   //this will decrease load by half
    %Status for cutoff command is either 1 (connected) or 0 (not connected)
    prompt = 'Enter your command: ';
    %get input and split command into parts
    command = strsplit(input(prompt,'s'));
    if strcmp(command{1},'load')
        disp('command is load');
        bus = str2num(command{2});
        incdec = str2double(command{3});
        mpc = loadcase('prevsolcase.m');
        mpc.bus(bus,PD) = mpc.bus(bus,PD)*incdec;
        results.runpf(mpc, runpf(mpc,mpopt,'solveoutput.txt','prevsolcase.m'));
    elseif strcmp(command{1},'cutoff')
        disp('command is cutoff');
        fbus = str2num(command{2});
        tbus = str2num(command{3});
        status = str2num(command{4});
        userBranch = strcat(num2str(fbus),{' '},num2str(tbus));
        %mpc = loadcase('prevsolcase.m');
        mpc = loadcase('prevsolcase.m');
        %mpc.branch()
        br = mpc.branch(:,F_BUS);
        tBus = mpc.branch(:,T_BUS);
        branchStatusColumn = 11;
        branches = cell(size(br,1),1);
        for i=1:size(br,1)
            strr = strcat(num2str(br(i)), {' '}, num2str(tBus(i)));
            branches{i,1} = strr{1};
        end
        %get the index of the branch
        indxs = find(ismember(branches,userBranch{1}));
        mpc.branch(indxs,branchStatusColumn) = status;
        results = runpf(mpc, runpf(mpc,mpopt,'solveoutput.txt','prevsolcase.m'));
        
    else
        disp('command not valid');
    end
    
%end
