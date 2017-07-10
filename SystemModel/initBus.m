critSize = size(criticalComps);
cBusInitLoads = zeros(critSize(2),7); %change size
%[ [bus, bus voltage, max bus voltage, min bus voltage, load power, basekV, base load Power] ]
for crit=1:critSize(2)
    cBus = criticalComps(crit);
    %find index of bus in the mpc.bus structure
    cbInd = find(ismember(mpc.bus(:,1),cBus));
    %bus information
    %get bus number, load info, and voltage at bus and store info
    cbInfo = [mpc.bus(cbInd,[1 8 12 13 3 10]) mpc.baseMVA];
    %cbInfo(1,7) = mpc.baseMVA;
    cBusInitLoads(crit,:) = cbInfo;
end

%get generator data
%%[ [bus, gen real Power, gen real Power Max, gen Real Power Min, 
%    gen Reactive Power, gen Reactive Power Max, gen Reactive Power Min, gen power base, generator number on bus]
%%]
%initiliazed to empty since we do not know how many generators will be
%added
cBusInitGens = [];
for crit=1:critSize(2)
    cBus = criticalComps(crit);
    %find index of bus in the mpc.gen structure, and there may be more
    %then one generator per bus
    cbInd = find(ismember(mpc.gen(:,1),cBus));
    numGens = size(cbInd);
    for gNum=1:numGens(1)
        %add to matrix.
       cBusInitGens = [cBusInitGens;mpc.gen(cbInd(gNum),[1 2 9 10 3 4 5]) mpc.baseMVA gNum];
    end
end

%get branch data
%[ [from bus, to bus, max power allowed on branch, branch status, reverse, mpc.baseMVA, branch number on bus] ]
cBranchInit = [];
%get the bus number that is labeled as the originating bus
cFBranch = mpc.branch(:,F_BUS);
cTBranch = mpc.branch(:,T_BUS);

for crit=1:critSize(2)
   cBus = criticalComps(crit);
   %find index of the bus in the cFBranch
   cbrFInd = find(ismember(cFBranch(:,1),cBus));
   %reverse will indicate whether the bus was found in the From (0) or To
   %(1) column
   reverse = 0;
   branNum = 1;
   %add the data if bus was found in the From branch
   for bBranch=1:size(cbrFInd,1)
      cBranchInit = [cBranchInit; mpc.branch(cbrFInd(bBranch),[1 2 7 11]) reverse mpc.baseMVA branNum];
      branNum = branNum + 1;
   end
   %if the bus is not in the from field, look in the to field
   %if size(cbrFInd,1)==0
   %find the bus number in the To branch also and add it 
       cbrFInd = find(ismember(cTBranch(:,1),cBus));
       reverse = 1;
   %end
   for bBranch=1:size(cbrFInd,1)
      cBranchInit = [cBranchInit; mpc.branch(cbrFInd(bBranch),[1 2 7 11]) reverse mpc.baseMVA branNum]; 
      branNum = branNum + 1;
   end
end
