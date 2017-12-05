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
    
    
}
