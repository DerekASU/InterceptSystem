package mdscssModel;

/*******************************************************************************
 * The ThreatClassX object is a Model class for class X threats
 ******************************************************************************/
public class ThreatClassX extends Missile
{
    // defining constants
    private static final int MAX_THRUST_X = 4;
    private static final int MAX_THRUST_Y = 4;
    private static final int MAX_THRUST_Z = 4;
    
    private static final double FRIC_COEFF_X = 0.04;
    private static final double FRIC_COEFF_Y = 0.04;
    private static final double FRIC_COEFF_Z = 0.04;
    
    /***************************************************************************
     * Constructor
     * 
     * @param pId The 2-char identification string of the threat
     **************************************************************************/
    public ThreatClassX(String pId)
    {
        super(pId, MissileType.THREAT, FRIC_COEFF_X, FRIC_COEFF_Y, FRIC_COEFF_Z,
                MAX_THRUST_X, MAX_THRUST_Y, MAX_THRUST_Z);
    }
    

}
