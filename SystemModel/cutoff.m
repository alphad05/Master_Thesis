%original bus types with each bus
busTypes = [2;2;1;1;1;1;2;1;1;1;1;1;3;2;2;2;1;2;1;1;2;2;2;1];

%concatenate selected from and to bus values
userBranch = strcat(num2str(vals(1,1)),{' '},num2str(vals(1,2)));

br = mpc.branch(:,F_BUS);
tBus = mpc.branch(:,T_BUS);

%branch column that contains status of switch
branchStatusColumn = 11;
branches = cell(size(br,1),1);
%concatenate the from and to bus values
for i=1:size(br,1)
    strr = strcat(num2str(br(i)),{' '},num2str(tBus(i)));
	branches{i,1} = strr{1};
end

%find the branch index and change the connection status of the branch
indxs = find(ismember(branches,userBranch{1}));
mpc.branch(indxs,branchStatusColumn) = vals(1,3);

%check for islands in mpc after changing status of branch
%change bus type back to what it was before finding new isolated
mpc.bus(:,BUS_TYPE) = busTypes;
[groups,isol] = find_islands(mpc)
isolatedSize = size(isol)
if isolatedSize(2) > 0
    for j=1:isolatedSize(:,1)
        isol(j)
        mpc.bus(isol(j),BUS_TYPE) = 4
    end
end

results = runpf(mpc,mpopt,'solveoutput.txt','prevsolcase.m');
%whether or not powerflow was able to converge (1=success, 0=fail)
runSuccess = results.success; 
