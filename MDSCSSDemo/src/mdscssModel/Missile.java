package mdscssModel;

/*******************************************************************************
 * The Missile object is a Model class that defines a missile and its attributes.
 ******************************************************************************/
public class Missile 
{
    // Enumerations
    public enum MissileType
    {
        UNDEFINED,
        THREAT,
        INTERCEPTOR;
    }
    
    // Member variables
    protected String id;
    protected MissileType type;
    protected int posX, posY, posZ;
    protected int thrustX, thrustY, thrustZ;
    protected double fricX, fricY, fricZ;
    public int maxThrustX, maxThrustY, maxThrustZ;
    
    /***************************************************************************
     * Constructor
     * 
     * @param pId The 2-char missile identification string
     * @param pType The type of the missile (interceptor or Threat)
     * @param pFricX The friction coefficient in the x direction
     * @param pFricY The friction coefficient in the Y direction
     * @param pFricZ The friction coefficient in the Z direction
     * @param pMThrustX The max acceleration in the x direction
     * @param pMThrustY The max acceleration in the y direction
     * @param pMThrustZ The max acceleration in the z direction
     **************************************************************************/
    public Missile(String pId, MissileType pType, double pFricX, double pFricY, double pFricZ,
                    int pMThrustX, int pMThrustY, int pMThrustZ)
    {
        id = pId;
        type = pType;
        
        fricX = pFricX;
        fricY = pFricY;
        fricZ = pFricZ;
        
        maxThrustX = pMThrustX;
        maxThrustY = pMThrustY;
        maxThrustZ = pMThrustZ;
    }
    
    /***************************************************************************
     * The getIdentifier function retrieves the id of the missile.
     * 
     * @return The 2-char missile identification string
     **************************************************************************/
    public String getIdentifier()
    {
        return id;
    }
    
    /***************************************************************************
     * The getIdentifier function retrieves the type of the missile.
     * 
     * @return The type of missile
     **************************************************************************/
    public MissileType getType()
    {
        return type;
    }
    
    /***************************************************************************
     * The getPositionVector function retrieves the position vector of the missile.
     * 
     * @return An integer array of size 3 representing the position of the missile
     * in the format of [posX, posY, posZ]
     **************************************************************************/
    public int[] getPositionVector()
    {
        int[] tmp = {posX, posY, posZ};
        return tmp;
    }
    
    /***************************************************************************
     * The getThrustVector function retrieves the thrust vector of the missile.
     * 
     * @return An integer array of size 3 representing the acceleration of the missile
     * in the format of [thrustX, thrustY, thrustZ]
     **************************************************************************/
    public int[] getThrustVector()
    {
        int[] tmp = {thrustX, thrustY, thrustZ};
        return tmp;
    }
    
    /***************************************************************************
     * The getFricVector function retrieves the friction coefficient vector of the missile.
     * 
     * @return An integer array of size 3 representing the friction coefficients
     * of the missile, in each direction, in the format of [fricX, fricY, fricZ]
     **************************************************************************/
    public double[] getFricVector()
    {
        double[] tmp = {fricX, fricY, fricZ};
        return tmp;
    }
    
    /***************************************************************************
     * The getPosX function retrieves the position of the missile in the X direction.
     * 
     * @return The X position of the missile in a 3d space
     **************************************************************************/
    public int getPosX()
    {
        return posX;
    }
    
    /***************************************************************************
     * The getPosY function retrieves the position of the missile in the Y direction.
     * 
     * @return The Y position of the missile in a 3d space
     **************************************************************************/
    public int getPosY()
    {
        return posY;
    }
    
    /***************************************************************************
     * getPosZ
     * 
     * retrieves the position of the missile in the Z direction
     * 
     * @return the Z position of the missile in a 3d space
     **************************************************************************/
    public int getPosZ()
    {
        return posZ;
    }
    
    /***************************************************************************
     * The getThrustX function retrieves the power level of the missile thruster in the X direction.
     * 
     * @return The X thrust value of the missile in a 3d space
     **************************************************************************/
    public int getThrustX()
    {
        return thrustX;
    }
    
    /***************************************************************************
     * The getThrustY function retrieves the power level of the missile thruster in the Y direction.
     * 
     * @return The X thrust value of the missile in a 3d space
     **************************************************************************/
    public int getThrustY()
    {
        return thrustY;
    }
    
    /***************************************************************************
     * The getThrustZ function retrieves the power level of the missile thruster in the Z direction.
     * 
     * @return The Z thrust value of the missile in a 3d space
     **************************************************************************/
    public int getThrustZ()
    {
        return thrustZ;
    }
    
    /***************************************************************************
     * The getFricX function retrieves the friction coefficient of the missile in the X direction.
     * 
     * @return The friction coefficient in the X direction
     **************************************************************************/
    public double getFricX()
    {
        return fricX;
    }
    
    /***************************************************************************
     * The getFricY function retrieves the friction coefficient of the missile in the Y direction.
     * 
     * @return The friction coefficient in the Y direction
     **************************************************************************/
    public double getFricY()
    {
        return fricY;
    }
    
    /***************************************************************************
     * The getFricZ function retrieves the friction coefficient of the missile in the Z direction.
     * 
     * @return The friction coefficient in the Z direction
     **************************************************************************/
    public double getFricZ()
    {
        return fricZ;
    }
    
    /***************************************************************************
     * The setPosition function updates the position of the missile in a 3d space.
     * 
     * @param paramX Position in the x direction
     * @param paramY Position in the y direction
     * @param paramZ Position in the z direction
     **************************************************************************/
    public void setPosition(int paramX, int paramY, int paramZ)
    {
        posX = paramX;
        posY = paramY;
        posZ = paramZ;
    }
    
    /***************************************************************************
     * The setThrustValue function updates the acceleration of the missile in a 3d space.
     * 
     * @param paramX Thrust in the x direction
     * @param paramY Thrust in the y direction
     * @param paramZ Thrust in the z direction
     **************************************************************************/
    public void setThrustValue(int paramX, int paramY, int paramZ)
    {
        thrustX = paramX;
        thrustY = paramY;
        thrustZ = paramZ;
    }
    
    /***************************************************************************
     * The setThrustX function updates the acceleration of the missile in the x direction.
     * 
     * @param pThrustX Thrust in the x direction
     **************************************************************************/
    public void setThrustX(int pThrustX)
    {
        thrustX = pThrustX;
    }
    
    /***************************************************************************
     * The setThrustY function updates the acceleration of the missile in the Y direction.
     * 
     * @param pThrustY Thrust in the Y direction
     **************************************************************************/
    public void setThrustY(int pThrustY)
    {
        thrustY = pThrustY;
    }
    
    /***************************************************************************
     * The setThrustZ function updates the acceleration of the missile in the Z direction.
     * 
     * @param pThrustZ Thrust in the Z direction
     **************************************************************************/
    public void setThrustZ(int pThrustZ)
    {
        thrustZ = pThrustZ;
    }
    
    /***************************************************************************
     * The getMissileClass function retrieves the class of the missile.
     * 
     * @return A char representing the class of the missile
     **************************************************************************/
    public char getMissileClass()
    {
        return id.charAt(0);
    }
    
}
