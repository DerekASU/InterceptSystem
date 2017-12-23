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
    private boolean bRunning, bFsPeriod;
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
     * Main control thread.  Handles control flow to initialize the controller
     * and maintain connections and operability with the subsystems.
     **************************************************************************/
    public void run()
    {
        long entryTime = 0, elapsedTime = 0;
        bRunning = true;
        bFsPeriod = false;

        System.out.println("ControlThread: Thread Started");
        
        try 
        {       
            while (bRunning) 
            {
               entryTime = System.currentTimeMillis();
               
               switch(threadState)
               {
                   case initializing: 
                       // Establish TCP connections
                       if(controlUtility.establishConnection())
                       {
                           threadState = controlState.activating;
                       }
                       // Check for socket failures, and handle 5 minute connection timeout
                       else
                       {
                           controlUtility.checkForFailure();
                       }
                           
                       //endure that the watchdog is disabled until all subsystems are connected
                       controlUtility.disableWatchdog();
                       
                       //Update operator with connection status
                       controlUtility.establishStatus();
                       break;
                   case activating: 
                       // populate the internal database with missiles
                       controlUtility.initializeModel();
                       
                       // start the watchdog
                       controlUtility.initializeWatchdog();

                       threadState = controlState.operational;
                       break;
                   case operational: 
                       // ping the watchdog
                       controlUtility.handleWatchdogTimer();
                       // update the database with updated information
                       controlUtility.updateModel();
                       // respond to operator interaction
                       controlUtility.handleControlLogic();
                       
                       // the PID controller has been tuned to an Fs value of 1
                       // while this thread wakes up every .5 seconds, to handle this
                       // PID updates and thrust commands are gated to only send every
                       // second
                        if(bFsPeriod)
                        {
                            controlUtility.handleThrustControl();
                            bFsPeriod = false;
                        }
                        else
                        {
                            bFsPeriod = true;
                        }

                       break;
                }
               
                // command the thread to sleep so that it wakes up exactly a half second from
                // the last time it awoke
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
            System.out.print("ControlThread: interrupted: stopping thread");
            bRunning = false;
        }
    }

}
