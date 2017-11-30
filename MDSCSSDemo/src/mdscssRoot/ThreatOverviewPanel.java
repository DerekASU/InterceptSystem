/*******************************************************************************
 * File: ThreatOverviewPanel.java
 * Description:
 *
 ******************************************************************************/
package mdscssRoot;

public class ThreatOverviewPanel extends javax.swing.JPanel 
{

    /***************************************************************************
     * ThreatOverviewPanel
     * 
     * Constructor
     **************************************************************************/
    public ThreatOverviewPanel() 
    {
        initComponents();
        
        tableScrollPane.getViewport().setBackground(new java.awt.Color(27,161,226));
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

        setBackground(new java.awt.Color(27, 161, 226));
        setMaximumSize(new java.awt.Dimension(577, 299));
        setMinimumSize(new java.awt.Dimension(577, 299));
        setPreferredSize(new java.awt.Dimension(577, 299));

        lblTitle.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Threat Overview:");

        tableScrollPane.setBackground(new java.awt.Color(255, 204, 204));
        tableScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 161, 226)));
        tableScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tableScrollPane.setOpaque(false);

        tblThreats.setBackground(java.awt.SystemColor.controlHighlight);
        tblThreats.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(27, 161, 226)));
        tblThreats.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tblThreats.setForeground(new java.awt.Color(65, 65, 65));
        tblThreats.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Threat ID", "Threat State", "Position (m)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblThreats.setGridColor(new java.awt.Color(27, 161, 226));
        tblThreats.setIntercellSpacing(new java.awt.Dimension(5, 15));
        tblThreats.setOpaque(false);
        tblThreats.setRowHeight(45);
        tblThreats.setSelectionBackground(new java.awt.Color(27, 161, 226));
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
                    .addComponent(tableScrollPane))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JTable tblThreats;
    // End of variables declaration//GEN-END:variables
}
