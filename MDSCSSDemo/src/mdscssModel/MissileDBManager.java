/*******************************************************************************
 * File: MissileDBManager.java
 * Description: Model class that manages a hashmap of missile IDs and a model 
 * representation of their states and attributes.  This object is the primary means
 * the controller and GUI retrieves and modifies information pertaining to missiles
 ******************************************************************************/
package mdscssModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
    
    /***************************************************************************
     * getMissile
     * 
     * retrieves a missile object given a specific identifier
     * 
     * @param pId - the id of the interceptor or threat to retrieve
     * 
     * @return a Missile object with the given id
     **************************************************************************/
    public Missile getMissile(String pId)
    {
        return activeMissiles.get(pId);
    }
    
    /***************************************************************************
     * getInterceptor
     * 
     * retrieves an interceptor object given a specific identifier
     * 
     * @param pId - the id of the interceptor retrieve
     * 
     * @return an interceptor object with the given id
     **************************************************************************/
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
    
    /***************************************************************************
     * getAssignedInterceptor
     * 
     * retrieves an interceptor object that has an assigned threat with the id
     * matching the given parameter
     * 
     * @param pId - the id of the assigned threat
     * 
     * @return the id of the interceptor that is assigned the given threat, or null
     *        if no such interceptor exists
     **************************************************************************/
    public String getAssignedInterceptor(String pId)
    {
       Interceptor tmp = null;
       String missileID = "";
        
        for(Iterator<HashMap.Entry<String, Missile>> it = activeMissiles.entrySet().iterator(); it.hasNext();)
        {
            missileID = (String)it.next().getKey();

            if(activeMissiles.get(missileID).getType() == Missile.MissileType.INTERCEPTOR)
            {
                tmp = (Interceptor)activeMissiles.get(missileID);
                
                if(tmp.getAssignedThreat().equals(pId))
                    return tmp.getIdentifier();
            }
        }
        
        return null;
    }
    
    /***************************************************************************
     * getThreat
     * 
     * retrieves an Missile object given a specific identifier
     * 
     * @param pId - the id of the threat to retrieve
     * 
     * @return an Missile object with the given id
     **************************************************************************/
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
    
    /***************************************************************************
     * getThreat
     * 
     * retrieves an Missile object given a specific identifier
     * 
     * @param pId - the id of the threat to retrieve
     * 
     * @return an Missile object with the given id
     **************************************************************************/
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
    
    /***************************************************************************
     * getMissileList
     * 
     * retrieves a list of IDs for every missile in the database
     * 
     * @return an ArrayList of missile IDs
     **************************************************************************/
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
    
    /***************************************************************************
     * getThreatList
     * 
     * retrieves a list of IDs for every threat in the database
     * 
     * @return an ArrayList of threat IDs
     **************************************************************************/
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
    
    /***************************************************************************
     * getAssignedThreats
     * 
     * retrieves a list of IDs for every threat in the database that has been
     * assigned an interceptor
     * 
     * @return an ArrayList of threat IDs
     **************************************************************************/
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
    
    /***************************************************************************
     * getUnassignedThreats
     * 
     * retrieves a list of IDs for every threat in the database that has not been
     * assigned an interceptor
     * 
     * @return an ArrayList of threat IDs
     **************************************************************************/
    public ArrayList<String> getUnassignedThreats()
    {
        String missileID = "";
        ArrayList<String> res = new ArrayList();
        ArrayList<String> assignThreats = getAssignedThreats();
        
        for(Iterator<HashMap.Entry<String, Missile>> it = activeMissiles.entrySet().iterator(); it.hasNext();)
        {
            missileID = (String)it.next().getKey();

            if(activeMissiles.get(missileID).getType() == Missile.MissileType.THREAT)
            {
                if(!assignThreats.contains(missileID))
                    res.add(missileID);
            }
        }
        
        return res;
    }
    
    /***************************************************************************
     * getUnassignedInterceptors
     * 
     * retrieves a list of IDs for every interceptor in the database that has not been
     * assigned a threat
     * 
     * @return an ArrayList of interceptor IDs
     **************************************************************************/
    public ArrayList<String> getUnassignedInterceptors()
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
                
                if(tmp.getAssignedThreat().equals("[UNASSIGNED]"))
                    res.add(missileID);
            }
        }
        
        return res;
    }
    
    /***************************************************************************
     * getInterceptorList
     * 
     * retrieves a list of IDs for every interceptor in the database
     * 
     * @return an interceptor of threat IDs
     **************************************************************************/
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
    
    /***************************************************************************
     * removeThreat
     * 
     * removes a threat from the database, given a specific identifier
     * 
     * @param pId - the id of the threat to remove
     **************************************************************************/
    public void removeThreat(String pId)
    {
        Missile tmp = activeMissiles.get(pId);
        
        if(tmp != null && tmp.getType() == Missile.MissileType.THREAT)
        {
            activeMissiles.remove(pId);
        }
    }
    
    /***************************************************************************
     * updateDatabase
     * 
     * populates the database based upon the list of ID's given.  if the given ID
     * list does not contain an item present in the database, it will be removed 
     * from the database.  If the list contains IDs for a missile that is not
     * present in the database, it will be created and added.
     * 
     * @param idList - the list of ID's the database should mirror
     **************************************************************************/
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
