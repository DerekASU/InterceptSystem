/*******************************************************************************
 * File: Interceptor.java
 * Description:
 *
 ******************************************************************************/
package mdscssModel;

abstract class Interceptor extends Missile
{
    public enum interceptorState
    {
        PRE_FLIGHT,
        IN_FLIGHT,
        DETONATED,
        DESTRUCTED;
    }
    
    protected interceptorState state;
    protected String assignedThreat;
    protected int detonationRange;
    
    protected boolean bDisabled;
    protected boolean bDetonationEnabled;
    protected boolean bDetonateOverride;
    protected boolean bAssignmentOverride;
    
    public interceptorState getState()
    {
        return state;
    }
    
    public String getAssignedThreat()
    {
        return assignedThreat;
    }
    
    public int getDetonationRange()
    {
        return detonationRange;
    }
    
    public boolean isDisabled()
    {
        return bDisabled;
    }
    
    public boolean isDetonationEnabled()
    {
        return bDetonationEnabled;
    }
    
    public boolean isDetonateOverriden()
    {
        return bDetonateOverride;
    }
    
    public boolean isAssignmentOverriden()
    {
        return bAssignmentOverride;
    }
    
    public void setState(interceptorState pState)
    {
        state = pState;
    }
    
    public void setAssignedThreat(String pID)
    {
        assignedThreat = pID;
    }
        
    public void setDisabled(boolean pDisable)
    {
        bDisabled = pDisable;
    }
    
    public void setDetonationEnabled(boolean pEnabled)
    {
        bDetonationEnabled = pEnabled;
    }
    
    public void setDetonateOverride(boolean pOverride)
    {
        bDetonateOverride = pOverride;
    }
    
    public void setAssignmentOverriden(boolean pOverride)
    {
        bAssignmentOverride = pOverride;
    }
}

