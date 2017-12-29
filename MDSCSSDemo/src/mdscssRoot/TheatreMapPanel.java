/*******************************************************************************
 * File: TheatreMapPanel.java
 * Description:
 *
 ******************************************************************************/
package mdscssRoot;


import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.exception.WWAbsentRequirementException;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwindx.examples.util.*;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwind.render.SurfaceSector;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.markers.*;

import java.awt.*;
import java.util.Arrays;
import java.util.ArrayList;

import mdscssModel.Interceptor;
import mdscssModel.Missile;
import mdscssRoot.InterceptorOverviewPanel;
import mdscssModel.MissileDBManager;

import java.awt.*;
import java.lang.Object;
import java.util.ArrayList;
import java.util.Arrays;
import mdscssModel.Interceptor;
import mdscssModel.MissileDBManager;

//TODO: 
//handle threat detonation 
//-> add ID labels 
//-> add position labels 
//-> handle pan on zoom 
//-> handle scaling of icons when zooming in
// figure out why we get a null pointer exception for one of the array lists in here (i.e close sim before this app) //line 107 in this file
// random bug: sometimes the assigned interceptor disappears from the map, only seen this happen for c class interceptors .... why? ... i think it has something to do with proximity to one another and the zoom level



public class TheatreMapPanel extends javax.swing.JPanel 
{
    final MarkerLayer layer = new MarkerLayer();
    final MarkerLayer threatLayer = new MarkerLayer();
    final RenderableLayer textLayer = new RenderableLayer();
    ArrayList<Marker> markers;
    ArrayList<Marker> threatMarkers;
    MissileDBManager mModel;
    MMODFrame mParent;
    Model canvasModel;
    
    // For San Diego 
    final static double SRC_LAT = 32.685047;
    final static double SRC_LON = -117.129356;
    
    // for Hawaii
    /*final static double SRC_LAT = 21.365268;
    final static double SRC_LON = -157.954177;*/

    /***************************************************************************
     * TheatreMapPanel
     * 
     * Constructor
     **************************************************************************/
    public TheatreMapPanel() 
    {
        initComponents();
        markers = new ArrayList();
        threatMarkers = new ArrayList();
        canvasModel = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
        this.worldWindowGLCanvas1.setModel(canvasModel);
        
    }
    
    public void initialize(MissileDBManager pModel, MMODFrame pParent)
    {
        mModel = pModel;
        mParent = pParent;
    }
    
    public void resetView()
    {
        // this function gets called when connection is lost with tyhe subsystems.  
        // this function should remove everything from the map, and get the map to be as if it was 
        // just starting for the first time
        this.worldWindowGLCanvas1.getModel().getLayers().removeAll();
        canvasModel = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
        this.worldWindowGLCanvas1.setModel(canvasModel);
        
    }
    
    public void handleInitialUpdate()
    {
        // this is called only once, on initial startup, or when connection has been re-established after being lost
        // this may or may not be necessary for this panel, when this is called, it means all entries in the database
        // are valid and populated
        

       
        
        
    }
    
    private double[] convertPosition(int posX, int posY)
    {
        double result[] = {0,0};
        
        // every 111,111 (y) meters is one degree latitude
        // every (x)111,111 *  cos(latitude) is one degree longitude
        
        result[0] = (SRC_LAT - (((double)posY)/111111));
        result[1] = SRC_LON - ((((double)posX)/111111) * Math.cos(result[0]));

        
        return result;
    }
    
    public void updatePanelContents()
    {
        // this is called every second, this is the periodic update, you should query the database here
        // and then update the map with new positions, states, assignments, etc
        
        
        
        // below is an example on how to traverse the model for every interceptor , this can easily be done for threats, or all missiles.  I reccomend you look at the 
        // MDSCSSController and the MissileDBManager, i updated all the functions with headers that explain their
        // functionality
        ArrayList<String> interceptors = mModel.getInterceptorList();
        ArrayList<String> threats = mModel.getThreatList();
        Interceptor tmpI;
        Missile tmpT;
        markers.clear();
        threatMarkers.clear();
        for (int i = 0; i < interceptors.size(); i++) {
            tmpI = mModel.getInterceptor(interceptors.get(i));
            double examplePosition[] = convertPosition(tmpI.getPosX(), tmpI.getPosY());
            PointPlacemark pm = new PointPlacemark(Position.fromDegrees(examplePosition[0], examplePosition[1]));
            switch(tmpI.getState()){
                case PRE_FLIGHT:
                    markers.add(new BasicMarker(Position.fromDegrees(examplePosition[0], examplePosition[1],1d),  new BasicMarkerAttributes(Material.YELLOW, BasicMarkerShape.SPHERE, 1d)));
                    break; 
                case IN_FLIGHT:
                    markers.add(new BasicMarker(Position.fromDegrees(examplePosition[0], examplePosition[1],1d),  new BasicMarkerAttributes(Material.MAGENTA, BasicMarkerShape.SPHERE, 1d)));
                    break;
                case DETONATED:
                    markers.add(new BasicMarker(Position.fromDegrees(examplePosition[0], examplePosition[1],1d),  new BasicMarkerAttributes(Material.GRAY, BasicMarkerShape.SPHERE, 1d)));
                    break;       
            }
            

        
            layer.setMarkers(markers);
        }
        ArrayList<String> assignedThreats = new ArrayList();
        for (int i =0; i < threats.size(); i++)
        {
            tmpT = mModel.getThreat(threats.get(i));
            
            assignedThreats = mModel.getAssignedThreats();
                
            double examplePosition[] = convertPosition(tmpT.getPosX(), tmpT.getPosY());
            
                if(assignedThreats.contains(tmpT.getIdentifier()))
                {
                    threatMarkers.add(new BasicMarker(Position.fromDegrees(examplePosition[0], examplePosition[1],1d), new BasicMarkerAttributes(Material.GREEN,BasicMarkerShape.CONE,1d)));
                }
                else
                {
                    threatMarkers.add(new BasicMarker(Position.fromDegrees(examplePosition[0], examplePosition[1],1d), new BasicMarkerAttributes(Material.RED,BasicMarkerShape.CONE,1d)));
                }
                
                threatLayer.setMarkers(threatMarkers);

            

            layer.setOverrideMarkerElevation(false);
            threatLayer.setOverrideMarkerElevation(false);
        }
            this.worldWindowGLCanvas1.getModel().getLayers().add(layer);
            this.worldWindowGLCanvas1.getModel().getLayers().add(threatLayer);
    }
    
    public void markThreatDestroyed(String threatID)
    {
        // called when a threat is destroyed and removed from the database.  after this is called, the threat
        // is removed from the database and any query to the database for this threat will result in a null
        // you should handle logic here that updates the map to show the threat has been destroyed
    }

    /***************************************************************************
     * initComponents
     * 
     * Creates and draws the container's swing components.  Autogenerated by
     * Netbeans IDE GUI Editor
     **************************************************************************/
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        worldWindowGLCanvas1 = new gov.nasa.worldwind.awt.WorldWindowGLCanvas();

        setBackground(new java.awt.Color(27, 161, 226));
        setMaximumSize(new java.awt.Dimension(876, 573));
        setMinimumSize(new java.awt.Dimension(876, 573));
        setPreferredSize(new java.awt.Dimension(876, 573));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(worldWindowGLCanvas1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(worldWindowGLCanvas1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private gov.nasa.worldwind.awt.WorldWindowGLCanvas worldWindowGLCanvas1;
    // End of variables declaration//GEN-END:variables
}
