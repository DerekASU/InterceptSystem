/*******************************************************************************
 * File: AppLauncher.java
 * Description:
 *
 ******************************************************************************/
package mdscssRoot;

import mdscssModel.MissileDBManager;
import mdscssControl.MDSCSSController;

public class AppLauncher 
{
    /***************************************************************************
     * main
     * 
     * main method executed at startup;
     * 
     * @param args - Command line arguments
     **************************************************************************/
    public static void main(String[] args) 
    {
        MMODFrame mmodGui;
        MissileDBManager dbMgr;
        MDSCSSController controller;
        
        // Set the "Look and Feel" of the GUI controls to the native Windows 
        try 
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) 
            {
                if ("Windows".equals(info.getName())) 
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {} 
         
        // Overrite the color scheme for the Swing Combobox controls
        javax.swing.UIManager.put("ComboBox.selectionBackground", new javax.swing.plaf.ColorUIResource(new java.awt.Color(27,161,226)));
        javax.swing.UIManager.put("ComboBox.selectionForeground", new javax.swing.plaf.ColorUIResource(new java.awt.Color(65,65,65)));

        // Initialize the main application objects
        dbMgr = new MissileDBManager();
        
        mmodGui = new MMODFrame();
        mmodGui.getContentPane().setBackground(new java.awt.Color(65,65,65));
        
        controller = new MDSCSSController(); 

        // Configure the main application objects
        controller.initialize(dbMgr, mmodGui);
        mmodGui.initialize(dbMgr, controller);
                
        // Launch the GUI
        mmodGui.setVisible(true);
    }
    
}
