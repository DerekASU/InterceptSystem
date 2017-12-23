/*******************************************************************************
 * File: Interceptor.java
 * Description: Model class that defines an interceptor, a type of missile
 *
 ******************************************************************************/
package mdscssModel;

import java.sql.Timestamp;

public class Interceptor extends Missile
{

    // Enumerations
    public enum interceptorState
    {
        PRE_FLIGHT,
        IN_FLIGHT,
        DETONATED,
        UNDEFINED;
    }
    
    // Member variables
    protected interceptorState state;
    protected String assignedThreat;
    protected int detonationRange;
    
    protected boolean bDisabled;
    protected boolean bDetonationEnabled;
    protected boolean bDetonateOverride;
    protected boolean bAssignmentOverride;
    
    protected Timestamp launchTime;
    
    protected double thrust_dx, thrust_ix, thrust_dy, thrust_iy, thrust_dz, thrust_iz;
    
    /***************************************************************************
     * Interceptor
     * 
     * Constructor; initializes member variables
     * 
     * @param pId - the 2-char missile identification string
     * @param pFricX - the friction coefficient in the x direction
     * @param pFricY - the friction coefficient in the Y direction
     * @param pFricZ - the friction coefficient in the Z direction
     * @param pMThrustX - the max acceleration in the x direction
     * @param pMThrusty - the max acceleration in the y direction
     * @param pMThrustZ - the max acceleration in the z direction
     * @param pAssigned - the identifier of the threat this interceptor has been assigned
     * @param pDetRng - the detonation radius of the interceptor
     **************************************************************************/
    public Interceptor(String pId, double pFricX, double pFricY, double pFricZ,
                    int pMThrustX, int pMThrustY, int pMThrustZ,
                    String pAssigned, int pDetRng)
    {
        super(pId, MissileType.INTERCEPTOR, pFricX, pFricY, pFricZ, pMThrustX, pMThrustY, pMThrustZ);
        
        state = interceptorState.PRE_FLIGHT;
        assignedThreat = pAssigned;
        detonationRange = pDetRng;
        
        launchTime = null;
        
        thrust_dx= thrust_ix= thrust_dy= thrust_iy= thrust_dz= thrust_iz = 0;
    }
    
    /***************************************************************************
     * getState
     * 
     * retrieves the state of the interceptor, which can either be pre-flight,
     * in-flight, detonated, or destructed
     * 
     * @return the state of the interceptor
     **************************************************************************/
    public interceptorState getState()
    {
        return state;
    }
    
    /***************************************************************************
     * getAssignedThreat
     * 
     * retrieves the id of the threat in which this interceptor is assigned to
     * 
     * @return the 2-char missile identification string of the threat, "N/A" 
     * if unassigned;
     **************************************************************************/
    public String getAssignedThreat()
    {
        return assignedThreat;
    }
    
    /***************************************************************************
     * getDetonationRange
     * 
     * retrieves the detonation radius of the interceptor in meters
     * 
     * @return the detonation radius of the interceptor
     **************************************************************************/
    public int getDetonationRange()
    {
        return detonationRange;
    }
    
    /***************************************************************************
     * isDisabled
     * 
     * retrieves the disabled state of the interceptor
     * 
     * @return whether or not the interceptor is disabled
     **************************************************************************/
    public boolean isDisabled()
    {
        return bDisabled;
    }
    
    /***************************************************************************
     * isDetonationEnabled
     * 
     * retrieves the detonation safety state of the interceptor
     * 
     * @return whether or not the interceptor is enabled for detonation
     **************************************************************************/
    public boolean isDetonationEnabled()
    {
        return bDetonationEnabled;
    }
    
    /***************************************************************************
     * isDetonateOverriden
     * 
     * retrieves the flag that determines if the detonation control of the 
     * interceptor has been overriden
     * 
     * @return whether or not the control mode has been overriden
     **************************************************************************/
    public boolean isDetonateOverriden()
    {
        return bDetonateOverride;
    }
    
    /***************************************************************************
     * isAssignmentOverriden
     * 
     * retrieves the flag that determines if the assignment control of the 
     * interceptor has been overriden
     * 
     * @return whether or not the control mode has been overriden
     **************************************************************************/
    public boolean isAssignmentOverriden()
    {
        return bAssignmentOverride;
    }
    
    /***************************************************************************
     * getLaunchTime
     * 
     * retrieves the timestamp representing the launch time of the interceptor
     * 
     * @return the timestamp representing the launch time of the interceptor
     **************************************************************************/
    public Timestamp getLaunchTime()
    {
        return launchTime;
    }
    
    /***************************************************************************
     * setState
     * 
     * assigns the state of the interceptor, which can either be pre-flight,
     * in-flight, detonated, or destructed
     * 
     * @param pState - the state of the interceptor
     **************************************************************************/
    public void setState(interceptorState pState)
    {
        state = pState;
    }
    
    /***************************************************************************
     * setAssignedThreat
     * 
     * assigns the id of the threat in which this interceptor is assigned to
     * 
     * @param pID - the 2-char missile identification string of the threat
     **************************************************************************/
    public void setAssignedThreat(String pID)
    {
        assignedThreat = pID;
    }
        
    /***************************************************************************
     * setDisabled
     * 
     * sets the disabled state of the interceptor
     * 
     * @param pDisable - whether or not the interceptor is disabled
     **************************************************************************/
    public void setDisabled(boolean pDisable)
    {
        bDisabled = pDisable;
    }
    
    /***************************************************************************
     * setDetonationEnabled
     * 
     * sets the detonation safety state of the interceptor
     * 
     * @param pEnabled - whether or not the interceptor is enabled for detonation
     **************************************************************************/
    public void setDetonationEnabled(boolean pEnabled)
    {
        bDetonationEnabled = pEnabled;
    }
    
    /***************************************************************************
     * setDetonateOverride
     * 
     * sets the flag that determines if the detonate control of the 
     * interceptor has been overriden
     * 
     * @param pOverride - whether or not the control mode has been overriden
     **************************************************************************/
    public void setDetonateOverride(boolean pOverride)
    {
        bDetonateOverride = pOverride;
    }
    
    /***************************************************************************
     * setAssignmentOverriden
     * 
     * sets the flag that determines if the assignment control of the 
     * interceptor has been overriden
     * 
     * @param pOverride - whether or not the control mode has been overriden
     **************************************************************************/
    public void setAssignmentOverriden(boolean pOverride)
    {
        bAssignmentOverride = pOverride;
    }
    
    /***************************************************************************
     * setLaunchTime
     * 
     * sets the timestamp representing the time of launch of the interceptor
     * 
     * @param pTime - the new launch time
     **************************************************************************/
    public void setLaunchTime(Timestamp pTime)
    {
        launchTime = pTime;
    }
    
    /***************************************************************************
     * getCtrlThrustDX
     * 
     * retrieves the ctrlThrust value used by the interceptor controller calculations
     * for the derivative portion in the X direction
     * 
     * @return the critical thrust value
     **************************************************************************/
    public double getCtrlThrustDX()
    {
        return thrust_dx;
    }
     /***************************************************************************
     * getCtrlThrustIX
     * 
     * retrieves the ctrlThrust value used by the interceptor controller calculations
     * for the Integrator portion in the X direction
     * 
     * @return the critical thrust value
     **************************************************************************/
    public double getCtrlThrustIX()
    {
        return thrust_ix;
    }
    
    /***************************************************************************
     * setCtrlThrustDX
     * 
     * sets the ctrlThrust value used by the interceptor controller calculations
     * for the derivative portion in the X direction
     * 
     * @param pThrust - the critical thrust value
     **************************************************************************/
    public void setCtrlThrustDX(double pThrust)
    {
        thrust_dx = pThrust;
    }
    /***************************************************************************
     * setCtrlThrustIX
     * 
     * sets the ctrlThrust value used by the interceptor controller calculations
     * for the Integrator portion in the X direction
     * 
     * @param pThrust - the critical thrust value
     **************************************************************************/
    public void setCtrlThrustIX(double pThrust)
    {
        thrust_ix = pThrust;
    }
    
    /***************************************************************************
     * getCtrlThrustDY
     * 
     * retrieves the ctrlThrust value used by the interceptor controller calculations
     * for the derivative portion in the Y direction
     * 
     * @return the critical thrust value
     **************************************************************************/
    public double getCtrlThrustDY()
    {
        return thrust_dy;
    }
    /***************************************************************************
     * getCtrlThrustIY
     * 
     * retrieves the ctrlThrust value used by the interceptor controller calculations
     * for the Integrator portion in the Y direction
     * 
     * @return the critical thrust value
     **************************************************************************/
    public double getCtrlThrustIY()
    {
        return thrust_iy;
    }
    
    /***************************************************************************
     * setCtrlThrustDY
     * 
     * sets the ctrlThrust value used by the interceptor controller calculations
     * for the derivative portion in the Y direction
     * 
     * @param pThrust - the critical thrust value
     **************************************************************************/
    public void setCtrlThrustDY(double pThrust)
    {
        thrust_dy = pThrust;
    }
    
    /***************************************************************************
     * setCtrlThrustIY
     * 
     * sets the ctrlThrust value used by the interceptor controller calculations
     * for the Integrator portion in the Y direction
     * 
     * @param pThrust - the critical thrust value
     **************************************************************************/
    public void setCtrlThrustIY(double pThrust)
    {
        thrust_iy = pThrust;
    }
    
    /***************************************************************************
     * getCtrlThrustDZ
     * 
     * retrieves the ctrlThrust value used by the interceptor controller calculations
     * for the derivative portion in the Z direction
     * 
     * @return the critical thrust value
     **************************************************************************/
    public double getCtrlThrustDZ()
    {
        return thrust_dz;
    }
    /***************************************************************************
     * getCtrlThrustIZ
     * 
     * retrieves the ctrlThrust value used by the interceptor controller calculations
     * for the Integrator portion in the Z direction
     * 
     * @return the critical thrust value
     **************************************************************************/
    public double getCtrlThrustIZ()
    {
        return thrust_iz;
    }
    /***************************************************************************
     * setCtrlThrustDZ
     * 
     * sets the ctrlThrust value used by the interceptor controller calculations
     * for the derivative portion in the Z direction
     * 
     * @param pThrust - the critical thrust value
     **************************************************************************/
    public void setCtrlThrustDZ(double pThrust)
    {
        thrust_dz = pThrust;
    }
    /***************************************************************************
     * setCtrlThrustIZ
     * 
     * sets the ctrlThrust value used by the interceptor controller calculations
     * for the Integrator portion in the Z direction
     * 
     * @param pThrust - the critical thrust value
     **************************************************************************/
    public void setCtrlThrustIZ(double pThrust)
    {
        thrust_iz = pThrust;
    }
}

