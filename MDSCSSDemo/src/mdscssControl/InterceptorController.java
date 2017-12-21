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
    private static final int ATTRIB_FS = 1;   ///should this be 1 or .5?
    
    private enum direction{dirX, dirY, dirZ};
    
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

        //position and final thrust
       // System.out.println(pInterceptor.getPosX() + " " +pThreat.getPosX() + " " + (pThreat.getPosX() - pInterceptor.getPosX()));
     
        if(Math.abs(error1) > D_SYS)
        {
            pInterceptor.setThrustX(pInterceptor.maxThrustX);
        }
        else
        {
            pInterceptor.setThrustX(final_thrust);
        }
        
        // y direction ------------------------------------------------------------------------------------->
        
        
        
        error1 = ((pThreat.getPosY() - pInterceptor.getPosY()) *0.5);

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

        //position and final thrust
        //System.out.println(pInterceptor.getPosX() + " " +pThreat.getPosX() + " " + (pThreat.getPosX() - pInterceptor.getPosX()));
     
        if(Math.abs(error1) > D_SYS)
        {
            pInterceptor.setThrustY(pInterceptor.maxThrustY);
        }
        else
        {
            pInterceptor.setThrustY(final_thrust);
        }
        
        // z direction ------------------------------------------------------------------------------------->
        
        
        
        error1 = ((pThreat.getPosZ() - pInterceptor.getPosZ()) *0.5);

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

        //position and final thrust
        //System.out.println((pThreat.getPosX() - pInterceptor.getPosX()) + " " + (pThreat.getPosY() - pInterceptor.getPosY()) + " " + (pThreat.getPosZ() - pInterceptor.getPosZ()));
     
        if(Math.abs(error1) > D_SYS)
        {
            pInterceptor.setThrustZ(pInterceptor.maxThrustZ);
        }
        else
        {

            pInterceptor.setThrustZ(final_thrust);
        }
    }
    
    
    /*    
    private int trackUniDirectional(Interceptor pInterceptor, Missile pThreat, direction pDir)
    {
        int final_thrust = 0, tPos = 0, intPos = 0, tmpI, tmpT, maxT = 0;
        double error2, thrust_p, thrust_d, error1, attrib_a, attrib_b, error3, thrust_i, tune = 0.0, ctD = 0.0, ctI=0.0;
        double attrib_ki, attrib_kf;
        
        
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
        
        switch(pDir)
        {
            case dirX:
                intPos = pInterceptor.getPosX();
                tPos = pThreat.getPosX();
                tune = TUNING_TABLE_X[tmpT][tmpI];
                ctD = pInterceptor.getCtrlThrustDX();
                ctI = pInterceptor.getCtrlThrustIX();
                maxT = pInterceptor.maxThrustX;
                break;
            case dirY:
                intPos = pInterceptor.getPosY();
                tPos = pThreat.getPosY();
                tune = TUNING_TABLE_Y[tmpT][tmpI];
                ctD = pInterceptor.getCtrlThrustDY();
                ctI = pInterceptor.getCtrlThrustIY();
                maxT = pInterceptor.maxThrustY;
                break;
            case dirZ:
                intPos = pInterceptor.getPosZ();
                tPos = pThreat.getPosZ();
                tune = TUNING_TABLE_Z[tmpT][tmpI];
                ctD = pInterceptor.getCtrlThrustDZ();
                ctI = pInterceptor.getCtrlThrustIZ();
                maxT = pInterceptor.maxThrustZ;
                break;
        }
        
        
        
        
        error1 = ((tPos - intPos) *0.5);
        
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
        
        thrust_d = (attrib_kf * (1 - Math.exp((attrib_a * -1)/ATTRIB_FS)) * error2) + (Math.exp((attrib_a * -1)/ATTRIB_FS)) * ctD;

        
        thrust_i = (1/ATTRIB_FS) * attrib_ki * error3  + ctI;
  
        
        final_thrust = quantAndSat(thrust_i + thrust_p + thrust_d, maxT);
        
        System.out.println(pInterceptor.getPosX() + " " +pThreat.getPosX() + " " + (pThreat.getPosX() - pInterceptor.getPosX()));
        
        
        switch(pDir)
        {
            case dirX:
                pInterceptor.setCtrlThrustDX(ctD);
                pInterceptor.setCtrlThrustIX(ctI);
                break;
            case dirY:
                pInterceptor.setCtrlThrustDY(ctD);
                pInterceptor.setCtrlThrustIY(ctI);
                break;
            case dirZ:
                pInterceptor.setCtrlThrustDZ(ctD);
                pInterceptor.setCtrlThrustIZ(ctI);
                break;
        }
                
        if(Math.abs(error1) > D_SYS)
        {
            return maxT;
        }
        else
        {
            return final_thrust;
        }

    }*/
    
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
