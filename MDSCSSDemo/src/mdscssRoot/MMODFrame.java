/*******************************************************************************
 * File: MMODFrame.java
 * Description:
 *
 ******************************************************************************/
package mdscssRoot;

import mdscssModel.MissileDBManager;
import mdscssControl.MDSCSSController;

public class MMODFrame extends javax.swing.JFrame 
{
    MissileDBManager mModel;
    MDSCSSController mController;
    
    /***************************************************************************
     * MMODFrame
     * 
     * Constructor
     **************************************************************************/
    public MMODFrame() 
    {
        javax.swing.ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/img/AppIcon.png"));
         
        setIconImage(icon.getImage());
        initComponents();
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
        setLocation(new java.awt.Point(25, 25));
        setMinimumSize(new java.awt.Dimension(1280, 1024));
        setResizable(false);
        setSize(new java.awt.Dimension(1280, 1024));

        lblTSS.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTSS.setForeground(new java.awt.Color(255, 255, 255));
        lblTSS.setText("TSS Software Version:");

        lblSysControl.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSysControl.setForeground(new java.awt.Color(255, 255, 255));
        lblSysControl.setText("System Control Mode:");

        TSSStatusIndicator.setEditable(false);
        TSSStatusIndicator.setBackground(new java.awt.Color(0, 204, 0));
        TSSStatusIndicator.setPreferredSize(new java.awt.Dimension(22, 22));

        lblTSSVersion.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTSSVersion.setForeground(new java.awt.Color(255, 255, 255));
        lblTSSVersion.setText("X.X");

        SMSSStatusIndicator.setEditable(false);
        SMSSStatusIndicator.setBackground(new java.awt.Color(0, 204, 0));
        SMSSStatusIndicator.setPreferredSize(new java.awt.Dimension(22, 22));

        lblSMSSVersion.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSMSSVersion.setForeground(new java.awt.Color(255, 255, 255));
        lblSMSSVersion.setText("X.X");

        lblSMSS.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSMSS.setForeground(new java.awt.Color(255, 255, 255));
        lblSMSS.setText("SMSS Software Version:");

        MCSSStatusIndicator.setEditable(false);
        MCSSStatusIndicator.setBackground(new java.awt.Color(0, 204, 0));
        MCSSStatusIndicator.setPreferredSize(new java.awt.Dimension(22, 22));

        lblMCSS.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMCSS.setForeground(new java.awt.Color(255, 255, 255));
        lblMCSS.setText("MCSS Software Version:");

        lblMCSSVersion.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMCSSVersion.setForeground(new java.awt.Color(255, 255, 255));
        lblMCSSVersion.setText("X.X");

        lblMDSCSSVersion.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMDSCSSVersion.setForeground(new java.awt.Color(255, 255, 255));
        lblMDSCSSVersion.setText("1.1A");

        cmbSysMode.setBackground(new java.awt.Color(65, 65, 65));
        cmbSysMode.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbSysMode.setForeground(new java.awt.Color(65, 65, 65));
        cmbSysMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Manual", "Automatic", "Forgiving" }));
        cmbSysMode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 161, 226)));
        cmbSysMode.setMinimumSize(new java.awt.Dimension(77, 30));

        javax.swing.GroupLayout theatreMapPanel1Layout = new javax.swing.GroupLayout(theatreMapPanel1);
        theatreMapPanel1.setLayout(theatreMapPanel1Layout);
        theatreMapPanel1Layout.setHorizontalGroup(
            theatreMapPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 876, Short.MAX_VALUE)
        );
        theatreMapPanel1Layout.setVerticalGroup(
            theatreMapPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 573, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(interceptorOverviewPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(threatOverviewPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
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
                                .addComponent(cmbSysMode, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(sMCDPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblSMSS)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblSMSSVersion)
                                .addGap(56, 56, 56)
                                .addComponent(MCSSStatusIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblMCSS)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblMCSSVersion)
                                .addGap(315, 315, 315)
                                .addComponent(lblMDSCSSVersion))
                            .addComponent(theatreMapPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(35, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSysControl)
                    .addComponent(cmbSysMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sMCDPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(theatreMapPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(interceptorOverviewPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(threatOverviewPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
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
