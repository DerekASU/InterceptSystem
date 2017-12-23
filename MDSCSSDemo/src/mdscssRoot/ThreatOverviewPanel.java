/*******************************************************************************
 * File: ThreatOverviewPanel.java
 * Description:
 *
 ******************************************************************************/
package mdscssRoot;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import mdscssModel.Interceptor;

public class ThreatOverviewPanel extends javax.swing.JPanel 
{
    TableRowSorter<TableModel> sorter;
    
    /***************************************************************************
     * ThreatOverviewPanel
     * 
     * Constructor
     **************************************************************************/
    public ThreatOverviewPanel() 
    {
        initComponents();
        
        tableScrollPane.getViewport().setBackground(new java.awt.Color(27,166,226));
        tblThreats.getTableHeader().setBackground(new java.awt.Color(27,166,226));
        
        ((DefaultTableCellRenderer)tblThreats.getTableHeader().getDefaultRenderer())
    .setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer cellRend = new DefaultTableCellRenderer();
        cellRend.setHorizontalAlignment(JLabel.CENTER);
        tblThreats.getColumnModel().getColumn(0).setCellRenderer(cellRend);
        tblThreats.getColumnModel().getColumn(1).setCellRenderer(cellRend);
        tblThreats.getColumnModel().getColumn(2).setCellRenderer(cellRend);
        
        tblThreats.getColumnModel().getColumn(0).setMaxWidth(120);
        tblThreats.getColumnModel().getColumn(0).setMinWidth(120);
        tblThreats.getColumnModel().getColumn(0).setPreferredWidth(120);
        
        tblThreats.getColumnModel().getColumn(1).setMaxWidth(120);
        tblThreats.getColumnModel().getColumn(1).setMinWidth(120);
        tblThreats.getColumnModel().getColumn(1).setPreferredWidth(120);
        
        
        sorter = new TableRowSorter<TableModel>(tblThreats.getModel());
        sorter.setSortable(0, false);
        sorter.setSortable(1, false);
        sorter.setSortable(2, false);
        tblThreats.setRowSorter(sorter);
    }
    
    public void resetView()
    {
        DefaultTableModel model = (DefaultTableModel)tblThreats.getModel();
        int tmp = model.getRowCount();
        
        for(int i = 0; i < tmp; i++)
        {
            model.removeRow(0);
        }
    }
    
    public void handleInitialUpdate()
    {
        tblThreats.setVisible(true);
        
    }
    
    public void addEntry(String pID, int[] pPos)
    {
        String[] rowData = new String[4];
        DefaultTableModel model = (DefaultTableModel)tblThreats.getModel();

        rowData[0] = pID;
        rowData[1] = "[UNASSIGNED]";
        rowData[2] = "[" + pPos[0] + ", " + pPos[1] + ", " + pPos[2] + "]";
         
        model.addRow(rowData);
    }
    

    public void removeEntry(String pID)
    {
        String entryID = "";
        DefaultTableModel model = (DefaultTableModel)tblThreats.getModel();
        
        for(int index = 0; index < model.getRowCount(); index++)
        {
            entryID = (String)model.getValueAt(index, 0);
            if(entryID.equals(pID))
            {
                model.removeRow(index);
            }
        }
    }
    
    public void updateEntry(String pID, String assignedI, int[] pPos)
    {
        String entryID = "";
        int index = 0;
        int tmp;
        boolean resort = false;
        DefaultTableModel model = (DefaultTableModel)tblThreats.getModel();
        
        for(index = 0; index < model.getRowCount(); index++)
        {
            entryID = (String)model.getValueAt(index, 0);
            if(entryID.equals(pID))
            {
                if(!assignedI.equals((String)model.getValueAt(index, 1)))
                {
                    model.setValueAt(assignedI, index, 1);
                    resort = true;
                }
                
                
                model.setValueAt( "[" + pPos[0] + ", " + pPos[1] + ", " + pPos[2] + "] ", index, 2);
                break;
            }
        }
        

        if(resort)
        sortThreats();
        
        
        
        for(index = 0; index < model.getRowCount(); index++)
        {
            tmp = tblThreats.convertRowIndexToView(index);
            
            if(model.getValueAt(index, 1).equals("[UNASSIGNED]"))
                {
                    tblThreats.addRowSelectionInterval(tmp,tmp);
                }
            else
            {
                tblThreats.removeRowSelectionInterval(tmp, tmp);
                
            }
        }
    }
    
    public void sortThreats()
    {
        
        sorter.setModel(tblThreats.getModel());
        
        ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>(25);

        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        
        
        
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
        tableScrollPane = new javax.swing.JScrollPane();
        tblThreats = new javax.swing.JTable();

        setBackground(new java.awt.Color(27, 166, 226));
        setMaximumSize(new java.awt.Dimension(577, 299));
        setMinimumSize(new java.awt.Dimension(577, 299));
        setPreferredSize(new java.awt.Dimension(577, 299));

        lblTitle.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
        lblTitle.setText("Threat Overview:");

        tableScrollPane.setBackground(new java.awt.Color(255, 204, 204));
        tableScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 166, 226)));
        tableScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tableScrollPane.setOpaque(false);

        tblThreats.setBackground(java.awt.SystemColor.controlHighlight);
        tblThreats.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 166, 226)));
        tblThreats.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tblThreats.setForeground(new java.awt.Color(65, 65, 65));
        tblThreats.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Threat ID", "Assignment State", "Position"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblThreats.setEnabled(false);
        tblThreats.setFocusable(false);
        tblThreats.setGridColor(new java.awt.Color(27, 166, 226));
        tblThreats.setIntercellSpacing(new java.awt.Dimension(5, 15));
        tblThreats.setOpaque(false);
        tblThreats.setRowHeight(45);
        tblThreats.setSelectionBackground(new java.awt.Color(255, 153, 153));
        tblThreats.setSelectionForeground(new java.awt.Color(0, 0, 0));
        tblThreats.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblThreats.getTableHeader().setResizingAllowed(false);
        tblThreats.getTableHeader().setReorderingAllowed(false);
        tableScrollPane.setViewportView(tblThreats);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JTable tblThreats;
    // End of variables declaration//GEN-END:variables
}
