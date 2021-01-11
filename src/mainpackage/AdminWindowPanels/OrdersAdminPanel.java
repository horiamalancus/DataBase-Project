/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpackage.AdminWindowPanels;

import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import mainpackage.ApplicationWindow;

/**
 *
 * @author cosmi
 */
public class OrdersAdminPanel extends javax.swing.JPanel {

    /**
     * Creates new form OrdersAdminPanel
     */
    private final ApplicationWindow appWindow;
    private TableRowSorter<DefaultTableModel> tr;
    private List<RowSorter.SortKey> sortKeys;

    public OrdersAdminPanel(ApplicationWindow appWindow) {
        this.appWindow = appWindow;
        initComponents();
        initFilter();

        initActionListeners();
    }

    private void initActionListeners() {
        showButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        deleteButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        showButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
    }

    private void initFilter() {
        tr = new TableRowSorter<>((DefaultTableModel) dataTable.getModel());
        dataTable.setRowSorter(tr);

        sortKeys = new ArrayList<>();
        for (int i = 0; i < dataTable.getColumnCount(); ++i) {
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.UNSORTED));
        }
    }

    public void startFilter() {
        tr.setRowFilter(RowFilter.regexFilter(filterTextField.getText()));
    }

    public void startAction(ActionEvent e) {
        JButton tmpEventButton = (JButton) e.getSource();

        Connection conn = appWindow.getDataBaseConnection().getConnection();

        DefaultTableModel tblModel;
        String id_comanda;
        String nr_masa;

        switch (tmpEventButton.getText()) {

            /////////////// STERGEREA DIN BAZA DE DATE ///////////////
            case "Sterge":

                tblModel = (DefaultTableModel) dataTable.getModel();

                if (dataTable.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Tabelul este gol.");
                } else if (dataTable.getSelectedRowCount() == 1) {

                    id_comanda = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0).toString();
                    nr_masa = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 2).toString();

                    try {
                        PreparedStatement p = conn.prepareStatement(
                                "SELECT c.id_comanda\n"
                                + "FROM Comenzi c, produse_comenzi pc, Produse p\n"
                                + "WHERE c.id_comanda = pc.Comenzi_id_comanda and c.id_comanda = (SELECT MAX(id_comanda) FROM Comenzi WHERE nr_masa = ?) and pc.Produse_nr_produs = p.nr_produs"
                        );
                        
                        p.setShort(1, Short.parseShort(nr_masa));
                        ResultSet r = p.executeQuery(); r.next();
                        int id_c = r.getInt(1);
                        
                        if(id_c != Integer.parseInt(id_comanda))
                        {
                            throw new Exception("Nu puteti sterge comenzi anterioare.");
                        }
                        
                        
                    } catch (SQLException ex) {
                        Logger.getLogger(OrdersAdminPanel.class.getName()).log(Level.SEVERE, null, ex);
                        break;
                    }
                    catch (Exception ex2)
                    {
                        JOptionPane.showMessageDialog(this, ex2.getMessage());
                        break;
                    }

                    try {

                        PreparedStatement ps = conn.prepareStatement("SELECT produse_nr_produs FROM produse_comenzi WHERE Comenzi_id_comanda = ?");
                        ps.setInt(1, Integer.parseInt(id_comanda));
                        ResultSet rs = ps.executeQuery();

                        String query
                                = "DECLARE\n"
                                + "    id_comanda_plasata Comenzi.id_comanda%TYPE;\n"
                                + "    nr_produs_comanda Produse.nr_produs%TYPE;\n"
                                + "    produse_comandate produse_comenzi.nr_produse_comandate%TYPE;\n"
                                + "    \n"
                                + "    produs_in_reteta Ingrediente.id_ingredient%TYPE;\n"
                                + "    produs_in_stoc stocuri_produs.stoc_produs%TYPE;\n"
                                + "BEGIN\n"
                                + "\n"
                                + "    SAVEPOINT sp;\n"
                                + "\n"
                                + "    id_comanda_plasata := '" + id_comanda + "';\n"
                                + "\n";

                        while (rs.next()) {

                            query += "    BEGIN\n"
                                    + "\n"
                                    + "        nr_produs_comanda := " + rs.getString(1) + ";\n"
                                    + "        SELECT nr_produse_comandate INTO produse_comandate FROM produse_comenzi WHERE Comenzi_id_comanda = id_comanda_plasata AND Produse_nr_produs = nr_produs_comanda;\n"
                                    + "\n"
                                    + "        SELECT COUNT(Produse_nr_produs) INTO produs_in_reteta FROM Retete r WHERE r.Produse_nr_produs = nr_produs_comanda;\n"
                                    + "        SELECT COUNT(Produse_nr_produs) INTO produs_in_stoc FROM stocuri_produs sp WHERE sp.Produse_nr_produs = nr_produs_comanda;\n"
                                    + "\n"
                                    + "        IF (produs_in_reteta > 0) THEN\n"
                                    + "            UPDATE Ingrediente i\n"
                                    + "            SET stoc_ingredient = stoc_ingredient + produse_comandate * (SELECT r.cantitate_ingredient FROM Retete r WHERE r.Produse_nr_produs = nr_produs_comanda and r.Ingrediente_id_ingredient = i.id_ingredient)\n"
                                    + "            WHERE EXISTS (SELECT 1 FROM Retete r WHERE Produse_nr_produs = nr_produs_comanda and r.Ingrediente_id_ingredient = i.id_ingredient);\n"
                                    + "        ELSIF (produs_in_stoc > 0) THEN\n"
                                    + "            UPDATE stocuri_produs sp\n"
                                    + "            SET stoc_produs = stoc_produs + produse_comandate\n"
                                    + "            WHERE sp.Produse_nr_produs = nr_produs_comanda;\n"
                                    + "        END IF;\n"
                                    + "    END;\n";

                        }
                        query += "    \n"
                                + "    DELETE FROM Comenzi WHERE id_comanda = id_comanda_plasata;\n"
                                + "    COMMIT;\n"
                                + "\n"
                                + "    EXCEPTION\n"
                                + "        WHEN OTHERS THEN\n"
                                + "            ROLLBACK TO sp;\n"
                                + "            RAISE;\n"
                                + "\n"
                                + "END;";

                        conn.createStatement().execute(query);

                        tblModel.removeRow(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()));
                        conn.createStatement().execute("commit");
                        JOptionPane.showMessageDialog(this, "Comanda stearsa cu succes");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Comanda nu s-a putut sterge: " + ex.getMessage());
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Selecteaza un singur rand pentru a sterge.");
                }
                break;

            /////////////// AFISARE/REFRESH JTABLE ///////////////
            case "Refresh":
                Refresh();
                break;

        }
    }

    private void Refresh() {
        filterTextField.setText("");
        startFilter();
        tr.setSortKeys(sortKeys);

        String id_comanda;
        String total_plata;
        String detalii_suplimentare;
        String nr_masa;
        String data_si_ora_comanda;

        try {
            ResultSet rs = appWindow.getDataBaseConnection().getConnection().createStatement().executeQuery(
                    "SELECT c.id_comanda, SUM(p.pret * pc.nr_produse_comandate) as total_plata, c.nr_masa,  to_char(c.detalii_suplimentare_comanda) as detalii_comanda, (SELECT to_char(c.data_comanda,'DD-MON-YYYY HH24:MI:SS') from dual) as data_si_ora_comanda\n"
                    + "FROM Comenzi c, produse_comenzi pc, Produse p\n"
                    + "WHERE c.id_comanda = pc.Comenzi_id_comanda and pc.Produse_nr_produs = p.nr_produs\n"
                    + "GROUP BY c.id_comanda, c.nr_masa, to_char(c.detalii_suplimentare_comanda), c.data_comanda\n"
                    + "ORDER BY c.id_comanda"
            );

            DefaultTableModel tblModel = (DefaultTableModel) dataTable.getModel();
            tblModel.setRowCount(0);

            while (rs.next()) {
                id_comanda = rs.getString(1);
                total_plata = rs.getString(2);
                nr_masa = rs.getString(3);
                detalii_suplimentare = rs.getString(4);
                data_si_ora_comanda = rs.getString(5);

                Object tblData[] = {Integer.parseInt(id_comanda), Float.parseFloat(total_plata), Short.parseShort(nr_masa), detalii_suplimentare, data_si_ora_comanda};
                tblModel = (DefaultTableModel) this.dataTable.getModel();
                tblModel.addRow(tblData);
            }

        } catch (SQLException ex) {
            Logger.getLogger(MenusAdminPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPanel = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        bottomPanel = new javax.swing.JPanel();
        buttonsPanel = new javax.swing.JPanel();
        deleteButton = new javax.swing.JButton();
        showButton = new javax.swing.JButton();
        insertUpdatePanel = new javax.swing.JPanel();
        filterTextField = new javax.swing.JTextField();
        filterLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1065, 718));

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id_comanda", "total_plata_lei", "nr_masa", "detalii_suplimentare", "data_comanda"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Short.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dataTable.getTableHeader().setReorderingAllowed(false);
        scrollPanel.setViewportView(dataTable);

        buttonsPanel.setLayout(new java.awt.GridBagLayout());

        deleteButton.setText("Sterge");
        deleteButton.setActionCommand("ButoaneComenziAdmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 63;
        gridBagConstraints.ipady = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 13, 14);
        buttonsPanel.add(deleteButton, gridBagConstraints);

        showButton.setText("Refresh");
        showButton.setActionCommand("ButoaneComenziAdmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 63;
        gridBagConstraints.ipady = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 13, 14);
        buttonsPanel.add(showButton, gridBagConstraints);

        javax.swing.GroupLayout insertUpdatePanelLayout = new javax.swing.GroupLayout(insertUpdatePanel);
        insertUpdatePanel.setLayout(insertUpdatePanelLayout);
        insertUpdatePanelLayout.setHorizontalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        insertUpdatePanelLayout.setVerticalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 351, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(insertUpdatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(insertUpdatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(bottomPanelLayout.createSequentialGroup()
                        .addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        filterTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTextFieldKeyReleased(evt);
            }
        });

        filterLabel.setText("Filter:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPanel)
            .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(filterLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 1045, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(scrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void filterTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterTextFieldKeyReleased
        startFilter();
    }//GEN-LAST:event_filterTextFieldKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JPanel insertUpdatePanel;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JButton showButton;
    // End of variables declaration//GEN-END:variables
}
