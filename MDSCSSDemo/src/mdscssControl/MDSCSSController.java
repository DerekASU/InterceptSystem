/*******************************************************************************
 * File: MDSCSSController.java
 * Description:
 *
 ******************************************************************************/
package mdscssControl;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import mdscssModel.*;
import mdscssRoot.MMODFrame;


/* note install wireshark and https://nmap.org/npcap/ to do loopback testing of tcp messages */

public class MDSCSSController 
{
    private static final int SMSS_SOCKET = 27015;
    private static final int MCSS_SOCKET = 27016;
    private static final int TSS_SOCKET = 27017;
    
    private MissileDBManager mModel;
    private MMODFrame mView;
    private Thread controller;
    
    private Socket tssTCP, mcssTCP, smssTCP;
    private boolean bInitialized;
    private int watchdogTime;
    
    
    
    
    /***************************************************************************
     * MDSCSSController
     * 
     * Constructor
     **************************************************************************/
    public MDSCSSController()
    {
        bInitialized = false;
        mModel = null;
        mView = null;
        
        watchdogTime = 0;
    }
    
    /***************************************************************************
     * initialize
     * 
     * initializes the controller on startup.  References to the view and 
     * model are established; initial socket connection to subsystems performed;
     * control thread is started
     * 
     * @param pModel - Reference to the Missile DB
     * @param pView - Reference to the GUI
     **************************************************************************/
    public void initialize(MissileDBManager pModel, MMODFrame pView) 
    {
        mModel = pModel;
        mView = pView;
        
        controller = new Thread(new ControlThread(this));
        controller.start();
        
        bInitialized = true;
    }
    
    public void finalize()
    {
        System.out.println("finalizing");
        
        if(bInitialized)
        {
            try{
                tssTCP.close();
                mcssTCP.close();
                smssTCP.close();
            } catch(Exception e){}
            
            controller.interrupt();
            
            bInitialized = false;
        }
        
    }
    
    public boolean establishConnection()
    {
        try
        {
           smssTCP = new Socket("localhost", SMSS_SOCKET);
           tssTCP = new Socket("localhost", TSS_SOCKET);
           mcssTCP = new Socket("localhost", MCSS_SOCKET);

           System.out.println("in init");
           mView.connectionEstablished();
           return true;
        } 
        catch(Exception e)
        {
            System.out.println("failed to init");
            return false;
        }
    }
    
    public void sendCommand()
    {
        byte[] payload = new byte[5];
        byte[] test = new byte[256];
        int bufferSize = 0;

        System.out.println("a");
        
        try 
        {
            DataOutputStream outBuffer = new DataOutputStream(tssTCP.getOutputStream());
            DataInputStream inFromServer = new DataInputStream(tssTCP.getInputStream());
            
            payload[0] = 6;
            
            outBuffer.write(payload, 0, 5);
            System.out.println("b");
            
            
            inFromServer.read(test, 0, 6);
            
            
            bufferSize = ((test[4] & 0xff) << 8) | (test[5] & 0xff);
            
            System.out.println("c: " + bufferSize);
            
            inFromServer.read(test, 0, bufferSize);
            System.out.println(new String(test, "UTF-8"));
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(MDSCSSController.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
    /***************************************************************************
     *  TSS Command Interface
     **************************************************************************/
    
    public int[] cmdTssTrackThreat(String missileID)
    {
        return null;
    }
    
    public int[] cmdTssTrackInterceptor(String missileID)
    {
        return null;
    }
    
    public String[] cmdTssGetThreatList()
    {
        return null;
    }
    
    public String cmdTssGetVersion()
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;
        
        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;
        
        try 
        {
            buffOut = new DataOutputStream(tssTCP.getOutputStream());
            buffIn = new DataInputStream(tssTCP.getInputStream());

            header[0] = 6;
            buffOut.write(header, 0, 5);

            buffIn.read(returnBuffer, 0, 6);
            bufferSize = ((returnBuffer[4] & 0xff) << 8) | (returnBuffer[5] & 0xff);

            buffIn.read(returnBuffer, 0, bufferSize);
            result = new String(returnBuffer, "UTF-8");

            result = result.substring(26);
        } 
        catch (IOException ex) 
        {
            System.out.println("MDSCSSController - cmdTssGetVersion: buffer failure\n" + ex.getMessage() +"\n");
        } 
        
        return result;
    }
    
    /***************************************************************************
     *  MCSS Command Interface
     **************************************************************************/
    
    public boolean cmdMcssLaunch(String missileID)
    {
        return false;
    }
    
    //TODO:!!!!!! Talk with john, look at resources .... always send a 1?..
    public boolean cmdMcssThrust()
    {
        return false;
    }
    
    public boolean cmdMcssDetonate(String missileID)
    {
        return false;
    }
    
    public boolean cmdMcssDestruct(String missileID)
    {
        return false;
    }
    
    public String[] cmdMcssGetInterceptorList()
    {
        return null;
    }
    
    public Interceptor.interceptorState cmdMcssgetState()
    {
        return Interceptor.interceptorState.DESTRUCTED;
    }
    
    public int[] cmdMcssgetLaunchSite(String missileID)
    {
        return null;
    }
    
    public String cmdMcssGetVersion()
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;
        
        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;
        
        try 
        {
            buffOut = new DataOutputStream(mcssTCP.getOutputStream());
            buffIn = new DataInputStream(mcssTCP.getInputStream());

            header[0] = 8;
            buffOut.write(header, 0, 5);

            buffIn.read(returnBuffer, 0, 6);
            bufferSize = ((returnBuffer[4] & 0xff) << 8) | (returnBuffer[5] & 0xff);

            buffIn.read(returnBuffer, 0, bufferSize);
            result = new String(returnBuffer, "UTF-8");

            result = result.substring(26);
        } 
        catch (IOException ex) 
        {
            System.out.println("MDSCSSController - cmdMcssGetVersion: buffer failure\n" + ex.getMessage() +"\n");
        } 
        
        return result;
    }
    
    public String cmdMcssGetCtrlVersion(String missileID)
    {
        return null;
    }
    
    /***************************************************************************
     *  SMSS Command Interface
     **************************************************************************/
    
    public boolean cmdSmssActivateSafety(String missileID)
    {
        return false;
    }
    
    public boolean cmdSmssDeactivateSafety(String missileID)
    {
        return false;
    }
    
    public boolean cmdSmssPingWatchdog(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[9];

        try 
        {
            buffOut = new DataOutputStream(smssTCP.getOutputStream());
            buffIn = new DataInputStream(smssTCP.getInputStream());

            watchdogTime++;
            
            // requires an MID? wtf?
            header[0] = 3;
            header[1] = (byte)(missileID.charAt(0));
            header[2] = (byte)(missileID.charAt(1));
            header[3] = watchdogTime;

            buffOut.write(header, 0, 9);

           
            result = true;
        } 
        catch (IOException ex) 
        {
            
        } 
        
        return true;
    }
    
    public boolean cmdSmssDetEnable(String missileID)
    {
        return false;
    }
    
    public boolean cmdSmssDestruct(String missileID)
    {
        return false;
    }
    
    public String cmdSmssGetVersion(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;
        
        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;
        
        try 
        {
            buffOut = new DataOutputStream(smssTCP.getOutputStream());
            buffIn = new DataInputStream(smssTCP.getInputStream());

            header[0] = 6;
            header[1] = (byte)(missileID.charAt(0));
            header[2] = (byte)(missileID.charAt(1));

            buffOut.write(header, 0, 5);

            buffIn.read(returnBuffer, 0, 6);
            bufferSize = ((returnBuffer[4] & 0xff) << 8) | (returnBuffer[5] & 0xff);

            buffIn.read(returnBuffer, 0, bufferSize);
            result = new String(returnBuffer, "UTF-8");
            result = result.substring(14);
        } 
        catch (IOException ex) 
        {
            System.out.println("MDSCSSController - cmdSmssGetVersion: buffer failure\n" + ex.getMessage() +"\n");
        } 
        
        return result;
    }
}
