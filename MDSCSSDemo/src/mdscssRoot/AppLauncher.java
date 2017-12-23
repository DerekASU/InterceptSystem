/*******************************************************************************
 * File: AppLauncher.java
 * Description:
 *
 ******************************************************************************/
package mdscssRoot;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
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
        
        
        try
{
    String[][] icons =
    {
        {"OptionPane.errorIcon", "65581"},
        {"OptionPane.warningIcon", "65577"},
        {"OptionPane.questionIcon", "65579"},
        {"OptionPane.informationIcon", "65583"}
    };
    //obtain a method for creating proper icons
    Method getIconBits = Class.forName("sun.awt.shell.Win32ShellFolder2").getDeclaredMethod("getIconBits", new Class[]{long.class, int.class});
    getIconBits.setAccessible(true);
    //calculate scaling factor
    double dpiScalingFactor = Toolkit.getDefaultToolkit().getScreenResolution() / 96.0;
    int icon32Size = (dpiScalingFactor == 1)?(32):((dpiScalingFactor == 1.25)?(40):((dpiScalingFactor == 1.5)?(45):((int) (32 * dpiScalingFactor))));
    Object[] arguments = {null, icon32Size};
    for (String[] s:icons)
    {
        if (UIManager.get(s[0]) instanceof ImageIcon)
        {
            arguments[0] = Long.valueOf(s[1]);
            //this method is static, so the first argument can be null
            int[] iconBits = (int[]) getIconBits.invoke(null, arguments);
            if (iconBits != null)
            {
                //create an image from the obtained array
                BufferedImage img = new BufferedImage(icon32Size, icon32Size, BufferedImage.TYPE_INT_ARGB);
                img.setRGB(0, 0, icon32Size, icon32Size, iconBits, 0, icon32Size);
                ImageIcon newIcon = new ImageIcon(img);
                //override previous icon with the new one
                UIManager.put(s[0], newIcon);
            }
        }
    }
}
catch (Exception e)
{
    e.printStackTrace();
}
         
        // Overrite the color scheme for the Swing Combobox controls
        javax.swing.UIManager.put("ComboBox.selectionBackground", new javax.swing.plaf.ColorUIResource(new java.awt.Color(27,166,226)));
        javax.swing.UIManager.put("ComboBox.selectionForeground", new javax.swing.plaf.ColorUIResource(new java.awt.Color(65,65,65)));

        // Initialize the main application objects
        dbMgr = new MissileDBManager();
        
        mmodGui = new MMODFrame();
        mmodGui.getContentPane().setBackground(new java.awt.Color(65,65,65));
        
        mmodGui.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        controller = new MDSCSSController(); 

        // Configure the main application objects
        controller.initialize(dbMgr, mmodGui);
        mmodGui.initialize(dbMgr, controller);
                
        // Launch the GUI
        mmodGui.setVisible(true);
    }
    
}
