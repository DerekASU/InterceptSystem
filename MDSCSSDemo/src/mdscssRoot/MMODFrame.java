/*******************************************************************************
 * File: MMODFrame.java
 * Description:
 *
 ******************************************************************************/
package mdscssRoot;
//todo:: add a destructall
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import mdscssModel.*;
import mdscssControl.MDSCSSController;
import mdscssControl.MDSCSSController.controlMode;

public class MMODFrame extends javax.swing.JFrame 
{
    MissileDBManager mModel;
    MDSCSSController mController;
    private Thread poller = null;
    
    
    /***************************************************************************
     * MMODFrame
     * 
     * Constructor
     **************************************************************************/
    public MMODFrame() 
    {
        javax.swing.ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/img/AppIcon.png"));
         //todo:: add a polling thread to the database? might not be necessary, only if latency becomes an issue
        setIconImage(icon.getImage());
        initComponents();
        
        cmbSysMode.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent ie) {
                if(ie.getStateChange() == ItemEvent.SELECTED){
                  handleSysModeSelection(ie);
               } 
            }
        });
        
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                handleClosing();
            }
        });

        
        resetView();
    }
    
    /***************************************************************************
     * initialize
     * 
     * initializes the MMOD GUI on startup.  References to the controller and 
     * model are established; initial population of controls is performed
     * 
     * @param pModel - Reference to the Missile DB
     * @param pController - Reference to the MDSCSS Controller object
     **************************************************************************/
    public void initialize(MissileDBManager pModel, MDSCSSController pController) 
    {
        mModel = pModel;
        mController = pController;
        
        interceptorOverviewPanel1.setParent(this);
        sMCDPanel1.initialize(mModel, this);
    }
    
    public void tssConnected(String version)
    {
        TSSStatusIndicator.setBackground(Color.GREEN);
        lblTSSVersion.setText(version);
    }
    
    public void mcssConnected(String version)
    {
        MCSSStatusIndicator.setBackground(Color.GREEN);
        lblMCSSVersion.setText(version);
    }
    
    public void smssConnected(String version)
    {
        SMSSStatusIndicator.setBackground(Color.GREEN);
        lblSMSSVersion.setText(version);
    }
    
    public void handleSubsystemFailure()
    {
        TSSStatusIndicator.setBackground(Color.ORANGE);
        MCSSStatusIndicator.setBackground(Color.ORANGE);
        SMSSStatusIndicator.setBackground(Color.ORANGE);
        
        
        Thread t = new Thread(new Runnable(){
        public void run(){
            JOptionPane.showMessageDialog(null,  "\nThe MDSCSS has detected a connection failure with\n" +
                                                    "one or more MDS subsystems.  If the connection\n" +
                                                    "cannot be re-established in 5 minutes, all interceptors\n" +
                                                    "will be destructed.\n", 
                                                    "Subsystem Connection Failure", 
                                                    JOptionPane.WARNING_MESSAGE);
        }
    });
  t.start();
        
        
        
        resetView();
    }
    
    public void handleCodeRed()
    {
        TSSStatusIndicator.setBackground(Color.RED);
        MCSSStatusIndicator.setBackground(Color.RED);
        SMSSStatusIndicator.setBackground(Color.RED);
        
        Thread t = new Thread(new Runnable(){
        public void run(){
           JOptionPane.showMessageDialog(null,  "\nThe MDSCSS has been unable to establis a connection\n" +
                                            "with an MDS subsystem.  All interceptors have been \n" +
                                            "destructed. A restart of the MDSCSS is required.\n", 
                                            "Subsystem Failure", 
                                            JOptionPane.ERROR_MESSAGE);
        }
    });
  t.start();
        
        
        
    }
    
    public void handleLaunchModeChange()
    {
        Thread t = new Thread(new Runnable(){
        public void run(){
           JOptionPane.showMessageDialog(null,  "\nAn alternative interceptor for threat assignment has\n" +
                                            "not been found.  The system has reverted to maunual assignment \n" +
                                            "and launch mode.\n", 
                                            "Automatic Launch Mode Change", 
                                            JOptionPane.ERROR_MESSAGE);
        }
    });
  t.start();
    }
    
    public void handleClosing()
    {
        int reply = JOptionPane.showConfirmDialog(null, "Are you sure you wish to exit?", "Exit MDSCSS", JOptionPane.YES_NO_OPTION);

  if (reply == JOptionPane.YES_OPTION) {

      mController.finalize();
    System.exit(0);

  }
        
    }
    
    public void handleInitialUpdate()
    {
        ArrayList<String> Missiles;
        Interceptor tmpI = null;
        Missile tmpT;
        
        cmbSysMode.setEnabled(true);
        
        //theatreMapPanel1.handleInitialUpdate();
        sMCDPanel1.handleInitialUpdate();
        threatOverviewPanel1.handleInitialUpdate();
        interceptorOverviewPanel1.handleInitialUpdate();
        updateInterceptors();
        
        
        
        
        Missiles = mModel.getInterceptorList();
        Collections.sort(Missiles);
        for(int i = 0; i < Missiles.size(); i++)
        {
            tmpI = mModel.getInterceptor(Missiles.get(i));
            
            interceptorOverviewPanel1.addEntry(tmpI.getIdentifier(),
                    tmpI.getState(), tmpI.getAssignedThreat(), tmpI.getPositionVector(), tmpI.isDisabled());
            
        }
        
        
        Missiles = mModel.getThreatList();
        Collections.sort(Missiles);
        for(int i = 0; i < Missiles.size(); i++)
        {
            tmpT = mModel.getThreat(Missiles.get(i));
            
            threatOverviewPanel1.addEntry(tmpT.getIdentifier(), tmpT.getPositionVector());
            
        }
        
        //todo:: find a better way to do this
        if(tmpI != null)
        {
            interceptorOverviewPanel1.handleSelChange(tmpI.getIdentifier());
            sMCDPanel1.handleSelChange(tmpI.getIdentifier());
        }
        
        poller = new Thread(new MMODPoller(this));
        poller.start();
        
    }
    
    public void handleThreatDestruction(String pID)
    {
        threatOverviewPanel1.removeEntry(pID);
        
    }
    
    public void periodicUpdate()
    {
        updateThreats();
        updateInterceptors();
        sMCDPanel1.updatePanelContents();

        
        String tmp = mController.getForgivingAssignmentState();
        
        if(tmp != null)
        {
            JOptionPane msg = new JOptionPane(tmp + ".\n Do you wish to approve this assignment?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
            JDialog dlg = msg.createDialog("Approve or Reject Forgiving Assignment");
            dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            
            dlg.addComponentListener(new ComponentAdapter() {
                public void componentShown(ComponentEvent e) {
                    super.componentShown(e);
                    Timer t = new Timer(40000,new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dlg.setVisible(false);
                        }
                    });
                    t.start();
                }
            });
        
            dlg.setVisible(true);
        
        if(msg.getValue().equals(0))
        {
            mController.approveForgivingAssignment();
        }
        else if(msg.getValue().equals(1))
        {
            mController.rejectForgivingAssignment();
        }
           
            
        }
        
    }
    
    private void updateThreats()
    {
        ArrayList<String> Interceptors;
        ArrayList<String> Threats;
        Interceptor tmpI;
        Missile tmp;
        String assignmentLabel = "";
        
        Interceptors = mModel.getInterceptorList();
        Threats = mModel.getThreatList();
        
        for(int i = 0; i < Threats.size(); i++)
        {
            tmp = mModel.getThreat(Threats.get(i));
            
            assignmentLabel = "[UNASSIGNED]";
            for(int j = 0; j < Interceptors.size(); j++)
            {
                tmpI = mModel.getInterceptor(Interceptors.get(j));                
                
                if(tmpI.getAssignedThreat().equals(tmp.getIdentifier()))
                {
                    assignmentLabel = tmpI.getIdentifier();
                }
            }
            
            threatOverviewPanel1.updateEntry(tmp.getIdentifier(), assignmentLabel, tmp.getPositionVector());
        }
        
        if(mController.getControlMode() == controlMode.Manual)
            cmbSysMode.setSelectedIndex(0);
        else if(mController.getControlMode() == controlMode.Automatic)
            cmbSysMode.setSelectedIndex(1);
        else
            cmbSysMode.setSelectedIndex(2);
        
    }
    
    private void updateInterceptors()
    {
        ArrayList<String> Interceptors;
        Interceptor tmpI;
        
        Interceptors = mModel.getInterceptorList();
        
        for(int i = 0; i < Interceptors.size(); i++)
        {
            tmpI = mModel.getInterceptor(Interceptors.get(i));
            
            interceptorOverviewPanel1.updateEntry(tmpI.getIdentifier(), tmpI.getState(), 
                    tmpI.getAssignedThreat(), tmpI.getPositionVector(), tmpI.isDisabled());
        }
        
    }
    
    public void ChangeSMCDSel(String pID)
    {
        sMCDPanel1.handleSelChange(pID);
    }
    
    public void ChangeIOverviewSel(String pID)
    {
        interceptorOverviewPanel1.handleSelChange(pID);
    }
    
    private void resetView()
    {
        if(poller!= null)
            poller.interrupt();
        
        cmbSysMode.setEnabled(false);
        
        //theatreMapPanel1.resetView();
        sMCDPanel1.resetView();
        threatOverviewPanel1.resetView();
        interceptorOverviewPanel1.resetView();
    }

    public void forwardLaunchCmd(String pID)
    {
        mController.cmdMcssLaunch(pID);
    }
    
    public void forwardDetonate(String pID)
    {
        mController.cmdSmssDetEnable(pID);
        mController.cmdMcssDetonate(pID);
    }
    
    public void forwardDestruct(String pID)
    {
        mController.cmdMcssDestruct(pID);
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

        lblTSS = new javax.swing.JLabel();
        lblSysControl = new javax.swing.JLabel();
        TSSStatusIndicator = new javax.swing.JTextField();
        lblTSSVersion = new javax.swing.JLabel();
        SMSSStatusIndicator = new javax.swing.JTextField();
        lblSMSSVersion = new javax.swing.JLabel();
        lblSMSS = new javax.swing.JLabel();
        MCSSStatusIndicator = new javax.swing.JTextField();
        lblMCSS = new javax.swing.JLabel();
        lblMCSSVersion = new javax.swing.JLabel();
        lblMDSCSSVersion = new javax.swing.JLabel();
        cmbSysMode = new javax.swing.JComboBox<>();
        sMCDPanel1 = new mdscssRoot.SMCDPanel();
        interceptorOverviewPanel1 = new mdscssRoot.InterceptorOverviewPanel();
        threatOverviewPanel1 = new mdscssRoot.ThreatOverviewPanel();
        theatreMapPanel1 = new mdscssRoot.TheatreMapPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MDSCSS Human Machine Interface");
        setAutoRequestFocus(false);
        setBackground(java.awt.Color.gray);
        setLocation(new java.awt.Point(205, 25));
        setMinimumSize(new java.awt.Dimension(1280, 1024));
        setResizable(false);
        setSize(new java.awt.Dimension(1280, 1024));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lblTSS.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTSS.setForeground(new java.awt.Color(255, 255, 255));
        lblTSS.setText("TSS Software Version:");

        lblSysControl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSysControl.setForeground(new java.awt.Color(255, 255, 255));
        lblSysControl.setText("System Control Mode:");

        TSSStatusIndicator.setEditable(false);
        TSSStatusIndicator.setBackground(new java.awt.Color(255, 255, 255));
        TSSStatusIndicator.setPreferredSize(new java.awt.Dimension(22, 22));

        lblTSSVersion.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTSSVersion.setForeground(new java.awt.Color(255, 255, 255));
        lblTSSVersion.setText("N/A");

        SMSSStatusIndicator.setEditable(false);
        SMSSStatusIndicator.setBackground(new java.awt.Color(255, 255, 255));
        SMSSStatusIndicator.setPreferredSize(new java.awt.Dimension(22, 22));

        lblSMSSVersion.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSMSSVersion.setForeground(new java.awt.Color(255, 255, 255));
        lblSMSSVersion.setText("N/A");

        lblSMSS.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSMSS.setForeground(new java.awt.Color(255, 255, 255));
        lblSMSS.setText("SMSS Software Version:");

        MCSSStatusIndicator.setEditable(false);
        MCSSStatusIndicator.setBackground(new java.awt.Color(255, 255, 255));
        MCSSStatusIndicator.setPreferredSize(new java.awt.Dimension(22, 22));

        lblMCSS.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMCSS.setForeground(new java.awt.Color(255, 255, 255));
        lblMCSS.setText("MCSS Software Version:");

        lblMCSSVersion.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMCSSVersion.setForeground(new java.awt.Color(255, 255, 255));
        lblMCSSVersion.setText("N/A");

        lblMDSCSSVersion.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMDSCSSVersion.setForeground(new java.awt.Color(255, 255, 255));
        lblMDSCSSVersion.setText("0.5B");

        cmbSysMode.setBackground(new java.awt.Color(65, 65, 65));
        cmbSysMode.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbSysMode.setForeground(new java.awt.Color(65, 65, 65));
        cmbSysMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Manual", "Automatic", "Forgiving" }));
        cmbSysMode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 161, 226)));
        cmbSysMode.setMinimumSize(new java.awt.Dimension(77, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(TSSStatusIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTSS)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTSSVersion)
                                .addGap(71, 71, 71)
                                .addComponent(SMSSStatusIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblSysControl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbSysMode, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSMSS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSMSSVersion)
                        .addGap(56, 56, 56)
                        .addComponent(MCSSStatusIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMCSS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMCSSVersion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblMDSCSSVersion))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(sMCDPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(theatreMapPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(interceptorOverviewPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 684, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(threatOverviewPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblTSS)
                        .addComponent(lblTSSVersion))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSMSS)
                        .addComponent(lblSMSSVersion))
                    .addComponent(SMSSStatusIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblMCSS)
                        .addComponent(lblMCSSVersion)
                        .addComponent(lblMDSCSSVersion))
                    .addComponent(MCSSStatusIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TSSStatusIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSysControl)
                    .addComponent(cmbSysMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(theatreMapPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 514, Short.MAX_VALUE)
                    .addComponent(sMCDPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(threatOverviewPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                    .addComponent(interceptorOverviewPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        
    }//GEN-LAST:event_formWindowClosing

    private void handleSysModeSelection(ItemEvent evt)
    {
        JComboBox dropdown = (JComboBox) evt.getSource();
          
        if(dropdown.getSelectedIndex() == 0)
            mController.setControlMode(controlMode.Manual);
        else if(dropdown.getSelectedIndex() == 1)
            mController.setControlMode(controlMode.Automatic);
        else
            mController.setControlMode(controlMode.Forgiving);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField MCSSStatusIndicator;
    private javax.swing.JTextField SMSSStatusIndicator;
    private javax.swing.JTextField TSSStatusIndicator;
    private javax.swing.JComboBox<String> cmbSysMode;
    private mdscssRoot.InterceptorOverviewPanel interceptorOverviewPanel1;
    private javax.swing.JLabel lblMCSS;
    private javax.swing.JLabel lblMCSSVersion;
    private javax.swing.JLabel lblMDSCSSVersion;
    private javax.swing.JLabel lblSMSS;
    private javax.swing.JLabel lblSMSSVersion;
    private javax.swing.JLabel lblSysControl;
    private javax.swing.JLabel lblTSS;
    private javax.swing.JLabel lblTSSVersion;
    private mdscssRoot.SMCDPanel sMCDPanel1;
    private mdscssRoot.TheatreMapPanel theatreMapPanel1;
    private mdscssRoot.ThreatOverviewPanel threatOverviewPanel1;
    // End of variables declaration//GEN-END:variables
}
