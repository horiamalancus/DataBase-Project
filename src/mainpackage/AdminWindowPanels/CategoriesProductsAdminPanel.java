/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpackage.AdminWindowPanels;

/**
 *
 * @author cosmi
 */
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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

public class CategoriesProductsAdminPanel extends javax.swing.JPanel {

    /**
     * Creates new form CategoriesProductsAdminPanel
     */
    private final ApplicationWindow appWindow;
    private TableRowSorter<DefaultTableModel> tr;
    private List<RowSorter.SortKey> sortKeys;

    public CategoriesProductsAdminPanel(ApplicationWindow appWindow) {
        this.appWindow = appWindow;
        initComponents();
        initFilter();

        initActionListeners();
    }

    private void initActionListeners() {
        insertButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
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

    public void fillComboBoxes() {

        int idx = (numeMeniuCB.getItemCount() == -1) ? -1 : numeMeniuCB.getSelectedIndex();
        int idx2 = (numeCategorieCB.getItemCount() == -1) ? -1 : numeCategorieCB.getSelectedIndex();
        int idx3 = (numeProdusCB.getItemCount() == -1) ? -1 : numeProdusCB.getSelectedIndex();

        numeMeniuCB.removeAllItems();
        numeCategorieCB.removeAllItems();
        numeProdusCB.removeAllItems();

        try {
            ResultSet rs1 = appWindow.getDataBaseConnection().getConnection().createStatement().executeQuery("SELECT nume_meniu FROM Meniuri");
            ResultSet rs2 = appWindow.getDataBaseConnection().getConnection().createStatement().executeQuery("SELECT nume_produs FROM Produse");
            PreparedStatement ps = appWindow.getDataBaseConnection().getConnection().prepareStatement("SELECT nume_categorie FROM Categorii WHERE Meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = ?)");

            while (rs1.next()) {
                numeMeniuCB.addItem(rs1.getString(1));
            }

            while (rs2.next()) {
                numeProdusCB.addItem(rs2.getString(1));
            }

            if (numeMeniuCB.getItemCount() != 0) {
                numeMeniuCB.setSelectedIndex((idx == -1) ? 0 : idx);
                ps.setString(1, numeMeniuCB.getSelectedItem().toString());

                ResultSet rs3 = ps.executeQuery();

                while (rs3.next()) {
                    numeCategorieCB.addItem(rs3.getString(1));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(CategoriesAdminPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        numeMeniuCB.setSelectedIndex((numeMeniuCB.getItemCount() == 0) ? -1 : (idx == -1) ? 0 : idx);
        numeCategorieCB.setSelectedIndex((numeCategorieCB.getItemCount() == 0) ? -1 : (idx2 == -1) ? 0 : idx2);
        numeProdusCB.setSelectedIndex((numeProdusCB.getItemCount() == 0) ? -1 : (idx3 == -1) ? 0 : idx3);

    }

    public void startAction(ActionEvent e) {
        JButton tmpEventButton = (JButton) e.getSource();

        Connection conn = appWindow.getDataBaseConnection().getConnection();

        DefaultTableModel tblModel;
        String nume_categorie;
        String nume_meniu;
        String nume_produs;

        switch (tmpEventButton.getText()) {
            /////////////// INSERARE ///////////////
            case "Inserare":

                try {

                    ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM categorii_produse");
                    rs.next();
                    int nr = rs.getInt(1);

                    if (nr != 0 && dataTable.getModel().getRowCount() == 0) {
                        JOptionPane.showMessageDialog(this, "Mai intai faceti un refresh la baza de date.");
                        break;
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }

                tr.setSortKeys(sortKeys);

                tblModel = (DefaultTableModel) this.dataTable.getModel();

                nume_meniu = numeMeniuCB.getSelectedItem().toString();
                nume_categorie = numeCategorieCB.getSelectedItem().toString();
                nume_produs = numeProdusCB.getSelectedItem().toString();

                ////////////////////// Modificare baza de date //////////////////////
                try {
                    PreparedStatement prepSt = conn.prepareStatement("INSERT INTO categorii_produse(Categorii_nr_categorie, Produse_nr_produs) VALUES((SELECT nr_categorie FROM Categorii WHERE nume_categorie = ? and Meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = ?)), (SELECT nr_produs FROM Produse WHERE nume_produs = ?))");
                    prepSt.setString(1, nume_categorie);
                    prepSt.setString(2, nume_meniu);
                    prepSt.setString(3, nume_produs);
                    prepSt.execute();

                    Object tfData[] = {nume_categorie, nume_produs};
                    tblModel.addRow(tfData);

                    conn.createStatement().execute("commit");
                    JOptionPane.showMessageDialog(this, "Inserare efectuata cu succes");
                } catch (SQLException ex) {
                    if (ex.getMessage().contains("ORA-00001")) {
                        JOptionPane.showMessageDialog(this, "Produs deja existent in aceasta categorie");
                    } else {
                        JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                    }
                }
                Refresh();
                break;

            /////////////// STERGEREA DIN BAZA DE DATE ///////////////
            case "Sterge":

                tblModel = (DefaultTableModel) dataTable.getModel();

                if (dataTable.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Tabelul este gol.");
                } else if (dataTable.getSelectedRowCount() == 1) {

                    nume_categorie = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0).toString();
                    nume_produs = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 1).toString();
                    nume_meniu = numeMeniuCB.getSelectedItem().toString();

                    try {
                        PreparedStatement prepSt = conn.prepareStatement("DELETE FROM categorii_produse WHERE Categorii_nr_categorie = (SELECT nr_categorie FROM Categorii WHERE nume_categorie = ? and Meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = ?)) AND Produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?)");
                        prepSt.setString(1, nume_categorie);
                        prepSt.setString(2, nume_meniu);
                        prepSt.setString(3, nume_produs);
                        prepSt.execute();

                        tblModel.removeRow(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()));

                        conn.createStatement().execute("commit");
                        JOptionPane.showMessageDialog(this, "Stergere efectuata cu succes");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
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

    public void startFilter() {
        tr.setRowFilter(RowFilter.regexFilter(filterTextField.getText()));
    }

    private void Refresh() {
        filterTextField.setText("");
        startFilter();
        tr.setSortKeys(sortKeys);
        fillComboBoxes();

        String nume_meniu = numeMeniuCB.getSelectedItem().toString();

        try {
            PreparedStatement ps = appWindow.getDataBaseConnection().getConnection().prepareStatement("SELECT c.nume_categorie, p.nume_produs FROM Categorii c, Produse p, categorii_produse cp, Meniuri m WHERE m.nume_meniu = ? AND m.nr_meniu = c.Meniuri_nr_meniu AND c.nr_categorie = cp.categorii_nr_categorie AND cp.produse_nr_produs = p.nr_produs");
            ps.setString(1, nume_meniu);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel tblModel = (DefaultTableModel) dataTable.getModel();
            tblModel.setRowCount(0);

            while (rs.next()) {
                String nume_categorie = rs.getString("nume_categorie");
                String nume_produs = rs.getString("nume_produs");

                Object tblData[] = {nume_categorie, nume_produs};
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
        numeMeniuLabel = new javax.swing.JLabel();
        numeCategorieLabel = new javax.swing.JLabel();
        numeProdusLabel = new javax.swing.JLabel();
        insertButton = new javax.swing.JButton();
        numeMeniuCB = new javax.swing.JComboBox<>();
        numeCategorieCB = new javax.swing.JComboBox<>();
        numeProdusCB = new javax.swing.JComboBox<>();
        filterTextField = new javax.swing.JTextField();
        filterLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1065, 718));

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "nume_categorie", "nume_produs"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dataTable.getTableHeader().setReorderingAllowed(false);
        dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dataTableMouseClicked(evt);
            }
        });
        dataTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dataTableKeyReleased(evt);
            }
        });
        scrollPanel.setViewportView(dataTable);

        buttonsPanel.setLayout(new java.awt.GridBagLayout());

        deleteButton.setText("Sterge");
        deleteButton.setActionCommand("ButoaneCategoriiProduseAdmin");
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
        showButton.setActionCommand("ButoaneCategoriiProduseAdmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 63;
        gridBagConstraints.ipady = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 13, 14);
        buttonsPanel.add(showButton, gridBagConstraints);

        numeMeniuLabel.setText("nume_meniu:");

        numeCategorieLabel.setText("nume_categorie:");

        numeProdusLabel.setText("nume_produs");

        insertButton.setText("Inserare");
        insertButton.setActionCommand("ButoaneCategoriiProduseAdmin");

        javax.swing.GroupLayout insertUpdatePanelLayout = new javax.swing.GroupLayout(insertUpdatePanel);
        insertUpdatePanel.setLayout(insertUpdatePanelLayout);
        insertUpdatePanelLayout.setHorizontalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numeMeniuLabel)
                    .addComponent(numeCategorieLabel)
                    .addComponent(numeProdusLabel))
                .addGap(30, 30, 30)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numeProdusCB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(numeMeniuCB, 0, 237, Short.MAX_VALUE)
                        .addComponent(numeCategorieCB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(87, 87, 87))
        );
        insertUpdatePanelLayout.setVerticalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numeMeniuLabel)
                    .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numeMeniuCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numeCategorieLabel)
                    .addComponent(numeCategorieCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numeProdusCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numeProdusLabel))
                .addContainerGap(89, Short.MAX_VALUE))
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
                        .addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
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

    private void dataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataTableMouseClicked

        String nume_categorie = dataTable.getValueAt(dataTable.getSelectedRow(), 0).toString();
        String nume_produs = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();

        numeCategorieCB.setSelectedItem(nume_categorie);
        numeProdusCB.setSelectedItem(nume_produs);
    }//GEN-LAST:event_dataTableMouseClicked

    private void dataTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataTableKeyReleased

        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            String nume_categorie = dataTable.getValueAt(dataTable.getSelectedRow(), 0).toString();
            String nume_produs = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();

            numeCategorieCB.setSelectedItem(nume_categorie);
            numeProdusCB.setSelectedItem(nume_produs);
        }
    }//GEN-LAST:event_dataTableKeyReleased

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
    private javax.swing.JButton insertButton;
    private javax.swing.JPanel insertUpdatePanel;
    private javax.swing.JComboBox<String> numeCategorieCB;
    private javax.swing.JLabel numeCategorieLabel;
    private javax.swing.JComboBox<String> numeMeniuCB;
    private javax.swing.JLabel numeMeniuLabel;
    private javax.swing.JComboBox<String> numeProdusCB;
    private javax.swing.JLabel numeProdusLabel;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JButton showButton;
    // End of variables declaration//GEN-END:variables
}
