package mdscssModel;

import java.sql.Timestamp;

/*******************************************************************************
 * The Interceptor object is a Model class that defines an interceptor, a type of missile
 ******************************************************************************/
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
     * Constructor
     * 
     * @param pId The 2-char missile identification string
     * @param pFricX The friction coefficient in the x direction
     * @param pFricY The friction coefficient in the Y direction
     * @param pFricZ The friction coefficient in the Z direction
     * @param pMThrustX The max acceleration in the x direction
     * @param pMThrustY The max acceleration in the y direction
     * @param pMThrustZ The max acceleration in the z direction
     * @param pAssigned The identifier of the threat this interceptor has been assigned
     * @param pDetRng The detonation radius of the interceptor
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
     * The getState function retrieves the state of the interceptor, which can either be pre-flight,
     * in-flight, detonated, or destructed.
     * 
     * @return The state of the interceptor
     **************************************************************************/
    public interceptorState getState()
    {
        return state;
    }
    
    /***************************************************************************
     * The getAssignedThreatfunction retrieves the id of the threat in which this interceptor is assigned to.
     * 
     * @return The 2-char missile identification string of the threat, "[UNASSIGNED]" 
     * if unassigned.
     **************************************************************************/
    public String getAssignedThreat()
    {
        return assignedThreat;
    }
    
    /***************************************************************************
     * The getDetonationRange function retrieves the detonation radius of the interceptor in meters.
     * 
     * @return The detonation radius of the interceptor.
     **************************************************************************/
    public int getDetonationRange()
    {
        return detonationRange;
    }
    
    /***************************************************************************
     * The isDisabled function retrieves the disabled state of the interceptor.
     * 
     * @return A boolean value indicating whether or not the interceptor is disabled
     **************************************************************************/
    public boolean isDisabled()
    {
        return bDisabled;
    }
    
    /***************************************************************************
     * The isDetonationEnabled function retrieves the detonation safety state of the interceptor.
     * 
     * @return A boolean value indicating whether or not the interceptor is enabled for detonation
     **************************************************************************/
    public boolean isDetonationEnabled()
    {
        return bDetonationEnabled;
    }
    
    /***************************************************************************
     * The isDetonateOverriden function retrieves the flag that indicates if the detonation control of the 
     * interceptor has been overriden.
     * 
     * @return A boolean value indicating whether or not the control mode has been overriden
     **************************************************************************/
    public boolean isDetonateOverriden()
    {
        return bDetonateOverride;
    }
    
    /***************************************************************************
     * The isAssignmentOverriden function retrieves the flag that determines if the assignment control of the 
     * interceptor has been overriden.
     * 
     * @return A boolean value indicating whether or not the control mode has been overriden
     **************************************************************************/
    public boolean isAssignmentOverriden()
    {
        return bAssignmentOverride;
    }
    
    /***************************************************************************
     * The getLaunchTime function retrieves the timestamp representing the launch time of the interceptor.
     * 
     * @return The timestamp representing the launch time of the interceptor
     **************************************************************************/
    public Timestamp getLaunchTime()
    {
        return launchTime;
    }
    
    /***************************************************************************
     * The setState function assigns the state of the interceptor, which can either be pre-flight,
     * in-flight, detonated, or destructed.
     * 
     * @param pState The state of the interceptor
     **************************************************************************/
    public void setState(interceptorState pState)
    {
        state = pState;
    }
    
    /***************************************************************************
     * The setAssignedThreat function assigns the id of the threat in which this interceptor is assigned to.
     * 
     * @param pID The 2-char missile identification string of the threat
     **************************************************************************/
    public void setAssignedThreat(String pID)
    {
        assignedThreat = pID;
    }
        
    /***************************************************************************
     * The setDisabled function sets the disabled state of the interceptor.
     * 
     * @param pDisable Whether or not the interceptor is disabled
     **************************************************************************/
    public void setDisabled(boolean pDisable)
    {
        bDisabled = pDisable;
    }
    
    /***************************************************************************
     * The setDetonationEnabled function sets the detonation safety state of the interceptor.
     * 
     * @param pEnabled Whether or not the interceptor is enabled for detonation
     **************************************************************************/
    public void setDetonationEnabled(boolean pEnabled)
    {
        bDetonationEnabled = pEnabled;
    }
    
    /***************************************************************************
     * The setDetonateOverride function sets the flag that determines if the detonate control of the 
     * interceptor has been overriden.
     * 
     * @param pOverride Whether or not the control mode has been overriden
     **************************************************************************/
    public void setDetonateOverride(boolean pOverride)
    {
        bDetonateOverride = pOverride;
    }
    
    /***************************************************************************
     * The setAssignmentOverriden function sets the flag that determines if the assignment control of the 
     * interceptor has been overriden.
     * 
     * @param pOverride Whether or not the control mode has been overriden
     **************************************************************************/
    public void setAssignmentOverriden(boolean pOverride)
    {
        bAssignmentOverride = pOverride;
    }
    
    /***************************************************************************
     * The setLaunchTime function sets the timestamp representing the time of launch of the interceptor.
     * 
     * @param pTime The new launch time
     **************************************************************************/
    public void setLaunchTime(Timestamp pTime)
    {
        launchTime = pTime;
    }
    
    /***************************************************************************
     * The getCtrlThrustDX function retrieves the ctrlThrust value used by the interceptor controller calculations
     * for the derivative portion in the X direction.
     * 
     * @return The critical thrust value
     **************************************************************************/
    public double getCtrlThrustDX()
    {
        return thrust_dx;
    }
     /***************************************************************************
     * The getCtrlThrustIX function retrieves the ctrlThrust value used by the interceptor controller calculations
     * for the Integrator portion in the X direction.
     * 
     * @return The critical thrust value
     **************************************************************************/
    public double getCtrlThrustIX()
    {
        return thrust_ix;
    }
    
    /***************************************************************************
     * The setCtrlThrustDX function sets the ctrlThrust value used by the interceptor controller calculations
     * for the derivative portion in the X direction.
     * 
     * @param pThrust The critical thrust value
     **************************************************************************/
    public void setCtrlThrustDX(double pThrust)
    {
        thrust_dx = pThrust;
    }
    /***************************************************************************
     * The setCtrlThrustIX function sets the ctrlThrust value used by the interceptor controller calculations
     * for the Integrator portion in the X direction.
     * 
     * @param pThrust The critical thrust value
     **************************************************************************/
    public void setCtrlThrustIX(double pThrust)
    {
        thrust_ix = pThrust;
    }
    
    /***************************************************************************
     * The getCtrlThrustDY function retrieves the ctrlThrust value used by the interceptor controller calculations
     * for the derivative portion in the Y direction.
     * 
     * @return The critical thrust value
     **************************************************************************/
    public double getCtrlThrustDY()
    {
        return thrust_dy;
    }
    /***************************************************************************
     * The getCtrlThrustIY function retrieves the ctrlThrust value used by the interceptor controller calculations
     * for the Integrator portion in the Y direction.
     * 
     * @return The critical thrust value
     **************************************************************************/
    public double getCtrlThrustIY()
    {
        return thrust_iy;
    }
    
    /***************************************************************************
     * The setCtrlThrustDY function sets the ctrlThrust value used by the interceptor controller calculations
     * for the derivative portion in the Y direction.
     * 
     * @param pThrust The critical thrust value
     **************************************************************************/
    public void setCtrlThrustDY(double pThrust)
    {
        thrust_dy = pThrust;
    }
    
    /***************************************************************************
     * The setCtrlThrustIY function sets the ctrlThrust value used by the interceptor controller calculations
     * for the Integrator portion in the Y direction.
     * 
     * @param pThrust The critical thrust value
     **************************************************************************/
    public void setCtrlThrustIY(double pThrust)
    {
        thrust_iy = pThrust;
    }
    
    /***************************************************************************
     * The getCtrlThrustDZ function retrieves the ctrlThrust value used by the interceptor controller calculations
     * for the derivative portion in the Z direction.
     * 
     * @return The critical thrust value
     **************************************************************************/
    public double getCtrlThrustDZ()
    {
        return thrust_dz;
    }
    /***************************************************************************
     * The getCtrlThrustIZ function retrieves the ctrlThrust value used by the interceptor controller calculations
     * for the Integrator portion in the Z direction.
     * 
     * @return The critical thrust value
     **************************************************************************/
    public double getCtrlThrustIZ()
    {
        return thrust_iz;
    }
    /***************************************************************************
     * The setCtrlThrustDZ function sets the ctrlThrust value used by the interceptor controller calculations
     * for the derivative portion in the Z direction.
     * 
     * @param pThrust The critical thrust value
     **************************************************************************/
    public void setCtrlThrustDZ(double pThrust)
    {
        thrust_dz = pThrust;
    }
    /***************************************************************************
     * The setCtrlThrustIZ function sets the ctrlThrust value used by the interceptor controller calculations
     * for the Integrator portion in the Z direction.
     * 
     * @param pThrust The critical thrust value
     **************************************************************************/
    public void setCtrlThrustIZ(double pThrust)
    {
        thrust_iz = pThrust;
    }
}

