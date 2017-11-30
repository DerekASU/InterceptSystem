/*******************************************************************************
 * File: InterceptorClassC.java
 * Description:
 *
 ******************************************************************************/
package mdscssModel;


public class InterceptorClassC extends Interceptor
{
    private static final int MAX_THRUST_X = 6;
    private static final int MAX_THRUST_Y = 6;
    private static final int MAX_THRUST_Z = 8;
    
    private static final double FRIC_COEFF_X = 0.02;
    private static final double FRIC_COEFF_Y = 0.03;
    private static final double FRIC_COEFF_Z = 0.04;
    
    private static final int DET_RANGE = 100;
    

    public InterceptorClassC(String pId)
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
