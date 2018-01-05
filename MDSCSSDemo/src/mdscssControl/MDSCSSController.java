package mdscssControl;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.ArrayList;

import mdscssModel.*;
import mdscssRoot.MMODFrame;

/** *****************************************************************************
 * The MDSCSSController is a utility object utilized by the control thread.  This object contains
 * interface definitions to the major subsystems, and utility functions utilized by the
 * control thread to control the system.
 ***************************************************************************** */
public class MDSCSSController {

    // Connection Constants
    private static final int SMSS_SOCKET = 27015;
    private static final int MCSS_SOCKET = 27016;
    private static final int TSS_SOCKET = 27017;
    private static final int SOCKET_TIMEOUT_MS = 2000;

    // System pointers
    private InterceptorController inCtrl;
    private MissileDBManager mModel;
    private MMODFrame mView;
    private Thread controller;

    // Forgiving control assignment variables
    private ArrayList<String> fgvRejectInterceptors;
    private Missile fgvCurrentThreat;
    private boolean bFgvAssignmentRejected, bFgvAssignmentApproved, bFgvAssignmentDone, bForceManualLaunch;
    private Timestamp fgvAssignmentTime;

    // Forgiving control detonation variables
    private Interceptor fgvCurrentInterceptor;
    private boolean bFgvDetonateRejected, bFgvDetonateApproved;
    private Timestamp bFgvDetonateTime;

    // Watchdog control variables
    private boolean bPurgeWatchdog;
    private int watchdogTime;

    // Socket control variables
    private Socket tssTCP, mcssTCP, smssTCP;
    Timestamp failureTimer;

    // System control variables
    private controlMode operationalState;

    public enum controlMode {
        Manual,
        Automatic,
        Forgiving
    }

    /***************************************************************************
     * Constructor
     **************************************************************************/
    public MDSCSSController() {
        mModel = null;
        mView = null;
        inCtrl = null;
        fgvCurrentThreat = null;
        fgvCurrentInterceptor = null;
        fgvAssignmentTime = null;
        failureTimer = null;
        bFgvDetonateTime = null;

        bPurgeWatchdog = false;
        bFgvAssignmentRejected = false;
        bFgvAssignmentApproved = false;
        bFgvDetonateApproved = false;
        bFgvDetonateRejected = false;
        bFgvAssignmentDone = false;
        bForceManualLaunch = false;

        watchdogTime = 0;
    }

    /***************************************************************************
     * The initialize function initializes the controller on startup. 
     * References to the view and model are established and initial socket 
     * connections to the subsystems are performed the function also ensures
     * that the control thread is started.
     *
     * @param pModel Reference to the Missile DB Manager object
     * @param pView Reference to the MMODFrame GUI Object
     **************************************************************************/
    public void initialize(MissileDBManager pModel, MMODFrame pView) {
        mModel = pModel;
        mView = pView;
        inCtrl = new InterceptorController();
        fgvRejectInterceptors = new ArrayList();

        operationalState = controlMode.Manual;

        controller = new Thread(new ControlThread(this));
        controller.start();
    }

    /***************************************************************************
     * The finalize function safely closes each TCP socket to the subsystems and 
     * stops the Control Thread.
     **************************************************************************/
    public void finalize() {
        try {
            if (tssTCP != null) {
                tssTCP.close();
            }
            if (mcssTCP != null) {
                mcssTCP.close();
            }
            if (smssTCP != null) {
                smssTCP.close();
            }
        } catch (Exception ex) {
            System.out.println("MDSCSSController - finalize: socket failure\n" + ex.getMessage() + "\n");
        }

        controller.interrupt();
    }

    /** *************************************************************************
     * The establishConnection function is called by the control thread to 
     * establish a connection with all the subsystems.
     *
     * @return true if all subsystems have been connected over TCP, false if there
     * is at least one subsystem that has been unable to connect.
     **************************************************************************/
    public boolean establishConnection() {
        boolean tssPass = false, mcssPass = false, smssPass = false;

        

        try {
            if (mcssTCP == null) {

                mcssTCP = new Socket("localhost", MCSS_SOCKET);
                mcssTCP.setSoTimeout(SOCKET_TIMEOUT_MS);
                mcssPass = true;
                disableWatchdog();
            } else {
                mcssPass = true;
            }
        } catch (Exception ex) {
            System.out.println("MDSCSSController - establishConnection: MCSS socket failure\n" + ex.getMessage() + "\n");
        }

        try {
            if (smssTCP == null) {
                smssTCP = new Socket("localhost", SMSS_SOCKET);
                smssTCP.setSoTimeout(SOCKET_TIMEOUT_MS);
                smssPass = true;
                disableWatchdog();
            } else {
                smssPass = true;
            }
        } catch (Exception ex) {
            System.out.println("MDSCSSController - establishConnection: SMSS socket failure\n" + ex.getMessage() + "\n");
        }

        
        try {
            if (tssTCP == null) {

                tssTCP = new Socket("localhost", TSS_SOCKET);
                tssTCP.setSoTimeout(SOCKET_TIMEOUT_MS);
                tssPass = true;
            } else {
                tssPass = true;
            }
        } catch (Exception ex) {
            System.out.println("MDSCSSController - establishConnection: TSS socket failure\n" + ex.getMessage() + "\n");
        }
        
        if (smssPass && tssPass && mcssPass) {
            failureTimer = null;
            return true;

        } else {
            return false;
        }

    }

    /***************************************************************************
     * The establishStatus function is called by the control thread to get the 
     * version of each subsystem, as well as their connection status. 
     * This function also pushes this data to the MMODFrame so that the operator 
     * is notified of each subsystem's connection status and version.
     * 
     * @return true if all subsystems versions have been retrieved, false if there
     * is at least one subsystem that has been unable to be contacted for its version.
     **************************************************************************/
    public boolean establishStatus() {
        String tmp = "";

        if (tssTCP != null) {
            tmp = cmdTssGetVersion();
            if (tmp != null) {
                mView.tssConnected(cmdTssGetVersion());
            } else {
                return false;
            }
        }

        if (mcssTCP != null) {
            tmp = cmdMcssGetVersion();
            if (tmp != null) {
                mView.mcssConnected(tmp);
            } else {
                return false;
            }
        }

        if (smssTCP != null && mcssTCP != null) {
            ArrayList<String> interceptors = cmdMcssGetInterceptorList();

            if (interceptors.size() > 0) {
                tmp = cmdSmssGetVersion(interceptors.get(0));
                if (tmp != null) {
                    mView.smssConnected(tmp);
                } else {
                    return false;
                }
            } else {
                mView.smssConnected("N/A");
            }
        }

        return true;
    }

    /***************************************************************************
     * The handleSocketFailure function is called when connection to a subsystem 
     * has been lost, or communication has been deemed erroneous. This function 
     * closes the TCP sockets, resets the controller's internal state, and 
     * begins monitoring of the process to establish reconnection with the subsystems.
     **************************************************************************/
    private void handleSocketFailure() {
        finalize();
        tssTCP = null;
        mcssTCP = null;
        smssTCP = null;

        failureTimer = new Timestamp(System.currentTimeMillis());
        mView.handleSubsystemFailure();

        controller = new Thread(new ControlThread(this));
        bPurgeWatchdog = false;
        setControlMode(operationalState);
        controller.start();
    }

    /***************************************************************************
     * The handleSocketFailure function is called when the control thread is 
     * attempting to establish connection. if connection is attempting to be 
     * re-established after a failure, this function maintains track if 5 minutes 
     * have elapsed since the failure.  If not, this function alerts the GUI 
     * and detonates all airborne interceptors.
     **************************************************************************/
    public void checkForFailure() {
        Timestamp tmp = new Timestamp(System.currentTimeMillis());

        if (failureTimer != null && (tmp.getTime() - failureTimer.getTime()) >= 5000) {
            mView.handleCodeRed();

            failureTimer = null;

            bPurgeWatchdog = true;
            initializeWatchdog();
            
            mModel.updateDatabase(new ArrayList<String>());

        }
    }

    /***************************************************************************
     * The initializeModel function is called by the control thread when connection 
     * is first established.  This function populates the internal missile database.
     **************************************************************************/
    public void initializeModel() {
        ArrayList<String> missiles = cmdMcssGetInterceptorList();

        missiles.addAll(cmdTssGetThreatList());

        mModel.updateDatabase(missiles);

        mView.handleInitialUpdate();
    }

    /***************************************************************************
     * The initializeWatchdog function is called by the control thread after 
     * connection with the subsystems and the missile database is initialized.  
     * This function enables the safety for all interceptors.
     **************************************************************************/
    public void initializeWatchdog() {
        int i;
        ArrayList<String> interceptors = cmdMcssGetInterceptorList();

        watchdogTime +=2;
        
        for (i = 0; i < interceptors.size(); i++) {
            cmdSmssActivateSafety(interceptors.get(i));
        }

    }

    /***************************************************************************
     * The disableWatchdog function disables the safety for all interceptors. 
     **************************************************************************/
    public void disableWatchdog() {
        int i;
        ArrayList<String> interceptors = cmdMcssGetInterceptorList();

        for (i = 0; i < interceptors.size(); i++) {
            cmdSmssDeactivateSafety(interceptors.get(i));
        }
    }

    /***************************************************************************
     * The handleWatchdogTimer function is called by the control thread every 0.5 seconds to notify
     * the SMSS that the MDSCSS is still operational.
     **************************************************************************/
    public void handleWatchdogTimer() {
        ArrayList<String> interceptors = mModel.getInterceptorList();
        Interceptor tmpI;
        watchdogTime++;

        for (int i = 0; i < interceptors.size(); i++) {
            tmpI = mModel.getInterceptor(interceptors.get(i));
            
            if (bPurgeWatchdog && tmpI.getState() == Interceptor.interceptorState.IN_FLIGHT) {
                continue;
            }

            if (tmpI.getState() != Interceptor.interceptorState.DETONATED) {
                cmdSmssPingWatchdog(interceptors.get(i), watchdogTime);
            }
        }
    }

    /***************************************************************************
     * The updateModel function is called by the control thread every 0.5 seconds to update
     * the internal missile database entries with new information from the TSS and MCSS.
     **************************************************************************/
    public void updateModel() {
        ArrayList<String> missiles = mModel.getInterceptorList();
        Interceptor tmpInt;
        Missile tmpThreat;
        int pos[] = new int[3];

        for (int i = 0; i < missiles.size(); i++) {
            tmpInt = mModel.getInterceptor(missiles.get(i));

            tmpInt.setState(cmdMcssgetState(tmpInt.getIdentifier()));

            if (tmpInt.getState() != Interceptor.interceptorState.DETONATED) {

                pos = cmdTssTrackInterceptor(tmpInt.getIdentifier());
                //todo :: bug here if we get a non-req id (ie 3 digit ID this is null, and all of our representations are ....
                if (pos != null) {
                    tmpInt.setPosition(pos[0], pos[1], pos[2]);
                }
            } /*else if (!tmpInt.getAssignedThreat().equals("[UNASSIGNED]")) {
                tmpInt.setAssignedThreat("[UNASSIGNED]");
            }TODO123*/
        }

        missiles = mModel.getThreatList();

        for (int i = 0; i < missiles.size(); i++) {
            tmpThreat = mModel.getThreat(missiles.get(i));

            pos = cmdTssTrackThreat(tmpThreat.getIdentifier());

            if (pos != null) {
                tmpThreat.setPosition(pos[0], pos[1], pos[2]);
            } else {
                ArrayList<String> threats = cmdTssGetThreatList();

                if (!threats.contains(tmpThreat.getIdentifier())) {

                    //if theres an interceptor in flight after this threat, send a destruct, if theres one in preflight, unassign
                    ArrayList<String> interceptors = mModel.getInterceptorList();
                    for (int j = 0; j < interceptors.size(); j++) {
                        tmpInt = mModel.getInterceptor(interceptors.get(j));
                        if (tmpInt.getAssignedThreat().equals(tmpThreat.getIdentifier())) {
                            if (tmpInt.getState() == Interceptor.interceptorState.IN_FLIGHT) {
                                cmdMcssDestruct(tmpInt.getIdentifier());
                                tmpInt.setAssignedThreat("[UNASSIGNED]");
                            } else {
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

    /***************************************************************************
     * The handleThrustControl function is called by the control thread every second to determine
     * the power levels of each in-flight interceptor and send the thrust commands.
     **************************************************************************/
    public void handleThrustControl() {
        ArrayList<String> interceptors = mModel.getInterceptorList();
        Interceptor tmpI;
        Missile tmpT;

        for (int i = 0; i < interceptors.size(); i++) {
            tmpI = mModel.getInterceptor(interceptors.get(i));

            if (tmpI.getState() == Interceptor.interceptorState.IN_FLIGHT) {
                tmpT = mModel.getThreat(tmpI.getAssignedThreat());

                if (tmpT != null) {
                    inCtrl.trackMissilePair(tmpI, tmpT);

                    cmdMcssThrust(interceptors.get(i), tmpI.getThrustX(), tmpI.getThrustY(), tmpI.getThrustZ());
                } 
            }
        }

    }

    /***************************************************************************
     * The getControlMode function retrieves the current system control mode.
     * 
     * @return A controlMode enumeration value that corresponds with the systems
     *         control state
     **************************************************************************/
    public controlMode getControlMode() {
        return operationalState;
    }

    /***************************************************************************
     * The setControlMode function sets the system control mode.
     * 
     * @param pMode The new system control mode
     **************************************************************************/
    public void setControlMode(controlMode pMode) {
        if (operationalState != pMode) {
            operationalState = pMode;
            fgvRejectInterceptors.clear();
            bFgvAssignmentRejected = false;
            fgvCurrentThreat = null;
            fgvAssignmentTime = null;
            bFgvDetonateTime = null;
            bFgvAssignmentApproved = false;
            fgvCurrentInterceptor = null;
            bFgvDetonateApproved = false;
            bFgvDetonateRejected = false;
            bFgvAssignmentDone = false;
        }
    }

    /***************************************************************************
     * The handleControlLogic function is called periodically by the control thread to 
     * handle automatic or forgiving control logic dictated by the system control mode.
     **************************************************************************/
    public void handleControlLogic() {
        switch (operationalState) {
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

    /***************************************************************************
     * The handleForgivingControl function handles the logic needed in order to 
     * perform forgiving assignment and detonation.
     **************************************************************************/
    private void handleForgivingControl() {
        ArrayList<String> threats = mModel.getUnassignedThreats();
        ArrayList<String> interceptors = mModel.getInterceptorList();
        Interceptor tmpI;
        Missile tmpT;
        Timestamp tmp = new Timestamp(System.currentTimeMillis());
        boolean bAssigned = false;

        if (threats.size() > 0 && bFgvAssignmentDone == false) {

            if (fgvCurrentThreat == null) {

                fgvRejectInterceptors.clear();
                fgvCurrentThreat = mModel.getThreat(threats.get(0));
                bAssigned = handleForgivingAssignment();
                fgvAssignmentTime = tmp;

                if (!bAssigned) {

                    fgvCurrentThreat = null;

                    if (!bForceManualLaunch) {
                        forceManualAssignment();
                        bForceManualLaunch = true;
                    }
                } else {
                    bForceManualLaunch = false;
                }

            } else if (fgvCurrentThreat != null && bFgvAssignmentRejected) {
                bFgvAssignmentRejected = false;
                fgvRejectInterceptors.add(mModel.getAssignedInterceptor(fgvCurrentThreat.getIdentifier()));
                mModel.getInterceptor(mModel.getAssignedInterceptor(fgvCurrentThreat.getIdentifier())).setAssignedThreat("[UNASSIGNED]");
                fgvAssignmentTime = tmp;

                bAssigned = handleForgivingAssignment();

                if (!bAssigned) {

                    fgvCurrentThreat = null;
                    if (!bForceManualLaunch) {
                        forceManualAssignment();
                        bForceManualLaunch = true;
                    }
                }
            }
        } else if (bFgvAssignmentDone == false) {
            bFgvAssignmentDone = true;
        }

        if (fgvCurrentThreat != null
                && ((tmp.getTime() - fgvAssignmentTime.getTime()) >= 4000 || bFgvAssignmentApproved)) {

            cmdMcssLaunch(mModel.getAssignedInterceptor(fgvCurrentThreat.getIdentifier()));
            bFgvAssignmentRejected = false;
            bFgvAssignmentApproved = false;
            fgvCurrentThreat = null;
        }

        if (bFgvAssignmentDone) {

            if (fgvCurrentInterceptor == null) {
                for (int i = 0; i < interceptors.size(); i++) {
                    tmpI = mModel.getInterceptor(interceptors.get(i));

                    if (tmpI.getState() == Interceptor.interceptorState.IN_FLIGHT
                            && tmpI.isDetonateOverriden() == false) {
                        tmpT = mModel.getThreat(tmpI.getAssignedThreat());

                        int[] pos = tmpI.getPositionVector();
                        int[] tPos = tmpT.getPositionVector();
                        double distance = Math.sqrt(Math.pow((tPos[0] - pos[0]), 2) + Math.pow((tPos[1] - pos[1]), 2) + Math.pow((tPos[2] - pos[2]), 2));

                        if (distance <= (tmpI.getDetonationRange() - 8)) {
                            fgvCurrentInterceptor = tmpI;
                            bFgvDetonateRejected = false;
                            bFgvDetonateApproved = false;
                            bFgvDetonateTime = new Timestamp(System.currentTimeMillis());
                        }
                    }
                }
            } else {
                tmpT = mModel.getThreat(fgvCurrentInterceptor.getAssignedThreat());

                int[] pos = fgvCurrentInterceptor.getPositionVector();
                int[] tPos = tmpT.getPositionVector();
                double distance = Math.sqrt(Math.pow((tPos[0] - pos[0]), 2) + Math.pow((tPos[1] - pos[1]), 2) + Math.pow((tPos[2] - pos[2]), 2));

                if (distance > fgvCurrentInterceptor.getDetonationRange()) {
                    fgvCurrentInterceptor = null;
                    bFgvDetonateRejected = false;
                    bFgvDetonateApproved = false;
                } else if (bFgvDetonateRejected) {
                    mView.handleDetModeChange(fgvCurrentInterceptor.getIdentifier());

                    bFgvDetonateRejected = false;
                    fgvCurrentInterceptor.setDetonateOverride(true);
                    fgvCurrentInterceptor = null;

                } else if (bFgvDetonateApproved || (System.currentTimeMillis() - bFgvDetonateTime.getTime()) >= 4000) {
                    cmdSmssDetEnable(fgvCurrentInterceptor.getIdentifier());
                    cmdMcssDetonate(fgvCurrentInterceptor.getIdentifier());
                    bFgvDetonateRejected = false;
                    bFgvDetonateApproved = false;
                    fgvCurrentInterceptor = null;
                }
            }
        }

    }

    /***************************************************************************
     * The handleAutomaticControl function handles the logic needed to perform 
     * automatic assignment and detonation
     **************************************************************************/
    private void handleAutomaticControl() {
        ArrayList<String> interceptors = mModel.getInterceptorList();
        ArrayList<String> threats = mModel.getUnassignedThreats();
        Interceptor tmpI;
        Missile tmpT;

        ArrayList<String> aInts = new ArrayList(), bInts = new ArrayList(), cInts = new ArrayList();

        for (int i = 0; i < interceptors.size(); i++) {
            tmpI = mModel.getInterceptor(interceptors.get(i));
            tmpT = mModel.getThreat(tmpI.getAssignedThreat());

            // detonate interce ptors in range to their threat
            if (tmpI.getState() == Interceptor.interceptorState.IN_FLIGHT
                    && tmpI.isDetonateOverriden() == false
                    && tmpT != null) {
                int[] pos = tmpI.getPositionVector();
                int[] tPos = tmpT.getPositionVector();
                double distance = Math.sqrt(Math.pow((tPos[0] - pos[0]), 2) + Math.pow((tPos[1] - pos[1]), 2) + Math.pow((tPos[2] - pos[2]), 2));

                if (distance <= (tmpI.getDetonationRange() - 8)) {
                    cmdSmssDetEnable(interceptors.get(i));
                    cmdMcssDetonate(interceptors.get(i));
                }
            } else if (tmpI.getState() == Interceptor.interceptorState.PRE_FLIGHT
                    && tmpI.isAssignmentOverriden() == false
                    && tmpI.isDisabled() == false
                    && tmpT == null) {
                switch (tmpI.getMissileClass()) {
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
            } else if (tmpI.getState() == Interceptor.interceptorState.PRE_FLIGHT
                    && tmpI.isAssignmentOverriden() == false
                    && tmpT != null) {
                cmdMcssLaunch(interceptors.get(i));
            }
        }

        //assign threats to interceptors and launch
        for (int i = 0; i < threats.size();) {
            tmpT = mModel.getThreat(threats.get(i));

            if (tmpT.getMissileClass() == 'X') {
                if (cInts.size() > 0) {
                    tmpI = mModel.getInterceptor(cInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    cInts.remove(0);
                } else if (bInts.size() > 0) {
                    tmpI = mModel.getInterceptor(bInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    bInts.remove(0);
                } else if (aInts.size() > 0) {
                    tmpI = mModel.getInterceptor(aInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    aInts.remove(0);
                } 

                threats.remove(i);
            } else {
                i++;
            }
        }
        
        for (int i = 0; i < threats.size();) {
            tmpT = mModel.getThreat(threats.get(i));

            if (tmpT.getMissileClass() == 'Y') {
                if (cInts.size() > 0) {
                    tmpI = mModel.getInterceptor(cInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    cInts.remove(0);
                } else if (bInts.size() > 0) {
                    tmpI = mModel.getInterceptor(bInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    bInts.remove(0);
                } else if (aInts.size() > 0) {
                    tmpI = mModel.getInterceptor(aInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    aInts.remove(0);
                }

                threats.remove(i);
            } else {
                i++;
            }
        }

        for (int i = 0; i < threats.size();) {
            tmpT = mModel.getThreat(threats.get(i));

            if (tmpT.getMissileClass() == 'Z') {
                if (bInts.size() > 0) {
                    tmpI = mModel.getInterceptor(bInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    bInts.remove(0);
                } else if (cInts.size() > 0) {
                    tmpI = mModel.getInterceptor(cInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    cInts.remove(0);
                } else if (aInts.size() > 0) {
                    tmpI = mModel.getInterceptor(aInts.get(0));
                    tmpI.setAssignedThreat(threats.get(i));
                    aInts.remove(0);
                }

                threats.remove(i);
            } else {
                i++;
            }
        }

        

    }

    /***************************************************************************
     * The handleForgivingAssignment function is called by the handleForgivingControl 
     * function to determine the next interceptor to assign to a threat.
     * 
     * @return A boolean value that indicates whether or not a suitable interceptor
     *  is available for assignment.
     **************************************************************************/
    private boolean handleForgivingAssignment() {
        ArrayList<String> interceptors = mModel.getUnassignedInterceptors();
        ArrayList<String> aInts = new ArrayList(), bInts = new ArrayList(), cInts = new ArrayList();
        Interceptor tmpI;
        boolean newlyAssigned = false;

        for (int i = 0; i < interceptors.size(); i++) {
            tmpI = mModel.getInterceptor(interceptors.get(i));

            if (tmpI.getState() == Interceptor.interceptorState.PRE_FLIGHT
                    && tmpI.isAssignmentOverriden() == false
                    && tmpI.isDisabled() == false
                    && !fgvRejectInterceptors.contains(interceptors.get(i))) {
                switch (tmpI.getMissileClass()) {
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

        if (fgvCurrentThreat.getMissileClass() == 'X') {
            if (cInts.size() > 0) {
                tmpI = mModel.getInterceptor(cInts.get(0));
                tmpI.setAssignedThreat(fgvCurrentThreat.getIdentifier());
                newlyAssigned = true;
            } else if (bInts.size() > 0) {
                tmpI = mModel.getInterceptor(bInts.get(0));
                tmpI.setAssignedThreat(fgvCurrentThreat.getIdentifier());
                newlyAssigned = true;
            } else if (aInts.size() > 0) {
                tmpI = mModel.getInterceptor(aInts.get(0));
                tmpI.setAssignedThreat(fgvCurrentThreat.getIdentifier());
                newlyAssigned = true;
            } 
        } else if (fgvCurrentThreat.getMissileClass() == 'Y') {
            if (cInts.size() > 0) {
                tmpI = mModel.getInterceptor(cInts.get(0));
                tmpI.setAssignedThreat(fgvCurrentThreat.getIdentifier());
                newlyAssigned = true;
            } else if (bInts.size() > 0) {
                tmpI = mModel.getInterceptor(bInts.get(0));
                tmpI.setAssignedThreat(fgvCurrentThreat.getIdentifier());
                newlyAssigned = true;
            }else if (aInts.size() > 0) {
                tmpI = mModel.getInterceptor(aInts.get(0));
                tmpI.setAssignedThreat(fgvCurrentThreat.getIdentifier());
                newlyAssigned = true;
            }
        } else if (fgvCurrentThreat.getMissileClass() == 'Z') {
            if (bInts.size() > 0) {
                tmpI = mModel.getInterceptor(bInts.get(0));
                tmpI.setAssignedThreat(fgvCurrentThreat.getIdentifier());
                newlyAssigned = true;
            }else if (cInts.size() > 0) {
                tmpI = mModel.getInterceptor(cInts.get(0));
                tmpI.setAssignedThreat(fgvCurrentThreat.getIdentifier());
                newlyAssigned = true;
            } else if (aInts.size() > 0) {
                tmpI = mModel.getInterceptor(aInts.get(0));
                tmpI.setAssignedThreat(fgvCurrentThreat.getIdentifier());
                newlyAssigned = true;
            }

        }

        return newlyAssigned;

    }

    /***************************************************************************
     * The getForgivingDetState function is called by the MMODFrame on a regular 
     * basis to determine whether or not a prompt should be presented to the operator
     * to approve or disprove a detonation while in the forgiving control mode.
     * 
     * @return - The identifier of the interceptor to be detonated, null if there
     *           is no interceptor eligible for detonation
     **************************************************************************/
    public String getForgivingDetState() {
        if (fgvCurrentInterceptor != null && bFgvDetonateApproved == false && bFgvDetonateRejected == false) {
            return fgvCurrentInterceptor.getIdentifier();
        } else {
            return null;
        }
    }

    /***************************************************************************
     * The getForgivingDetState function is called by the MMODFrame on a regular basis 
     * to determine whether or not a prompt should be presented to the operator 
     * to approve or disprove an assignment while in the forgiving control mode.
     * 
     * @return - The identifier of the interceptor and threat to be assigned, null if there
     *           is no eligible pair for assignment
     **************************************************************************/
    public String getForgivingAssignmentState() {
        if (fgvCurrentThreat != null && bFgvAssignmentApproved == false && bFgvAssignmentRejected == false) {
            return ("Threat [" + fgvCurrentThreat.getIdentifier() + "] has been assigned to Interceptor [" + mModel.getAssignedInterceptor(fgvCurrentThreat.getIdentifier()) + "]");
        } else {
            return null;
        }
    }

    /***************************************************************************
     * The approveForgivingAssignment function is called by the MMODFRame if the 
     * operator has approved the current assignment in the forgiving mode of control.
     **************************************************************************/
    public void approveForgivingAssignment() {
        bFgvAssignmentApproved = true;
    }

    /***************************************************************************
     * The rejectForgivingAssignment function is called by the MMODFRame if the 
     * operator has rejected the current assignment in the forgiving mode of control.
     **************************************************************************/
    public void rejectForgivingAssignment() {
        bFgvAssignmentRejected = true;
    }

    /***************************************************************************
     * The approveForgivingDet function is called by the MMODFrame if the 
     * operator has approved the current detonation choice in the forgiving mode of control.
     **************************************************************************/
    public void approveForgivingDet() {
        bFgvDetonateApproved = true;
    }

    /***************************************************************************
     * The rejectForgivingDet function is called by the MMODFrame if the 
     * operator has rejected the current detonation choice in the forgiving mode of control.
     **************************************************************************/
    public void rejectForgivingDet() {
        bFgvDetonateRejected = true;
    }

    /***************************************************************************
     * The forceManualAssignment function is called by the forgiving control logic to set the assignment override state of
     * every interceptor to manual, if an alternative assignment in forgiving
     * control is impossible.
     **************************************************************************/
    private void forceManualAssignment() {
        ArrayList<String> interceptors = mModel.getInterceptorList();
        Interceptor tmpI;

        for (int i = 0; i < interceptors.size(); i++) {
            tmpI = mModel.getInterceptor(interceptors.get(i));
            tmpI.setAssignmentOverriden(true);
        }

        mView.handleLaunchModeChange();
    }

    /***************************************************************************
     * TSS Command Interface
     **************************************************************************/
    
    /***************************************************************************
     * The cmdTssTrackThreat function queries the TSS for a threat's position.
     * 
     * @param missileID The id of the Threat to query
     * 
     * @return An array of 3 integers that represent the position in the x, y, and z plane
     **************************************************************************/
    public int[] cmdTssTrackThreat(String missileID) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        int result[] = new int[3];

        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[12];
        int bufferSize = 0;

        if (tssTCP != null) {
            try {
                buffOut = new DataOutputStream(tssTCP.getOutputStream());
                buffIn = new DataInputStream(tssTCP.getInputStream());

                header[0] = 1;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));

                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if (returnHeader[3] == 0 && bufferSize == 12) {
                    buffIn.read(returnBuffer, 0, bufferSize);

                    result[0] = ((returnBuffer[0] & 0xff) << 24) | ((returnBuffer[1] & 0xff) << 16) | ((returnBuffer[2] & 0xff) << 8) | (returnBuffer[3] & 0xff);
                    result[1] = ((returnBuffer[4] & 0xff) << 24) | ((returnBuffer[5] & 0xff) << 16) | ((returnBuffer[6] & 0xff) << 8) | (returnBuffer[7] & 0xff);
                    result[2] = ((returnBuffer[8] & 0xff) << 24) | ((returnBuffer[9] & 0xff) << 16) | ((returnBuffer[10] & 0xff) << 8) | (returnBuffer[11] & 0xff);
                } else {
                    System.out.println("MDSCSSController - cmdTssTrackThreat: unexpected or failed response\n");
                    result = null;
                }

            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdTssTrackThreat: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdTssTrackThreat: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdTssTrackInterceptor function queries the TSS for a interceptor's position.
     * 
     * @param missileID The id of the interceptor to query
     * 
     * @return An array of 3 integers that represent the position in the x, y, and z plane
     **************************************************************************/
    public int[] cmdTssTrackInterceptor(String missileID) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        int result[] = new int[3];

        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[12];
        int bufferSize = 0;

        if (tssTCP != null) {
            try {
                buffOut = new DataOutputStream(tssTCP.getOutputStream());
                buffIn = new DataInputStream(tssTCP.getInputStream());

                header[0] = 2;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));

                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if (returnHeader[3] == 0 && bufferSize == 12) {
                    buffIn.read(returnBuffer, 0, bufferSize);

                    result[0] = ((returnBuffer[0] & 0xff) << 24) | ((returnBuffer[1] & 0xff) << 16) | ((returnBuffer[2] & 0xff) << 8) | (returnBuffer[3] & 0xff);
                    result[1] = ((returnBuffer[4] & 0xff) << 24) | ((returnBuffer[5] & 0xff) << 16) | ((returnBuffer[6] & 0xff) << 8) | (returnBuffer[7] & 0xff);
                    result[2] = ((returnBuffer[8] & 0xff) << 24) | ((returnBuffer[9] & 0xff) << 16) | ((returnBuffer[10] & 0xff) << 8) | (returnBuffer[11] & 0xff);
                } else {
                    System.out.println("MDSCSSController - cmdTssTrackInterceptor: unexpected or failed response\n");
                    result = null;
                }

            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdTssTrackInterceptor: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
                result = null;
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdTssTrackInterceptor: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdTssGetThreatList function queries the TSS for a list of known threats.
     * Note: Per requirement, all threats are assumed to have a 2-char ID.
     * 
     * @return An arraylist containing the ids of all known threats
     **************************************************************************/
    public ArrayList<String> cmdTssGetThreatList() {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        ArrayList<String> result = new ArrayList();

        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[2];
        int bufferSize = 0;

        if (tssTCP != null) {
            try {
                buffOut = new DataOutputStream(tssTCP.getOutputStream());
                buffIn = new DataInputStream(tssTCP.getInputStream());

                header[0] = 5;
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if (returnHeader[3] == 0 && bufferSize > 0) {
                    result = new ArrayList();

                    for (int i = 0; i < bufferSize; i += 2) {
                        buffIn.read(returnBuffer, 0, 2);
                        result.add(new String(returnBuffer, "UTF-8"));
                    }
                } else {
                    System.out.println("MDSCSSController - cmdTssGetThreatList: unexpected or failed response\n");
                }
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdTssGetThreatList: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdTssGetThreatList: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdTssGetVersion function queries the TSS for its version string and parses out the version number.
     * 
     * @return A String representation of the version number of the TSS
     **************************************************************************/
    public String cmdTssGetVersion() {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;

        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;

        if (tssTCP != null) {
            try {
                buffOut = new DataOutputStream(tssTCP.getOutputStream());
                buffIn = new DataInputStream(tssTCP.getInputStream());

                header[0] = 6;
                buffOut.write(header, 0, 5);

                buffIn.read(returnBuffer, 0, 6);
                bufferSize = ((returnBuffer[4] & 0xff) << 8) | (returnBuffer[5] & 0xff);

                if (returnBuffer[3] == 0) {
                    buffIn.read(returnBuffer, 0, bufferSize);
                    result = new String(returnBuffer, "UTF-8");

                    result = result.substring(27, 30);
                } else {
                    System.out.println("MDSCSSController - cmdTssGetVersion: unexpected or failed response\n");
                }
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdTssGetVersion: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdTssGetVersion: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * MCSS Command Interface
     **************************************************************************/
    /***************************************************************************
     * The cmdMcssLaunch function sends the launch command to the MCSS for a specified interceptor.
     * 
     * @param missileID The id of the interceptor to launch
     * 
     * @return The success status of the launch command
     **************************************************************************/
    public boolean cmdMcssLaunch(String missileID) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;

        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];

        if (mcssTCP != null) {
            try {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 1;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdMcssLaunch: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdMcssLaunch: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdMcssThrust function ends the thrust command to the MCSS for a specified interceptor, each
     * power level will persist for 1 second of duration.
     * 
     * @param missileID The id of the interceptor to control
     * @param pwrX The power level in the x direction
     * @param pwrY The power level in the y direction
     * @param pwrZ The power level in the z direction
     * 
     * @return The success status of the thrust command
     **************************************************************************/
    public boolean cmdMcssThrust(String missileID, int pwrX, int pwrY, int pwrZ) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        int tmp;

        byte[] header = new byte[53];
        byte[] returnHeader = new byte[6];

        if (mcssTCP != null) {
            try {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 2;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));

                // header will be 48 bytes long
                header[4] = (byte) 48;

                // set the thrust time values all to 1 second
                header[8] = header[12] = header[16] = header[20] = header[24] = header[28] = 0x6;

                if (pwrX > 0) {
                    tmp = Math.abs(pwrX);
                    header[29] = (byte) ((tmp >> 24) & 0xff);
                    header[30] = (byte) ((tmp >> 16) & 0xff);
                    header[31] = (byte) ((tmp >> 8) & 0xff);
                    header[32] = (byte) (tmp & 0xff);
                } else {
                    tmp = Math.abs(pwrX);
                    header[33] = (byte) ((tmp >> 24) & 0xff);
                    header[34] = (byte) ((tmp >> 16) & 0xff);
                    header[35] = (byte) ((tmp >> 8) & 0xff);
                    header[36] = (byte) (tmp & 0xff);
                }

                if (pwrY > 0) {
                    tmp = Math.abs(pwrY);
                    header[37] = (byte) ((tmp >> 24) & 0xff);
                    header[38] = (byte) ((tmp >> 16) & 0xff);
                    header[39] = (byte) ((tmp >> 8) & 0xff);
                    header[40] = (byte) (tmp & 0xff);
                } else {
                    tmp = Math.abs(pwrY);
                    header[41] = (byte) ((tmp >> 24) & 0xff);
                    header[42] = (byte) ((tmp >> 16) & 0xff);
                    header[43] = (byte) ((tmp >> 8) & 0xff);
                    header[44] = (byte) (tmp & 0xff);
                }

                if (pwrZ > 0) {
                    tmp = Math.abs(pwrZ);
                    header[45] = (byte) ((tmp >> 24) & 0xff);
                    header[46] = (byte) ((tmp >> 16) & 0xff);
                    header[47] = (byte) ((tmp >> 8) & 0xff);
                    header[48] = (byte) (tmp & 0xff);
                } else {
                    tmp = Math.abs(pwrZ);
                    header[49] = (byte) ((tmp >> 24) & 0xff);
                    header[50] = (byte) ((tmp >> 16) & 0xff);
                    header[51] = (byte) ((tmp >> 8) & 0xff);
                    header[52] = (byte) (tmp & 0xff);
                }

                buffOut.write(header, 0, 53);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdMcssThrust: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdMcssThrust: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdMcssDetonate function sends the detonate command to the MCSS for a specified interceptor.
     * 
     * @param missileID The id of the interceptor to detonate
     * 
     * @return The success status of the detonate command
     **************************************************************************/
    public boolean cmdMcssDetonate(String missileID) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;

        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];

        if (mcssTCP != null) {
            try {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 3;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdMcssDetonate: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdMcssDetonate: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdMcssDestruct function sends the destruct command to the MCSS for a specified interceptor.
     * 
     * @param missileID The id of the interceptor to destroy
     * 
     * @return The success status of the destruct command
     **************************************************************************/
    public boolean cmdMcssDestruct(String missileID) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;

        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];

        if (mcssTCP != null) {
            try {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 4;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdMcssDestruct: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdMcssDestruct: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdMcssGetInterceptorList function queries the MCSS for a list of known interceptors.
     * Note: Per requirement, all interceptors are assumed to have a 2-char ID.
     * 
     * @return An arraylist containing the ids of all known interceptors
     **************************************************************************/
    public ArrayList<String> cmdMcssGetInterceptorList() {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        ArrayList<String> result = new ArrayList();

        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[2];
        int bufferSize = 0;

        if (mcssTCP != null) {
            try {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 5;
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if (returnHeader[3] == 0 && bufferSize > 0) {
                    for (int i = 0; i < bufferSize; i += 2) {
                        buffIn.read(returnBuffer, 0, 2);
                        result.add(new String(returnBuffer, "UTF-8"));
                    }
                } else {
                    System.out.println("MDSCSSController - cmdMcssGetInterceptorList: unexpected or failed response\n");
                }
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdMcssGetInterceptorList: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdMcssGetInterceptorList: buffer failure\n" + ex.getMessage() + "\n");
                
                
                //TODOhandleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdMcssgetState function queries the MCSS for a specified interceptor's state.
     * 
     * @param missileID The id of the interceptor to query
     * 
     * @return An enumeration value representing the state of the specified interceptor
     **************************************************************************/
    public Interceptor.interceptorState cmdMcssgetState(String missileID) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        Interceptor.interceptorState result = Interceptor.interceptorState.UNDEFINED;

        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[25];
        int bufferSize = 0;

        if (mcssTCP != null) {
            try {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 6;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if (returnHeader[3] == 0 && bufferSize == 25) {
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
                } else {
                    System.out.println("MDSCSSController - cmdMcssgetState: unexpected or failed response\n");
                }
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdMcssgetState: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdMcssgetState: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdMcssGetLaunchSite function queries the MCSS for a interceptor's launch position.
     * 
     * @param missileID The id of the interceptor to query
     * 
     * @return An array of 3 integers that represent the launch position in the x, y, and z plane
     **************************************************************************/
    public int[] cmdMcssGetLaunchSite(String missileID) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        int result[] = new int[3];

        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[12];
        int bufferSize = 0;

        if (mcssTCP != null) {
            try {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 7;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));

                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if (returnHeader[3] == 0 && bufferSize == 12) {
                    buffIn.read(returnBuffer, 0, bufferSize);

                    result[0] = ((returnBuffer[0] & 0xff) << 24) | ((returnBuffer[1] & 0xff) << 16) | ((returnBuffer[2] & 0xff) << 8) | (returnBuffer[3] & 0xff);
                    result[1] = ((returnBuffer[4] & 0xff) << 24) | ((returnBuffer[5] & 0xff) << 16) | ((returnBuffer[6] & 0xff) << 8) | (returnBuffer[7] & 0xff);
                    result[2] = ((returnBuffer[8] & 0xff) << 24) | ((returnBuffer[9] & 0xff) << 16) | ((returnBuffer[10] & 0xff) << 8) | (returnBuffer[11] & 0xff);
                    System.out.println(result[0]);
                } else {
                    System.out.println("MDSCSSController - cmdMcssGetLaunchSite: unexpected or failed response\n");
                    result = null;
                }
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdMcssGetLaunchSite: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdMcssGetLaunchSite: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdMcssGetVersion function queries the MCSS for its version string and parses out the version number.
     * 
     * @return A string representation of the version number of the MCSS
     **************************************************************************/
    public String cmdMcssGetVersion() {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;

        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;

        if (mcssTCP != null) {
            try {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 8;
                buffOut.write(header, 0, 5);

                buffIn.read(returnBuffer, 0, 6);
                bufferSize = ((returnBuffer[4] & 0xff) << 8) | (returnBuffer[5] & 0xff);

                buffIn.read(returnBuffer, 0, bufferSize);
                result = new String(returnBuffer, "UTF-8");

                result = result.substring(26, 29);
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdMcssGetVersion: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdMcssGetVersion: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdMcssGetCtrlVersion function queries the MCSS for the version string of a specific interceptor controller.
     * 
     * @param missileID The id of the interceptor to query
     * 
     * @return The version string of the specific interceptor controller
     **************************************************************************/
    public String cmdMcssGetCtrlVersion(String missileID) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;

        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;

        if (mcssTCP != null) {
            try {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 9;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnBuffer, 0, 6);
                bufferSize = ((returnBuffer[4] & 0xff) << 8) | (returnBuffer[5] & 0xff);

                buffIn.read(returnBuffer, 0, bufferSize);
                result = new String(returnBuffer, "UTF-8");
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdMcssGetCtrlVersion: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdMcssGetCtrlVersion: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * SMSS Command Interface
     **************************************************************************/
    /***************************************************************************
     * The cmdSmssActivateSafety function sends the activate command to the SMSS for a specified interceptor.
     * 
     * @param missileID The id of the interceptor to activate the safety for
     * 
     * @return The success status of the activate command
     **************************************************************************/
    public boolean cmdSmssActivateSafety(String missileID) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;

        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];

        if (smssTCP != null) {
            try {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 1;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdSmssActivateSafety: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdSmssActivateSafety: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdSmssDeactivateSafety function sends the deactivate command to the SMSS for a specified interceptor.
     * 
     * @param missileID The id of the interceptor to deactivate the safety for
     * 
     * @return The success status of the deactivate command
     **************************************************************************/
    public boolean cmdSmssDeactivateSafety(String missileID) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;

        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];

        if (smssTCP != null) {
            try {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 2;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdSmssDeactivateSafety: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdSmssDeactivateSafety: buffer failure\n" + ex.getMessage() + "\n");
                //handleSocketFailure();
            }

        }

        return result;
    }

    /***************************************************************************
     * The cmdSmssPingWatchdog function sends the safety watchdog command to the SMSS for a specified interceptor.
     * 
     * @param missileID The id of the interceptor to update
     * @param pTimer The watchdog count value to set
     * 
     * @return The success status of the ping command
     **************************************************************************/
    public boolean cmdSmssPingWatchdog(String missileID, int pTimer) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;

        byte[] header = new byte[9];
        byte[] returnHeader = new byte[6];

        if (smssTCP != null) {
            try {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                // requires an MID? wtf?
                header[0] = 3;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));
                header[4] = 4;
                header[5] = (byte) ((pTimer >> 24) & 0xff);
                header[6] = (byte) ((pTimer >> 16) & 0xff);
                header[7] = (byte) ((pTimer >> 8) & 0xff);
                header[8] = (byte) (pTimer & 0xff);
                buffOut.write(header, 0, 9);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdSmssPingWatchdog: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdSmssPingWatchdog: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdSmssDetEnable command sends the detonate enable command to the 
     * SMSS for a specified interceptor.
     * 
     * @param missileID The id of the interceptor to enable detonation
     * 
     * @return The success status of the deactivate command
     **************************************************************************/
    public boolean cmdSmssDetEnable(String missileID) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;

        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];

        if (smssTCP != null) {
            try {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 4;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdSmssDetEnable: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdSmssDetEnable: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdSmssDestruct command sends the destruct command to the SMSS for a specified interceptor.
     * 
     * @param missileID The id of the interceptor to destroy
     * 
     * @return The success status of the destruct command
     **************************************************************************/
    public boolean cmdSmssDestruct(String missileID) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;

        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];

        if (smssTCP != null) {
            try {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 5;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdSmssDestruct: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdSmssDestruct: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }

    /***************************************************************************
     * The cmdSmssGetVersion queries the SMSS for its version string and parses out the version number.
     * 
     * @return A string representation of the version number of the SMSS
     **************************************************************************/
    public String cmdSmssGetVersion(String missileID) {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;

        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;

        if (smssTCP != null) {
            try {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 6;
                header[1] = (byte) (missileID.charAt(0));
                header[2] = (byte) (missileID.charAt(1));

                buffOut.write(header, 0, 5);

                buffIn.read(returnBuffer, 0, 6);
                bufferSize = ((returnBuffer[4] & 0xff) << 8) | (returnBuffer[5] & 0xff);

                buffIn.read(returnBuffer, 0, bufferSize);
                result = new String(returnBuffer, "UTF-8");
                result = result.substring(14, 18);
            } catch (SocketTimeoutException ex) {
                System.out.println("MDSCSSController - cmdSmssGetVersion: warning 2 second socket timeout\n" + ex.getMessage() + "\n");
            } catch (IOException ex) {
                System.out.println("MDSCSSController - cmdSmssGetVersion: buffer failure\n" + ex.getMessage() + "\n");
                handleSocketFailure();
            }
        }

        return result;
    }
}
