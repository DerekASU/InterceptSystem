/*******************************************************************************
 * File: MDSCSSController.java
 * Description:
 *
 ******************************************************************************/
package mdscssControl;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import mdscssModel.*;
import mdscssRoot.MMODFrame;

//TODO::: handle a different exception for timeouts? 
/* note install wireshark and https://nmap.org/npcap/ to do loopback testing of tcp messages */

public class MDSCSSController 
{
    private static final int SMSS_SOCKET = 27015;
    private static final int MCSS_SOCKET = 27016;
    private static final int TSS_SOCKET = 27017;
    private static final int SOCKET_TIMEOUT_MS = 2000;
    
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
        if(bInitialized)
        {
            try
            {
                tssTCP.close();
                mcssTCP.close();
                smssTCP.close();
            } 
            catch(Exception ex)
            {
                System.out.println("MDSCSSController - finalize: socket failure\n" + ex.getMessage() +"\n");
            }
            
            controller.interrupt();
            
            bInitialized = false;
        }
    }
    
    public boolean establishConnection()
    {
        try
        {
            // Connect the sockets and update the GUI's status Indicators and versions
            if(tssTCP == null)
            {
                tssTCP = new Socket("localhost", TSS_SOCKET);
                tssTCP.setSoTimeout(SOCKET_TIMEOUT_MS);
            }
            if(mcssTCP == null)
            {
                mcssTCP = new Socket("localhost", MCSS_SOCKET);
                mcssTCP.setSoTimeout(SOCKET_TIMEOUT_MS);
            }
            if(smssTCP == null)
            {
                smssTCP = new Socket("localhost", SMSS_SOCKET);
                smssTCP.setSoTimeout(SOCKET_TIMEOUT_MS);
            }

           return true;
        } 
        catch(Exception ex)
        {
            System.out.println("MDSCSSController - establishConnection: socket failure\n" + ex.getMessage() +"\n");
            return false;
        }
    }   
    
    public boolean establishStatus()
    {
        String tmp = "";
        
            if(tssTCP != null)
            {
                tmp = cmdTssGetVersion();
                if(tmp!= null)
                    mView.tssConnected(cmdTssGetVersion());
                else
                    return false;
            }
            
            if(mcssTCP != null)
            {
                tmp = cmdMcssGetVersion();
                if(tmp!= null)
                    mView.mcssConnected(tmp);
                else
                    return false;
            }
            
            if(smssTCP != null && mcssTCP != null)
            {
                ArrayList<String> interceptors = cmdMcssGetInterceptorList();
                
                if(interceptors.size() > 0)
                {
                    tmp = cmdSmssGetVersion(interceptors.get(0));
                    if(tmp!= null)
                        mView.smssConnected(tmp);
                    else
                    return false;
                }
                else
                {
                    mView.smssConnected("N/A");
                }
            }

           return true;
    }
    
    public void initializeModel()
    {
        //todo reconcile db with subsystem results, delete deleted, add new
        //
    }
    
    public void initializeWatchdog()
    {
        int i;
        ArrayList<String> interceptors = cmdMcssGetInterceptorList();
        //todo:: replace with getInterceptorList
        for(i = 0; i < interceptors.size(); i++)
        {
            cmdSmssDeactivateSafety(interceptors.get(i));
        }
        
        for(i = 0; i < interceptors.size(); i++)
        {
            cmdSmssActivateSafety(interceptors.get(i));
        }
        
    }
    
    public void handleWatchdogTimer()
    {
        watchdogTime++; 
        
        //todo, replace with a get to the model
        ArrayList<String> interceptors = cmdMcssGetInterceptorList();
        
        for(int i = 0; i < interceptors.size(); i++)
        {
            cmdSmssPingWatchdog(interceptors.get(i), watchdogTime);
        }
        
    }
    
    public void handleSocketFailure()
    {
        finalize();
        tssTCP = null;
        mcssTCP = null;
        smssTCP = null;
        
        //purge the model
        
        //TODO:: notify view
        
        controller = new Thread(new ControlThread(this));
        controller.start();
        
        bInitialized = true;
    }
    
    /***************************************************************************
     *  TSS Command Interface
     **************************************************************************/
    
    public int[] cmdTssTrackThreat(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        int result[] = new int[3];
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[12];
        int bufferSize = 0;
        
        if(tssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(tssTCP.getOutputStream());
                buffIn = new DataInputStream(tssTCP.getInputStream());

                header[0] = 1;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));

                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if(returnHeader[3] == 0 && bufferSize == 12)
                {
                    buffIn.read(returnBuffer, 0, bufferSize);

                    result[0] = ((returnBuffer[0] & 0xff) << 24) | ((returnBuffer[1] & 0xff) << 16) | ((returnBuffer[2] & 0xff) << 8) | (returnBuffer[3] & 0xff);
                    result[1] = ((returnBuffer[4] & 0xff) << 24) | ((returnBuffer[5] & 0xff) << 16) | ((returnBuffer[6] & 0xff) << 8) | (returnBuffer[7] & 0xff);
                    result[2] = ((returnBuffer[8] & 0xff) << 24) | ((returnBuffer[9] & 0xff) << 16) | ((returnBuffer[10] & 0xff) << 8) | (returnBuffer[11] & 0xff);
                }
                else
                {
                    System.out.println("MDSCSSController - cmdTssTrackThreat: unexpected or failed response\n");
                    result = null;
                }
                
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdTssTrackThreat: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdTssTrackThreat: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public int[] cmdTssTrackInterceptor(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        int result[] = new int[3];
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[12];
        int bufferSize = 0;
        
        if(tssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(tssTCP.getOutputStream());
                buffIn = new DataInputStream(tssTCP.getInputStream());

                header[0] = 2;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));

                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if(returnHeader[3] == 0 && bufferSize == 12)
                {
                    buffIn.read(returnBuffer, 0, bufferSize);

                    result[0] = ((returnBuffer[0] & 0xff) << 24) | ((returnBuffer[1] & 0xff) << 16) | ((returnBuffer[2] & 0xff) << 8) | (returnBuffer[3] & 0xff);
                    result[1] = ((returnBuffer[4] & 0xff) << 24) | ((returnBuffer[5] & 0xff) << 16) | ((returnBuffer[6] & 0xff) << 8) | (returnBuffer[7] & 0xff);
                    result[2] = ((returnBuffer[8] & 0xff) << 24) | ((returnBuffer[9] & 0xff) << 16) | ((returnBuffer[10] & 0xff) << 8) | (returnBuffer[11] & 0xff);
                    System.out.println(result[0]);
                }
                else
                {
                    System.out.println("MDSCSSController - cmdTssTrackInterceptor: unexpected or failed response\n");
                    result = null;
                }
                
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdTssTrackInterceptor: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdTssTrackInterceptor: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public ArrayList<String> cmdTssGetThreatList()
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        ArrayList<String> result = null;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[2];
        int bufferSize = 0;
        
        if(tssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(tssTCP.getOutputStream());
                buffIn = new DataInputStream(tssTCP.getInputStream());

                header[0] = 5;
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if(returnHeader[3] == 0 && bufferSize > 0)
                {
                    result = new ArrayList();

                    for(int i = 0; i < bufferSize; i+=2)
                    {
                        buffIn.read(returnBuffer, 0, 2);
                        result.add(new String(returnBuffer, "UTF-8"));
                    }
                }
                else
                {
                    System.out.println("MDSCSSController - cmdTssGetThreatList: unexpected or failed response\n");
                }                
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdTssGetThreatList: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdTssGetThreatList: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            }  
        }

        
        return result;
    }
    
    public String cmdTssGetVersion()
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;
        
        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;
        
        if(tssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(tssTCP.getOutputStream());
                buffIn = new DataInputStream(tssTCP.getInputStream());

                header[0] = 6;
                buffOut.write(header, 0, 5);

                buffIn.read(returnBuffer, 0, 6);
                bufferSize = ((returnBuffer[4] & 0xff) << 8) | (returnBuffer[5] & 0xff);

                if(returnBuffer[3] == 0)
                {
                    buffIn.read(returnBuffer, 0, bufferSize);
                    result = new String(returnBuffer, "UTF-8");

                    result = result.substring(27, 30);
                }
                else
                {
                    System.out.println("MDSCSSController - cmdTssGetVersion: unexpected or failed response\n");
                }
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdTssGetVersion: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdTssGetVersion: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    /***************************************************************************
     *  MCSS Command Interface
     **************************************************************************/
    
    public boolean cmdMcssLaunch(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 1;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssLaunch: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssLaunch: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    
    public boolean cmdMcssThrust(String missileID, int pwrX, int pwrY, int pwrZ)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        int tmp;
        
        byte[] header = new byte[53];
        byte[] returnHeader = new byte[6];
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 2;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                
                // header will be 48 bytes long
                header[4] = (byte)48;    
                
                // set the thrust time values all to 1 second
                header[8] = header[12] = header[16] = header[20] = header[24] = header[28] = 0x1;
                
                if(pwrX > 0)
                {
                    tmp = Math.abs(pwrX);
                    header[29] = (byte)((tmp >> 24) & 0xff);
                    header[30] = (byte)((tmp >> 16) & 0xff);
                    header[31] = (byte)((tmp >> 8) & 0xff);
                    header[32] = (byte)(tmp & 0xff);
                }
                else
                {
                    tmp = Math.abs(pwrX);
                    header[33] = (byte)((tmp >> 24) & 0xff);
                    header[34] = (byte)((tmp >> 16) & 0xff);
                    header[35] = (byte)((tmp >> 8) & 0xff);
                    header[36] = (byte)(tmp & 0xff);
                }
                
                if(pwrY > 0)
                {
                    tmp = Math.abs(pwrY);
                    header[37] = (byte)((tmp >> 24) & 0xff);
                    header[38] = (byte)((tmp >> 16) & 0xff);
                    header[39] = (byte)((tmp >> 8) & 0xff);
                    header[40] = (byte)(tmp & 0xff);
                }
                else
                {
                    tmp = Math.abs(pwrY);
                    header[41] = (byte)((tmp >> 24) & 0xff);
                    header[42] = (byte)((tmp >> 16) & 0xff);
                    header[43] = (byte)((tmp >> 8) & 0xff);
                    header[44] = (byte)(tmp & 0xff);
                }
                
                if(pwrZ > 0)
                {
                    tmp = Math.abs(pwrZ);
                    header[45] = (byte)((tmp >> 24) & 0xff);
                    header[46] = (byte)((tmp >> 16) & 0xff);
                    header[47] = (byte)((tmp >> 8) & 0xff);
                    header[48] = (byte)(tmp & 0xff);
                }
                else
                {
                    tmp = Math.abs(pwrZ);
                    header[49] = (byte)((tmp >> 24) & 0xff);
                    header[50] = (byte)((tmp >> 16) & 0xff);
                    header[51] = (byte)((tmp >> 8) & 0xff);
                    header[52] = (byte)(tmp & 0xff);
                }
                
                buffOut.write(header, 0, 53);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssThrust: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssThrust: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public boolean cmdMcssDetonate(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 3;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssDetonate: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssDetonate: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            }  
        }
        
        return result;
    }
    
    public boolean cmdMcssDestruct(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 4;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssDestruct: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssDestruct: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public ArrayList<String> cmdMcssGetInterceptorList()
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        ArrayList<String> result = new ArrayList();
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[2];
        int bufferSize = 0;
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 5;
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if(returnHeader[3] == 0 && bufferSize > 0)
                {
                    for(int i = 0; i < bufferSize; i+=2)
                    {
                        buffIn.read(returnBuffer, 0, 2);
                        result.add(new String(returnBuffer, "UTF-8"));
                    }
                }
                else
                {
                    System.out.println("MDSCSSController - cmdMcssGetInterceptorList: unexpected or failed response\n");
                }
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssGetInterceptorList: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssGetInterceptorList: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public Interceptor.interceptorState cmdMcssgetState(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        Interceptor.interceptorState result = Interceptor.interceptorState.UNDEFINED;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[25];
        int bufferSize = 0;
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 6;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if(returnHeader[3] == 0 && bufferSize == 25)
                {
                    buffIn.read(returnBuffer, 0, 25);

                    if(returnBuffer[0] == 0)
                    {
                        result = Interceptor.interceptorState.DETONATED;
                    }
                    else if(returnBuffer[0] == 1)
                    {
                        result = Interceptor.interceptorState.PRE_FLIGHT;
                    }
                    else if (returnBuffer[0] == 2)
                    {
                        result = Interceptor.interceptorState.IN_FLIGHT;
                    }
                }
                else
                {
                    System.out.println("MDSCSSController - cmdMcssgetState: unexpected or failed response\n");
                }
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssgetState: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssgetState: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public int[] cmdMcssGetLaunchSite(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        int result[] = new int[3];
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        byte[] returnBuffer = new byte[12];
        int bufferSize = 0;
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 7;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));

                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                bufferSize = ((returnHeader[4] & 0xff) << 8) | (returnHeader[5] & 0xff);

                if(returnHeader[3] == 0 && bufferSize == 12)
                {
                    buffIn.read(returnBuffer, 0, bufferSize);

                    result[0] = ((returnBuffer[0] & 0xff) << 24) | ((returnBuffer[1] & 0xff) << 16) | ((returnBuffer[2] & 0xff) << 8) | (returnBuffer[3] & 0xff);
                    result[1] = ((returnBuffer[4] & 0xff) << 24) | ((returnBuffer[5] & 0xff) << 16) | ((returnBuffer[6] & 0xff) << 8) | (returnBuffer[7] & 0xff);
                    result[2] = ((returnBuffer[8] & 0xff) << 24) | ((returnBuffer[9] & 0xff) << 16) | ((returnBuffer[10] & 0xff) << 8) | (returnBuffer[11] & 0xff);
                    System.out.println(result[0]);
                }
                else
                {
                    System.out.println("MDSCSSController - cmdMcssGetLaunchSite: unexpected or failed response\n");
                    result = null;
                }   
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssGetLaunchSite: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssGetLaunchSite: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public String cmdMcssGetVersion()
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;
        
        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;
        
        if(mcssTCP != null)
        {
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

                result = result.substring(26, 29);    
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssGetVersion: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssGetVersion: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }

        return result;
    }
    
    public String cmdMcssGetCtrlVersion(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;
        
        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;
        
        if(mcssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(mcssTCP.getOutputStream());
                buffIn = new DataInputStream(mcssTCP.getInputStream());

                header[0] = 9;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnBuffer, 0, 6);
                bufferSize = ((returnBuffer[4] & 0xff) << 8) | (returnBuffer[5] & 0xff);

                buffIn.read(returnBuffer, 0, bufferSize);
                result = new String(returnBuffer, "UTF-8");
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdMcssGetCtrlVersion: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdMcssGetCtrlVersion: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    /***************************************************************************
     *  SMSS Command Interface
     **************************************************************************/
    
    public boolean cmdSmssActivateSafety(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(smssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 1;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);             
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdSmssActivateSafety: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdSmssActivateSafety: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public boolean cmdSmssDeactivateSafety(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(smssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 2;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdSmssDeactivateSafety: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdSmssDeactivateSafety: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            }  
            
        }
        
        return result;
    }
    
    public boolean cmdSmssPingWatchdog(String missileID, int pTimer)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[9];
        byte[] returnHeader = new byte[6];

        if(smssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                // requires an MID? wtf?
                header[0] = 3;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                header[4] = 4;
                header[5] = (byte)((pTimer >> 24) & 0xff);
                header[6] = (byte)((pTimer >> 16) & 0xff);
                header[7] = (byte)((pTimer >> 8) & 0xff);
                header[8] = (byte)(pTimer & 0xff);
                buffOut.write(header, 0, 9);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdSmssPingWatchdog: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdSmssPingWatchdog: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public boolean cmdSmssDetEnable(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(smssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 4;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdSmssDetEnable: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdSmssDetEnable: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
    
    public boolean cmdSmssDestruct(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        boolean result = false;
        
        byte[] header = new byte[5];
        byte[] returnHeader = new byte[6];
        
        if(smssTCP != null)
        {
            try 
            {
                buffOut = new DataOutputStream(smssTCP.getOutputStream());
                buffIn = new DataInputStream(smssTCP.getInputStream());

                header[0] = 5;
                header[1] = (byte)(missileID.charAt(0));
                header[2] = (byte)(missileID.charAt(1));
                buffOut.write(header, 0, 5);

                buffIn.read(returnHeader, 0, 6);
                result = (returnHeader[3] == 0);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdSmssDestruct: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdSmssDestruct: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            }  
        }
        
        return result;
    }
    
    public String cmdSmssGetVersion(String missileID)
    {
        DataOutputStream buffOut;
        DataInputStream buffIn;
        String result = null;
        
        byte[] header = new byte[5];
        byte[] returnBuffer = new byte[256];
        int bufferSize = 0;
        
        if(smssTCP != null)
        {
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
                result = result.substring(14,18);
            } 
            catch(SocketTimeoutException ex)
            {
                System.out.println("MDSCSSController - cmdSmssGetVersion: warning 2 second socket timeout\n" + ex.getMessage() +"\n");
            }
            catch (IOException ex) 
            {
                System.out.println("MDSCSSController - cmdSmssGetVersion: buffer failure\n" + ex.getMessage() +"\n");
                handleSocketFailure();
            } 
        }
        
        return result;
    }
}
