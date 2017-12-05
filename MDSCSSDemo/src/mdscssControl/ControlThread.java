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
        System.out.print("Thread Started:");
        
        try 
        {       
            while (bRunning) 
            {
               if(!bCtEstablished)
               {
                   bCtEstablished = controlUtility.establishConnection();
                   
                   controlUtility.sendCommand();
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
