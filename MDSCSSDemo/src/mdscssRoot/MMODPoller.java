/*******************************************************************************
 * File: MMODPoller.java
 * Description: Polling thread for the main GUI components.  This thread awakes 
 * every second to command the GUI components to query the model and update
 * their states.  this is done independently from the control thread to maintain
 * latency requirements, and independent of the main event thread to minimize
 * visible lag
 ******************************************************************************/
package mdscssRoot;

public class MMODPoller implements Runnable
{
    private MMODFrame mView;
    private boolean bRunning;

    /***************************************************************************
     * MMODPoller
     * 
     * Constructor
     * 
     * @param pMMOD - reference to the MMOD JFrame
     **************************************************************************/
    public MMODPoller(MMODFrame pMMOD)
    {
        bRunning = false;
        mView = pMMOD;
    }
    
    /***************************************************************************
     * Run
     * 
     * Handles periodic update logic
     **************************************************************************/
    public void run()
    {
        bRunning = true;
        long entryTime = 0;
        long elapsedTime = 0;
                
        System.out.println("ControlThread: Thread Started");
        
        try 
        {       
            while (bRunning) 
            {
                entryTime = System.currentTimeMillis();
                
                mView.periodicUpdate();
                       
                elapsedTime = System.currentTimeMillis() - entryTime;
                if(elapsedTime < 900)
                {
                    Thread.sleep(900 - elapsedTime);
                }
                else if(elapsedTime > 1500)
                {
                    System.out.println("MMODPoller -- Warning: latency time approaching 1 second");
                }
            }

        } 
        catch (InterruptedException e) 
        {
            System.out.print("MMODPoller -- interrupted: stopping thread");
            bRunning = false;
        }
    }
    
}