/*******************************************************************************
 * File: MDSCSSController.java
 * Description:
 *
 ******************************************************************************/
package mdscssControl;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import mdscssModel.*;
import mdscssRoot.MMODFrame;


/* note install wireshark and https://nmap.org/npcap/ to do loopback testing of tcp messages */

public class MDSCSSController 
{
    private static final int SMSS_SOCKET = 27015;
    private static final int MCSS_SOCKET = 27016;
    private static final int TSS_SOCKET = 27017;
    private static final int SOCKET_TIMEOUT_MS = 2000;
    
    private InterceptorController inCtrl;
    private MissileDBManager mModel;
    private MMODFrame mView;
    private Thread controller;
    
    ArrayList<String> rejectedInterceptors;
    Missile curForgivingThreat;
    boolean forgivingRejected, forgivingApproved;
    Timestamp forgivingAssignTime;
    
    private boolean purgeWatchdog, manualLaunchMode;
    
    private Socket tssTCP, mcssTCP, smssTCP;
    Timestamp failureTimer;
    
    private int watchdogTime;
    
    private controlMode operationalState;
    public enum controlMode
    { 
        Manual,
        Automatic,
        Forgiving
    }
    
    /***************************************************************************
     * MDSCSSController
     * 
     * Constructor
     **************************************************************************/
    public MDSCSSController()
    {
        mModel = null;
        mView = null;
        inCtrl = null;
        
        purgeWatchdog = false;
        forgivingRejected = false;
        forgivingApproved = false;
        curForgivingThreat = null;
        watchdogTime = 0;
    }
    
    /***************************************************************************
     * initialize
     * 
     * initializes the controller on startup.  References to the view and 
     * model are established; initial socket connection to subsystems performed;
     * control thread is started
     * 
     * @param pModel - Reference to the Missile DB
     * @param pView - Reference to the GUI
     **************************************************************************/
    public void initialize(MissileDBManager pModel, MMODFrame pView) 
    {
        mModel = pModel;
        mView = pView;
        operationalState = controlMode.Manual;
        inCtrl = new InterceptorController();
        rejectedInterceptors = new ArrayList();
        controller = new Thread(new ControlThread(this));
        failureTimer = null;
        forgivingAssignTime = null;
        controller.start();
        manualLaunchMode = false;
    }
    
    
    public void handleThrustControl()
    {
        ArrayList<String> interceptors = mModel.getInterceptorList();
        Interceptor tmpI;
        Missile tmpT;

        for(int i = 0; i < interceptors.size(); i++)
        {
            tmpI = mModel.getInterceptor(interceptors.get(i));

            if(tmpI.getState() == Interceptor.interceptorState.IN_FLIGHT)
            {
                tmpT = mModel.getThreat(tmpI.getAssignedThreat());

                if(tmpT != null)
                {
                    inCtrl.trackMissilePair(tmpI, tmpT);

                    cmdMcssThrust(interceptors.get(i), tmpI.getThrustX(), tmpI.getThrustY(), tmpI.getThrustZ());
                }
                else
                {
                    System.out.println("manual control, tracking threat thats not assigned ERROR");
                }
            }
        }
                    
    }
    
    public void handleControlLogic()
    {
        switch(operationalState)
        {
            case Automatic:
                handleAutomaticControl();
                break;
            case Forgiving:
                handleForgivingControl();
                break;
            default:
                break;
        }
    }
    
    private void handleAutomaticControl()
    {
        ArrayList<String> interceptors = mModel.getInterceptorList();
        ArrayList<String> threats = mModel.getUnassignedThreats();
        Interceptor tmpI;
        Missile tmpT;
        
        ArrayList<String> aInts = new ArrayList(), bInts = new ArrayList(), cInts = new ArrayList();

        
        
        for(int i = 0; i < interceptors.size(); i++)
        {
            tmpI = mModel.getInterceptor(interceptors.get(i));
            tmpT = mModel.getThreat(tmpI.getAssignedThreat());
            
            // detonate interce ptors in range to their threat
            if(tmpI.getState() == Interceptor.interceptorState.IN_FLIGHT && 
               tmpI.isDetonateOverriden() == false &&
               tmpT != null)
            {
                int[] pos = tmpI.getPositionVector();
                int[] tPos = tmpT.getPositionVector();
                double distance = Math.sqrt(Math.pow((tPos[0] - pos[0]), 2) + Math.pow((tPos[1] - pos[1]), 2) + Math.pow((tPos[2] - pos[2]), 2));
                                
                if(distance <= (tmpI.getDetonationRange() - 8))
                {
                    cmdSmssDetEnable(interceptors.get(i));
                    cmdMcssDetonate(interceptors.get(i));
                }
            }
            else if(tmpI.getState() == Interceptor.interceptorState.PRE_FLIGHT &&
                    tmpI.isAssignmentOverriden() == false &&
                    tmpT == null)
            {
                switch(tmpI.getMissileClass())
                {
                    case 'A':
                        aInts.add(interceptors.get(i));
                        break;
                    case 'B':
                        bInts.add(interceptors.get(i));
                        break;
                    case 'C':
                        cInts.add(interceptors.get(i));
                        break;
                }
            }
            else if(tmpI.getState() == Interceptor.interceptorState.PRE_FLIGHT &&
                    tmpI.isAssignmentOverriden() == false &&
                    tmpT != null)
            {
                cmdMcssLaunch(interceptors.get(i));
            }
        }
        
        //assign threats to interceptors and launch
        for(int i = 0; i < threats.size(); )
        {
            tmpT = mModel.getThreat(threats.get(i));
            
            if(tmpT.getMissileClass() == 'Y')
            {
                if(cInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(cInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    cInts.remove(0);
                }
                else if(aInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(aInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    aInts.remove(0);
                }
                
                threats.remove(i);
            }
            else
            {
                i++;
            }  
        }
        
        for(int i = 0; i < threats.size(); )
        {
            tmpT = mModel.getThreat(threats.get(i));
            
            if(tmpT.getMissileClass() == 'Z')
            {
                if(bInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(bInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    bInts.remove(0);
                }
                else if(cInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(cInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    cInts.remove(0);
                }
                
                threats.remove(i);
            }
            else
            {
                i++;
            }  
        }
        
        for(int i = 0; i < threats.size(); )
        {
            tmpT = mModel.getThreat(threats.get(i));
            
            if(tmpT.getMissileClass() == 'X')
            {
                if(cInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(cInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    cInts.remove(0);
                }
                else if(aInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(aInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    aInts.remove(0);
                }
                else if(bInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(bInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    bInts.remove(0);
                }
                
                
                threats.remove(i);
            }
            else
            {
                i++;
            }  
        }
                
        
    }
    
    private boolean handleForgivingAssignment()
    {
        ArrayList<String> interceptors = mModel.getUnassignedInterceptors();
        ArrayList<String> aInts = new ArrayList(), bInts = new ArrayList(), cInts = new ArrayList();
        Interceptor tmpI;
        boolean newlyAssigned = false;
        
        for(int i = 0; i < interceptors.size(); i++)
        {
            tmpI = mModel.getInterceptor(interceptors.get(i));
            
            if(tmpI.getState() == Interceptor.interceptorState.PRE_FLIGHT &&
               tmpI.isAssignmentOverriden() == false && 
               !rejectedInterceptors.contains(interceptors.get(i)))
            {
                switch(tmpI.getMissileClass())
                {
                    case 'A':
                        aInts.add(interceptors.get(i));
                        break;
                    case 'B':
                        bInts.add(interceptors.get(i));
                        break;
                    case 'C':
                        cInts.add(interceptors.get(i));
                        break;
                }
            }
        }
        
        

            if(curForgivingThreat.getMissileClass() == 'Y')
            {
                if(cInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(cInts.get(0));
                    tmpI.setAssignedThreat(curForgivingThreat.getIdentifier());
                    newlyAssigned = true;
                }
                else if(aInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(aInts.get(0));
                    tmpI.setAssignedThreat(curForgivingThreat.getIdentifier());
                    newlyAssigned = true;
                }
            }
            else if(curForgivingThreat.getMissileClass() == 'Z')
            {
                if(bInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(bInts.get(0));
                    tmpI.setAssignedThreat(curForgivingThreat.getIdentifier());
                    newlyAssigned = true;
                }
                else if(cInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(cInts.get(0));
                    tmpI.setAssignedThreat(curForgivingThreat.getIdentifier());
                    newlyAssigned = true;
                }

            }
            else if(curForgivingThreat.getMissileClass() == 'X')
            {
                if(cInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(cInts.get(0));
                    tmpI.setAssignedThreat(curForgivingThreat.getIdentifier());
                    newlyAssigned = true;
                }
                else if(aInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(aInts.get(0));
                    tmpI.setAssignedThreat(curForgivingThreat.getIdentifier());
                    newlyAssigned = true;
                }
                else if(bInts.size()>0)
                {
                    tmpI = mModel.getInterceptor(bInts.get(0));
                    tmpI.setAssignedThreat(curForgivingThreat.getIdentifier());
                    newlyAssigned = true;
                }
            }

            return newlyAssigned;
        
    }
    
    private void handleForgivingControl()
    {
        ArrayList<String>  threats = mModel.getUnassignedThreats();
        Timestamp tmp = new Timestamp(System.currentTimeMillis());
        boolean bAssigned = false;
        

        if(threats.size() > 0)
        {

            if(curForgivingThreat == null)
            {

                rejectedInterceptors.clear();
                curForgivingThreat = mModel.getThreat(threats.get(0));
                bAssigned = handleForgivingAssignment();
                forgivingAssignTime = tmp;
                
                if(!bAssigned)
                {

                    curForgivingThreat = null;
                    
                    if(!manualLaunchMode)
                    {
                    forceManualAssignment();
                    manualLaunchMode = true;
                    }
                }
                else
                {
                    manualLaunchMode = false;
                }

            }
            else if(curForgivingThreat != null && forgivingRejected)
            {
                forgivingRejected = false;
                rejectedInterceptors.add(mModel.getAssignedInterceptor(curForgivingThreat.getIdentifier()));
                mModel.getInterceptor(mModel.getAssignedInterceptor(curForgivingThreat.getIdentifier())).setAssignedThreat("[UNASSIGNED]");
                forgivingAssignTime = tmp;
                
                bAssigned = handleForgivingAssignment();
       
                if(!bAssigned)
                {
                 
                    curForgivingThreat = null;
                    if(!manualLaunchMode)
                    {
                    forceManualAssignment();
                    manualLaunchMode = true;
                    }
                }
            }
        }
        
        if(curForgivingThreat != null &&
          ((tmp.getTime() - forgivingAssignTime.getTime()) >40000 || forgivingApproved))
        {
      
            cmdMcssLaunch(mModel.getAssignedInterceptor(curForgivingThreat.getIdentifier()));
            forgivingRejected = false;
            forgivingApproved = false;
            curForgivingThreat = null;
        }
        
        // todo:: handle detonation coverage
    }
    
    public String getForgivingAssignmentState()
    {
        if(curForgivingThreat != null && forgivingApproved == false && forgivingRejected == false)
            return ("Threat [" + curForgivingThreat.getIdentifier() + "] has been assigned to Interceptor ["+mModel.getAssignedInterceptor(curForgivingThreat.getIdentifier())+"]");
        else
            return null;
    }
    
    public void approveForgivingAssignment()
    {
        forgivingApproved = true;
    }
    
    public void rejectForgivingAssignment()
    {
        forgivingRejected = true;
    }

    private void forceManualAssignment()
    {
        ArrayList<String> interceptors = mModel.getInterceptorList();
        Interceptor tmpI;
        
        for(int i = 0; i < interceptors.size(); i++)
        {
            tmpI = mModel.getInterceptor(interceptors.get(i));
            tmpI.setAssignmentOverriden(true);
        }
        
        mView.handleLaunchModeChange();
    }
    
    public void setControlMode(controlMode pMode)
    {
        
        if(operationalState != pMode)
        {
        operationalState = pMode;
        rejectedInterceptors.clear();
        forgivingRejected = false;
        curForgivingThreat = null;
        forgivingAssignTime = null;
        forgivingApproved = false;
        }
    }
    
    public controlMode getControlMode()
    {
        return operationalState;
    }
    
    public void finalize()
    {        
            try
            {
                if(tssTCP != null)
                    tssTCP.close();
                if(mcssTCP != null)
                    mcssTCP.close();
                if(smssTCP != null)
                    smssTCP.close();
            } 
            catch(Exception ex)
            {
                System.out.println("MDSCSSController - finalize: socket failure\n" + ex.getMessage() +"\n");
            }
            
            controller.interrupt();
    }
    
    public boolean establishConnection()
    {
        boolean tssPass = false, mcssPass = false, smssPass = false;

        try{
            if(tssTCP == null)
            {

                tssTCP = new Socket("localhost", TSS_SOCKET);
                tssTCP.setSoTimeout(SOCKET_TIMEOUT_MS);
                tssPass = true;
            }
            else
            {
                tssPass = true;
            }
        }
        catch(Exception ex)
        {
            System.out.println("MDSCSSController - establishConnection: TSS socket failure\n" + ex.getMessage() +"\n");
        }
        
        try{
            if(mcssTCP == null)
            {
                
                mcssTCP = new Socket("localhost", MCSS_SOCKET);
                mcssTCP.setSoTimeout(SOCKET_TIMEOUT_MS);
                mcssPass = true;
            }
            else
            {
                mcssPass = true;
            }
        }
        catch(Exception ex)
        {
            System.out.println("MDSCSSController - establishConnection: MCSS socket failure\n" + ex.getMessage() +"\n");
        }
            
        try{
            if(smssTCP == null)
            {
                smssTCP = new Socket("localhost", SMSS_SOCKET);
                smssTCP.setSoTimeout(SOCKET_TIMEOUT_MS);
                smssPass = true;
            }
            else
            {
                smssPass = true;
            }
        }
        catch(Exception ex)
        {
            System.out.println("MDSCSSController - establishConnection: SMSS socket failure\n" + ex.getMessage() +"\n");
        }

        if(smssPass && tssPass && mcssPass)
        {
            failureTimer = null;
            return true;
            
        }
        else
        {
            return false;
        }

    }   
    
    public void checkForFailure()
    {
        Timestamp tmp = new Timestamp(System.currentTimeMillis());
        int i;
        ArrayList<String> interceptors = mModel.getInterceptorList();

        if(failureTimer != null && (tmp.getTime() - failureTimer.getTime()) >= 300000)
        {
            mView.handleCodeRed();
            
            failureTimer = null;
            
            purgeWatchdog = true;
            initializeWatchdog();
            
        }
    }
    
    public boolean establishStatus()
    {
        String tmp = "";
        
            if(tssTCP != null)
            {
                tmp = cmdTssGetVersion();
                if(tmp!= null)
                    mView.tssConnected(cmdTssGetVersion());
                else
                    return false;
            }
            
            if(mcssTCP != null)
            {
                tmp = cmdMcssGetVersion();
                if(tmp!= null)
                    mView.mcssConnected(tmp);
                else
                    return false;
            }
            
            if(smssTCP != null && mcssTCP != null)
            {
                ArrayList<String> interceptors = cmdMcssGetInterceptorList();
                
                if(interceptors.size() > 0)
                {
                    tmp = cmdSmssGetVersion(interceptors.get(0));
                    if(tmp!= null)
                        mView.smssConnected(tmp);
                    else
                    return false;
                }
                else
                {
                    mView.smssConnected("N/A");
                }
            }

           return true;
    }
    
    public void initializeModel()
    {
        ArrayList<String> missiles = cmdMcssGetInterceptorList();
        
        missiles.addAll(cmdTssGetThreatList());
        
        mModel.updateDatabase(missiles);
        
        mView.handleInitialUpdate();
    }
    
    public void initializeWatchdog()
    {
        int i;
        ArrayList<String> interceptors = mModel.getInterceptorList();
        
        for(i = 0; i < interceptors.size(); i++)
        {            
            cmdSmssActivateSafety(interceptors.get(i));
        }
        
        
    }
    
    public void disableWatchdog()
    {
         int i;
        ArrayList<String> interceptors = mModel.getInterceptorList();
        
        for(i = 0; i < interceptors.size(); i++)
        {
            cmdSmssDeactivateSafety(interceptors.get(i));
        }
    }
    
    public void updateModel()
    {
        ArrayList<String> missiles = mModel.getInterceptorList();
        Interceptor tmpInt;
        Missile tmpThreat;
        int pos[] = new int[3];
        
        
        
        for(int i = 0; i < missiles.size(); i++)
        {
            tmpInt = mModel.getInterceptor(missiles.get(i));
            
            tmpInt.setState(cmdMcssgetState(tmpInt.getIdentifier()));
            
            if(tmpInt.getState() != Interceptor.interceptorState.DETONATED)
            {
                
                pos = cmdTssTrackInterceptor(tmpInt.getIdentifier());
                //todo :: bug here if we get a non-req id (ie 3 digit ID this is null, and all of our representations are ....
                if(pos != null)
                tmpInt.setPosition(pos[0], pos[1], pos[2]);
            }
            else if(!tmpInt.getAssignedThreat().equals("[UNASSIGNED]"))
            {               
                tmpInt.setAssignedThreat("[UNASSIGNED]");
            }
        }
        
        missiles = mModel.getThreatList();
        
        for(int i = 0; i < missiles.size(); i++)
        {
            tmpThreat = mModel.getThreat(missiles.get(i));
            
            
            pos = cmdTssTrackThreat(tmpThreat.getIdentifier());
            
            if(pos != null)
            {
                tmpThreat.setPosition(pos[0], pos[1], pos[2]);
            }
            else
            {                
                ArrayList<String> threats = cmdTssGetThreatList();
                
                if(!threats.contains(tmpThreat.getIdentifier()))
                {
                    System.out.println(tmpThreat.getIdentifier() + " is no longer in the list TSS");
                    
                    //if theres an interceptor in flight after this threat, send a destruct, if theres one in preflight, unassign
                    ArrayList<String> interceptors = mModel.getInterceptorList();
                    for(int j = 0; j < interceptors.size(); j++)
                    {
                        tmpInt = mModel.getInterceptor(interceptors.get(j));
                        if(tmpInt.getAssignedThreat().equals(tmpThreat.getIdentifier()))
                        {
                            if(tmpInt.getState() == Interceptor.interceptorState.IN_FLIGHT)
                            {
                                cmdMcssDestruct(tmpInt.getIdentifier());
                            }
                            else
                            {
                                tmpInt.setAssignedThreat("[UNASSIGNED]");
                            }
                        }
                    }
                    
                    
                    mView.handleThreatDestruction(tmpThreat.getIdentifier());
                    mModel.removeThreat(tmpThreat.getIdentifier());
                }
                
            }
        }
        
        
    }
    
    public void handleWatchdogTimer()
    {
        ArrayList<String> interceptors = mModel.getInterceptorList();
        Interceptor tmpI;
        
        if(purgeWatchdog)
            return;
        
        watchdogTime++; 
        
        for(int i = 0; i < interceptors.size(); i++)
        {
            tmpI = mModel.getInterceptor(interceptors.get(i));
            
            if(tmpI.getState() != Interceptor.interceptorState.DETONATED)
            cmdSmssPingWatchdog(interceptors.get(i), watchdogTime);
        }
    }
    
    public void handleSocketFailure()
    {
        finalize();
        tssTCP = null;
        mcssTCP = null;
        smssTCP = null;
        

        failureTimer = new Timestamp(System.currentTimeMillis());
        mView.handleSubsystemFailure();
                
        controller = new Thread(new ControlThread(this));
        purgeWatchdog = false;
        controller.start();
    }
    
    /***************************************************************************
     *  TSS Command Interface
     **************************************************************************/
    
    public int[] cmdTssTrackThreat(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        int result[] = new int[3];
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[12];
        int bufferSize = 0;
        
        if(tssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(tssTCP.getOutputStream());
                buffIn = new DataInputStream(tssTCP.getInputStream());

                header[0] = 1;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));

                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if(returnHeader[3] == 0 && bufferSize == 12)
                {
                    buffIn.read(returnBuffer, 0, bufferSize);

                    result[0] = ((returnBuffer[0] & 0xff) << 24) | ((returnBuffer[1] & 0xff) << 16) | ((returnBuffer[2] & 0xff) << 8) | (returnBuffer[3] & 0xff);
                    result[1] = ((returnBuffer[4] & 0xff) << 24) | ((returnBuffer[5] & 0xff) << 16) | ((returnBuffer[6] & 0xff) << 8) | (returnBuffer[7] & 0xff);
                    result[2] = ((returnBuffer[8] & 0xff) << 24) | ((returnBuffer[9] & 0xff) << 16) | ((returnBuffer[10] & 0xff) << 8) | (returnBuffer[11] & 0xff);
                }
                else
                {
                    System.out.println("MDSCSSController - cmdTssTrackThreat: unexpected or failed response\n");
                    result = null;
                }
                
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdTssTrackThreat: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdTssTrackThreat: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public int[] cmdTssTrackInterceptor(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        int result[] = new int[3];
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[12];
        int bufferSize = 0;
        
        if(tssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(tssTCP.getOutputStream());
                buffIn = new DataInputStream(tssTCP.getInputStream());

                header[0] = 2;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));

                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if(returnHeader[3] == 0 && bufferSize == 12)
                {
                    buffIn.read(returnBuffer, 0, bufferSize);

                    result[0] = ((returnBuffer[0] & 0xff) << 24) | ((returnBuffer[1] & 0xff) << 16) | ((returnBuffer[2] & 0xff) << 8) | (returnBuffer[3] & 0xff);
                    result[1] = ((returnBuffer[4] & 0xff) << 24) | ((returnBuffer[5] & 0xff) << 16) | ((returnBuffer[6] & 0xff) << 8) | (returnBuffer[7] & 0xff);
                    result[2] = ((returnBuffer[8] & 0xff) << 24) | ((returnBuffer[9] & 0xff) << 16) | ((returnBuffer[10] & 0xff) << 8) | (returnBuffer[11] & 0xff);
                }
                else
                {
                    System.out.println("MDSCSSController - cmdTssTrackInterceptor: unexpected or failed response\n");
                    result = null;
                }
                
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdTssTrackInterceptor: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
                result = null;
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdTssTrackInterceptor: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public ArrayList<String> cmdTssGetThreatList()
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        ArrayList<String> result = new ArrayList();
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[2];
        int bufferSize = 0;
        
        if(tssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(tssTCP.getOutputStream());
                buffIn = new DataInputStream(tssTCP.getInputStream());

                header[0] = 5;
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if(returnHeader[3] == 0 && bufferSize > 0)
                {
                    result = new ArrayList();

                    for(int i = 0; i < bufferSize; i+=2)
                    {
                        buffIn.read(returnBuffer, 0, 2);
                        result.add(new String(returnBuffer, "UTF-8"));
                    }
                }
                else
                {
                    System.out.println("MDSCSSController - cmdTssGetThreatList: unexpected or failed response\n");
                }                
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdTssGetThreatList: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdTssGetThreatList: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            }  
        }

        
        return result;
    }
    
    public String cmdTssGetVersion()
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;
        
        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;
        
        if(tssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(tssTCP.getOutputStream());
                buffIn = new DataInputStream(tssTCP.getInputStream());

                header[0] = 6;
                buffOut.write(header, 0, 5);

                buffIn.read(returnBuffer, 0, 6);
                bufferSize = ((returnBuffer[4] & 0xff) << 8) | (returnBuffer[5] & 0xff);

                if(returnBuffer[3] == 0)
                {
                    buffIn.read(returnBuffer, 0, bufferSize);
                    result = new String(returnBuffer, "UTF-8");

                    result = result.substring(27, 30);
                }
                else
                {
                    System.out.println("MDSCSSController - cmdTssGetVersion: unexpected or failed response\n");
                }
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdTssGetVersion: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdTssGetVersion: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    /***************************************************************************
     *  MCSS Command Interface
     **************************************************************************/
    
    public boolean cmdMcssLaunch(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 1;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssLaunch: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssLaunch: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    
    public boolean cmdMcssThrust(String missileID, int pwrX, int pwrY, int pwrZ)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        int tmp;
        
        byte[] header = new byte[53];
        byte[] returnHeader = new byte[6];
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 2;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                
                // header will be 48 bytes long
                header[4] = (byte)48;    
                
                // set the thrust time values all to 1 second
                header[8] = header[12] = header[16] = header[20] = header[24] = header[28] = 0x6;
                
                if(pwrX > 0)
                {
                    tmp = Math.abs(pwrX);
                    header[29] = (byte)((tmp >> 24) & 0xff);
                    header[30] = (byte)((tmp >> 16) & 0xff);
                    header[31] = (byte)((tmp >> 8) & 0xff);
                    header[32] = (byte)(tmp & 0xff);
                }
                else
                {
                    tmp = Math.abs(pwrX);
                    header[33] = (byte)((tmp >> 24) & 0xff);
                    header[34] = (byte)((tmp >> 16) & 0xff);
                    header[35] = (byte)((tmp >> 8) & 0xff);
                    header[36] = (byte)(tmp & 0xff);
                }
                
                if(pwrY > 0)
                {
                    tmp = Math.abs(pwrY);
                    header[37] = (byte)((tmp >> 24) & 0xff);
                    header[38] = (byte)((tmp >> 16) & 0xff);
                    header[39] = (byte)((tmp >> 8) & 0xff);
                    header[40] = (byte)(tmp & 0xff);
                }
                else
                {
                    tmp = Math.abs(pwrY);
                    header[41] = (byte)((tmp >> 24) & 0xff);
                    header[42] = (byte)((tmp >> 16) & 0xff);
                    header[43] = (byte)((tmp >> 8) & 0xff);
                    header[44] = (byte)(tmp & 0xff);
                }
                
                if(pwrZ > 0)
                {
                    tmp = Math.abs(pwrZ);
                    header[45] = (byte)((tmp >> 24) & 0xff);
                    header[46] = (byte)((tmp >> 16) & 0xff);
                    header[47] = (byte)((tmp >> 8) & 0xff);
                    header[48] = (byte)(tmp & 0xff);
                }
                else
                {
                    tmp = Math.abs(pwrZ);
                    header[49] = (byte)((tmp >> 24) & 0xff);
                    header[50] = (byte)((tmp >> 16) & 0xff);
                    header[51] = (byte)((tmp >> 8) & 0xff);
                    header[52] = (byte)(tmp & 0xff);
                }
                
                buffOut.write(header, 0, 53);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssThrust: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssThrust: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public boolean cmdMcssDetonate(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 3;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssDetonate: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssDetonate: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            }  
        }
        
        return result;
    }
    
    public boolean cmdMcssDestruct(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 4;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssDestruct: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssDestruct: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public ArrayList<String> cmdMcssGetInterceptorList()
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        ArrayList<String> result = new ArrayList();
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[2];
        int bufferSize = 0;
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 5;
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if(returnHeader[3] == 0 && bufferSize > 0)
                {
                    for(int i = 0; i < bufferSize; i+=2)
                    {
                        buffIn.read(returnBuffer, 0, 2);
                        result.add(new String(returnBuffer, "UTF-8"));
                    }
                }
                else
                {
                    System.out.println("MDSCSSController - cmdMcssGetInterceptorList: unexpected or failed response\n");
                }
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssGetInterceptorList: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssGetInterceptorList: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public Interceptor.interceptorState cmdMcssgetState(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        Interceptor.interceptorState result = Interceptor.interceptorState.UNDEFINED;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[25];
        int bufferSize = 0;
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 6;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if(returnHeader[3] == 0 && bufferSize == 25)
                {
                    buffIn.read(returnBuffer, 0, 25);

                    switch (returnBuffer[0]) {
                        case 0:
                            result = Interceptor.interceptorState.DETONATED;
                            break;
                        case 1:
                            result = Interceptor.interceptorState.PRE_FLIGHT;
                            break;
                        case 2:
                            result = Interceptor.interceptorState.IN_FLIGHT;
                            break;
                        default:
                            break;
                    }
                }
                else
                {
                    System.out.println("MDSCSSController - cmdMcssgetState: unexpected or failed response\n");
                }
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssgetState: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssgetState: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public int[] cmdMcssGetLaunchSite(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        int result[] = new int[3];
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[12];
        int bufferSize = 0;
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 7;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));

                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if(returnHeader[3] == 0 && bufferSize == 12)
                {
                    buffIn.read(returnBuffer, 0, bufferSize);

                    result[0] = ((returnBuffer[0] & 0xff) << 24) | ((returnBuffer[1] & 0xff) << 16) | ((returnBuffer[2] & 0xff) << 8) | (returnBuffer[3] & 0xff);
                    result[1] = ((returnBuffer[4] & 0xff) << 24) | ((returnBuffer[5] & 0xff) << 16) | ((returnBuffer[6] & 0xff) << 8) | (returnBuffer[7] & 0xff);
                    result[2] = ((returnBuffer[8] & 0xff) << 24) | ((returnBuffer[9] & 0xff) << 16) | ((returnBuffer[10] & 0xff) << 8) | (returnBuffer[11] & 0xff);
                    System.out.println(result[0]);
                }
                else
                {
                    System.out.println("MDSCSSController - cmdMcssGetLaunchSite: unexpected or failed response\n");
                    result = null;
                }   
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssGetLaunchSite: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssGetLaunchSite: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public String cmdMcssGetVersion()
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;
        
        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 8;
                buffOut.write(header, 0, 5);

                buffIn.read(returnBuffer, 0, 6);
                bufferSize = ((returnBuffer[4] & 0xff) << 8) | (returnBuffer[5] & 0xff);

                buffIn.read(returnBuffer, 0, bufferSize);
                result = new String(returnBuffer, "UTF-8");

                result = result.substring(26, 29);    
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssGetVersion: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssGetVersion: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }

        return result;
    }
    
    public String cmdMcssGetCtrlVersion(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;
        
        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 9;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnBuffer, 0, 6);
                bufferSize = ((returnBuffer[4] & 0xff) << 8) | (returnBuffer[5] & 0xff);

                buffIn.read(returnBuffer, 0, bufferSize);
                result = new String(returnBuffer, "UTF-8");
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssGetCtrlVersion: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssGetCtrlVersion: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    /***************************************************************************
     *  SMSS Command Interface
     **************************************************************************/
    
    public boolean cmdSmssActivateSafety(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(smssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 1;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);             
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdSmssActivateSafety: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdSmssActivateSafety: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public boolean cmdSmssDeactivateSafety(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(smssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 2;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdSmssDeactivateSafety: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdSmssDeactivateSafety: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            }  
            
        }
        
        return result;
    }
    
    public boolean cmdSmssPingWatchdog(String missileID, int pTimer)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[9];
        byte[] returnHeader = new byte[6];

        if(smssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                // requires an MID? wtf?
                header[0] = 3;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                header[4] = 4;
                header[5] = (byte)((pTimer >> 24) & 0xff);
                header[6] = (byte)((pTimer >> 16) & 0xff);
                header[7] = (byte)((pTimer >> 8) & 0xff);
                header[8] = (byte)(pTimer & 0xff);
                buffOut.write(header, 0, 9);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdSmssPingWatchdog: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdSmssPingWatchdog: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public boolean cmdSmssDetEnable(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(smssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 4;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdSmssDetEnable: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdSmssDetEnable: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public boolean cmdSmssDestruct(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(smssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 5;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdSmssDestruct: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdSmssDestruct: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            }  
        }
        
        return result;
    }
    
    public String cmdSmssGetVersion(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;
        
        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;
        
        if(smssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 6;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));

                buffOut.write(header, 0, 5);

                buffIn.read(returnBuffer, 0, 6);
                bufferSize = ((returnBuffer[4] & 0xff) << 8) | (returnBuffer[5] & 0xff);

                buffIn.read(returnBuffer, 0, bufferSize);
                result = new String(returnBuffer, "UTF-8");
                result = result.substring(14,18);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdSmssGetVersion: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdSmssGetVersion: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
}
