/*******************************************************************************
 * File: InterceptorClassC.java
 * Description: model class for class c interceptors
 *
 ******************************************************************************/
package mdscssModel;


public class InterceptorClassC extends Interceptor
{
    // defining constants
    private static final int MAX_THRUST_X = 6;
    private static final int MAX_THRUST_Y = 6;
    private static final int MAX_THRUST_Z = 8;
    
    private static final double FRIC_COEFF_X = 0.02;
    private static final double FRIC_COEFF_Y = 0.03;
    private static final double FRIC_COEFF_Z = 0.04;
    
    private static final int DET_RANGE = 100;
    
    /***************************************************************************
     * InterceptorClassC
     * 
     * Constructor
     * 
     * @param pId - the 2-char identification string of the interceptor
     **************************************************************************/
    public InterceptorClassC(String pId)
    {
        super(pId, FRIC_COEFF_X, FRIC_COEFF_Y, FRIC_COEFF_Z,
                MAX_THRUST_X, MAX_THRUST_Y, MAX_THRUST_Z, "NA", DET_RANGE);
    }
    
}
