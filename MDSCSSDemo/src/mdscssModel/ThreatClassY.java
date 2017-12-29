package mdscssModel;

/*******************************************************************************
 * The ThreatClassY object is a Model class for class Y threats
 ******************************************************************************/
public class ThreatClassY extends Missile
{
    // defining constants
    private static final int MAX_THRUST_X = 4;
    private static final int MAX_THRUST_Y = 4;
    private static final int MAX_THRUST_Z = 6;
    
    private static final double FRIC_COEFF_X = 0.06;
    private static final double FRIC_COEFF_Y = 0.07;
    private static final double FRIC_COEFF_Z = 0.04;
    
    /***************************************************************************
     * Constructor
     * 
     * @param pId The 2-char identification string of the threat
     **************************************************************************/
    public ThreatClassY(String pId)
    {
        super(pId, MissileType.THREAT, FRIC_COEFF_X, FRIC_COEFF_Y, FRIC_COEFF_Z,
                MAX_THRUST_X, MAX_THRUST_Y, MAX_THRUST_Z);
    }
    
}
