/*******************************************************************************
 * File: ControlThread.java
 * Description:
 *
 ******************************************************************************/
package mdscssControl;

import java.util.ArrayList;

public class ControlThread implements Runnable
{
    private MDSCSSController controlUtility;
    private boolean bRunning;
    private controlState threadState;
    
    private enum controlState
    { 
        initializing,
        connecting,
        activating,
        operational
    }
    
    
    /***************************************************************************
     * ControlThread
     * 
     * Constructor
     **************************************************************************/
    public ControlThread(MDSCSSController pController)
    {
        bRunning = false;
        threadState = controlState.initializing;
        controlUtility = pController;
    }
    
    /***************************************************************************
     * Run
     * 
     * TODO::Add description
     **************************************************************************/
    public void run()
    {
        bRunning = true;
        long entryTime = 0;
        long elapsedTime = 0;
        boolean everyother = false;

                
        System.out.println("ControlThread: Thread Started");
        
        try 
        {       
            while (bRunning) 
            {
               entryTime = System.currentTimeMillis();
               controlUtility.handleWatchdogTimer();
               
               switch(threadState)
               {
                   case initializing: 
                       
                       if(controlUtility.establishConnection())
                       {
                           threadState = controlState.activating;
                       }
                       else
                       {
                           controlUtility.checkForFailure();
                       }
                           

                       controlUtility.establishStatus();


                       
                       break;
                   case activating: 
                       controlUtility.initializeModel();
                       controlUtility.initializeWatchdog();
                       threadState = controlState.operational;
                       
                       break;
                   case operational: 
                       //ping all watchdogs, todo:: timer for if > 5 status is red, and all things would detonate? or should that be in the catch in the controller commands
                       
                       
                       controlUtility.updateModel();
                       
                        if(everyother)
                        {
                            controlUtility.handleOperationalControl();
                            everyother = false;
                        }
                        else
                            everyother = true;


                       
                      
                       break;
               }
               
                 elapsedTime = System.currentTimeMillis() - entryTime;
                        if(elapsedTime < 500)
                        {
                             Thread.sleep(500 - elapsedTime);
                        }
                        else if(elapsedTime > 1000)
                        {
                            System.out.println("ControlThread -- Warning: latency time approaching 1 second [" + elapsedTime + "]");
                        }
            }
        } 
        catch (InterruptedException e) 
        {
            System.out.print("ControlThread -- interrupted: stopping thread");
            bRunning = false;
        }
    }

}
