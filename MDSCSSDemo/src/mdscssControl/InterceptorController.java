package mdscssControl;

import mdscssModel.Interceptor;
import mdscssModel.Missile;

/*******************************************************************************
 * The InterceptorController object handles the PID controller logic to compensate the 
 * thrust levels of a given interceptor as it tracks a threat.
 ******************************************************************************/
public class InterceptorController 
{
    //row = threat, col = interceptor
    private static final double[][] TUNING_TABLE_X = {{1.1,       1.3,      0.9},
                                                      {1.2,    1.2,      1.2},
                                                      {1.2,       1.3,      1.1}};
    
    private static final double[][] TUNING_TABLE_Y = {{1.1,       1.3,      1.1},
                                                      {1.2,     1.2,     1.2},
                                                      {1.2,    1.3,   1.1}};
    
    private static final double[][] TUNING_TABLE_Z = {{1.1,     1.3,    1.1},
                                                      {1.2,       1.2,    1.2},
                                                      {1.2,       1.3,    1.2}};
    
    private static final double[][] GAIN_TABLE     = {{2,         2,    2},
                                                      {2,       2,    2},
                                                      {2,      2,    2}};
    private static final int ATTRIB_KP = 1;
    private static final int ATTRIB_KC = 1;
    private static final int ATTRIB_FS = 1;   
    private static final int D_SYS = 10000;
    private static final int D_INT = 300;
    
    /***************************************************************************
     * Constructor
     **************************************************************************/
    public InterceptorController()
    {
        //empty constructor
    }
    
    /***************************************************************************
     * The trackMissilePair function updates the provided interceptor with new thrust values to better track
     * its assigned threat.
     * 
     * @param pInterceptor The interceptor to update
     * @param pThreat pInterceptor's assigned threat
     **************************************************************************/
    public void trackMissilePair(Interceptor pInterceptor, Missile pThreat)
    {        
        int tmpI, tmpT, final_thrust;
        double error2, thrust_p, thrust_d, error1, tune, error3, thrust_i;
        double attrib_ki, attrib_kf, attrib_a, attrib_b;
        
        
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
        
        // X direction ------------------------------------------------------------------------------------->
        error1 = ((pThreat.getPosX() - pInterceptor.getPosX()) *GAIN_TABLE[tmpT][tmpI]);
        tune = TUNING_TABLE_X[tmpT][tmpI];
        attrib_a = 1.3 * tune;
        attrib_b = 0.03 * tune;
        attrib_ki = (ATTRIB_KC * Math.pow(attrib_b, 2))/attrib_a;
        attrib_kf =((2 * ATTRIB_KC * attrib_b) - attrib_ki - (ATTRIB_KP * attrib_a))/attrib_a;
        
        if(Math.abs(error1) > D_SYS)
        {
            error2 = 0;
        }
        else
        {
            error2 = error1;
        }
        if(Math.abs(error1) > D_INT)
        {
            error3 = 0;
        }
        else
        {
            error3 = error2;
        }

        thrust_p = error2 * ATTRIB_KP;
        thrust_d = (attrib_kf * (1 - Math.exp((attrib_a * -1)/ATTRIB_FS)) * error2) + (Math.exp((attrib_a * -1)/ATTRIB_FS)) * pInterceptor.getCtrlThrustDX();
        
        thrust_i = (1/ATTRIB_FS) * attrib_ki * error3  + pInterceptor.getCtrlThrustIX();
        pInterceptor.setCtrlThrustIX(thrust_i);
        
        final_thrust = quantAndSat(thrust_i + thrust_p + thrust_d, pInterceptor.maxThrustX);
        pInterceptor.setCtrlThrustDX(thrust_d);

        if(Math.abs(error1) > D_SYS)
        {
            pInterceptor.setThrustX(pInterceptor.maxThrustX);
        }
        else
        {
            pInterceptor.setThrustX(final_thrust);
        }
        
        // Y direction ------------------------------------------------------------------------------------->
        error1 = ((pThreat.getPosY() - pInterceptor.getPosY()) *GAIN_TABLE[tmpT][tmpI]);
        tune = TUNING_TABLE_Y[tmpT][tmpI];
        attrib_a = 1.3 * tune;
        attrib_b = 0.03 * tune;
        attrib_ki = (ATTRIB_KC * Math.pow(attrib_b, 2))/attrib_a;
        attrib_kf =((2 * ATTRIB_KC * attrib_b) - attrib_ki - (ATTRIB_KP * attrib_a))/attrib_a;
        
        if(Math.abs(error1) > D_SYS)
        {
            error2 = 0;
        }
        else
        {
            error2 = error1;
        }
        if(Math.abs(error1) > D_INT)
        {
            error3 = 0;
        }
        else
        {
            error3 = error2;
        }
        
        thrust_p = error2 * ATTRIB_KP;
        thrust_d = (attrib_kf * (1 - Math.exp((attrib_a * -1)/ATTRIB_FS)) * error2) + (Math.exp((attrib_a * -1)/ATTRIB_FS)) * pInterceptor.getCtrlThrustDY();
        
        thrust_i = (1/ATTRIB_FS) * attrib_ki * error3  + pInterceptor.getCtrlThrustIY();
        pInterceptor.setCtrlThrustIY(thrust_i);
        
        final_thrust = quantAndSat(thrust_i + thrust_p + thrust_d, pInterceptor.maxThrustY);
        pInterceptor.setCtrlThrustDY(thrust_d);
     
        if(Math.abs(error1) > D_SYS)
        {
            pInterceptor.setThrustY(pInterceptor.maxThrustY);
        }
        else
        {
            pInterceptor.setThrustY(final_thrust);
        }
        
        // Z direction ------------------------------------------------------------------------------------->
        error1 = ((pThreat.getPosZ() - pInterceptor.getPosZ()) *GAIN_TABLE[tmpT][tmpI]);
        tune = TUNING_TABLE_Z[tmpT][tmpI];
        attrib_a = 1.3 * tune;
        attrib_b = 0.03 * tune;
        attrib_ki = (ATTRIB_KC * Math.pow(attrib_b, 2))/attrib_a;
        attrib_kf =((2 * ATTRIB_KC * attrib_b) - attrib_ki - (ATTRIB_KP * attrib_a))/attrib_a;
        
        if(Math.abs(error1) > D_SYS)
        {
            error2 = 0;
        }
        else
        {
            error2 = error1;
        }
        if(Math.abs(error1) > D_INT)
        {
            error3 = 0;
        }
        else
        {
            error3 = error2;
        }

        thrust_p = error2 * ATTRIB_KP;
        thrust_d = (attrib_kf * (1 - Math.exp((attrib_a * -1)/ATTRIB_FS)) * error2) + (Math.exp((attrib_a * -1)/ATTRIB_FS)) * pInterceptor.getCtrlThrustDZ();
        
        thrust_i = (1/ATTRIB_FS) * attrib_ki * error3  + pInterceptor.getCtrlThrustIZ();
        pInterceptor.setCtrlThrustIZ(thrust_i);
        
        final_thrust = quantAndSat(thrust_i + thrust_p + thrust_d, pInterceptor.maxThrustZ);
        pInterceptor.setCtrlThrustDZ(thrust_d);

        if(Math.abs(error1) > D_SYS)
        {
            pInterceptor.setThrustZ(pInterceptor.maxThrustZ);
        }
        else
        {
            pInterceptor.setThrustZ(final_thrust);
        }
    }
    
    /***************************************************************************
     * Given a calculated thrust value and a max bound, this function quantizes 
     * and saturates the thrust value into a power level that can be accepted by
     * the MCS thrust command
     * 
     * @param pThrust The PID calculated thrust value
     * @param pMax The maximum thrust value
     **************************************************************************/
    private int quantAndSat(double pThrust, int pMax)
    {        
        if(pThrust < (-0.875 * pMax))
        {
            return (pMax * -1);
        }
        else if(pThrust > (-0.875 * pMax) && pThrust < (-0.625 * pMax))
        {
            return (int) ((pMax * 0.75) * -1);
        }
        else if(pThrust > (-0.625 * pMax) && pThrust < (-0.325 * pMax))
        {
            return (int) ((pMax * 0.5) * -1);
        }
        else if(pThrust > (-0.325 * pMax) && pThrust < (-0.125 * pMax))
        {
            return (int) ((pMax * 0.25) * -1);
        }
        else if(pThrust > (-0.125 * pMax) && pThrust < (0.125 * pMax))
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
