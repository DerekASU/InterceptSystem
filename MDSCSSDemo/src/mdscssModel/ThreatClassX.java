/*******************************************************************************
 * File: ThreatClassX.java
 * Description:
 *
 ******************************************************************************/
package mdscssModel;


public class ThreatClassX extends Missile
{
    private static final int MAX_THRUST_X = 4;
    private static final int MAX_THRUST_Y = 4;
    private static final int MAX_THRUST_Z = 4;
    
    private static final double FRIC_COEFF_X = 0.04;
    private static final double FRIC_COEFF_Y = 0.04;
    private static final double FRIC_COEFF_Z = 0.04;
    
    /***************************************************************************
     * ThreatClassX
     * 
     * Constructor
     **************************************************************************/
    public ThreatClassX(String pId)
    {
        id = pId;
        
        fricX = FRIC_COEFF_X;
        fricY = FRIC_COEFF_Y;
        fricZ = FRIC_COEFF_Z;
        
        maxThrustX = MAX_THRUST_X; 
        maxThrustY = MAX_THRUST_Y; 
        maxThrustZ = MAX_THRUST_Z; 
    }
    

}
