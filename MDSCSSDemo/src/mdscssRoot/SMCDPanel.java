/*******************************************************************************
 * File: SMCDPanel.java
 * Description:
 *
 ******************************************************************************/
package mdscssRoot;

import java.util.ArrayList;


//!!!!!! TODO:  do we need to add implementation for controller software version on a per interceptor basis?


public class SMCDPanel extends javax.swing.JPanel 
{
    ArrayList<SMCDWrapper> windowList;
    boolean bSeparateWindow;
    
    /***************************************************************************
     * SMCDPanel
     * 
     * Constructor
     **************************************************************************/
    public SMCDPanel() 
    {
        windowList = new ArrayList();
        bSeparateWindow = false;
        
        initComponents();
    }
    
    public void resetView()
    {
        btnDestruct.setEnabled(false);
        btnDetonate.setEnabled(false);
        btnLaunch.setEnabled(false);
        btnNewWindow.setVisible(false);
        tglDisable.setEnabled(false);
        
        cmbAssignedThreat.setVisible(false);
        cmbSelInterceptor.setVisible(false);
        cmbAssignmentMode.setEnabled(false);
        cmbDetonateMode.setEnabled(false);
        
        txtInterceptorPosition.setText("N/A");
        txtInterceptorState.setText("N/A");
        txtThreatDistance.setText("N/A");
        txtThreatPosition.setText("N/A");
        
        SMCDWrapper tmp;
        for(int i = 0; i < windowList.size(); i++)
        {
            tmp = windowList.get(i);
            if(tmp != null)
            tmp.forceClose();
        }
        
        windowList.clear();
    }
    
    public void handleInitialUpdate()
    {
        
        if(!bSeparateWindow)
        {
            btnNewWindow.setVisible(true);
        }
        
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

        lblTitle = new javax.swing.JLabel();
        btnNewWindow = new javax.swing.JLabel();
        cmbSelInterceptor = new javax.swing.JComboBox<>();
        lblInterceptor = new javax.swing.JLabel();
        lblIState = new javax.swing.JLabel();
        txtInterceptorState = new javax.swing.JTextField();
        txtInterceptorPosition = new javax.swing.JTextField();
        lblIPos = new javax.swing.JLabel();
        lblThreat = new javax.swing.JLabel();
        cmbAssignedThreat = new javax.swing.JComboBox<>();
        lblTPos = new javax.swing.JLabel();
        txtThreatPosition = new javax.swing.JTextField();
        txtThreatDistance = new javax.swing.JTextField();
        lblTDist = new javax.swing.JLabel();
        lblAssignOvrrd = new javax.swing.JLabel();
        cmbAssignmentMode = new javax.swing.JComboBox<>();
        cmbDetonateMode = new javax.swing.JComboBox<>();
        lblDetOvrrd = new javax.swing.JLabel();
        tglDisable = new javax.swing.JToggleButton();
        btnDestruct = new javax.swing.JButton();
        btnDetonate = new javax.swing.JButton();
        btnLaunch = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setBackground(new java.awt.Color(27, 161, 226));
        setMaximumSize(new java.awt.Dimension(323, 573));
        setMinimumSize(new java.awt.Dimension(323, 573));
        setPreferredSize(new java.awt.Dimension(323, 573));

        lblTitle.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Interceptor Control:");

        btnNewWindow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/openIcon2.png"))); // NOI18N
        btnNewWindow.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnNewWindowMousePressed(evt);
            }
        });

        cmbSelInterceptor.setBackground(new java.awt.Color(65, 65, 65));
        cmbSelInterceptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbSelInterceptor.setForeground(new java.awt.Color(65, 65, 65));
        cmbSelInterceptor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "X1" }));
        cmbSelInterceptor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 161, 226)));
        cmbSelInterceptor.setPreferredSize(new java.awt.Dimension(47, 30));

        lblInterceptor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblInterceptor.setForeground(new java.awt.Color(255, 255, 255));
        lblInterceptor.setText("Selected Interceptor:");

        lblIState.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblIState.setForeground(new java.awt.Color(255, 255, 255));
        lblIState.setText("Interceptor State:");

        txtInterceptorState.setEditable(false);
        txtInterceptorState.setBackground(new java.awt.Color(27, 161, 226));
        txtInterceptorState.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtInterceptorState.setForeground(new java.awt.Color(255, 255, 255));
        txtInterceptorState.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInterceptorState.setPreferredSize(new java.awt.Dimension(69, 30));

        txtInterceptorPosition.setEditable(false);
        txtInterceptorPosition.setBackground(new java.awt.Color(27, 161, 226));
        txtInterceptorPosition.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtInterceptorPosition.setForeground(new java.awt.Color(255, 255, 255));
        txtInterceptorPosition.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInterceptorPosition.setPreferredSize(new java.awt.Dimension(69, 30));

        lblIPos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblIPos.setForeground(new java.awt.Color(255, 255, 255));
        lblIPos.setText("InterceptorPosition:");

        lblThreat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblThreat.setForeground(new java.awt.Color(255, 255, 255));
        lblThreat.setText("Assigned Threat:");

        cmbAssignedThreat.setBackground(new java.awt.Color(65, 65, 65));
        cmbAssignedThreat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbAssignedThreat.setForeground(new java.awt.Color(65, 65, 65));
        cmbAssignedThreat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "X1" }));
        cmbAssignedThreat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 161, 226)));
        cmbAssignedThreat.setPreferredSize(new java.awt.Dimension(47, 30));

        lblTPos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTPos.setForeground(new java.awt.Color(255, 255, 255));
        lblTPos.setText("Threat Position:");

        txtThreatPosition.setEditable(false);
        txtThreatPosition.setBackground(new java.awt.Color(27, 161, 226));
        txtThreatPosition.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtThreatPosition.setForeground(new java.awt.Color(255, 255, 255));
        txtThreatPosition.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtThreatPosition.setPreferredSize(new java.awt.Dimension(69, 30));

        txtThreatDistance.setEditable(false);
        txtThreatDistance.setBackground(new java.awt.Color(27, 161, 226));
        txtThreatDistance.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtThreatDistance.setForeground(new java.awt.Color(255, 255, 255));
        txtThreatDistance.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtThreatDistance.setPreferredSize(new java.awt.Dimension(69, 30));

        lblTDist.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTDist.setForeground(new java.awt.Color(255, 255, 255));
        lblTDist.setText("Distance To Threat:");

        lblAssignOvrrd.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblAssignOvrrd.setForeground(new java.awt.Color(255, 255, 255));
        lblAssignOvrrd.setText("Assignment Override:");

        cmbAssignmentMode.setBackground(new java.awt.Color(65, 65, 65));
        cmbAssignmentMode.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbAssignmentMode.setForeground(new java.awt.Color(65, 65, 65));
        cmbAssignmentMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default" }));
        cmbAssignmentMode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 161, 226)));
        cmbAssignmentMode.setPreferredSize(new java.awt.Dimension(75, 30));

        cmbDetonateMode.setBackground(new java.awt.Color(65, 65, 65));
        cmbDetonateMode.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbDetonateMode.setForeground(new java.awt.Color(65, 65, 65));
        cmbDetonateMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default" }));
        cmbDetonateMode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 161, 226)));
        cmbDetonateMode.setPreferredSize(new java.awt.Dimension(75, 30));

        lblDetOvrrd.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDetOvrrd.setForeground(new java.awt.Color(255, 255, 255));
        lblDetOvrrd.setText("Detonate Override:");

        tglDisable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tglDisable.setText("Disabled");
        tglDisable.setToolTipText("");
        tglDisable.setMaximumSize(new java.awt.Dimension(77, 30));
        tglDisable.setMinimumSize(new java.awt.Dimension(119, 26));
        tglDisable.setPreferredSize(new java.awt.Dimension(127, 30));

        btnDestruct.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnDestruct.setText("Destruct");
        btnDestruct.setEnabled(false);
        btnDestruct.setPreferredSize(new java.awt.Dimension(127, 30));

        btnDetonate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnDetonate.setText("Detonate");
        btnDetonate.setPreferredSize(new java.awt.Dimension(127, 30));
        btnDetonate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnDetonateMousePressed(evt);
            }
        });

        btnLaunch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnLaunch.setText("Launch");
        btnLaunch.setPreferredSize(new java.awt.Dimension(127, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNewWindow)
                        .addGap(8, 8, 8))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblInterceptor)
                            .addComponent(lblIState)
                            .addComponent(lblIPos)
                            .addComponent(lblThreat)
                            .addComponent(lblTPos)
                            .addComponent(lblTDist))
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtInterceptorState, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtInterceptorPosition, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbAssignedThreat, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtThreatPosition, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtThreatDistance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbSelInterceptor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lblDetOvrrd)
                        .addGap(33, 33, 33)
                        .addComponent(cmbDetonateMode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lblAssignOvrrd)
                        .addGap(18, 18, 18)
                        .addComponent(cmbAssignmentMode, 0, 153, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tglDisable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLaunch, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnDetonate, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDestruct, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addComponent(btnNewWindow))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblInterceptor)
                    .addComponent(cmbSelInterceptor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIState)
                    .addComponent(txtInterceptorState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIPos)
                    .addComponent(txtInterceptorPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblThreat)
                    .addComponent(cmbAssignedThreat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTPos)
                    .addComponent(txtThreatPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTDist)
                    .addComponent(txtThreatDistance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAssignOvrrd)
                    .addComponent(cmbAssignmentMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDetOvrrd)
                    .addComponent(cmbDetonateMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDestruct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tglDisable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDetonate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLaunch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewWindowMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNewWindowMousePressed
        SMCDWrapper tmpWindow;

        windowList.add(new SMCDWrapper(String.valueOf(cmbSelInterceptor.getSelectedItem()), this));
        tmpWindow = windowList.get(windowList.size()-1);
        tmpWindow.getContentPane().setBackground(new java.awt.Color(65,65,65));
        tmpWindow.setVisible(true);
    }//GEN-LAST:event_btnNewWindowMousePressed

    private void btnDetonateMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDetonateMousePressed
        // TODO here we have to cycle through the window list, check for null entries and delete them
        // check to see if one is made for this, if so bring to front and center, otherwise check to see if > 5
        // if greater than 5, dialog alert for max windows, otherwise create
        
        
    }//GEN-LAST:event_btnDetonateMousePressed

    
    public void hideExpandControls()
    {
        btnNewWindow.setVisible(false);
        bSeparateWindow = true;
    }
    
    public void setDisplayedInterceptor(String pID)
    {
        
    }
    
  

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDestruct;
    private javax.swing.JButton btnDetonate;
    private javax.swing.JButton btnLaunch;
    private javax.swing.JLabel btnNewWindow;
    private javax.swing.JComboBox<String> cmbAssignedThreat;
    private javax.swing.JComboBox<String> cmbAssignmentMode;
    private javax.swing.JComboBox<String> cmbDetonateMode;
    private javax.swing.JComboBox<String> cmbSelInterceptor;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblAssignOvrrd;
    private javax.swing.JLabel lblDetOvrrd;
    private javax.swing.JLabel lblIPos;
    private javax.swing.JLabel lblIState;
    private javax.swing.JLabel lblInterceptor;
    private javax.swing.JLabel lblTDist;
    private javax.swing.JLabel lblTPos;
    private javax.swing.JLabel lblThreat;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JToggleButton tglDisable;
    private javax.swing.JTextField txtInterceptorPosition;
    private javax.swing.JTextField txtInterceptorState;
    private javax.swing.JTextField txtThreatDistance;
    private javax.swing.JTextField txtThreatPosition;
    // End of variables declaration//GEN-END:variables
}
