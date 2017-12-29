package mdscssRoot;

/*******************************************************************************
 * The MMODPoller thread awakes 
 * every second to command the GUI components to query the model and update
 * their states.
 ******************************************************************************/
public class MMODPoller implements Runnable
{
    private MMODFrame mView;
    private boolean bRunning;

    /***************************************************************************
     * Constructor
     * 
     * @param pMMOD Reference to the MMOD JFrame
     **************************************************************************/
    public MMODPoller(MMODFrame pMMOD)
    {
        bRunning = false;
        mView = pMMOD;
    }
    
    /***************************************************************************
     * The Run function tells the MMODFrame, and all of its components, to access the MissileDBManager and update their visible states.
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