/*******************************************************************************
 * File: Missile.java
 * Description:
 *
 ******************************************************************************/
package mdscssModel;

public abstract class Missile 
{
    protected String id;
    protected int posX, posY, posZ;
    protected int thrustX, thrustY, thrustZ;
    protected double fricX, fricY, fricZ;
    protected int maxThrustX, maxThrustY, maxThrustZ;
    
    public int[] getPositionVector()
    {
        int[] tmp = {posX, posY, posZ};
        return tmp;
    }
    
    public int[] getThrustVector()
    {
        int[] tmp = {thrustX, thrustY, thrustZ};
        return tmp;
    }
    
    public double[] getFricVector()
    {
        double[] tmp = {fricX, fricY, fricZ};
        return tmp;
    }
    
    public int getPosX()
    {
        return posX;
    }
    
    public int getPosY()
    {
        return posY;
    }
    
    public int getPosZ()
    {
        return posZ;
    }
    
    public int getThrustX()
    {
        return thrustX;
    }
    
    public int getThrustY()
    {
        return thrustY;
    }
    
    public int getThrustZ()
    {
        return thrustZ;
    }
    
    public double getFricX()
    {
        return fricX;
    }
    
    public double getFricY()
    {
        return fricY;
    }
    
    public double getFricZ()
    {
        return fricZ;
    }
    
    public void setPosition(int paramX, int paramY, int paramZ)
    {
        posX = paramX;
        posY = paramY;
        posZ = paramZ;
    }
    
    public void setThrustValue(int paramX, int paramY, int paramZ)
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
    }
    
}
