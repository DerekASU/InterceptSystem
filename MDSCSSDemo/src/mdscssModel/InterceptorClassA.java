/*******************************************************************************
 * File: InterceptorClassA.java
 * Description: Model class for class A interceptors
 *
 ******************************************************************************/
package mdscssModel;


public class InterceptorClassA extends Interceptor
{
    // defining constants
    private static final int MAX_THRUST_X = 8;
    private static final int MAX_THRUST_Y = 8;
    private static final int MAX_THRUST_Z = 8;
    
    private static final double FRIC_COEFF_X = 0.03;
    private static final double FRIC_COEFF_Y = 0.03;
    private static final double FRIC_COEFF_Z = 0.06;
    
    private static final int DET_RANGE = 100;
    
    /***************************************************************************
     * InterceptorClassA
     * 
     * Constructor
     * 
     * @param pId - the 2-char identification string of the interceptor
     **************************************************************************/
    public InterceptorClassA(String pId)
    {
        super(pId, FRIC_COEFF_X, FRIC_COEFF_Y, FRIC_COEFF_Z,
                MAX_THRUST_X, MAX_THRUST_Y, MAX_THRUST_Z, "NA", DET_RANGE);
    }
}
