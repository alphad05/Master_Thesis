%for newVals graphs in newVals folder. yindx contains branch being predicted. xindx and yindx are columns of branch in the newVals file.
xindx = [27 33 33 33 33 7 34];
yindx = [29 18 19 20 32 26 30];
branch = ["16-17->18-17" "23-20->13-11" "23-20->14-11" "23-20->13-12" "23-20->19-20" "3-24->15-24" "21-22->22-17"];

for m=1:size(xindx,2)
    x = newVals(:,xindx(m)); %newvals contains branch power data for each branch
    y = newVals(:,yindx(m)); 
    scatter(x,y)
    hold on
    X = [ones(length(x),1) x];
    b = X\y;
    yValue = X*b;
    
    %calculate coefficient of determination (R^2). 
    rsq = 1 - sum((y-yValue).^2)/sum((y-mean(y)).^2);
    
    plot(x,yValue) %plot line of fit
    xlabel('power values (p.u)')
    ylabel('power values (p.u)')
    title(strcat({'branch '},branch(m),{' y='},num2str(b(1)),{'+'},num2str(b(2)),{'x'},{' r^2='},num2str(rsq)))
    display(strcat({'branch '},branch(m),{' y='},num2str(b(1)),{'+'},num2str(b(2)),{'x'},{' r^2='},num2str(rsq)));
    grid on
    fname = strcat({'graphs\newVals\versusgraph'},num2str(m));
    print(fname{1},'-dpng')
    clf('reset')
end
