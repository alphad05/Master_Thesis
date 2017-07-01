%this portion uses variable n from the dataforCorrelations.mat file which
%is branch cutoff data. 
%branches = ["1-2 vs 2-6" "1-3 vs 3-24" "1-5 vs 10-5" "2-4 vs 9-4" "2-6 vs 10-6" "9-3 vs 3-24" "9-4 vs 2-4" "10-5 vs 1-5" "3-24 vs 15-24" "10-6 vs 2-6" "7-8 vs 10-8" "9-8 vs 7-8" "10-8 vs 7-8" "13-11 vs 19-16" "14-11 vs 16-14" "9-11 vs 9-3" "13-12 vs 13-23" "23-12 vs 13-12" "13-23 vs 13-11" "16-14 vs 14-11" "16-15 vs 17-16" "21-15 vs 17-16" "15-24 vs 3-24" "17-16 vs 18-17" "19-16 vs 19-20" "18-17 vs 18-21" "9-12 vs 9-3" "22-17 vs 21-22" "18-21 vs 18-17" "19-20 vs 19-16" "23-20 vs 19-16" "21-22 vs 22-17" "10-12 vs 10-11" "10-11 vs 10-12"];
%xindx = [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 24 25 26 27 28 29 30 33 34 36 37 38];
%yindx = [5 9 8 7 10 9 4 3 24 5 13 11 11 26 20 6 19 17 14 15 25 25 9 27 32 30 6 36 27 26 26 29 38 37];

%this portion uses the loadIncr variable from the loadIncr_data_corr.mat
%file
%xindx = [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 27 28 29 30 31 32 34 36 38];
%yindx = [6 6 5 17 9 2 27 9 5 5 12 13 12 15 14 17 16 20 36 18 36 36 21 38 32 7 30 31 28 24 26 36 22 31];
%branches = ["1-2->(9-3)" "1-3->(9-3)" "1-5->(2-6)" "2-4->(10-12)" "2-6->(10-5)" "9-3->(1-3)" "3-24->(15-24)" "9-4->(10-5)" "10-5->(2-6)" "10-6->(2-6)" "7-8->(9-8)" "9-8->(10-8)" "10-8->(9-8)" "9-11->(9-12)" "9-12->(9-11)" "10-11->(10-12)" "10-12->(10-11)" "13-11->(13-12)" "14-11->(23-20)" "13-12->(13-11)" "23-12->(23-20)" "13-23->(23-20)" "16-14->(23-12)" "16-15->(21-22)" "21-15->(18-21)" "15-24->(3-24)" "17-16->(18-17)" "19-16->(22-17)" "18-17->(17-16)" "22-17->(16-15)" "18-21->(21-15)" "19-20->(23-20)" "23-20->(13-23)" "21-22 ->22-17"];

%this portion is for volt_all variable from the volt_all_data.mat
%xindx = [3 4 5 6 7 8 9 10 11 12 19 20 24];
%yindx = [9 9 10 10 8 7 11 11 10 10 20 19 3];
%buses = ["3->9" "4->9" "5->10" "6->10" "7->8" "8->7" "9->11" "10->11" "11->10"  "12->10" "19->20" "20->19" "24->3"];

%this portion combines the cutoff and load increase branch data, in the
%cutoff_increase_data. graphs located in all_power folder
%xindx = [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34];
%yindx = [6 6 6 7 1 2 4 6 23 34 13 13 12 17 20 17 14 31 31 15 20 29 9 26 30 24 17 32 22 31 30 28 18 17];
%branch = ["1-2->9-3" "1-3->9-3" "1-5->9-3" "2-4->9-4" "2-6->1-2" "9-3->1-3" "9-4->2-4" "10-5->9-3" "3-24->15-24" "10-6->10-11" "7-8->10-8" "9-8->10-8" "10-8->9-8" "13-11->13-12" "14-11->16-14" "9-11->13-12" "13-12->13-11" "23-12->23-20" "13-23->23-20" "16-14->14-11" "16-15->16-14" "21-15->18-21" "15-24->3-24" "17-16->18-17" "19-16->19-20" "18-17->17-16" "9-12->13-12" "22-17->21-22" "18-21->21-15" "19-20->23-20" "23-20->19-20" "21-22->22-17" "10-12->23-12" "10-11->13-12"];

%for new runs in new_runs data. graphs located in new_runs folder
%branch = ["1-2->2-4"	"9-3->3-24" "9-3->15-24" "9-12->13-12" "10-6->2-6" "16-14->13-11" "16-14->14-11" "23-12->19-20" "21-22->22-17" "18-21->18-17"];
%xindx = [1 6 6 15 10 23 23 21 34 31];
%yindx = [4 7 26	20 5 18 19 32 30 29];

%for newOnes in newOnes_data. graphs located in newOnes folder
%xindx=[1 10 17 6 6 27 28 28 34 33];
%yindx=[4 5 18 26 7 29 19 20 30 32];     
%branch=["1-2->2-4" "10-6>2-6" "10-12->13-11" "9-3->15-24" "9-3->3-24" "17-16->18-17" "19-16->14-11" "19-16->13-12" "21-22->22-17" "23-20->19-20"];

%xindx=[1 10 22 6 6 27 28 28 34 33];
%yindx=[4 5 18 26 7 29 19 20 30 32];     
%branch=["1-2->2-4" "10-6>2-6" "13-23->13-11" "9-3->15-24" "9-3->3-24" "17-16->18-17" "19-16->14-11" "19-16->13-12" "21-22->22-17" "23-20->19-20"];


%for new_run_results2_data graphs located in new_run_results2 folder
%xindx=[12 10 27 28 34 22 33 33 7];
%yindx=[4 5 29 32 30 20 18 19 26];        
%branch=["9-8->2-4" "10-6->2-6" "16-17->18-17" "16-19->19-20" "21-22->22-17" "23-13->13-12" "23-20->13-11" "23-20->14-11" "3-24->15-24"];


%for newVals graphs in newVals folder.
xindx = [27 33 33 33 33 7 34];
yindx = [29 18 19 20 32 26 30];
branch = ["16-17->18-17" "23-20->13-11" "23-20->14-11" "23-20->13-12" "23-20->19-20" "3-24->15-24" "21-22->22-17"];

for m=1:size(xindx,2)
    x = newVals(:,xindx(m)); %use n instead of loadIncr if using first portion or volt_all if using third portion or cutoff_increase
    y = newVals(:,yindx(m)); %use n instead of loadIncr if using first portion or volt_all if using third portion or cutoff_increase
    scatter(x,y)
    hold on
    X = [ones(length(x),1) x];
    b = X\y;
    %b = x\y;
    yValue = X*b;
    %yValue = b*x;
    %y = b(1) + b(2)x  -- line that is also plotted along with the scatter
    %plot
    
    %calculate coefficient of determination (R^2). Higher R^2 means better
    %prediction.
    rsq = 1 - sum((y-yValue).^2)/sum((y-mean(y)).^2);
    
    plot(x,yValue) %plot line of fit
    %xlabel('power values (p.u)')
    %ylabel('power values (p.u)')
    xlabel('power values (p.u)')
    ylabel('power values (p.u)')
    %title(strcat({'Branch '},branches(m),{' y='},num2str(b(1)),{'+'},num2str(b(2)),{'x'}))
    title(strcat({'branch '},branch(m),{' y='},num2str(b(1)),{'+'},num2str(b(2)),{'x'},{' r^2='},num2str(rsq)))
    display(strcat({'branch '},branch(m),{' y='},num2str(b(1)),{'+'},num2str(b(2)),{'x'},{' r^2='},num2str(rsq)));
    grid on
    %fname = strcat({'graphs\loadIncr_folder\versusgraph'},num2str(m));
    fname = strcat({'graphs\newVals\versusgraph'},num2str(m));
    print(fname{1},'-dpng')
    clf('reset')
end