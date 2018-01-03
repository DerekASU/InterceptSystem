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
// theres an index out of bounds exception due to thread concurrency.I don't think this effects operation, we recover, need to test in deployed version, iff issues occur, then make it so that everytihing is done in the update function
//-> add ID labels 
//-> add position labels 



public class TheatreMapPanel extends javax.swing.JPanel 
{
    final MarkerLayer layer = new MarkerLayer();
    final MarkerLayer threatLayer = new MarkerLayer();
    final MarkerLayer removedLayer = new MarkerLayer();
    final RenderableLayer textLayer = new RenderableLayer();
    ArrayList<Marker> markers;
    ArrayList<Marker> threatMarkers;
    ArrayList<Marker> removedThreats;
    ArrayList<Marker> LocalremovedThreats;
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
        removedThreats = new ArrayList();
        LocalremovedThreats  = new ArrayList();
        //canvasModel = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
        //this.worldWindowGLCanvas1.setModel(canvasModel);
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
        
        if(canvasModel != null)
        {
        this.worldWindowGLCanvas1.getModel().getLayers().removeAll();
        canvasModel = null;
        }
        
        
    }
    
    public void handleInitialUpdate()
    {
        // this is called only once, on initial startup, or when connection has been re-established after being lost
        // this may or may not be necessary for this panel, when this is called, it means all entries in the database
        // are valid and populated
        
        canvasModel = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
        this.worldWindowGLCanvas1.setModel(canvasModel);
       
        
        this.worldWindowGLCanvas1.getView().setEyePosition(Position.fromDegrees(SRC_LAT, SRC_LON,8000));
        
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
        
        ArrayList<String> interceptors = mModel.getInterceptorList();
        ArrayList<String> threats = mModel.getThreatList();
        Interceptor tmpI;
        Missile tmpT;
        
        markers.clear();
        threatMarkers.clear();
        

        for (int i = 0; i < interceptors.size(); i++) 
        {
            tmpI = mModel.getInterceptor(interceptors.get(i));
            double examplePosition[] = convertPosition(tmpI.getPosX(), tmpI.getPosY());
            
            PointPlacemark tmp = new PointPlacemark(Position.fromDegrees(examplePosition[0], examplePosition[1],1d));
            PointPlacemarkAttributes tmp2 = new PointPlacemarkAttributes();
            tmp2.setDrawImage(false);
            tmp.setAttributes(tmp2);
            
            tmp.setLabelText("TEST!!!");
            textLayer.addRenderable(tmp);
            
            switch(tmpI.getState()){
                case PRE_FLIGHT:
                    markers.add(new BasicMarker(Position.fromDegrees(examplePosition[0], examplePosition[1],1d),  new BasicMarkerAttributes(Material.YELLOW, BasicMarkerShape.SPHERE, 1d)));
                    break; 
                case IN_FLIGHT:
                    markers.add(new BasicMarker(Position.fromDegrees(examplePosition[0], examplePosition[1],1d),  new BasicMarkerAttributes(Material.MAGENTA, BasicMarkerShape.SPHERE, 1d)));
                    break;
                case DETONATED:
                    markers.add(new BasicMarker(Position.fromDegrees(examplePosition[0], examplePosition[1],1d),  new BasicMarkerAttributes(Material.LIGHT_GRAY, BasicMarkerShape.SPHERE, 1d)));
                    break;       
            }            
        }
        

        ArrayList<String> assignedThreats = new ArrayList();
        for (int i =0; i < threats.size(); i++)
        {
            tmpT = mModel.getThreat(threats.get(i));
            
            assignedThreats = mModel.getAssignedThreats();
                
            double examplePosition[] = convertPosition(tmpT.getPosX(), tmpT.getPosY());
            
                if(assignedThreats.contains(tmpT.getIdentifier()))
                {
                    threatMarkers.add(new BasicMarker(Position.fromDegrees(examplePosition[0], examplePosition[1],tmpT.getPosZ()), new BasicMarkerAttributes(Material.GREEN,BasicMarkerShape.CONE,1d)));
                }
                else
                {
                    threatMarkers.add(new BasicMarker(Position.fromDegrees(examplePosition[0], examplePosition[1],tmpT.getPosZ()), new BasicMarkerAttributes(Material.RED,BasicMarkerShape.CONE,1d)));
                }

        }
        
            layer.setKeepSeparated(false);
            threatLayer.setKeepSeparated(false);

            if(threatMarkers.size() == 0)
            {
                threatLayer.clearList();
            }
            else
            {
                threatLayer.setMarkers(threatMarkers);
                threatLayer.setOverrideMarkerElevation(true);
            }
            
            layer.setMarkers(markers);
            layer.setOverrideMarkerElevation(true);
            
            
            this.worldWindowGLCanvas1.getModel().getLayers().add(layer);
            this.worldWindowGLCanvas1.getModel().getLayers().add(threatLayer);
            this.worldWindowGLCanvas1.getModel().getLayers().add(textLayer);
            
            
            removedThreats = (ArrayList<Marker>) (LocalremovedThreats.clone());
            
            removedLayer.setMarkers(removedThreats);
        removedLayer.setKeepSeparated(false);
        removedLayer.setOverrideMarkerElevation(true);
        this.worldWindowGLCanvas1.getModel().getLayers().add(removedLayer);
    }
    
    public void markThreatDestroyed(String threatID)
    {
        Missile tmpT = mModel.getThreat(threatID);
        double examplePosition[] = convertPosition(tmpT.getPosX(), tmpT.getPosY());
        
        LocalremovedThreats.add(new BasicMarker(Position.fromDegrees(examplePosition[0], examplePosition[1],1d), new BasicMarkerAttributes(Material.GRAY,BasicMarkerShape.CONE,1d)));

    }
    
    public void handleSelChange(String pID)
    {
        Interceptor tmpI = mModel.getInterceptor(pID);
        double examplePosition[] = convertPosition(tmpI.getPosX(), tmpI.getPosY());
        
        this.worldWindowGLCanvas1.getView().setEyePosition(Position.fromDegrees(examplePosition[0], examplePosition[1],8000));
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
