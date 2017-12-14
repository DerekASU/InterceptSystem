
package mdscssRoot;

/**
 *
 * @author Will - Work
 */
public class MMODPoller implements Runnable
{
    private MMODFrame mView;
    private boolean bRunning;

    public MMODPoller(MMODFrame pMMOD)
    {
        bRunning = false;
        mView = pMMOD;
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
                
        System.out.println("ControlThread: Thread Started");
        
        try 
        {       
            while (bRunning) 
            {
                entryTime = System.currentTimeMillis();
                
                mView.periodicUpdate();
                       
                elapsedTime = System.currentTimeMillis() - entryTime;
                if(elapsedTime < 1000)
                {
                    Thread.sleep(500 - elapsedTime);
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