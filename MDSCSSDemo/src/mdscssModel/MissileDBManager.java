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
       
       if(tmp == null)
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
        Missile tmp;
        Set missileSet = activeMissiles.entrySet();
        Iterator missileIt = missileSet.iterator();
        ArrayList<String> res = new ArrayList();
        
        while(missileIt.hasNext())
        {
            tmp = ((Missile)(missileIt.next()));
            res.add(tmp.getIdentifier());
        }

        return res;
    }
    
    public ArrayList<String> getThreatList()
    {
        Missile tmp;
        Set missileSet = activeMissiles.entrySet();
        Iterator missileIt = missileSet.iterator();
        ArrayList<String> res = new ArrayList();
        
        while(missileIt.hasNext())
        {
            tmp = ((Missile)(missileIt.next()));
            
            if(tmp.getType() == Missile.MissileType.THREAT)
            {
                res.add(tmp.getIdentifier());
            }
        }
        
        return res;
    }
    
    public ArrayList<String> getInterceptorList()
    {
        Missile tmp;
        Set missileSet = activeMissiles.entrySet();
        Iterator missileIt = missileSet.iterator();
        ArrayList<String> res = new ArrayList();
        
        while(missileIt.hasNext())
        {
            tmp = ((Missile)(missileIt.next()));
            
            if(tmp.getType() == Missile.MissileType.INTERCEPTOR)
            {
                res.add(tmp.getIdentifier());
            }
        }
        
        return res;
    }
    
    public void updateDatabase(ArrayList<String> idList)
    {
        Missile tmp = null;
        String missileID = "";
        Set missileSet = activeMissiles.keySet();
        Iterator missileIt = missileSet.iterator();
        /*
        prereqs:  determine how the data comes back from the TSS
        if a threat has been elminated, does it still show up in the list?
        ----if it does, then controller needs to filter it out of the list before giving it to the db
        */
        
        while(missileIt.hasNext())
        {
            missileID = (String)missileIt.next();
            if(!idList.contains(missileID))
            {
                activeMissiles.remove(missileID);
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
