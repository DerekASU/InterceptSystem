package mdscssModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/*******************************************************************************
 * The MissileDBManager object manages a hashmap of missile IDs and a model 
 * representation of their states and attributes.  This object is the primary means
 * the controller and GUI retrieves and modifies information pertaining to missiles.
 ******************************************************************************/
public class MissileDBManager 
{
    private HashMap<String, Missile> activeMissiles;
    
    /***************************************************************************
     * Constructor
     **************************************************************************/
    public MissileDBManager() 
    {
        activeMissiles = new HashMap();
    }
    
    /***************************************************************************
     * The getMissile function retrieves a missile object given a specific identifier.
     * 
     * @param pId The id of the interceptor or threat to retrieve
     * 
     * @return A Missile object with the given id
     **************************************************************************/
    public Missile getMissile(String pId)
    {
        return activeMissiles.get(pId);
    }
    
    /***************************************************************************
     * The getInterceptor function retrieves an interceptor object given a specific identifier.
     * 
     * @param pId The id of the interceptor retrieve
     * 
     * @return An interceptor object with the given id
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
     * The getAssignedInterceptor function retrieves an interceptor object that has an assigned threat with the id
     * matching the given parameter.
     * 
     * @param pId The id of the assigned threat
     * 
     * @return The id of the interceptor that is assigned the given threat, or null
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
     * The getThreatfunction retrieves an Missile object given a specific identifier.
     * 
     * @param pId The id of the threat to retrieve
     * 
     * @return A Missile object with the given id
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
     * The contains function returns a boolean value that indicates whether or not
     * the database contains a missile with the specified ID.
     * 
     * @param pId The id of the missile
     * 
     * @return True if the database contains the missile, false otherwise
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
     * The getMissileList function retrieves a list of IDs for every missile in the database.
     * 
     * @return An ArrayList of missile IDs
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
     * The getThreatList function retrieves a list of IDs for every threat in the database.
     * 
     * @return An ArrayList of threat IDs
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
     * The getAssignedThreats function retrieves a list of IDs for every threat in the database that has been
     * assigned an interceptor.
     * 
     * @return An ArrayList of threat IDs
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
     * The getUnassignedThreats function retrieves a list of IDs for every threat in the database that has not been
     * assigned an interceptor.
     * 
     * @return An ArrayList of threat IDs
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
     * The getUnassignedInterceptors function retrieves a list of IDs for every interceptor in the database that has not been
     * assigned a threat.
     * 
     * @return An ArrayList of interceptor IDs
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
     * The getInterceptorList function retrieves a list of IDs for every interceptor in the database.
     * 
     * @return An interceptor of threat IDs
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
     * The removeThreat function removes a threat from the database, given a specific identifier.
     * 
     * @param pId The id of the threat to remove
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
     * The updateDatabase function populates the database based upon the list of ID's given.  If the given ID
     * list does not contain an item present in the database, it will be removed 
     * from the database.  If the list contains IDs for a missile that is not
     * present in the database, it will be created and added.
     * 
     * @param idList The list of ID's the database should mirror
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
