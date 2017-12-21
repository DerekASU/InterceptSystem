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

import java.awt.*;
import java.util.Arrays;

public class TheatreMapPanel extends javax.swing.JPanel 
{

    /***************************************************************************
     * TheatreMapPanel
     * 
     * Constructor
     **************************************************************************/
    public TheatreMapPanel() 
    {
        initComponents();
        this.worldWindowGLCanvas1.setModel(new BasicModel());
    }
    
    public void resetView()
    {
    
    }
    
    public void handleInitialUpdate()
    {
        
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
