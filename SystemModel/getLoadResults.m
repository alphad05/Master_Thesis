critSize = size(criticalComps);
cBusInfo = zeros(critSize(2),3);
%change load power to per unit
%results.bus(:,(3)) = results.bus(:,(3))/100;
for crit=1:critSize(2)
    cBus = criticalComps(crit);
    %find index of bus in the results.bus structure
    cbInd = find(ismember(results.bus(:,1),cBus));
    %bus information
    %get bus number, load info, and voltage at bus and store info
    cbInfo = results.bus(cbInd,[1 3 8]);
    cBusInfo(crit,:) = cbInfo;
end

%get generator data
%%[ [bus, gen real Power, gen real Power Max, gen Real Power Min, 
%    gen Reactive Power, gen Reactive Power Max, gen Reactive Power Min, gen power base, generator number]
%%]
%initiliazed to empty since we do not know how many generators will be
%added
cBusUpdateGens = [];
for crit=1:critSize(2)
    cBus = criticalComps(crit);
    %find index of bus in the mpc.gen structure, and there may be more
    %then one generator per bus
    cbInd = find(ismember(results.gen(:,1),cBus));
    numGens = size(cbInd);
    for gNum=1:numGens(1)
        %add to matrix, with the generator number for the bus.
       cBusUpdateGens = [cBusUpdateGens;results.gen(cbInd(gNum),[1 2 9 10 3 4 5]) mpc.baseMVA gNum];
    end
end

%get branch result data
%base on what reverse is the real power injected into the 'from' (PF) end of the
%branch will be used, or the real power injected into the 'to' (PT) end of the
%branch will be used.
%[ [from bus, to bus, branch power, branch status, reverse, branch number on bus] ]
cBranchUpdate = [];
%get the bus number that is labeled as the originating bus
cFBranch = results.branch(:,F_BUS);
cTBranch = results.branch(:,T_BUS);

for crit=1:critSize(2)
   cBus = criticalComps(crit);
   %find index of the bus in the cFBranch
   cbrFInd = find(ismember(cFBranch(:,1),cBus));
   %reverse will indicate whether the bus was found in the From (0) or To
   %(1) column
   reverse = 0;
   branNum = 1;
   %add the data if bus was found in the From branch, reverse is 0
   for bBranch=1:size(cbrFInd,1)
      power = max(abs(results.branch(cbrFInd(bBranch),14)),abs(results.branch(cbrFInd(bBranch),16)));
      %cBranchUpdate = [cBranchUpdate; results.branch(cbrFInd(bBranch),[1 2 14 11]) reverse branNum];
      cBranchUpdate = [cBranchUpdate; results.branch(cbrFInd(bBranch),[1 2]) power results.branch(cbrFInd(bBranch),11) reverse branNum];
      branNum = branNum + 1;
   end
   
   %find the bus number in the To branch also and add it, reverse is 1
   cbrFInd = find(ismember(cTBranch(:,1),cBus));
   reverse = 1;
   for bBranch=1:size(cbrFInd,1)
      power = max(abs(results.branch(cbrFInd(bBranch),14)),abs(results.branch(cbrFInd(bBranch),16)));
      %cBranchUpdate = [cBranchUpdate; results.branch(cbrFInd(bBranch),[1 2 14 11]) reverse branNum]; 
      cBranchUpdate = [cBranchUpdate; results.branch(cbrFInd(bBranch),[1 2]) power results.branch(cbrFInd(bBranch),11) reverse branNum];
      branNum = branNum + 1;
   end
end
