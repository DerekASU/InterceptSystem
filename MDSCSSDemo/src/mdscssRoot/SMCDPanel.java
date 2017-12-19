/*******************************************************************************
 * File: SMCDPanel.java
 * Description:
 *
 ******************************************************************************/
package mdscssRoot;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import javafx.scene.paint.Color;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import mdscssModel.Interceptor;
import mdscssModel.Missile;
import mdscssModel.MissileDBManager;


public class SMCDPanel extends javax.swing.JPanel 
{
    ArrayList<SMCDWrapper> windowList;
    MissileDBManager mModel;
    MMODFrame mParent;
    boolean bSeparateWindow, bIsActive;
    
    /***************************************************************************
     * SMCDPanel
     * 
     * Constructor
     **************************************************************************/
    public SMCDPanel() 
    {
        windowList = new ArrayList();
        bSeparateWindow = false;
        bIsActive = false;
        
        initComponents();
        
        ((JLabel)cmbAssignedThreat.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)cmbSelInterceptor.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        ((JLabel)cmbAssignmentMode.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)cmbDetonateMode.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        
        
        cmbAssignedThreat.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent ie) {
                if(ie.getStateChange() == ItemEvent.SELECTED){
                  handleAssignedThreatSelection(ie);
               } 
            }
        });
        
        cmbSelInterceptor.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent ie) {
                if(ie.getStateChange() == ItemEvent.SELECTED){
                  handleInterceptorSelection(ie);
               } 
            }
        });
        
        cmbAssignmentMode.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent ie) {
                if(ie.getStateChange() == ItemEvent.SELECTED){
                  handleAssignModeSelection(ie);
               } 
            }
        });
        
        cmbDetonateMode.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent ie) {
                if(ie.getStateChange() == ItemEvent.SELECTED){
                  handleDetModeSelection(ie);
               } 
            }
        });
        
    }
    
    public void resetView()
    {
        bIsActive = false;
        
        btnDestruct.setEnabled(false);
        btnDetonate.setEnabled(false);
        btnLaunch.setEnabled(false);
        btnNewWindow.setVisible(false);
        tglDisable.setEnabled(false);
        
        lblLaunchTime.setVisible(false);
        
        cmbAssignedThreat.setEnabled(false);
        cmbSelInterceptor.setEnabled(false);
        cmbAssignmentMode.setEnabled(false);
        cmbDetonateMode.setEnabled(false);
        
        txtInterceptorPosition.setText("[NA]");
        txtInterceptorState.setText("[NA]");
        txtThreatDistance.setText("[NA]");
        txtThreatPosition.setText("[NA]");
        
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
        //populate dropdowns,  only unassigned threats shown
        
        ArrayList<String> missiles;

        cmbSelInterceptor.removeAllItems();
        cmbAssignedThreat.removeAllItems();
        
        cmbAssignedThreat.addItem("[UNASSIGNED]");
        cmbAssignedThreat.setSelectedItem("[UNASSIGNED]");
        
        missiles = mModel.getInterceptorList();
        Collections.sort(missiles);
        for(int i = 0; i < missiles.size(); i++)
        {
            cmbSelInterceptor.addItem(missiles.get(i));
        }
        
        missiles = mModel.getThreatList();
        Collections.sort(missiles);
        for(int i = 0; i < missiles.size(); i++)
        {

            cmbAssignedThreat.addItem(missiles.get(i));
            
        }

    }
        
    public void handleSelChange(String pID)
    {
        Timestamp tmp = null;
        
        if(!bSeparateWindow)
        {
            btnNewWindow.setVisible(true);
        }
        bIsActive = true;
        
        btnNewWindow.setVisible(true);
        
        cmbSelInterceptor.setEnabled(true);
        cmbSelInterceptor.setSelectedItem(pID);
        
        tmp = mModel.getInterceptor(pID).getTimestamp();
        
        if(tmp != null)
        {
            lblLaunchTime.setVisible(true);
            lblLaunchTime.setText("Time of Launch:  " + tmp.toString());
        }
        else
        {
            lblLaunchTime.setVisible(false);
        }
        
        updatePanelContents();
    }
    
    public void updatePanelContents()
    {
        Interceptor tmp = mModel.getInterceptor((String)cmbSelInterceptor.getSelectedItem());
        ArrayList<String> threats = mModel.getThreatList();
        Missile tmpT;
        String posText = "";
        DecimalFormat sciNote =  new DecimalFormat("0.##E0");
        
        
        if(tmp != null){
            String assignedThreat = tmp.getAssignedThreat();
        int[] pos = tmp.getPositionVector();
        int[] tPos;
        
        if(!bIsActive)
            return;
        
        
        for(int i = 1; i < cmbAssignedThreat.getItemCount(); i++)
        {
            if(!threats.contains(cmbAssignedThreat.getItemAt(i)))
            {
                cmbAssignedThreat.removeItemAt(i);
            }
        }
        
        
        
        if(!tmp.isDisabled())
        {
            tglDisable.setText("Enabled");
            tglDisable.setSelected(true);
        }
        else
        {
            tglDisable.setText("Disabled");
            tglDisable.setSelected(false);
            cmbAssignmentMode.setEnabled(false);
            cmbAssignedThreat.setEnabled(false);
            cmbDetonateMode.setEnabled(false);
            btnDestruct.setEnabled(false);
            btnDetonate.setEnabled(false);
            btnLaunch.setEnabled(false);
        }
        
        //todo optimization, do nothing if we are in the detonated or disabled ... state, nothing will change ....?
        switch (tmp.getState()) {
            case DETONATED:
                txtInterceptorState.setText("Destroyed");
                cmbAssignmentMode.setEnabled(false);
                cmbAssignedThreat.setEnabled(false);
                cmbDetonateMode.setEnabled(false);
                btnDestruct.setEnabled(false);
                btnDetonate.setEnabled(false);
                btnLaunch.setEnabled(false);
                tglDisable.setEnabled(false);
                
                txtInterceptorPosition.setForeground(java.awt.Color.LIGHT_GRAY);
                txtInterceptorState.setForeground(java.awt.Color.LIGHT_GRAY);
                break;
            case PRE_FLIGHT:
                if(!tmp.isDisabled()){
                    cmbAssignmentMode.setEnabled(true);
                    cmbAssignedThreat.setEnabled(true);
                    cmbDetonateMode.setEnabled(true);
                    btnLaunch.setEnabled(true);
                    tglDisable.setEnabled(true);
                    txtInterceptorState.setText("Ready");
                }
                else
                {
                    txtInterceptorState.setText("Disabled");
                    
                }
                btnDestruct.setEnabled(false);
                btnDetonate.setEnabled(false);
                
                txtInterceptorPosition.setForeground(java.awt.Color.WHITE);
                txtInterceptorState.setForeground(java.awt.Color.WHITE);
                break;
            case IN_FLIGHT:
                txtInterceptorState.setText("Launched");
                if(!tmp.isDisabled()){
                    cmbDetonateMode.setEnabled(true);
                    btnDestruct.setEnabled(true);
                    
                }
                cmbAssignmentMode.setEnabled(false);
                cmbAssignedThreat.setEnabled(false);
                btnLaunch.setEnabled(false);
                tglDisable.setEnabled(false);
                
                txtInterceptorPosition.setForeground(java.awt.Color.WHITE);
                txtInterceptorState.setForeground(java.awt.Color.WHITE);
                break;
            default:
                txtInterceptorState.setText("[UNKNOWN]");
                //todo ... disable controls
                break;
        }
        
        posText = ("[" + pos[0] + "," + pos[1] + "," + pos[2] + "] m");
        txtInterceptorPosition.setToolTipText(posText);
        
        if(pos[0] > 999999 || pos[0] < -99999)
            posText = sciNote.format(pos[0]) +",";
        else
            posText = (pos[0] + ",");
        
        if(pos[1] > 999999 || pos[1] < -99999)
            posText += sciNote.format(pos[1]) +",";
        else
            posText += (pos[1] + ",");
        
        if(pos[2] > 999999 || pos[2] < -99999)
            posText += sciNote.format(pos[2]);
        else
            posText += pos[2];
        
        txtInterceptorPosition.setText(posText);
        cmbAssignedThreat.setSelectedItem(assignedThreat);
        
        if(assignedThreat.equals("[UNASSIGNED]"))
        {
            txtThreatDistance.setText("[NA]");
            txtThreatPosition.setText("[NA]");
            txtThreatDistance.setForeground(java.awt.Color.white);
        }
        else
        {
            tmpT = mModel.getThreat(assignedThreat);
            if(tmpT != null)
            {
                DecimalFormat rounder = new DecimalFormat("0.000");
                tPos = tmpT.getPositionVector();
                
                posText = ("[" + tPos[0] + "," + tPos[1] + "," + tPos[2] + "] m");
        txtThreatPosition.setToolTipText(posText);
        
        if(tPos[0] > 999999 || tPos[0] < -99999)
            posText = sciNote.format(tPos[0]) +",";
        else
            posText = (tPos[0] + ",");
        
        if(tPos[1] > 999999 || tPos[1] < -99999)
            posText += sciNote.format(tPos[1]) +",";
        else
            posText += (tPos[1] + ",");
        
        if(tPos[2] > 999999 || tPos[2] < -99999)
            posText += sciNote.format(tPos[2]);
        else
            posText += tPos[2];
        
        txtThreatPosition.setText(posText);
                
                double distance = Math.pow((tPos[0] - pos[0]), 2) + Math.pow((tPos[1] - pos[1]), 2) + Math.pow((tPos[2] - pos[2]), 2);
                distance = Math.sqrt(distance);
                txtThreatDistance.setText(rounder.format(distance) + " m");
                
                
                if(distance <= tmp.getDetonationRange())
                {
                    txtThreatDistance.setForeground(java.awt.Color.green);
                    
                    if(!tmp.isDisabled() && tmp.getState() == Interceptor.interceptorState.IN_FLIGHT)
                    btnDetonate.setEnabled(true);
                }
                else
                {
                    txtThreatDistance.setForeground(java.awt.Color.white);
                    btnDetonate.setEnabled(false);
                }
                
            }
        }
        
        if(tmp.isAssignmentOverriden())
        {
            cmbAssignmentMode.setSelectedIndex(1);
        }
        else
        {
            cmbAssignmentMode.setSelectedIndex(0);
        }
        
        if(tmp.isDetonateOverriden())
        {
            cmbDetonateMode.setSelectedIndex(1);
        }
        else
        {
            cmbDetonateMode.setSelectedIndex(0);
        }
        
        
        
        for(int i = 0; i < windowList.size(); i++)
        {
            if(windowList.get(i)!=null)
            {
                windowList.get(i).update();
            }
        }
    }
    }
    
    public void initialize(MissileDBManager pModel, MMODFrame pParent)
    {
        mModel = pModel;
        mParent = pParent;
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
        lblLaunchTime = new javax.swing.JLabel();

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
        txtInterceptorState.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtInterceptorState.setForeground(new java.awt.Color(255, 255, 255));
        txtInterceptorState.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInterceptorState.setPreferredSize(new java.awt.Dimension(69, 30));

        txtInterceptorPosition.setEditable(false);
        txtInterceptorPosition.setBackground(new java.awt.Color(27, 161, 226));
        txtInterceptorPosition.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtInterceptorPosition.setForeground(new java.awt.Color(255, 255, 255));
        txtInterceptorPosition.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtInterceptorPosition.setPreferredSize(new java.awt.Dimension(69, 30));

        lblIPos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblIPos.setForeground(new java.awt.Color(255, 255, 255));
        lblIPos.setText("Interceptor Position:");

        lblThreat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblThreat.setForeground(new java.awt.Color(255, 255, 255));
        lblThreat.setText("Assigned Threat:");

        cmbAssignedThreat.setBackground(new java.awt.Color(65, 65, 65));
        cmbAssignedThreat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbAssignedThreat.setForeground(new java.awt.Color(65, 65, 65));
        cmbAssignedThreat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 161, 226)));
        cmbAssignedThreat.setPreferredSize(new java.awt.Dimension(47, 30));

        lblTPos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTPos.setForeground(new java.awt.Color(255, 255, 255));
        lblTPos.setText("Threat Position:");

        txtThreatPosition.setEditable(false);
        txtThreatPosition.setBackground(new java.awt.Color(27, 161, 226));
        txtThreatPosition.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtThreatPosition.setForeground(new java.awt.Color(255, 255, 255));
        txtThreatPosition.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtThreatPosition.setPreferredSize(new java.awt.Dimension(69, 30));

        txtThreatDistance.setEditable(false);
        txtThreatDistance.setBackground(new java.awt.Color(27, 161, 226));
        txtThreatDistance.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
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
        cmbAssignmentMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Manual" }));
        cmbAssignmentMode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 161, 226)));
        cmbAssignmentMode.setPreferredSize(new java.awt.Dimension(75, 30));

        cmbDetonateMode.setBackground(new java.awt.Color(65, 65, 65));
        cmbDetonateMode.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbDetonateMode.setForeground(new java.awt.Color(65, 65, 65));
        cmbDetonateMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Manual" }));
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
        tglDisable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglDisableActionPerformed(evt);
            }
        });

        btnDestruct.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnDestruct.setText("Destruct");
        btnDestruct.setEnabled(false);
        btnDestruct.setPreferredSize(new java.awt.Dimension(127, 30));
        btnDestruct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDestructActionPerformed(evt);
            }
        });

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
        btnLaunch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLaunchActionPerformed(evt);
            }
        });

        lblLaunchTime.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLaunchTime.setForeground(new java.awt.Color(255, 255, 255));
        lblLaunchTime.setText("Time of Launch:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNewWindow)
                        .addGap(8, 8, 8))
                    .addGroup(layout.createSequentialGroup()
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblDetOvrrd)
                        .addGap(33, 33, 33)
                        .addComponent(cmbDetonateMode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
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
                        .addGap(9, 9, 9))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblLaunchTime)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(lblLaunchTime)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewWindowMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNewWindowMousePressed
        SMCDWrapper tmpWindow;

        for(int i = 0; i < windowList.size(); i++)
        {
            if(windowList.get(i)==null)
            {
                windowList.remove(i);
            }
        }
        
        if(windowList.size() < 15)
        {
            windowList.add(new SMCDWrapper(String.valueOf(cmbSelInterceptor.getSelectedItem()), this, mModel, mParent));
            tmpWindow = windowList.get(windowList.size()-1);
            tmpWindow.getContentPane().setBackground(new java.awt.Color(65,65,65));
            tmpWindow.setVisible(true);
        }
        else
        {
            JOptionPane.showMessageDialog(null,  "\nThe maximum amount of external SMCD windows have been opened.\n" +
                                                    "Close or re-assign unused, existing windows.\n", 
                                                    "Max Amount of SMCD Opened", 
                                                    JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnNewWindowMousePressed

    private void btnDetonateMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDetonateMousePressed
         mParent.forwardDetonate((String)cmbSelInterceptor.getSelectedItem());       
        
    }//GEN-LAST:event_btnDetonateMousePressed

    private void tglDisableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglDisableActionPerformed
        boolean isEnabled = tglDisable.isSelected();
        Interceptor tmpI = mModel.getInterceptor((String)cmbSelInterceptor.getSelectedItem());
        
        if(!isEnabled)
        {
            tmpI.setAssignedThreat("[UNASSIGNED]");
            tglDisable.setText("Disabled");
            tglDisable.setSelected(false);
            tmpI.setDisabled(true);
        }
        else
        {
            tglDisable.setText("Enabled");
            tglDisable.setSelected(true);
            tmpI.setDisabled(false);
        }
        
        
        
    }//GEN-LAST:event_tglDisableActionPerformed

    private void btnLaunchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaunchActionPerformed
        Timestamp tmp = new Timestamp(System.currentTimeMillis());
        Interceptor tmpI = mModel.getInterceptor((String)cmbSelInterceptor.getSelectedItem());
        
        if(!tmpI.getAssignedThreat().equals("[UNASSIGNED]"))
        {
            lblLaunchTime.setVisible(true);
            lblLaunchTime.setText("Time of Launch:  " + tmp.toString());

            tmpI.setTimestamp(tmp);
            mParent.forwardLaunchCmd(tmpI.getIdentifier());
        }
        else
        {
            JOptionPane.showMessageDialog(null,  "\nUnable to launch interceptor.  Please assign\n" +
                                                    "a threat to target.\n", 
                                                    "Launch Failure", 
                                                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnLaunchActionPerformed

    private void btnDestructActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDestructActionPerformed
        mParent.forwardDestruct((String)cmbSelInterceptor.getSelectedItem());
        
        
    }//GEN-LAST:event_btnDestructActionPerformed

    private void handleInterceptorSelection(ItemEvent evt)
    {
        if(!bSeparateWindow && mParent != null)
        {
            JComboBox dropdown = (JComboBox) evt.getSource();
            mParent.ChangeIOverviewSel((String)dropdown.getSelectedItem());
        }
    }
    
    private void handleAssignModeSelection(ItemEvent evt)
    {
        if(mModel != null && bIsActive)
        {
            JComboBox dropdown = (JComboBox) evt.getSource();
            Interceptor tmpI = mModel.getInterceptor((String)cmbSelInterceptor.getSelectedItem());
            

                tmpI.setAssignmentOverriden((dropdown.getSelectedIndex() != 0));
        }
    }
    
    private void handleDetModeSelection(ItemEvent evt)
    {
        if(mModel != null && bIsActive)
        {
            JComboBox dropdown = (JComboBox) evt.getSource();
            Interceptor tmpI = mModel.getInterceptor((String)cmbSelInterceptor.getSelectedItem());
            
            tmpI.setDetonateOverride((dropdown.getSelectedIndex() != 0));
        }
    }
    
    
    private void handleAssignedThreatSelection(ItemEvent evt)
    {
       JComboBox dropdown = (JComboBox) evt.getSource(); 
       ArrayList<String> assignedThreats;

       
       if(mModel != null && bIsActive)
       {
           assignedThreats = mModel.getAssignedThreats();
           Interceptor tmpI = mModel.getInterceptor((String)cmbSelInterceptor.getSelectedItem());
           
        if(!tmpI.getAssignedThreat().equals((String)dropdown.getSelectedItem()))
        {
           if(assignedThreats.contains((String)dropdown.getSelectedItem()))
           {
               JOptionPane.showMessageDialog(null,  "\nThe slected threat has already been assigned an interceptor.\n" +
                                                    "To reassign the threat to this interceptor, the threat must\n" +
                                                    "first be unassigned from the existing interceptor.\n", 
                                                    "Threat Already Assigned", 
                                                    JOptionPane.WARNING_MESSAGE);
           }
           else
           {
               
                tmpI.setAssignedThreat((String)dropdown.getSelectedItem());
           }
        }
       }
    }
    
    public void hideExpandControls()
    {
        btnNewWindow.setVisible(false);
        bSeparateWindow = true;
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
    private javax.swing.JLabel lblLaunchTime;
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
