/*******************************************************************************
 * File: ControlThread.java
 * Description:
 *
 ******************************************************************************/
package mdscssControl;

public class ControlThread implements Runnable
{
    private MDSCSSController controlUtility;
    private boolean bRunning, bCtEstablished;
    
    /***************************************************************************
     * ControlThread
     * 
     * Constructor
     **************************************************************************/
    public ControlThread(MDSCSSController pController)
    {
        bRunning = false;
        bCtEstablished = false;
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
        boolean tmp = false;
        System.out.print("Thread Started:");
        
        try 
        {       
            while (bRunning) 
            {
                
               if(!bCtEstablished)
               {
                   
                   bCtEstablished = controlUtility.establishConnection();
                   
                   controlUtility.cmdSmssActivateSafety("A3");
                   controlUtility.cmdMcssLaunch("A3");
                   
                   controlUtility.cmdMcssThrust("A3", 0, 0, 0);
                                      
               }
               else
               {                  
                   controlUtility.cmdSmssPingWatchdog("A3");
                   controlUtility.cmdMcssThrust("A3", 4, 4, 4);

               }
               
               Thread.sleep(500);
               
               
            }
        } 
        catch (InterruptedException e) 
        {
            System.out.print(" interrupted:");
            bRunning = false;
        }
    }
}
