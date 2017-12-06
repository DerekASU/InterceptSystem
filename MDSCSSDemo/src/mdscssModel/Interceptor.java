/*******************************************************************************
 * File: Interceptor.java
 * Description: Model class that defines an interceptor, a type of missile
 *
 ******************************************************************************/
package mdscssModel;

public class Interceptor extends Missile
{
    // Enumerations
    public enum interceptorState
    {
        PRE_FLIGHT,
        IN_FLIGHT,
        DETONATED,
        DESTRUCTED;
    }
    
    // Member variables
    protected interceptorState state;
    protected String assignedThreat;
    protected int detonationRange;
    
    protected boolean bDisabled;
    protected boolean bDetonationEnabled;
    protected boolean bDetonateOverride;
    protected boolean bAssignmentOverride;
    
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
}

