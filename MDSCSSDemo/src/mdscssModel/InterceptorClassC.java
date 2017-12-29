package mdscssModel;

/*******************************************************************************
 * The InterceptorClassC object is a Model class for class C interceptors
 ******************************************************************************/
public class InterceptorClassC extends Interceptor
{
    // defining constants
    private static final int MAX_THRUST_X = 6;
    private static final int MAX_THRUST_Y = 6;
    private static final int MAX_THRUST_Z = 8;
    
    private static final double FRIC_COEFF_X = 0.02;
    private static final double FRIC_COEFF_Y = 0.03;
    private static final double FRIC_COEFF_Z = 0.04;
    
    private static final int DET_RANGE = 200;
    
    /***************************************************************************
     * Constructor
     * 
     * @param pId The 2-char identification string of the interceptor
     **************************************************************************/
    public InterceptorClassC(String pId)
    {
        super(pId, FRIC_COEFF_X, FRIC_COEFF_Y, FRIC_COEFF_Z,
                MAX_THRUST_X, MAX_THRUST_Y, MAX_THRUST_Z, "[UNASSIGNED]", DET_RANGE);
    }
    
}
