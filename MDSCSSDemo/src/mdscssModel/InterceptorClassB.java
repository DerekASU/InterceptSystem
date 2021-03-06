package mdscssModel;

/*******************************************************************************
 * The InterceptorClassB object is a Model class for class B interceptors
 ******************************************************************************/
public class InterceptorClassB extends Interceptor
{
    // defining constants
    private static final int MAX_THRUST_X = 10;
    private static final int MAX_THRUST_Y = 10;
    private static final int MAX_THRUST_Z = 12;
    
    private static final double FRIC_COEFF_X = 0.05;
    private static final double FRIC_COEFF_Y = 0.06;
    private static final double FRIC_COEFF_Z = 0.03;
    
    private static final int DET_RANGE = 80;
    
    /***************************************************************************
     * Constructor
     * 
     * @param pId The 2-char identification string of the interceptor
     **************************************************************************/
    public InterceptorClassB(String pId)
    {
        super(pId, FRIC_COEFF_X, FRIC_COEFF_Y, FRIC_COEFF_Z,
                MAX_THRUST_X, MAX_THRUST_Y, MAX_THRUST_Z, "[UNASSIGNED]", DET_RANGE);
    }
    
}
