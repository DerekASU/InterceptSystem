/*******************************************************************************
 * File: MissileDBManager.java
 * Description:
 *
 ******************************************************************************/
package mdscssModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class MissileDBManager 
{
    private HashMap<String, Missile> activeMissiles;
    
    /***************************************************************************
     * MissileDBManager
     * 
     * Constructor
     **************************************************************************/
    public MissileDBManager() 
    {
        activeMissiles = new HashMap();
    }
    
    public Missile getMissile(String pId)
    {
        return activeMissiles.get(pId);
    }
    
    public Interceptor getInterceptor(String pId)
    {
        Missile tmp = activeMissiles.get(pId);
        Interceptor tmp2;
        
        if(tmp != null && tmp.getType() == Missile.MissileType.INTERCEPTOR)
        {
            return (Interceptor)tmp;
        }
        else
        {
            return null;
        }
    }
    
    public Missile getThreat(String pId)
    {
        Missile tmp = activeMissiles.get(pId);
        
        if(tmp != null && tmp.getType() == Missile.MissileType.THREAT)
        {
            return tmp;
        }
        else
        {
            return null;
        }
    }
    
    public boolean contains(String pId)
    {       
       Missile tmp = activeMissiles.get(pId);
       
       
       if(tmp != null)
       {
           return true;
       }
       else
       {
           return false;
       }
    }
    
    public ArrayList<String> getMissileList()
    {
        Missile tmp = null;
        String missileID = "";
        ArrayList<String> res = new ArrayList();


        for(Iterator<HashMap.Entry<String, Missile>> it = activeMissiles.entrySet().iterator(); it.hasNext();)
        {
            missileID = (String)it.next().getKey();
            tmp = activeMissiles.get(missileID);

            res.add(tmp.getIdentifier());
        }

        return res;
    }
    
    public ArrayList<String> getThreatList()
    {
        Missile tmp = null;
        String missileID = "";
        ArrayList<String> res = new ArrayList();


        for(Iterator<HashMap.Entry<String, Missile>> it = activeMissiles.entrySet().iterator(); it.hasNext();)
        {
            missileID = (String)it.next().getKey();
            tmp = activeMissiles.get(missileID);
            
            if(tmp.getType() == Missile.MissileType.THREAT)
            {
                res.add(tmp.getIdentifier());
            }
        }
        
        return res;
    }
    
    public ArrayList<String> getAssignedThreats()
    {
        Interceptor tmp = null;
        String missileID = "";
        ArrayList<String> res = new ArrayList();
        
        for(Iterator<HashMap.Entry<String, Missile>> it = activeMissiles.entrySet().iterator(); it.hasNext();)
        {
            missileID = (String)it.next().getKey();

            if(activeMissiles.get(missileID).getType() == Missile.MissileType.INTERCEPTOR)
            {
                tmp = (Interceptor)activeMissiles.get(missileID);
                
                if(!tmp.getAssignedThreat().equals("[UNASSIGNED]"))
                    res.add(tmp.getAssignedThreat());
            }
        }
        
        return res;
    }
    
    public ArrayList<String> getInterceptorList()
    {
        Missile tmp = null;
        String missileID = "";
        ArrayList<String> res = new ArrayList();


        for(Iterator<HashMap.Entry<String, Missile>> it = activeMissiles.entrySet().iterator(); it.hasNext();)
        {
            missileID = (String)it.next().getKey();
            tmp = activeMissiles.get(missileID);
            
            if(tmp.getType() == Missile.MissileType.INTERCEPTOR)
            {
                res.add(tmp.getIdentifier());
            }
        }
        
        return res;
    }
    
    public void removeThreat(String pId)
    {
        Missile tmp = activeMissiles.get(pId);
        
        if(tmp != null && tmp.getType() == Missile.MissileType.THREAT)
        {
            activeMissiles.remove(pId);
        }
    }
    
    public void updateDatabase(ArrayList<String> idList)
    {
        Missile tmp = null;
        String missileID = "";
        
        for(Iterator<HashMap.Entry<String, Missile>> it = activeMissiles.entrySet().iterator(); it.hasNext();)
        {
            missileID = (String)it.next().getKey();
            if(!idList.contains(missileID))
            {
                it.remove();
            }
        }
        
        /* traverse the database and add new entries */
        for(int i = 0; i < idList.size(); i++)
        {
            missileID = idList.get(i);
            tmp = activeMissiles.get(missileID);
            
            /* if the missile is not in the database */
            if(tmp == null)
            {
                char missileClass = idList.get(i).charAt(0);
                
                switch(missileClass)
                {
                    case 'X': 
                        activeMissiles.put(missileID, new ThreatClassX(missileID));
                        break;
                    case 'Y': 
                        activeMissiles.put(missileID, new ThreatClassY(missileID));
                        break;
                    case 'Z': 
                        activeMissiles.put(missileID, new ThreatClassZ(missileID));
                        break;
                    case 'A': 
                        activeMissiles.put(missileID, new InterceptorClassA(missileID));
                        break;
                    case 'B': 
                        activeMissiles.put(missileID, new InterceptorClassB(missileID));
                        break;
                    case 'C': 
                        activeMissiles.put(missileID, new InterceptorClassC(missileID));
                        break;
                    default:
                        break;
                }
            }
        }        
    }
}
