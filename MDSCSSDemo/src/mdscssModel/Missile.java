/*******************************************************************************
 * File: Missile.java
 * Description: Model class that defines a missile and its attributes
 *
 ******************************************************************************/
package mdscssModel;

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
     * Missile
     * 
     * Constructor; initializes member variables
     * 
     * @param pId - the 2-char missile identification string
     * @param pType - the type of the missile (interceptor or Threat)
     * @param pFricX - the friction coefficient in the x direction
     * @param pFricY - the friction coefficient in the Y direction
     * @param pFricZ - the friction coefficient in the Z direction
     * @param pMThrustX - the max acceleration in the x direction
     * @param pMThrusty - the max acceleration in the y direction
     * @param pMThrustZ - the max acceleration in the z direction
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
     * getIdentifier
     * 
     * retrieves the id of the missile
     * 
     * @return the 2-char missile identification string
     **************************************************************************/
    public String getIdentifier()
    {
        return id;
    }
    
    /***************************************************************************
     * getIdentifier
     * 
     * retrieves the type of the missile
     * 
     * @return the type of missile
     **************************************************************************/
    public MissileType getType()
    {
        return type;
    }
    
    /***************************************************************************
     * getPositionVector
     * 
     * retrieves the position vector of the missile
     * 
     * @return an integer array of size 3 representing the position of the missile
     * in the format of [posX, posY, posZ]
     **************************************************************************/
    public int[] getPositionVector()
    {
        int[] tmp = {posX, posY, posZ};
        return tmp;
    }
    
    /***************************************************************************
     * getThrustVector
     * 
     * retrieves the thrust vector of the missile
     * 
     * @return an integer array of size 3 representing the acceleration of the missile
     * in the format of [thrustX, thrustY, thrustZ]
     **************************************************************************/
    public int[] getThrustVector()
    {
        int[] tmp = {thrustX, thrustY, thrustZ};
        return tmp;
    }
    
    /***************************************************************************
     * getFricVector
     * 
     * retrieves the friction coefficient vector of the missile
     * 
     * @return an integer array of size 3 representing the friction coefficients
     * of the missile, in each direction, in the format of [fricX, fricY, fricZ]
     **************************************************************************/
    public double[] getFricVector()
    {
        double[] tmp = {fricX, fricY, fricZ};
        return tmp;
    }
    
    /***************************************************************************
     * getPosX
     * 
     * retrieves the position of the missile in the X direction
     * 
     * @return the X position of the missile in a 3d space
     **************************************************************************/
    public int getPosX()
    {
        return posX;
    }
    
    /***************************************************************************
     * getPosY
     * 
     * retrieves the position of the missile in the Y direction
     * 
     * @return the Y position of the missile in a 3d space
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
     * getThrustX
     * 
     * retrieves the acceleration of the missile in the X direction
     * 
     * @return the X thrust value of the missile in a 3d space
     **************************************************************************/
    public int getThrustX()
    {
        return thrustX;
    }
    
    /***************************************************************************
     * getThrustY
     * 
     * retrieves the acceleration of the missile in the Y direction
     * 
     * @return the Y thrust value of the missile in a 3d space
     **************************************************************************/
    public int getThrustY()
    {
        return thrustY;
    }
    
    /***************************************************************************
     * getThrustZ
     * 
     * retrieves the acceleration of the missile in the Z direction
     * 
     * @return the Z thrust value of the missile in a 3d space
     **************************************************************************/
    public int getThrustZ()
    {
        return thrustZ;
    }
    
    /***************************************************************************
     * getFricX
     * 
     * retrieves the friction coefficient of the missile in the X direction
     * 
     * @return the friction coefficient in the X direction
     **************************************************************************/
    public double getFricX()
    {
        return fricX;
    }
    
    /***************************************************************************
     * getFricY
     * 
     * retrieves the friction coefficient of the missile in the Y direction
     * 
     * @return the friction coefficient in the Y direction
     **************************************************************************/
    public double getFricY()
    {
        return fricY;
    }
    
    /***************************************************************************
     * getFricZ
     * 
     * retrieves the friction coefficient of the missile in the Z direction
     * 
     * @return the friction coefficient in the Z direction
     **************************************************************************/
    public double getFricZ()
    {
        return fricZ;
    }
    
    /***************************************************************************
     * setPosition
     * 
     * updates the position of the missile in a 3d space
     * 
     * @param paramX - position in the x direction
     * @param paramY - position in the y direction
     * @param paramZ - position in the z direction
     **************************************************************************/
    public void setPosition(int paramX, int paramY, int paramZ)
    {
        posX = paramX;
        posY = paramY;
        posZ = paramZ;
    }
    
    //todo:: doesn't work right, fix in cleanup phase
    /***************************************************************************
     * setThrustValue
     * 
     * updates the acceleration of the missile in a 3d space
     * 
     * @param paramX - thrust in the x direction
     * @param paramY - thrust in the y direction
     * @param paramZ - thrust in the z direction
     **************************************************************************/
   /* public void setThrustValue(int paramX, int paramY, int paramZ)
    {
        if(paramX > maxThrustX || paramY > maxThrustY || paramZ > maxThrustZ)
        {
            thrustX = maxThrustX;
            thrustY = maxThrustY;
            thrustZ = maxThrustZ;
        }
        else if(paramX < (maxThrustX * -1) || paramY < (maxThrustY * -1) || paramZ < (maxThrustZ * -1))
        {
            thrustX = (maxThrustX * -1);
            thrustY = (maxThrustY * -1);
            thrustZ = (maxThrustZ * -1);
        }
        else
        {
            thrustX = paramX;
            thrustY = paramY;
            thrustZ = paramZ;
        }
    }*/
    
    public void setThrustX(int pThrustX)
    {
        thrustX = pThrustX;
    }
    
    public void setThrustY(int pThrustY)
    {
        thrustY = pThrustY;
    }
    
    public void setThrustZ(int pThrustZ)
    {
        thrustZ = pThrustZ;
    }
    
    public char getMissileClass()
    {
        return id.charAt(0);
    }
    
}
