/*******************************************************************************
 * File: InterceptorOverviewPanel.java
 * Description:
 *
 ******************************************************************************/
package mdscssRoot;

import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import mdscssModel.Interceptor;


public class InterceptorOverviewPanel extends javax.swing.JPanel 
{
    MMODFrame mParent;
    /***************************************************************************
     * InterceptorOverviewPanel
     * 
     * Constructor
     **************************************************************************/
    public InterceptorOverviewPanel() 
    {
        initComponents();
        
        mParent = null;
        
        tableScrollPane.getViewport().setBackground(new java.awt.Color(27,161,226));
        
        DefaultTableCellRenderer cellRend = new DefaultTableCellRenderer();
        cellRend.setHorizontalAlignment(JLabel.CENTER);
        tblInterceptors.getColumnModel().getColumn(0).setCellRenderer(cellRend);
        tblInterceptors.getColumnModel().getColumn(1).setCellRenderer(cellRend);
        tblInterceptors.getColumnModel().getColumn(2).setCellRenderer(cellRend);
        tblInterceptors.getColumnModel().getColumn(3).setCellRenderer(cellRend);
        
        //todo:: move to initcomponents when no longer using gui builder
        tblInterceptors.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent evt){
                tblInterceptorsRowSelected(evt);
            }
        });
        
    }
    
    public void resetView()
    {
        
        System.out.println("IN RESET");
        
        DefaultTableModel model = (DefaultTableModel)tblInterceptors.getModel();
        int tmp = model.getRowCount();
        
        for(int i = 0; i < tmp; i++)
        {
            model.removeRow(0);
        }

    }
    
    public void handleInitialUpdate()
    {
        tblInterceptors.setVisible(true);
        
    }
    
    public void addEntry(String pID, Interceptor.interceptorState pState, String pThreat, int[] pPos)
    {
        String[] rowData = new String[4];
        DefaultTableModel model = (DefaultTableModel)tblInterceptors.getModel();
        
        switch (pState) {
            case DETONATED:
                rowData[1] = "Detonated";
                break;
            case PRE_FLIGHT:
                rowData[1] = "Pre-Flight";
                break;
            case IN_FLIGHT:
                rowData[1] = "In-Flight";
                break;
            default:
                rowData[1] = "[UNKNOWN]";
                break;
        }
        
        rowData[0] = pID;
        rowData[2] = pThreat;
        rowData[3] = "[" + pPos[0] + "," + pPos[1] + "," + pPos[2] + "]";
         
        model.addRow(rowData);
    }
    
    public void updateEntry(String pID, Interceptor.interceptorState pState, String assignment, int[] pPos)
    {
        String entryID = "";
        DefaultTableModel model = (DefaultTableModel)tblInterceptors.getModel();
        
        for(int i = 0; i < model.getRowCount(); i++)
        {
            entryID = (String)model.getValueAt(i, 0);
            if(entryID.equals(pID))
            {
                switch (pState) {
            case DETONATED:
                model.setValueAt("Detonated", i, 1);
                break;
            case PRE_FLIGHT:
                model.setValueAt("Pre-Flight", i, 1);
                break;
            case IN_FLIGHT:
                model.setValueAt("In-Flight", i, 1);
                break;
            default:
                model.setValueAt("[UNKNOWN]", i, 1);
                break;
        }
                
                model.setValueAt(assignment, i, 2);
                model.setValueAt( "[" + pPos[0] + "," + pPos[1] + "," + pPos[2] + "]", i, 3);
                break;
            }
        }
    }
    
    
    
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
    
    private void tblInterceptorsRowSelected(ListSelectionEvent evt)
    {
        if(evt.getValueIsAdjusting() && mParent != null)
        {
            int index = tblInterceptors.getSelectedRow();
            String id = (String)(tblInterceptors.getModel().getValueAt(index, 0));
            mParent.ChangeSMCDSel(id);
        }
    }
    
    public void setParent(MMODFrame pParent)
    {
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

        lblTable = new javax.swing.JLabel();
        tableScrollPane = new javax.swing.JScrollPane();
        tblInterceptors = new javax.swing.JTable();

        setBackground(new java.awt.Color(27, 161, 226));
        setMaximumSize(new java.awt.Dimension(622, 299));
        setMinimumSize(new java.awt.Dimension(622, 299));
        setPreferredSize(new java.awt.Dimension(622, 299));

        lblTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTable.setForeground(new java.awt.Color(255, 255, 255));
        lblTable.setText("Interceptor Overview:");

        tableScrollPane.setBackground(new java.awt.Color(255, 204, 204));
        tableScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 161, 226)));
        tableScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tableScrollPane.setOpaque(false);

        tblInterceptors.setBackground(java.awt.SystemColor.controlHighlight);
        tblInterceptors.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 161, 226)));
        tblInterceptors.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tblInterceptors.setForeground(new java.awt.Color(65, 65, 65));
        tblInterceptors.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Interceptor ID", "Interceptor State", "Assigned Threat", "Position (m)"
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
        tblInterceptors.setGridColor(new java.awt.Color(27, 161, 226));
        tblInterceptors.setIntercellSpacing(new java.awt.Dimension(5, 15));
        tblInterceptors.setOpaque(false);
        tblInterceptors.setRowHeight(45);
        tblInterceptors.setSelectionBackground(new java.awt.Color(27, 161, 226));
        tblInterceptors.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblInterceptors.getTableHeader().setResizingAllowed(false);
        tblInterceptors.getTableHeader().setReorderingAllowed(false);
        tableScrollPane.setViewportView(tblInterceptors);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTable)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTable;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JTable tblInterceptors;
    // End of variables declaration//GEN-END:variables
}
