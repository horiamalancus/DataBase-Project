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

public class ProductStockPanel extends javax.swing.JPanel {

    /**
     * Creates new form productStockPanel
     */
    private final ApplicationWindow appWindow;
    private TableRowSorter<DefaultTableModel> tr;
    private List<RowSorter.SortKey> sortKeys;

    public ProductStockPanel(ApplicationWindow appWindow) {
        this.appWindow = appWindow;
        initComponents();
        initFilter();

        initActionListeners();
    }

    private void initActionListeners() {
        insertButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        showButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        deleteButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        deleteBoxesButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
        updateButton.addActionListener(appWindow.getAppActionListener().getButtonClickListener());
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

        int idx = (numeProdusCB.getItemCount() == -1) ? -1 : numeProdusCB.getSelectedIndex();

        numeProdusCB.removeAllItems();

        try {
            ResultSet rs = appWindow.getDataBaseConnection().getConnection().createStatement().executeQuery("SELECT nume_produs FROM Produse");

            while (rs.next()) {
                numeProdusCB.addItem(rs.getString(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(CategoriesAdminPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        numeProdusCB.setSelectedIndex((numeProdusCB.getItemCount() == 0) ? -1 : (idx == -1) ? 0 : idx);

    }

    public void startFilter() {
        tr.setRowFilter(RowFilter.regexFilter(filterTextField.getText()));
    }

    public void startAction(ActionEvent e) {
        JButton tmpEventButton = (JButton) e.getSource();

        Connection conn = appWindow.getDataBaseConnection().getConnection();

        DefaultTableModel tblModel;
        String nume_produs;
        String stoc_produs;

        switch (tmpEventButton.getText()) {
            /////////////// INSERARE ///////////////
            case "Inserare":

                try {

                    PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM stocuri_produs");
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    int nr = rs.getInt(1);

                    if (nr != 0 && dataTable.getModel().getRowCount() == 0) {
                        JOptionPane.showMessageDialog(this, "Mai intai faceti un refresh la baza de date.");
                        break;
                    }

                } catch (SQLException ex) {
                    if (ex.getMessage().contains("ORA-00001")) {
                        JOptionPane.showMessageDialog(this, "Stoc deja existent.");
                    } else if (ex.getMessage().contains("ORA-02290")) {
                        JOptionPane.showMessageDialog(this, "Stocul trebuie sa fie pozitiv.");
                    } else if (ex.getMessage().contains("ORA-01438")) {
                        JOptionPane.showMessageDialog(this, "Numarul stocului prea mare. Introduceti un numar de forma ???");
                    } else {
                        JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                    }

                } catch (NumberFormatException ex2) {
                    JOptionPane.showMessageDialog(this, "Stocul produselor trebuie sa contina doar cifre.");
                }

                if (stocProdusTextField.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Casetele sunt goale.");
                    break;
                }

                tr.setSortKeys(sortKeys);

                tblModel = (DefaultTableModel) this.dataTable.getModel();

                nume_produs = numeProdusCB.getSelectedItem().toString();
                stoc_produs = stocProdusTextField.getText();

                try {
                    PreparedStatement prepSt = conn.prepareStatement("INSERT INTO stocuri_produs(stoc_produs, Produse_nr_produs) VALUES(?, (SELECT nr_produs FROM Produse WHERE nume_produs = ?))");
                    prepSt.setString(1, stoc_produs);
                    prepSt.setString(2, nume_produs);
                    prepSt.execute();

                    Object tfData[] = {nume_produs, Short.parseShort(stoc_produs)};
                    tblModel.addRow(tfData);

                    conn.createStatement().execute("commit");
                    JOptionPane.showMessageDialog(this, "Inserare realizata cu succes");
                } catch (SQLException ex) {
                    if (ex.getMessage().contains("ORA-00001")) {
                        JOptionPane.showMessageDialog(this, "Stoc deja existent.");
                    } else if (ex.getMessage().contains("ORA-02290")) {
                        JOptionPane.showMessageDialog(this, "Stocul trebuie sa fie pozitiv.");
                    } else if (ex.getMessage().contains("ORA-01438")) {
                        JOptionPane.showMessageDialog(this, "Numarul stocului prea mare. Introduceti un numar de forma ???");
                    } else {
                        JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                    }

                } catch (NumberFormatException ex2) {
                    JOptionPane.showMessageDialog(this, "Stocul produselor trebuie sa contina doar cifre.");
                }
                Refresh();
                break;

            /////////////// STERGEREA CASTETELOR TEXT FIELD ///////////////
            case "Sterge casetele":
                stocProdusTextField.setText("");
                numeProdusCB.setSelectedIndex((numeProdusCB.getItemCount() == 0) ? -1 : 0);
                break;

            /////////////// MODIFICAREA IN BAZA DE DATE ///////////////
            case "Modificare":

                tblModel = (DefaultTableModel) dataTable.getModel();

                if (dataTable.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Tabelul este gol.");
                } else if (dataTable.getSelectedRowCount() == 1) {

                    stoc_produs = stocProdusTextField.getText();
                    nume_produs = numeProdusCB.getSelectedItem().toString();

                    try {

                        PreparedStatement prepSelectSt = conn.prepareStatement("SELECT stoc_produs FROM stocuri_produs WHERE Produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?)");
                        prepSelectSt.setString(1, nume_produs);
                        ResultSet resultSelectSet = prepSelectSt.executeQuery();
                        resultSelectSet.next();

                        String str = resultSelectSet.getString(1);

                        if (!stoc_produs.equals(resultSelectSet.getString(1))) {
                            PreparedStatement prepUpdateSt1 = conn.prepareStatement("UPDATE stocuri_produs SET stoc_produs = ? WHERE Produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?)");
                            prepUpdateSt1.setShort(1, Short.parseShort(stoc_produs));
                            prepUpdateSt1.setString(2, nume_produs);
                            prepUpdateSt1.execute();
                        }

                        tblModel.setValueAt(nume_produs, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0);
                        tblModel.setValueAt(Short.parseShort(stoc_produs), dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 1);

                        conn.createStatement().execute("commit");
                        JOptionPane.showMessageDialog(this, "Modificare realizata cu succes");
                    } catch (SQLException ex) {

                        if (ex.getMessage().contains("ORA-00001")) {
                            JOptionPane.showMessageDialog(this, "Stoc deja existent.");
                        } else if (ex.getMessage().contains("ORA-02290")) {
                            JOptionPane.showMessageDialog(this, "Stocul trebuie sa fie pozitiv.");
                        } else if (ex.getMessage().contains("ORA-01438")) {
                            JOptionPane.showMessageDialog(this, "Numarul stocului prea mare. Introduceti un numar de forma ???");
                        } else {
                            JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                        }

                    } catch (NumberFormatException ex2) {
                        JOptionPane.showMessageDialog(this, "Stocul produselor trebuie sa contina doar cifre.");
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Selecteaza un singur rand pentru a modifica.");

                }
                Refresh();
                break;

            /////////////// STERGEREA DIN BAZA DE DATE ///////////////
            case "Sterge":

                tblModel = (DefaultTableModel) dataTable.getModel();

                if (dataTable.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Tabelul este gol.");
                } else if (dataTable.getSelectedRowCount() == 1) {

                    nume_produs = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0).toString();

                    try {
                        PreparedStatement prepSt = conn.prepareStatement("DELETE FROM stocuri_produs WHERE Produse_nr_produs = (SELECT nr_produs FROM Produse WHERE nume_produs = ?)");
                        prepSt.setString(1, nume_produs);
                        prepSt.execute();

                        conn.createStatement().execute("commit");
                        JOptionPane.showMessageDialog(this, "Stergere realizata cu succes");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                    }

                    tblModel.removeRow(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()));

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
        fillComboBoxes();

        try {
            ResultSet rs = appWindow.getDataBaseConnection().getConnection().createStatement().executeQuery("SELECT * FROM stocuri_produs ORDER BY Produse_nr_produs");

            DefaultTableModel tblModel = (DefaultTableModel) dataTable.getModel();
            tblModel.setRowCount(0);

            while (rs.next()) {
                String stoc_produs = rs.getString(1);
                String nume_produs = rs.getString(2);

                PreparedStatement ps = appWindow.getDataBaseConnection().getConnection().prepareStatement("SELECT nume_produs FROM Produse WHERE nr_produs = ?");
                ps.setShort(1, Short.valueOf(nume_produs));
                ResultSet rs2 = ps.executeQuery();
                rs2.next();
                nume_produs = rs2.getString(1);

                Object tblData[] = {nume_produs, Short.parseShort(stoc_produs)};
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
        updateButton = new javax.swing.JButton();
        insertUpdatePanel = new javax.swing.JPanel();
        nume_produs = new javax.swing.JLabel();
        stocProdusLabel = new javax.swing.JLabel();
        stocProdusTextField = new javax.swing.JTextField();
        insertButton = new javax.swing.JButton();
        deleteBoxesButton = new javax.swing.JButton();
        numeProdusCB = new javax.swing.JComboBox<>();
        filterTextField = new javax.swing.JTextField();
        filterLabel = new javax.swing.JLabel();

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "nume_produs", "stoc_produs"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Short.class
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
        deleteButton.setActionCommand("ButoaneStocuriAdmin");
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
        showButton.setActionCommand("ButoaneStocuriAdmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 63;
        gridBagConstraints.ipady = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 13, 14);
        buttonsPanel.add(showButton, gridBagConstraints);

        updateButton.setText("Modificare");
        updateButton.setActionCommand("ButoaneStocuriAdmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 63;
        gridBagConstraints.ipady = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 13, 14);
        buttonsPanel.add(updateButton, gridBagConstraints);

        nume_produs.setText("nume_produs");

        stocProdusLabel.setText("stoc_produs");

        insertButton.setText("Inserare");
        insertButton.setActionCommand("ButoaneStocuriAdmin");

        deleteBoxesButton.setText("Sterge casetele");
        deleteBoxesButton.setActionCommand("ButoaneStocuriAdmin");

        javax.swing.GroupLayout insertUpdatePanelLayout = new javax.swing.GroupLayout(insertUpdatePanel);
        insertUpdatePanel.setLayout(insertUpdatePanelLayout);
        insertUpdatePanelLayout.setHorizontalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, insertUpdatePanelLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stocProdusLabel)
                    .addComponent(nume_produs))
                .addGap(18, 18, 18)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stocProdusTextField)
                    .addComponent(numeProdusCB, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(deleteBoxesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(87, 87, 87))
        );
        insertUpdatePanelLayout.setVerticalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nume_produs)
                    .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numeProdusCB, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stocProdusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stocProdusLabel)
                    .addComponent(deleteBoxesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(147, Short.MAX_VALUE))
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
                        .addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
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

        String nume_produs = dataTable.getValueAt(dataTable.getSelectedRow(), 0).toString();
        String stoc_produs = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();

        numeProdusCB.setSelectedItem(nume_produs);
        stocProdusTextField.setText(stoc_produs);
    }//GEN-LAST:event_dataTableMouseClicked

    private void dataTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataTableKeyReleased

        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            String nume_produs = dataTable.getValueAt(dataTable.getSelectedRow(), 0).toString();
            String stoc_produs = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();

            numeProdusCB.setSelectedItem(nume_produs);
            stocProdusTextField.setText(stoc_produs);
        }
    }//GEN-LAST:event_dataTableKeyReleased

    private void filterTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filterTextFieldKeyReleased
        startFilter();
    }//GEN-LAST:event_filterTextFieldKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton deleteBoxesButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JButton insertButton;
    private javax.swing.JPanel insertUpdatePanel;
    private javax.swing.JComboBox<String> numeProdusCB;
    private javax.swing.JLabel nume_produs;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JButton showButton;
    private javax.swing.JLabel stocProdusLabel;
    private javax.swing.JTextField stocProdusTextField;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
