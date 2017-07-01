# Master_Thesis
Contains code and data used during my master thesis.

<b>Versions:</b><br/>
Java: <br/>
  - MATLAB communications was done by using the programs in matlabcontrol-4.1.0.jar obtained from the <a href="https://code.google.com/archive/p/matlabcontrol/">matlabcontrol</a> google code archive. <br/>

Python 2: Only the python programs in the IEC104TCP require Python 2. Original IEC104TCP was made by <a href="https://github.com/RocyLuo/IEC104TCP">RocyLuo</a>. <br/>
Python 3: All python programs except for IEC104TCP are in Python 3. <br/>
MATLAB: All MATLAB scripts were written in MATLAB R2017a. <br/>
Matpower: matpower6.0

<b>How to run:</b>
- Make sure that the matpower6.0 folder is in the same directory as the "SystemModel" folder and that the case24_ieee_rts_alpha.m file is with the matpower6.0 folder.
- Run the SystemModel.java program with the "SystemModel" folder and this will also start MATLAB.
  - Choose option for which violations to catch. Option "4" (Voltage and branch power violations) was option used for data.
- Run the commandTranslation.py program within the "IEC104Command with socket" folder
