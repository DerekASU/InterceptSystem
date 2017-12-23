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
import java.util.ArrayList;
import java.util.Arrays;
import mdscssModel.Interceptor;
import mdscssModel.MissileDBManager;

public class TheatreMapPanel extends javax.swing.JPanel 
{
    final MarkerLayer layer = new MarkerLayer();
    ArrayList<Marker> markers;
    MissileDBManager mModel;
    MMODFrame mParent;
    Model canvasModel;
    
    /***************************************************************************
     * TheatreMapPanel
     * 
     * Constructor
     **************************************************************************/
    public TheatreMapPanel() 
    {
        initComponents();
        markers = new ArrayList();
        this.worldWindowGLCanvas1.setModel(new BasicModel());
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
    }
    
    public void handleInitialUpdate()
    {
        // this is called only once, on initial startup, or when connection has been re-established after being lost
        // this may or may not be necessary for this panel, when this is called, it means all entries in the database
        // are valid and populated
        ArrayList<String> interceptors = mModel.getInterceptorList();
        Interceptor tmpI;
        
        canvasModel = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
        this.worldWindowGLCanvas1.setModel(canvasModel);
        
        for(int i = 0; i < interceptors.size();  i++)
        {
            tmpI = mModel.getInterceptor(interceptors.get(i));
            markers.add(new BasicMarker(Position.fromDegrees(tmpI.getPosX(),i,0d),  new BasicMarkerAttributes(Material.RED, BasicMarkerShape.SPHERE, 1d, 10, 5)));
        }
        
        
        
        layer.setOverrideMarkerElevation(true);
        layer.setMarkers(markers);
        this.worldWindowGLCanvas1.getModel().getLayers().add(layer);
        
        this.worldWindowGLCanvas1.redraw();
    }
    
    public void updatePanelContents()
    {
        // this is called every second, this is the periodic update, you should query the database here
        // and then update the map with new positions, states, assignments, etc
        
        
        
        // below is an example on how to traverse the model for every interceptor , this can easily be done for threats, or all missiles.  I reccomend you look at the 
        // MDSCSSController and the MissileDBManager, i updated all the functions with headers that explain their
        // functionality
        ArrayList<String> interceptors = mModel.getInterceptorList();
        Interceptor tmpI;

        for (int i = 0; i < interceptors.size(); i++) {
            tmpI = mModel.getInterceptor(interceptors.get(i));
            
            // or position, assignment, .....
            //tmpI.getState()
        }
        
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
