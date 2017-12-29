package mdscssRoot;

import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import mdscssModel.MissileDBManager;

/*******************************************************************************
 * The SMCDWrapper is a JFrame that wraps an instance of a SMCD panel so that it can
 * be displayed as its own window.
 ******************************************************************************/
public class SMCDWrapper extends javax.swing.JFrame 
{
    SMCDPanel mParent;
    SMCDWrapper mSelf; 
    
    /***************************************************************************
     * Constructor
     * 
     * @param pID The ID of the current active interceptor selection
     * @param pParent Reference to the SMCD panel spawning the window
     * @param pModel Reference to the missile db manager
     * @param pMMOD Reference to the multi missile display
     **************************************************************************/
    public SMCDWrapper(String pID, SMCDPanel pParent, MissileDBManager pModel, MMODFrame pMMOD) 
    {
        this.setTitle("Single Missile Control");
        javax.swing.ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/img/appIcon.png"));
        setIconImage(icon.getImage());
        
        
        initComponents();
        
        sMCDPanel1.initialize(pModel, pMMOD);
        
        sMCDPanel1.handleInitialUpdate();
        sMCDPanel1.handleSelChange(pID);
        sMCDPanel1.hideExpandControls();
        mSelf = this;
        
    }
    
    /***************************************************************************
     * The update function forwards the update call from the MMODPoller to the underlying SMCD panel
     **************************************************************************/
    public void update()
    {
        sMCDPanel1.updatePanelContents();
    }
    
     /***************************************************************************
     * The forceClose function dispatches a close event for this JFrame.  This is invoked from the 
     * control thread if access to the subsystems has been compromised.  
     **************************************************************************/
    public void forceClose()
    {
        setVisible(false);

        EventQueue.invokeLater(new Runnable(){
            public void run(){
                mSelf.dispatchEvent(new WindowEvent(mSelf, WindowEvent.WINDOW_CLOSING));
            }
        });
        

    }

    /***************************************************************************
     * The initComponents function creates and draws the container's swing components.  Autogenerated by
     * Netbeans IDE GUI Editor.
     **************************************************************************/
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sMCDPanel1 = new mdscssRoot.SMCDPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImages(null);
        setMaximumSize(new java.awt.Dimension(357, 533));
        setMinimumSize(new java.awt.Dimension(357, 533));
        setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sMCDPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sMCDPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 511, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private mdscssRoot.SMCDPanel sMCDPanel1;
    // End of variables declaration//GEN-END:variables
}
