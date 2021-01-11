/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpackage.AdminWindowPanels;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
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
public class CategoriesAdminPanel extends javax.swing.JPanel {

    /**
     * Creates new form categoriesAdminPanel
     */
    private final ApplicationWindow appWindow;
    private TableRowSorter<DefaultTableModel> tr;
    private List<RowSorter.SortKey> sortKeys;

    public CategoriesAdminPanel(ApplicationWindow appWindow) {
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

        int idx = (selectMenuCB.getItemCount() == -1) ? -1 : selectMenuCB.getSelectedIndex();

        selectMenuCB.removeAllItems();

        try {
            ResultSet rs = appWindow.getDataBaseConnection().getConnection().createStatement().executeQuery("SELECT nume_meniu FROM Meniuri");

            while (rs.next()) {
                selectMenuCB.addItem(rs.getString(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(CategoriesAdminPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        selectMenuCB.setSelectedIndex((selectMenuCB.getItemCount() == 0) ? -1 : (idx == -1) ? 0 : idx);

    }

    public void startFilter() {
        tr.setRowFilter(RowFilter.regexFilter(filterTextField.getText()));
    }

    public void startAction(ActionEvent e) {
        JButton tmpEventButton = (JButton) e.getSource();

        Connection conn = appWindow.getDataBaseConnection().getConnection();

        DefaultTableModel tblModel;
        String nr_categorie;
        String nume_categorie;
        String nume_meniu;
        String detalii_suplimentare_categorie;
        String data_crearii;

        switch (tmpEventButton.getText()) {
            /////////////// INSERARE ///////////////
            case "Inserare":

                try {
                    nume_meniu = selectMenuCB.getSelectedItem().toString();

                    PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Categorii WHERE Meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = ?)");
                    ps.setString(1, nume_meniu);
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    int nr = rs.getInt(1);

                    if (nr != 0 && dataTable.getModel().getRowCount() == 0) {
                        JOptionPane.showMessageDialog(this, "Mai intai faceti un refresh la baza de date.");
                        break;
                    }

                } catch (SQLException ex) {
                    if (ex.getMessage().contains("ORA-00001")) {
                        JOptionPane.showMessageDialog(this, "Categorie deja existenta");
                    } else if (ex.getMessage().contains("ORA-02290")) {
                        JOptionPane.showMessageDialog(this, "Categoriile trebuie sa contina doar cuvinte, primul cuvânt începând cu majusculă, opțional delimitate prin ‘-‘");
                    } else {
                        JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                    }
                }

                if (numeCategorieTextField.getText().equals("") && detaliiSuplimentareTextField.getText().equals("")) {
                    JOptionPane.showMessageDialog(this, "Casetele sunt goale.");
                    break;
                }

                tr.setSortKeys(sortKeys);

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(System.currentTimeMillis());
                String currentDate = formatter.format(date);

                tblModel = (DefaultTableModel) this.dataTable.getModel();

                nume_categorie = numeCategorieTextField.getText();
                nume_meniu = selectMenuCB.getSelectedItem().toString();
                detalii_suplimentare_categorie = detaliiSuplimentareTextField.getText();

                ////////////////////// Modificare baza de date //////////////////////
                try {
                    PreparedStatement prepSt = conn.prepareStatement("INSERT INTO Categorii(nume_categorie, Meniuri_nr_meniu, detalii_suplimentare_categorie) VALUES(?, (SELECT nr_meniu FROM Meniuri where nume_meniu = ?), ?)");
                    prepSt.setString(1, nume_categorie);
                    prepSt.setString(2, nume_meniu);
                    prepSt.setString(3, detalii_suplimentare_categorie);
                    prepSt.execute();

                    ResultSet rs = conn.createStatement().executeQuery("SELECT MAX(nr_categorie) FROM Categorii");
                    rs.next();
                    nr_categorie = rs.getString(1);
                    Object tfData[] = {Short.parseShort(nr_categorie), nume_categorie, detalii_suplimentare_categorie, currentDate, nume_meniu};
                    tblModel.addRow(tfData);

                    conn.createStatement().execute("commit");
                    JOptionPane.showMessageDialog(this, "Categorie adaugata cu succes");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
                Refresh();
                break;

            /////////////// STERGEREA CASTETELOR TEXT FIELD ///////////////
            case "Sterge casetele":
                numeCategorieTextField.setText("");
                detaliiSuplimentareTextField.setText("");
                selectMenuCB.setSelectedIndex((selectMenuCB.getItemCount() == 0) ? -1 : 0);
                break;

            /////////////// MODIFICAREA IN BAZA DE DATE ///////////////
            case "Modificare":

                tblModel = (DefaultTableModel) dataTable.getModel();

                if (dataTable.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Tabelul este gol.");
                } else if (dataTable.getSelectedRowCount() == 1) {

                    nr_categorie = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0).toString();
                    nume_categorie = numeCategorieTextField.getText();
                    detalii_suplimentare_categorie = detaliiSuplimentareTextField.getText();
                    nume_meniu = selectMenuCB.getSelectedItem().toString();
                    data_crearii = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 3).toString();

                    Savepoint sp = null;

                    try {
                        
                        conn.setAutoCommit(false);
                        sp = conn.setSavepoint("sp");
                        
                        PreparedStatement prepSelectSt = conn.prepareStatement("SELECT c.nume_categorie, c.detalii_suplimentare_categorie, m.nume_meniu FROM Categorii c, Meniuri m WHERE c.nr_categorie = ? AND m.nr_meniu = c.Meniuri_nr_meniu");
                        prepSelectSt.setShort(1, Short.parseShort(nr_categorie));
                        ResultSet resultSelectSet = prepSelectSt.executeQuery();
                        resultSelectSet.next();

                        if (!nume_meniu.equals(resultSelectSet.getString("nume_meniu"))) {
                            PreparedStatement prepUpdateSt2 = conn.prepareStatement("UPDATE Categorii SET Meniuri_nr_meniu = (SELECT nr_meniu FROM Meniuri WHERE nume_meniu = ?) WHERE nr_categorie = ?");
                            prepUpdateSt2.setString(1, nume_meniu);
                            prepUpdateSt2.setShort(2, Short.parseShort(nr_categorie));
                            prepUpdateSt2.execute();
                        }

                        if (!nume_categorie.equals(resultSelectSet.getString("nume_categorie"))) {
                            PreparedStatement prepUpdateSt1 = conn.prepareStatement("UPDATE Categorii SET nume_categorie = ? WHERE nr_categorie = ?");
                            prepUpdateSt1.setString(1, nume_categorie);
                            prepUpdateSt1.setShort(2, Short.parseShort(nr_categorie));
                            prepUpdateSt1.execute();
                        }

                        if (!detalii_suplimentare_categorie.equals(resultSelectSet.getString("detalii_suplimentare_categorie"))) {
                            PreparedStatement prepUpdateSt2 = conn.prepareStatement("UPDATE Categorii SET detalii_suplimentare_categorie = ? WHERE nr_categorie = ?");
                            prepUpdateSt2.setString(1, detalii_suplimentare_categorie);
                            prepUpdateSt2.setShort(2, Short.parseShort(nr_categorie));
                            prepUpdateSt2.execute();
                        }

                        tblModel.setValueAt(Short.parseShort(nr_categorie), dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0);
                        tblModel.setValueAt(nume_categorie, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 1);
                        tblModel.setValueAt(detalii_suplimentare_categorie, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 2);
                        tblModel.setValueAt(data_crearii, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 3);
                        tblModel.setValueAt(nume_meniu, dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 4);

                        conn.createStatement().execute("commit");
                        if(!conn.getAutoCommit()) conn.setAutoCommit(true);
                        JOptionPane.showMessageDialog(this, "Categorie modificata cu succes");
                    } catch (SQLException ex) {

                        
                        try {
                            conn.rollback(sp);
                            conn.setAutoCommit(true);
                        } catch (SQLException ex1) {
                            Logger.getLogger(IngredientsAdminPanel.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                        
                        if (ex.getMessage().contains("ORA-00001")) {
                            JOptionPane.showMessageDialog(this, "Categorie deja existenta");
                        } else if (ex.getMessage().contains("ORA-02290")) {
                            JOptionPane.showMessageDialog(this, "Categoriile trebuie sa contina doar cuvinte, primul cuvânt începând cu majusculă, opțional delimitate prin ‘-‘");
                        } else {
                            JOptionPane.showMessageDialog(this, "Eroare necunoscuta");
                        }

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

                    nr_categorie = tblModel.getValueAt(dataTable.convertRowIndexToModel(dataTable.getSelectedRow()), 0).toString();

                    try {
                        PreparedStatement prepSt = conn.prepareStatement("DELETE FROM Categorii WHERE nr_categorie = ?");
                        prepSt.setString(1, nr_categorie);
                        prepSt.execute();

                        conn.createStatement().execute("commit");
                        JOptionPane.showMessageDialog(this, "Categorie inlaturata cu succes");
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

        String nume_meniu = selectMenuCB.getSelectedItem().toString();

        try {
            PreparedStatement ps = appWindow.getDataBaseConnection().getConnection().prepareStatement("SELECT c.nr_categorie, c.nume_categorie, c.detalii_suplimentare_categorie, c.data_crearii, m.nume_meniu FROM Categorii c, Meniuri m WHERE m.nr_meniu = c.Meniuri_nr_meniu AND m.nume_meniu = ? ORDER BY c.nr_categorie");
            ps.setString(1, nume_meniu);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel tblModel = (DefaultTableModel) dataTable.getModel();
            tblModel.setRowCount(0);

            while (rs.next()) {
                String nr_categorie = rs.getString("nr_categorie");
                String nume_categorie = rs.getString("nume_categorie");
                String detalii_suplimentare_categorie = rs.getString("detalii_suplimentare_categorie");
                String data_crearii = rs.getDate("data_crearii").toString();
                nume_meniu = rs.getString("nume_meniu");

                Object tblData[] = {Short.parseShort(nr_categorie), nume_categorie, detalii_suplimentare_categorie, data_crearii, nume_meniu};
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
        updateButton = new javax.swing.JButton();
        insertUpdatePanel = new javax.swing.JPanel();
        numeCategorieLabel = new javax.swing.JLabel();
        detaliiSuplimentareLabel = new javax.swing.JLabel();
        numeCategorieTextField = new javax.swing.JTextField();
        detaliiSuplimentareTextField = new javax.swing.JTextField();
        insertButton = new javax.swing.JButton();
        deleteBoxesButton = new javax.swing.JButton();
        selectMenuCB = new javax.swing.JComboBox<>();
        selectMenuLabel = new javax.swing.JLabel();
        filterTextField = new javax.swing.JTextField();
        filterLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1065, 718));

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "nr_categorie", "nume_categorie", "detalii_suplimentare", "data_creare", "nume_meniu"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Short.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        deleteButton.setActionCommand("ButoaneCategoriiAdmin");
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
        showButton.setActionCommand("ButoaneCategoriiAdmin");
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
        updateButton.setActionCommand("ButoaneCategoriiAdmin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 63;
        gridBagConstraints.ipady = 48;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 12, 13, 14);
        buttonsPanel.add(updateButton, gridBagConstraints);

        numeCategorieLabel.setText("nume_categorie:");

        detaliiSuplimentareLabel.setText("detalii_suplimentare:");

        insertButton.setText("Inserare");
        insertButton.setActionCommand("ButoaneCategoriiAdmin");

        deleteBoxesButton.setText("Sterge casetele");
        deleteBoxesButton.setActionCommand("ButoaneCategoriiAdmin");

        selectMenuCB.setToolTipText("");
        selectMenuCB.setName(""); // NOI18N

        selectMenuLabel.setText("Selecteaza meniu:");

        javax.swing.GroupLayout insertUpdatePanelLayout = new javax.swing.GroupLayout(insertUpdatePanel);
        insertUpdatePanel.setLayout(insertUpdatePanelLayout);
        insertUpdatePanelLayout.setHorizontalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(detaliiSuplimentareLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(numeCategorieLabel)
                    .addComponent(selectMenuLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                        .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(detaliiSuplimentareTextField)
                            .addComponent(numeCategorieTextField))
                        .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(deleteBoxesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(87, 87, 87))
                    .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                        .addComponent(selectMenuCB, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        insertUpdatePanelLayout.setVerticalGroup(
            insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(insertUpdatePanelLayout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numeCategorieTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numeCategorieLabel)
                    .addComponent(insertButton, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(detaliiSuplimentareTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(detaliiSuplimentareLabel)
                    .addComponent(deleteBoxesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(insertUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectMenuCB, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectMenuLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
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

        String nume_categorie = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();
        String detalii_suplimentare_meniu = (dataTable.getValueAt(dataTable.getSelectedRow(), 2) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 2).toString();
        String nume_meniu = dataTable.getValueAt(dataTable.getSelectedRow(), 4).toString();

        numeCategorieTextField.setText(nume_categorie);
        detaliiSuplimentareTextField.setText(detalii_suplimentare_meniu);
        selectMenuCB.setSelectedItem(nume_meniu);
    }//GEN-LAST:event_dataTableMouseClicked

    private void dataTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataTableKeyReleased

        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            String nume_categorie = dataTable.getValueAt(dataTable.getSelectedRow(), 1).toString();
            String detalii_suplimentare_meniu = (dataTable.getValueAt(dataTable.getSelectedRow(), 2) == null) ? "" : dataTable.getValueAt(dataTable.getSelectedRow(), 2).toString();
            String nume_meniu = dataTable.getValueAt(dataTable.getSelectedRow(), 4).toString();

            numeCategorieTextField.setText(nume_categorie);
            detaliiSuplimentareTextField.setText(detalii_suplimentare_meniu);
            selectMenuCB.setSelectedItem(nume_meniu);
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
    private javax.swing.JLabel detaliiSuplimentareLabel;
    private javax.swing.JTextField detaliiSuplimentareTextField;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JButton insertButton;
    private javax.swing.JPanel insertUpdatePanel;
    private javax.swing.JLabel numeCategorieLabel;
    private javax.swing.JTextField numeCategorieTextField;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JComboBox<String> selectMenuCB;
    private javax.swing.JLabel selectMenuLabel;
    private javax.swing.JButton showButton;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
