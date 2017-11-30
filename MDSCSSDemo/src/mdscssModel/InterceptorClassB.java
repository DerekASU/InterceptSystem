/*******************************************************************************
 * File: InterceptorClassB.java
 * Description:
 *
 ******************************************************************************/
package mdscssModel;


public class InterceptorClassB extends Interceptor
{
    private static final int MAX_THRUST_X = 10;
    private static final int MAX_THRUST_Y = 10;
    private static final int MAX_THRUST_Z = 12;
    
    private static final double FRIC_COEFF_X = 0.05;
    private static final double FRIC_COEFF_Y = 0.06;
    private static final double FRIC_COEFF_Z = 0.03;
    
    private static final int DET_RANGE = 100;
    

    public InterceptorClassB(String pId)
    {
        id = pId;
        state = interceptorState.PRE_FLIGHT;
        assignedThreat = "N/A";
        
        fricX = FRIC_COEFF_X;
        fricY = FRIC_COEFF_Y;
        fricZ = FRIC_COEFF_Z;
        
        maxThrustX = MAX_THRUST_X; 
        maxThrustY = MAX_THRUST_Y; 
        maxThrustZ = MAX_THRUST_Z;
        
        detonationRange = DET_RANGE;
    }
    
}
