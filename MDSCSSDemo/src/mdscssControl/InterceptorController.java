/*******************************************************************************
 * File: InterceptorController.java
 * Description:
 *
 ******************************************************************************/
package mdscssControl;

import mdscssModel.Interceptor;
import mdscssModel.Missile;

public class InterceptorController 
{
    private static final int D_SYS = 10000;
    private static final int D_INT = 300;
    private static final double[][] TUNING_TABLE_X = {{1, 1, 0.5},
                                                      {0.45, 1, 0.5},
                                                      {1, 1, 0.5}};
    private static final double[][] TUNING_TABLE_Y = {{1, 1, 1},
                                                      {0.5, 1, 0.5},
                                                      {0.45, 0.45, 0.5}};
    private static final double[][] TUNING_TABLE_Z = {{0.5, 0.5, 0.45},
                                                      {1, 0.5, 1},
                                                      {1, 0.5, 0.5}};
    
    private static final int ATTRIB_KP = 1;
    private static final int ATTRIB_KC = 1;
    private static final int ATTRIB_FS = 2;   ///should this be 1 or .5?
    
    
    
    /***************************************************************************
     * InterceptorController
     * 
     * Constructor
     **************************************************************************/
    public InterceptorController()
    {
        
    }
    
    public void trackMissilePair(Interceptor pInterceptor, Missile pThreat)
    {
        int tmpI, tmpT, final_thrust;
        double error2, thrust_p, thrust_d, error1, tune, attrib_a, attrib_b, error3, thrust_i;
        double attrib_ki, attrib_kf;
        
        
        // For X direction
        error1 = ((pThreat.getPosX() - pInterceptor.getPosX()) *0.5);
        if(pInterceptor.getMissileClass() == 'A')
            tmpI = 0;
        else if(pInterceptor.getMissileClass() == 'B')
            tmpI = 1;
        else
            tmpI = 2;
        
        if(pThreat.getMissileClass() == 'X')
            tmpT = 0;
        else if(pThreat.getMissileClass() == 'Y')
            tmpT = 1;
        else
            tmpT = 2;
        
        
        
        
        tune = TUNING_TABLE_X[tmpT][tmpI];
        attrib_a = 1.3 * tune;
        attrib_b = 0.03 * tune;
        attrib_ki = (ATTRIB_KC * Math.pow(attrib_b, 2))/attrib_a;
        attrib_kf =((2 * ATTRIB_KC * attrib_b) - attrib_ki - (ATTRIB_KP * attrib_a))/attrib_a;
        
        if(error1 > D_SYS)
        {
            error2 = 0;
        }
        else
        {
            error2 = error1;
        }
        if(error1 > D_INT)
        {
            error3 = 0;
        }
        else
        {
            error3 = error2;
        }
        
        
        
        thrust_p = error2 * ATTRIB_KP;
        
        thrust_d = attrib_kf * (1 - Math.exp((attrib_a * -1)/ATTRIB_FS)) * error2 + (1 - Math.exp((attrib_a * -1)/ATTRIB_FS)) * pInterceptor.getCtrlThrustD();
        pInterceptor.setCtrlThrustD(thrust_d);
        
        thrust_i = (1/ATTRIB_FS) * attrib_ki * error3  + pInterceptor.getCtrlThrustI();
        pInterceptor.setCtrlThrustI(thrust_i);
        
        final_thrust = quantAndSat(thrust_i + thrust_p + thrust_d, pInterceptor.maxThrustX);
        
        System.out.println(final_thrust);
        
        if(error1 > D_SYS)
        {
            pInterceptor.setThrustX(pInterceptor.maxThrustX);
        }
        else
        {
            //todo:: individual sets
            pInterceptor.setThrustX(final_thrust);
        }
        
    }
    
    private int quantAndSat(double pThrust, int pMax)
    {
        /*
            v = [-1, -0.875, -0.625, -0.375, -0.125, 0.125, 0.375, 0.625, 0.875]
            V = v*max_thrust;
        */
        
        if(pThrust < (-0.875 * pMax))
        {
            return (pMax * -1);
        }
        else if(pThrust < (-0.875 * pMax) && pThrust > (-0.625 * pMax))
        {
            return (int) ((pMax * 0.75) * -1);
        }
        else if(pThrust < (-0.625 * pMax) && pThrust > (-0.325 * pMax))
        {
            return (int) ((pMax * 0.5) * -1);
        }
        else if(pThrust < (-0.325 * pMax) && pThrust > (-0.125 * pMax))
        {
            return (int) ((pMax * 0.25) * -1);
        }
        else if(pThrust < (-0.125 * pMax) && pThrust > (0.125 * pMax))
        {
            return 0;
        }
        else if(pThrust > (0.125 * pMax) && pThrust < (0.325 * pMax))
        {
            return (int) (pMax * 0.25);
        }
        else if(pThrust > (0.325 * pMax) && pThrust < (0.625 * pMax))
        {
            return (int) (pMax * 0.5);
        }
        else if(pThrust > (0.625 * pMax) && pThrust < (0.875 * pMax))
        {
            return (int) (pMax * 0.75);
        }
        else
        {
            return pMax;
        }

    }
    
}
