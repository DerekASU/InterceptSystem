package mdscssRoot;

import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import mdscssModel.Interceptor;

/*******************************************************************************
 * The InterceptorOverviewPanel Class populates and controls the list of interceptors
 * presented to the operator.
 ******************************************************************************/
public class InterceptorOverviewPanel extends javax.swing.JPanel 
{
    MMODFrame mParent;
    
    /***************************************************************************
     * Constructor
     **************************************************************************/
    public InterceptorOverviewPanel() 
    {
        initComponents();
        
        mParent = null;
        
        tableScrollPane.getViewport().setBackground(new java.awt.Color(27,166,226));
        tblInterceptors.getTableHeader().setBackground(new java.awt.Color(27,166,226));
        
        ((DefaultTableCellRenderer)tblInterceptors.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer cellRend = new DefaultTableCellRenderer();
        cellRend.setHorizontalAlignment(JLabel.CENTER);
        tblInterceptors.getColumnModel().getColumn(0).setCellRenderer(cellRend);
        tblInterceptors.getColumnModel().getColumn(1).setCellRenderer(cellRend);
        tblInterceptors.getColumnModel().getColumn(2).setCellRenderer(cellRend);
        tblInterceptors.getColumnModel().getColumn(3).setCellRenderer(cellRend);
        
        tblInterceptors.getColumnModel().getColumn(0).setMaxWidth(120);
        tblInterceptors.getColumnModel().getColumn(0).setMinWidth(120);
        tblInterceptors.getColumnModel().getColumn(0).setPreferredWidth(120);
        
        tblInterceptors.getColumnModel().getColumn(1).setMaxWidth(120);
        tblInterceptors.getColumnModel().getColumn(1).setMinWidth(120);
        tblInterceptors.getColumnModel().getColumn(1).setPreferredWidth(120);
        
        tblInterceptors.getColumnModel().getColumn(2).setMaxWidth(120);
        tblInterceptors.getColumnModel().getColumn(2).setMinWidth(120);
        tblInterceptors.getColumnModel().getColumn(2).setPreferredWidth(120);
        
        tblInterceptors.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent evt){
                tblInterceptorsRowSelected(evt);
            }
        });
        
    }
    
    /***************************************************************************
     * The resetView function is called when the MDSCSS looses connection to the subsystems. This function
     * clears the table and disables the destruct button.
     **************************************************************************/
    public void resetView()
    {
        DefaultTableModel model = (DefaultTableModel)tblInterceptors.getModel();
        int tmp = model.getRowCount();
        
        for(int i = 0; i < tmp; i++)
        {
            model.removeRow(0);
        }
        
        btnDestruct.setEnabled(false);

    }
    
    /***************************************************************************
     * The handleInitialUpdate function is called when the MDSCSS achieves connection to the subsystems. This function
     * ensures that the table is visible and ready to be populated.
     **************************************************************************/
    public void handleInitialUpdate()
    {
        tblInterceptors.setVisible(true);
        
    }
    
    /***************************************************************************
     * The addEntry function is called by the MMODFrame's initial update function to populate the table
     * with interceptors.
     * 
     * @param pID The id of the interceptor
     * @param pState The state of the interceptor
     * @param pThreat The assigned threat of the interceptor
     * @param pPos An array of 3 integers representing the threats position
     * @param isDisabled The disabled state of the interceptor
     **************************************************************************/
    public void addEntry(String pID, Interceptor.interceptorState pState, String pThreat, int[] pPos, boolean isDisabled)
    {
        String[] rowData = new String[4];
        DefaultTableModel model = (DefaultTableModel)tblInterceptors.getModel();
        
        switch (pState) {
            case DETONATED:
                rowData[1] = "Destroyed";
                break;
            case PRE_FLIGHT:
                if(isDisabled)
                    rowData[1] = "Disabled";
                else
                    rowData[1] = "Ready";                  
                break;
            case IN_FLIGHT:
                rowData[1] = "Launched";
                break;
            default:
                rowData[1] = "[UNKNOWN]";
                break;
        }
        
        rowData[0] = pID;
        rowData[2] = pThreat;
        rowData[3] = "[" + pPos[0] + ", " + pPos[1] + ", " + pPos[2] + "]";
         
        model.addRow(rowData);
    }
    
    /***************************************************************************
     * The updateEntry function is called periodically to update the table  with the interceptor's latest 
     * attributes.
     * 
     * @param pID The id of the interceptor
     * @param pState The state of the interceptor
     * @param assignment The assigned threat of the interceptor
     * @param pPos An array of 3 integers representing the threats position
     * @param isDisabled The disabled state of the interceptor
     **************************************************************************/
    public void updateEntry(String pID, Interceptor.interceptorState pState, String assignment, int[] pPos, boolean isDisabled)
    {
        String entryID = "";
        boolean oneLaunched = false;
        DefaultTableModel model = (DefaultTableModel)tblInterceptors.getModel();
        
        for(int i = 0; i < model.getRowCount(); i++)
        {
            entryID = (String)model.getValueAt(i, 0);
            if(entryID.equals(pID))
            {
                switch (pState) {
                    case DETONATED:
                        model.setValueAt("Destroyed", i, 1);
                        break;
                    case PRE_FLIGHT:
                        if(isDisabled)
                            model.setValueAt("Disabled", i, 1);
                        else
                            model.setValueAt("Ready", i, 1);
                        break;
                    case IN_FLIGHT:
                        model.setValueAt("Launched", i, 1);
                        break;
                    default:
                        model.setValueAt("[UNKNOWN]", i, 1);
                        break;
                }
                
                model.setValueAt(assignment, i, 2);
                model.setValueAt( "[" + pPos[0] + ", " + pPos[1] + ", " + pPos[2] + "]", i, 3);
            }
            
            if (model.getValueAt(i, 1).equals("Launched"))
            {
                oneLaunched = true;
            }
        }
        
        if(oneLaunched)
        {
            btnDestruct.setEnabled(true);
        }
        else
        {
            btnDestruct.setEnabled(false);
        }
    }
    
    /***************************************************************************
     * The handleSelChange function is called when a new interceptor has been selected by the SMCD; makes it so
     * the new active interceptor selection has its corresponding row highlighted.
     * 
     * @param pID The new active interceptor to select
     **************************************************************************/
    public void handleSelChange(String pID)
    {
        DefaultTableModel model = (DefaultTableModel)tblInterceptors.getModel();
        
        for(int i = 0; i < model.getRowCount(); i++)
        {
            if (model.getValueAt(i, 0).equals(pID))
            {
                tblInterceptors.changeSelection(i, 0, false, false);
            }
        }
    }
    
    /***************************************************************************
     * The tblInterceptorsRowSelected is an event handler for when a row is selected in the table.  The function notifies the MMODFrame
     * so that it can tell the SMCD panel to update its selection.
     * 
     * @param evt The event object
     **************************************************************************/
    private void tblInterceptorsRowSelected(ListSelectionEvent evt)
    {
        if(evt.getValueIsAdjusting() && mParent != null)
        {
            int index = tblInterceptors.getSelectedRow();
            String id = (String)(tblInterceptors.getModel().getValueAt(index, 0));
            mParent.ChangeSMCDSel(id);
        }
    }
    
    /***************************************************************************
     * The setParent function is called by the MMODFrame to give the interceptor overview panel a reference
     * to the JFrame.
     * 
     * @param pParent The reference pointer to the MMODFrame
     **************************************************************************/
    public void setParent(MMODFrame pParent)
    {
        mParent = pParent;
    }

    /***************************************************************************
     * The initComponents function creates and draws the container's swing components.  Autogenerated by
     * Netbeans IDE GUI Editor.
     **************************************************************************/
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTable = new javax.swing.JLabel();
        tableScrollPane = new javax.swing.JScrollPane();
        tblInterceptors = new javax.swing.JTable();
        btnDestruct = new javax.swing.JButton();

        setBackground(new java.awt.Color(27, 166, 226));
        setMaximumSize(new java.awt.Dimension(622, 299));
        setMinimumSize(new java.awt.Dimension(622, 299));
        setPreferredSize(new java.awt.Dimension(622, 299));

        lblTable.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        lblTable.setText("Interceptor Overview:");

        tableScrollPane.setBackground(new java.awt.Color(255, 204, 204));
        tableScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 166, 226)));
        tableScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tableScrollPane.setOpaque(false);

        tblInterceptors.setBackground(java.awt.SystemColor.controlHighlight);
        tblInterceptors.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 166, 226)));
        tblInterceptors.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tblInterceptors.setForeground(new java.awt.Color(65, 65, 65));
        tblInterceptors.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Interceptor ID", "Interceptor State", "Assigned Threat", "Position"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInterceptors.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblInterceptors.setGridColor(new java.awt.Color(27, 166, 226));
        tblInterceptors.setIntercellSpacing(new java.awt.Dimension(5, 15));
        tblInterceptors.setOpaque(false);
        tblInterceptors.setRowHeight(45);
        tblInterceptors.setSelectionBackground(new java.awt.Color(27, 166, 226));
        tblInterceptors.setSelectionForeground(new java.awt.Color(0, 0, 0));
        tblInterceptors.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblInterceptors.getTableHeader().setResizingAllowed(false);
        tblInterceptors.getTableHeader().setReorderingAllowed(false);
        tableScrollPane.setViewportView(tblInterceptors);

        btnDestruct.setBackground(new java.awt.Color(27, 166, 226));
        btnDestruct.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnDestruct.setText("Emergency Destruct");
        btnDestruct.setEnabled(false);
        btnDestruct.setPreferredSize(new java.awt.Dimension(127, 30));
        btnDestruct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDestructActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 247, Short.MAX_VALUE)
                        .addComponent(btnDestruct, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTable)
                    .addComponent(btnDestruct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    /***************************************************************************
     * The btnDestructActionPerformed is an action handler for when the emergency destruct button is pressed.  The 
     * function forwards the destruct command the MMODFrame for each airborne interceptor 
     * 
     * @param evt The event object
     **************************************************************************/
    private void btnDestructActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDestructActionPerformed
        DefaultTableModel model = (DefaultTableModel)tblInterceptors.getModel();
        
        for(int i = 0; i < model.getRowCount(); i++)
        {
            if (model.getValueAt(i, 1).equals("Launched"))
            {
                mParent.forwardDestruct((String)model.getValueAt(i, 0));
            }
        }

    }//GEN-LAST:event_btnDestructActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDestruct;
    private javax.swing.JLabel lblTable;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JTable tblInterceptors;
    // End of variables declaration//GEN-END:variables
}
